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
package com.github.mob41.osumer.io.legacy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.github.mob41.organdebug.DebugDump;
import com.github.mob41.organdebug.DumpManager;
import com.github.mob41.osums.io.Downloader;

public class URLDownloader extends Downloader {

    private static final int MAX_BUFFER_SIZE = 1024;

    private final URL url;

    private final String folder;

    private final String fileName;

    private int size = -1;

    private int downloaded = 0;

    private int status = -1;

    public URLDownloader(String downloadFolder, String fileName, URL downloadUrl) {
        this.url = downloadUrl;
        this.folder = downloadFolder;
        this.fileName = fileName;
    }

    public String getDownloadFolder() {
        return folder;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUrl() {
        return url.toString();
    }

    public int getDownloaded() {
        return downloaded;
    }

    public int getSize() {
        return size;
    }

    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    public int getStatus() {
        return status;
    }

    public void pause() {
        status = PAUSED;
        reportState();
    }

    public void resume() {
        status = DOWNLOADING;
        reportState();
        download();
    }

    public void cancel() {
        status = CANCELLED;
        reportState();

        File file = new File(folder + "\\" + fileName + ".osz");
        file.delete();

        downloaded = 0;
    }

    private void error() {
        status = ERROR;
        reportState();
    }

    public void download() {
        if (status != DOWNLOADING && status != COMPLETED) {
            status = DOWNLOADING;
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        RandomAccessFile file = null;
        InputStream in = null;

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Range", "bytes=" + downloaded + "-");

            conn.connect();

            if (conn.getResponseCode() / 100 != 2) {
                error();
            }

            int len = conn.getContentLength();

            if (len < 1) {
                error();
            }

            if (size == -1) {
                size = len;
                reportState();
            }

            file = new RandomAccessFile(folder + "\\" + fileName, "rw");
            file.seek(downloaded);

            in = conn.getInputStream();

            while (status == DOWNLOADING) {
                if (size == downloaded) {
                    break;
                }

                byte[] buffer = size - downloaded > MAX_BUFFER_SIZE ? new byte[MAX_BUFFER_SIZE]
                        : new byte[size - downloaded];
                int read = in.read(buffer);
                if (read == -1) {
                    break;
                }

                file.write(buffer, 0, read);
                downloaded += read;
                reportState();
            }

            if (status == DOWNLOADING) {
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
                status = COMPLETED;
                
                reportState();
            }
        } catch (IOException e) {
            DumpManager.getInstance().addDump(new DebugDump(null, "(Try&catch try)", "Error reporting and debug dump",
                    "(Try&catch finally)", "Error when downloading", false, e));
            error();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void reportState() {
        setChanged();
        notifyObservers();
    }

}
