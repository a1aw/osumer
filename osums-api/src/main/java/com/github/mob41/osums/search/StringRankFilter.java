package com.github.mob41.osums.search;

public class StringRankFilter implements RankFilter {

	private final String rank;
	
	public StringRankFilter(String rank) {
		this.rank = rank;
	}
	
	public String getRank() {
		return rank;
	}

	@Override
	public String handleUrl(String url) {
		return url;
	};
	
}
