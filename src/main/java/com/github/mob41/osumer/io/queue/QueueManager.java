package com.github.mob41.osumer.io.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.io.Downloader;

public class QueueManager {

    public static final int DEFAULT_MAX_THREADS = 4;

    public final List<Queue> queues;

    private Thread thread;

    private boolean keepRunning = false;

    private final Config config;

    // private int maxThread = 4;

    public QueueManager(Config config) {
        queues = new CopyOnWriteArrayList<Queue>();
        this.config = config;
        startQueuing();
    }

    public void startQueuing() {
        if (!keepRunning) {
            keepRunning = true;
            thread = new Thread() {
                public void run() {
                    while (keepRunning) {
                        final int maxThread = config.getMaxThreads();

                        int running = 0;
                        List<Queue> list = getList();
                        for (Queue queue : list) {
                            if (queue.getDownloader().getStatus() == Downloader.DOWNLOADING) {
                                running++;
                            }
                        }

                        if (running < maxThread) {
                            for (Queue queue : list) {
                                if (running >= maxThread) {
                                    break;
                                }

                                if (queue.getDownloader().getStatus() == -1) {
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

    public void stopQueuing() {
        if (keepRunning) {
            keepRunning = false;
            thread.interrupt();
        }
    }

    public List<Queue> getList() {
        return Collections.unmodifiableList(queues);
    }

    public boolean addQueue(Queue queue) {
        if (getQueue(queue.getName()) != null) {
            return false;
        } else {
            queues.add(queue);
            return true;
        }
    }

    public boolean removeQueue(Queue queue) {
        if (queues.contains(queue)) {
            queues.remove(queue);
            return true;
        } else {
            return false;
        }
    }

    public Queue getQueue(String name) {
        List<Queue> copyList = Collections.unmodifiableList(queues);

        for (Queue queue : copyList) {
            if (queue.getName().equals(name)) {
                return queue;
            }
        }

        return null;
    }

    public void startAll() {
        List<Queue> copyList = Collections.unmodifiableList(queues);

        for (Queue queue : copyList) {
            queue.start();
        }
    }

    public void cancelAll() {
        List<Queue> copyList = Collections.unmodifiableList(queues);

        for (Queue queue : copyList) {
            queue.cancel();
            removeQueue(queue);
        }
    }

    public void pauseAll() {
        List<Queue> copyList = Collections.unmodifiableList(queues);

        for (Queue queue : copyList) {
            queue.pause();
        }
    }

    public void resumeAll() {
        List<Queue> copyList = Collections.unmodifiableList(queues);

        for (Queue queue : copyList) {
            queue.resume();
        }
    }

}
