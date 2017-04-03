package com.github.mob41.osumer.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Observable;

import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DumpManager;

public class OsuDownloader extends Observable implements Runnable{
	
	public static final int DOWNLOADING = 0;
	
	public static final int PAUSED = 1;
	
	public static final int COMPLETED = 2;
	
	public static final int CANCELLED = 3;
	
	public static final int ERROR = 4;
	
	private static final int MAX_BUFFER_SIZE = 1024;
	
	private final URL url;
	
	private final Osu osu;
	
	private final String folder;
	
	private final String fileName;
	
	private int size = -1;
	
	private int downloaded = 0;
	
	private int status;

	public OsuDownloader(String downloadFolder, String fileName, Osu osu, URL downloadUrl) {
		this.url = downloadUrl;
		this.osu = osu;
		this.folder = downloadFolder;
		this.fileName = fileName;
		
		status = DOWNLOADING;
		
		download();
	}
	
	public String getUrl(){
		return url.toString();
	}
	
	public int getSize(){
		return size;
	}
	
	public float getProgress(){
		return ((float) downloaded / size) * 100;
	}
	
	public int getStatus(){
		return status;
	}
	
	public void pause(){
		status = PAUSED;
		reportState();
	}
	
	public void resume(){
		status = DOWNLOADING;
		reportState();
		download();
	}
	
	public void cancel(){
		status = CANCELLED;
		reportState();
		
		File file = new File(folder + "\\" + fileName + ".osz");
		file.delete();
		
		downloaded = 0;
	}
	
	private void error(){
		status = ERROR;
		reportState();
	}
	
	private void download(){
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		RandomAccessFile file = null;
		InputStream in = null;
		
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestProperty("Range", "bytes=" + downloaded + "-");
			
			if (osu.getCookies().getCookieStore().getCookies().size() > 0) {
			    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
			    conn.setRequestProperty("Cookie",
			    join(";", osu.getCookies().getCookieStore().getCookies()));    
			}
			
			conn.connect();
			
			if (conn.getResponseCode() / 100 != 2){
				error();
			}
			
			int len = conn.getContentLength();
			
			if (len < 1){
				error();
			}
			
			if (size == -1){
				size = len;
				reportState();
			}
			
			file = new RandomAccessFile(folder + "\\" + fileName + ".osz", "rw");
			file.seek(downloaded);
			
			in = conn.getInputStream();
			
			while (status == DOWNLOADING){
				byte[] buffer = size - downloaded > MAX_BUFFER_SIZE ?
						new byte[MAX_BUFFER_SIZE] :
						new byte[size - downloaded];
				int read = in.read(buffer);
				if (read == -1){
					break;
				}
				
				file.write(buffer, 0, read);
				downloaded += read;
				reportState();
			}
			
			if (status == DOWNLOADING){
				status = COMPLETED;
				reportState();
			}
		} catch (IOException e){
			DumpManager.getInstance()
				.addDump(new DebugDump(
						null,
						"(Try&catch try)",
						"Error reporting and debug dump",
						"(Try&catch finally)",
						"Error when downloading",
						false,
						e));
			error();
		} finally {
			if (file != null){
				try {
					file.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
			
			if (in != null){
				try {
					in.close();
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private void reportState(){
		setChanged();
		notifyObservers();
	}
	
	private static String join(String separator, List<HttpCookie> objs){
		String out = "";
		
		String str;
		for (int i = 0; i < objs.size(); i++){
			str = objs.get(i).toString();
			
			out += str + separator;
			
			if (i != objs.size() - 1){
				out += " ";
			}
		}
		
		return out;
	}

}
