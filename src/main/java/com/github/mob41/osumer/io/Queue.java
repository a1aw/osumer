package com.github.mob41.osumer.io;

import java.awt.image.BufferedImage;

public class Queue {

	private final Downloader downloader;
	
	private final String name;
	
	private final BufferedImage thumb;
	
	public Queue(String name, Downloader downloader, BufferedImage thumb) {
		this.downloader = downloader;
		this.name = name;
		this.thumb = thumb;
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

	public BufferedImage getThumb() {
		return thumb;
	}

}
