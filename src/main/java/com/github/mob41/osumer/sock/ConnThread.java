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
package com.github.mob41.osumer.sock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;

import com.github.mob41.osumer.ArgParser;
import com.github.mob41.osumer.io.beatmap.Osumer;
import com.github.mob41.osums.io.Osums;

public class ConnThread extends Thread {

    private final Socket socket;

    private final SockThread sockThread;

    public ConnThread(SockThread sockThread, Socket socket) {
        this.socket = socket;
        this.sockThread = sockThread;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String line = reader.readLine();

            if (line != null) {
                if (line.equals("RUN ")) {
                    sockThread.getFrame().setVisible(true);
                    sockThread.getFrame().setLocationRelativeTo(null);
                    sockThread.getFrame().setAlwaysOnTop(true);
                    sockThread.getFrame().requestFocus();
                    sockThread.getFrame().setAlwaysOnTop(false);
                    writer.println("OK");
                } else if (line.startsWith("RUN")) {
                    String[] args = line.substring(4).split(" ");

                    // ArgParser ap = new ArgParser(args);

                    String urlStrDy = null;
                    for (int i = 0; i < args.length; i++) {
                        if (Osums.isVaildBeatMapUrl(args[i])) {
                            urlStrDy = args[i];
                            break;
                        }
                    }

                    final String urlStr = urlStrDy;

                    if (urlStr != null) {
                        new Thread() {
                            public void run() {
                                sockThread.getFrame().addBtQueue(urlStr, false, true, null, null);
                            }
                        }.start();
                    }

                    writer.println("OK");
                }
            }

            writer.flush();
            writer.close();
            reader.close();

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
