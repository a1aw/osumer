/*******************************************************************************
 * Any modification, copies of sections of this file must be attached with this
 * license and shown clearly in the developer's project. The code can be used
 * as long as you state clearly you do not own it. Any violation might result in
 *  a take-down.
 *
 * MIT License
 *
 * Copyright (c) 2016, 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.github.mob41.osumer.daemon;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.io.Downloader;
import com.github.mob41.osumer.queue.Queue;

public class QueueManager {

    public static final int DEFAULT_MAX_THREADS = 4;

    public final List<Queue> queues;

    private Thread thread;

    private boolean keepRunning = false;

    private final Configuration config;

    // private int maxThread = 4;

    public QueueManager(Configuration config) {
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
                        final int delay = config.getNextCheckDelay();

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
                            sleep(delay);
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
