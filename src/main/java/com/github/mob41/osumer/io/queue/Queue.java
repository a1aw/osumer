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
package com.github.mob41.osumer.io.queue;

import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import com.github.mob41.osumer.io.Downloader;

public class Queue {

    private final Downloader downloader;

    private final String name;

    private final BufferedImage thumb;

    private long startTime = 0;

    private final QueueAction[] beforeActions;

    private final QueueAction[] afterActions;

    public Queue(String name, Downloader downloader, BufferedImage thumb, QueueAction[] beforeActions,
            QueueAction[] afterActions) {
        this.downloader = downloader;
        this.name = name;
        this.thumb = thumb;
        this.beforeActions = beforeActions;
        this.afterActions = afterActions;
    }

    public void runBeforeActions() {
        if (beforeActions != null) {
            for (QueueAction action : beforeActions) {
                action.run(this);
            }
        }
    }

    public void runAfterActions() {
        if (afterActions != null) {
            for (QueueAction action : afterActions) {
                action.run(this);
            }
        }
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public String getName() {
        return name;
    }

    public float getProgress() {
        return downloader.getProgress();
    }

    public void start() {
        runBeforeActions();

        setStartTime(System.nanoTime());
        downloader.download();

        downloader.deleteObservers();
        downloader.addObserver(new Observer() {

            @Override
            public void update(Observable arg0, Object arg1) {
                if (downloader.getStatus() == Downloader.COMPLETED) {
                    runAfterActions();
                }
            }

        });
    }

    public void pause() {
        downloader.pause();
    }

    public void resume() {
        setStartTime(System.nanoTime());
        downloader.resume();
    }

    public void cancel() {
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
