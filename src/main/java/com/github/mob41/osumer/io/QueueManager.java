package com.github.mob41.osumer.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class QueueManager {
	
	public final List<Queue> queues;

	private Thread thread;
	
	private boolean keepRunning = false;
	
	private int maxThread = 4;
	
	public QueueManager() {
		queues = new CopyOnWriteArrayList<Queue>();
		startQueuing();
	}
	
	public void startQueuing(){
		if (!keepRunning){
			keepRunning = true;
			thread = new Thread(){
				public void run(){
					while(keepRunning){
						int running = 0;
						List<Queue> list = getList();
						for (Queue queue : list){
							if (queue.getDownloader().getStatus() == Downloader.DOWNLOADING){
								running++;
							}
						}
						
						if (running < maxThread){
							for (Queue queue : list){
								if (running >= maxThread){
									break;
								}
								
								if (queue.getDownloader().getStatus() == -1){
									queue.start();
									running++;
								}
							}
						}
						try {
							sleep(2000);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			};
			thread.start();
		}
	}
	
	public void stopQueuing(){
		if (keepRunning){
			keepRunning = false;
			thread.interrupt();
		}
	}
	
	public List<Queue> getList(){
		return Collections.unmodifiableList(queues);
	}
	
	public boolean addQueue(Queue queue){
		if (getQueue(queue.getName()) != null){
			return false;
		} else {
			queues.add(queue);
			return true;
		}
	}
	
	public boolean removeQueue(Queue queue){
		if (queues.contains(queue)){
			queues.remove(queue);
			return true;
		} else {
			return false;
		}
	}
	
	public Queue getQueue(String name){
		List<Queue> copyList = Collections.unmodifiableList(queues);
		
		for (Queue queue : copyList){
			if (queue.getName().equals(name)){
				return queue;
			}
		}
		
		return null;
	}
	
	public void startAll(){
		List<Queue> copyList = Collections.unmodifiableList(queues);
		
		for (Queue queue : copyList){
			queue.start();
		}
	}
	
	public void cancelAll(){
		List<Queue> copyList = Collections.unmodifiableList(queues);
		
		for (Queue queue : copyList){
			queue.cancel();
			removeQueue(queue);
		}
	}
	
	public void pauseAll(){
		List<Queue> copyList = Collections.unmodifiableList(queues);
		
		for (Queue queue : copyList){
			queue.pause();
		}
	}
	
	public void resumeAll(){
		List<Queue> copyList = Collections.unmodifiableList(queues);
		
		for (Queue queue : copyList){
			queue.resume();
		}
	}

}
