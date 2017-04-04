package com.github.mob41.osumer.io;

import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

public class Queue {

	private final Downloader downloader;
	
	private final String name;
	
	private final BufferedImage thumb;
	
	private long startTime = 0;
	
	private final QueueAction[] beforeActions;
	
	private final QueueAction[] afterActions;
	
	public Queue(String name, Downloader downloader, BufferedImage thumb, QueueAction[] beforeActions, QueueAction[] afterActions) {
		this.downloader = downloader;
		this.name = name;
		this.thumb = thumb;
		this.beforeActions = beforeActions;
		this.afterActions = afterActions;
	}
	
	public void runBeforeActions(){
		if (beforeActions != null){
			for (QueueAction action : beforeActions){
				action.run(this);
			}
		}
	}
	
	public void runAfterActions(){
		if (afterActions != null){
			for (QueueAction action : afterActions){
				action.run(this);
			}
		}
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
		runBeforeActions();
		
		setStartTime(System.nanoTime());
		downloader.download();
		
		downloader.deleteObservers();
		downloader.addObserver(new Observer(){

			@Override
			public void update(Observable arg0, Object arg1) {
				if (downloader.getStatus() == Downloader.COMPLETED){
					runAfterActions();
				}
			}
			
		});
	}
	
	public void pause(){
		downloader.pause();
	}
	
	public void resume(){
		setStartTime(System.nanoTime());
		downloader.resume();
	}
	
	public void cancel(){
		downloader.cancel();
		downloader.deleteObservers();
	}

	public BufferedImage getThumb() {
		return thumb;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public QueueAction[] getBeforeActions() {
		return beforeActions;
	}

	public QueueAction[] getAfterActions() {
		return afterActions;
	}

}
