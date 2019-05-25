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
package com.github.mob41.osumer.io;

import java.util.Observable;

import com.github.mob41.osumer.debug.DebugDump;

public abstract class Downloader extends Observable implements Runnable {

    public static final int DOWNLOADING = 0;

    public static final int PAUSED = 1;

    public static final int COMPLETED = 2;

    public static final int CANCELLED = 3;

    public static final int ERROR = 4;
    
    public abstract DebugDump getErrorDump();

    public abstract String getDownloadFolder();

    public abstract String getFileName();

    public abstract String getUrl();

    public abstract int getDownloaded();

    public abstract int getSize();

    public abstract float getProgress();

    public abstract int getStatus();

    public abstract void pause();

    public abstract void resume();

    public abstract void cancel();

    public abstract void download();

    public abstract void run();

}
