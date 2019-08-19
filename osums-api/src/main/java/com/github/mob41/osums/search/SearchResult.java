package com.github.mob41.osums.search;

public class SearchResult {

	private final SongResult[] result;
	
	private final int currentPage;
	
	private final int totalPages;

	public SearchResult(SongResult[] result, int currentPage, int totalPages) {
		super();
		this.result = result;
		this.currentPage = currentPage;
		this.totalPages = totalPages;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public SongResult[] getResult() {
		return result;
	}
	
}
