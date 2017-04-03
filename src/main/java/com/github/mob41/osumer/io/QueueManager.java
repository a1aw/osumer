package com.github.mob41.osumer.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class QueueManager {
	
	public final List<Queue> queues;

	public QueueManager() {
		queues = new CopyOnWriteArrayList<Queue>();
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
		List<Queue> copyList = new ArrayList<Queue>(queues.size());
		Collections.copy(copyList, queues);
		
		for (Queue queue : copyList){
			if (queue.getName().equals(name)){
				return queue;
			}
		}
		
		return null;
	}
	
	public void startAll(){
		List<Queue> copyList = new ArrayList<Queue>(queues.size());
		Collections.copy(copyList, queues);
		
		for (Queue queue : copyList){
			queue.start();
		}
	}
	
	public void cancelAll(){
		List<Queue> copyList = new ArrayList<Queue>(queues.size());
		Collections.copy(copyList, queues);
		
		for (Queue queue : copyList){
			queue.cancel();
			removeQueue(queue);
		}
	}
	
	public void pauseAll(){
		List<Queue> copyList = new ArrayList<Queue>(queues.size());
		Collections.copy(copyList, queues);
		
		for (Queue queue : copyList){
			queue.pause();
		}
	}
	
	public void resumeAll(){
		List<Queue> copyList = new ArrayList<Queue>(queues.size());
		Collections.copy(copyList, queues);
		
		for (Queue queue : copyList){
			queue.resume();
		}
	}

}
