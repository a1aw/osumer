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

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;

import javax.annotation.processing.Processor;
import javax.swing.JOptionPane;
import javax.tools.JavaCompiler;

import com.github.mob41.osumer.ArgParser;
import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.Main;
import com.github.mob41.osumer.Osumer;
import com.github.mob41.osumer.io.Installer;
import com.github.mob41.osums.io.beatmap.Osums;

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
                    sockThread.getFrame().checkUpdate();
                    
                    sockThread.getFrame().setVisible(true);
                    sockThread.getFrame().setLocationRelativeTo(null);
                    sockThread.getFrame().setAlwaysOnTop(true);
                    sockThread.getFrame().requestFocus();
                    sockThread.getFrame().setAlwaysOnTop(false);
                    writer.println("OK");
                } else if (line.startsWith("RUN")) {
                    sockThread.getFrame().checkUpdate();
                    
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
                                sockThread.getFrame().addQuietBtQueue(urlStr);
                                //sockThread.getFrame().addBtQueue(urlStr, false, false, true, null, null);
                            }
                        }.start();
                    } else {
                        String argstr = buildArgStr(args);
                        Config config = sockThread.getFrame().getConfig();
                        
                        // Run the default browser application
                        System.out.println(config.getDefaultBrowser());
                        if (config.getDefaultBrowser() == null || config.getDefaultBrowser().isEmpty()) {
                            System.out.println(config.getDefaultBrowser());
                            JOptionPane.showInputDialog(null,
                                    "No default browser path is specified. Please maunally launch the browser the following arguments:",
                                    "osumer - Automatic browser switching", JOptionPane.INFORMATION_MESSAGE, null, null, argstr);
                            return;
                        }

                        String browserPath = Installer.getBrowserExePath(config.getDefaultBrowser());
                        System.out.println(browserPath);
                        if (browserPath == null) {
                            JOptionPane.showMessageDialog(null,
                                    "Cannot read browser executable path in registry.\nCannot start default browser application for:\n"
                                            + argstr,
                                    "Configuration Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        File file = new File(browserPath.replaceAll("\"", ""));
                        if (!file.exists()) {
                            JOptionPane.showMessageDialog(null,
                                    "The specified browser application does not exist.\nCannot start default browser application for:\n"
                                            + argstr,
                                    "Configuration Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        try {
                            Runtime.getRuntime().exec(browserPath + " " + argstr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    writer.println("OK");
                } else if (line.equals("STOP")) {
                    System.exit(0);
                    return;
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

    private static String buildArgStr(String[] args) {
        String out = "";
        for (int i = 0; i < args.length; i++) {
            out += args[i];
            if (i != args.length - 1) {
                out += " ";
            }
        }
        return out;
    }

}
