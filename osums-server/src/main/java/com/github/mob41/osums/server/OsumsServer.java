package com.github.mob41.osums.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.github.mob41.osums.Osums;
import com.github.mob41.osums.OsumsNewParser;
import com.github.mob41.osums.OsumsOldParser;
import com.github.mob41.osums.search.StringRankFilter;
import com.github.mob41.osums.search.RankFilter;
import com.github.mob41.osums.search.SearchFilter;
import com.github.mob41.osums.search.SearchResult;
import com.github.mob41.osums.search.SongResult;

public class OsumsServer {
	
	public static final String DEFAULT_PROP_FILE_NAME = "config.properties";
	
	private static final String KEY_JDBC_DRIVER = "jdbc-driver";
	
	private static final String DEFAULT_JDBC_DRIVER = "org.sqlite.JDBC";
	
	private static final String KEY_DOWNLOAD_BASIC_RESULTS = "download-basic-results";
	
	private static final String DEFAULT_DOWNLOAD_BASIC_RESULTS = "false";
	
	private static final String KEY_DB_URL = "db-url";
	
	private static final String DEFAULT_DB_URL = "jdbc:sqlite:cache.db";
	
	private static final String KEY_OSU_USERNAME = "osu-username";
	
	private static final String KEY_OSU_PASSWORD = "osu-password";
	
	private static final String KEY_SERVER_PORT = "port";
	
	private static final int DEFAULT_SERVER_PORT = 6099;
	
	private final String propFileName;
	
	private Properties prop;
	
	private Connection conn;
	
	private final Osums osums;
	
	private static Logger logger = Logger.getLogger(OsumsServer.class);
	
	public OsumsServer(Osums osums, String propFileName) throws Exception {
		this.osums = osums;
		this.propFileName = propFileName;
		prop = new Properties();
		
		load();
		
		if (!prop.containsKey(KEY_OSU_USERNAME) || !prop.containsKey(KEY_OSU_PASSWORD) || prop.getProperty(KEY_OSU_USERNAME).isEmpty() || prop.getProperty(KEY_OSU_PASSWORD).isEmpty()) {
			logger.error("You must provide an osu! username and password in properties file to allow osums-server to search and check songs online!");
			System.exit(-1);
			return;
		}
		
		String user = prop.getProperty(KEY_OSU_USERNAME);
		String pass = prop.getProperty(KEY_OSU_PASSWORD);
		
		logger.info("Logging in as \"" + user + "\"...");
		
		int result = osums.login(user, pass);
		
		if (result != 0) {
			logger.error("Login failed with status code " + result + "! Terminating server.");
			System.exit(-1);
			return;
		}
		
		logger.info("Successfully logged in as \"" + user + "\".");
		
		//Class.forName(prop.getProperty(KEY_JDBC_DRIVER));
	    conn = DriverManager.getConnection(prop.getProperty(KEY_DB_URL));

	    importSQL(conn, OsumsServer.class.getClassLoader().getResourceAsStream("osums.sql"));

	    if (prop.containsKey(KEY_DOWNLOAD_BASIC_RESULTS) && prop.getProperty(KEY_DOWNLOAD_BASIC_RESULTS).equals("true")) {
			logger.info("Server is configured to download all basic song results to the database once.");
	    	downloadBasicSongResults();
	    }
	}
	
	public int getTotalSongs() {
		Statement st = null;
		try {
			String sql = "SELECT COUNT(*) FROM songs";
			st = conn.createStatement();
			ResultSet result = st.executeQuery(sql);
			int out = result.getInt(1);
			st.close();
			return out;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public void downloadBasicSongResults() throws Exception{
		logger.info("[Stage 1/2] Downloading all results from all rank status...");
		
		String[] ranks = {
				OsumsNewParser.RANK_LOVED,
				OsumsNewParser.RANK_PENDING,
				OsumsNewParser.RANK_QUALIFIED,
				OsumsNewParser.RANK_RANKED
		};
		
		SearchResult result;
		SearchFilter[] filters;
		String rank;
		List<SongResult> list = new ArrayList<SongResult>();
		for (int i = 0; i < ranks.length; i++) {
			rank = ranks[i];
			logger.info("[Stage 1/2] Requesting results from \"" + rank + "...");
			filters = new SearchFilter[] { new StringRankFilter(rank) };
			
			int page = 1;
			do {
				logger.info("[Stage 1/2] [" + rank + "] Requesting results from page " + page + "...");
				result = osums.searchOnlineMaps(null, filters, page);
				
				if (result == null) {
					logger.error("[Stage 1/2] [" + rank + "] No results returned at " + page + "!");
					continue;
				}
				
				list.addAll(Arrays.asList(result.getResult()));
				
				logger.info("[Stage 1/2] [" + rank + "] Completed fetching results from page " + page + ". (" + page + "/" + result.getTotalPages() + ") (" + (int) (Math.floor((page / (float) result.getTotalPages() + i) / (float) ranks.length * 100.0)) + "%)");
				
				if (result.getCurrentPage() < result.getTotalPages()) {
					page++;
				}
			} while (result != null && result.getCurrentPage() < result.getTotalPages());
		}
		
		logger.info("[Stage 2/2] Importing all results to database...");

		int error = 0;
		PreparedStatement pst = conn.prepareStatement("INSERT INTO songs (id, rank, artist, title, creator, tags, favourites, plays) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		SongResult r;
		for (int i = 0; i < list.size(); i++) {
			r = list.get(i);
			logger.info("[Stage 2/2] Importing " + r.getId() + " (" + (i + 1) + "/" + list.size() + ") (" + (int) (Math.floor(i / (float) list.size() * 100)) + "%)");
			pst.setInt(1, r.getId());
			pst.setString(2, r.getRank());
			pst.setString(3, r.getArtist());
			pst.setString(4, r.getTitle());
			pst.setString(5, r.getCreator());
			pst.setString(6, join(r.getTags(), ","));
			pst.setInt(7, r.getFavourites());
			pst.setInt(8, r.getPlays());
			try {
				pst.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				error++;
			}
		}
		pst.close();
		logger.info("SQL Statements ended with " + error + " errors.");
		logger.info("All basic song results have been downloaded and imported.");
		
		prop.put(KEY_DOWNLOAD_BASIC_RESULTS, "false");
		save();
	}
	
	private String join(String[] array, String seperator) {
		String text = "";
		for (int i = 0; i < array.length; i++) {
			text += array[i];
			if (i != array.length - 1) {
				text += seperator;
			}
		}
		return text;
	}
	
	public SearchResult searchSong(String keywords, SearchFilter[] filters, int items, int page) {
		PreparedStatement pst = null;
		try {
			String sql = "SELECT * FROM songs";
			
			if (keywords != null && !keywords.isEmpty()) {
				sql += 
					" WHERE (id LIKE ?" + 
					" OR artist LIKE ?" +
					" OR title LIKE ?" +
					" OR creator LIKE ?" +
					" OR tags LIKE ?)";
			}
			
			sql += " LIMIT ? OFFSET ?";
			
			pst = conn.prepareStatement(sql);
			
			int offset = 0;
			
			if (keywords != null && !keywords.isEmpty()) {
				for (int i = 1; i <= 5; i++) {
					pst.setString(i, "%" + keywords + "%");
				}
				offset = 5;
			}
			
			pst.setInt(offset + 1, items);
			pst.setInt(offset + 2, items * (page - 1));
			
			List<SongResult> list = new ArrayList<SongResult>();
			
			ResultSet rows = pst.executeQuery();
			
			while (rows.next()) {
				String[] tags = rows.getString("tags").split(",");
				list.add(new SongResult(rows.getInt("id"), rows.getString("rank"), rows.getString("artist"), rows.getString("title"), rows.getString("creator"), tags, rows.getInt("favourites"), rows.getInt("plays")));
			}
			
			SongResult[] out = new SongResult[list.size()];
			for (int i = 0; i < out.length; i++) {
				out[i] = list.get(i);
			}
			
			pst.close();
			
			int totalPages = (int) Math.ceil(getTotalSongs() / items);
			
			return new SearchResult(out, page, totalPages);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void load() throws IOException {
		File file = new File(propFileName);
		if (!file.exists()) {
			generate();
			return;
		}
		FileInputStream in = new FileInputStream(file);
		prop.load(in);
		in.close();
	}
	
	private static void importSQL(Connection conn, InputStream in) throws SQLException {
	    Scanner s = new Scanner(in);
	    s.useDelimiter("(;(\r)?\n)|(--\n)");
	    Statement st = null;
	    try
	    {
	        st = conn.createStatement();
	        while (s.hasNext())
	        {
	            String line = s.next();
	            if (line.startsWith("/*!") && line.endsWith("*/"))
	            {
	                int i = line.indexOf(' ');
	                line = line.substring(i + 1, line.length() - " */".length());
	            }

	            if (line.trim().length() > 0)
	            {
	                st.execute(line);
	            }
	        }
	    }
	    finally
	    {
	        if (st != null) st.close();
	        s.close();
	    }
	}
	
	private void generate() throws IOException {
		prop.put(KEY_JDBC_DRIVER, DEFAULT_JDBC_DRIVER);
		prop.put(KEY_DB_URL, DEFAULT_DB_URL);
		prop.put(KEY_SERVER_PORT, Integer.toString(DEFAULT_SERVER_PORT));
		prop.put(KEY_DOWNLOAD_BASIC_RESULTS, DEFAULT_DOWNLOAD_BASIC_RESULTS);
		prop.put(KEY_OSU_USERNAME, "");
		prop.put(KEY_OSU_PASSWORD, "");
		save();
	}
	
	private void save() throws IOException {
		File file = new File(propFileName);
		FileOutputStream out = new FileOutputStream(file);
		prop.store(out, "osums-server Configuration");
		out.flush();
		out.close();
	}

	public int getServerPort() {
		int serverPort = -1;
		try {
			serverPort = Integer.parseInt(prop.getProperty(KEY_SERVER_PORT));
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}
		return serverPort;
	}
}
