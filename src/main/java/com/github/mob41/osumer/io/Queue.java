package com.github.mob41.osumer.io;

public class Queue {

	private final Downloader downloader;
	
	private final String name;
	
	public Queue(String name, Downloader downloader) {
		this.downloader = downloader;
		this.name = name;
	}
	
	public Downloader getDownloader(){
		return downloader;
	}
	
	public String getName(){
		return name;
	}
	
	public float getProgress(){
		return downloader.getProgress();
	}
	
	public void start(){
		downloader.download();
	}
	
	public void pause(){
		downloader.pause();
	}
	
	public void resume(){
		downloader.resume();
	}
	
	public void cancel(){
		downloader.cancel();
	}

}
