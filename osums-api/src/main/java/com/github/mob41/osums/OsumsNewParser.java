package com.github.mob41.osums;

import org.jsoup.nodes.Document;

import com.github.mob41.osumer.debug.WithDumpException;
import com.github.mob41.osums.beatmap.OsuBeatmap;
import com.github.mob41.osums.beatmap.OsuSong;
import com.github.mob41.osums.search.SongResult;
import com.github.mob41.osums.search.SearchFilter;
import com.github.mob41.osums.search.SearchResult;

public class OsumsNewParser extends Osums {

	@Override
	public SearchResult searchOnlineMaps(String keywords, SearchFilter[] filters) throws WithDumpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int login(String username, String password) throws WithDumpException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean logout() throws WithDumpException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OsuSong getSongInfo(String link) throws WithDumpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OsuBeatmap getBeatmapInfo(String link) throws WithDumpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OsuSong parseSong(String originalUrl, Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult searchOnlineMaps(String keywords, SearchFilter[] filters, int page) throws WithDumpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SongResult[] parseSearch(String category, Document doc) throws WithDumpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVaildBeatmapUrl(String url) {
		return checkVaildBeatmapUrl(url);
	}

	public static boolean checkVaildBeatmapUrl(String url) {
		return false;
	}

}
