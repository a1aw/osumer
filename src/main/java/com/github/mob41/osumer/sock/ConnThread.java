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
import com.github.mob41.osumer.io.beatmap.Osu;

public class ConnThread extends Thread{
    
    private final Socket socket;
    
    private final SockThread sockThread;
    
    public ConnThread(SockThread sockThread, Socket socket) {
        this.socket = socket;
        this.sockThread = sockThread;
    }

    @Override
    public void run(){
        try {
            socket.setSoTimeout(5000);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            
            String line = reader.readLine();
            
            if (line != null){
                if (line.equals("RUN ")){
                    sockThread.getFrame().setVisible(true);
                    sockThread.getFrame().setLocationRelativeTo(null);
                    sockThread.getFrame().setAlwaysOnTop(true);
                    sockThread.getFrame().requestFocus();
                    sockThread.getFrame().setAlwaysOnTop(false);
                    writer.println("OK");
                } else if (line.startsWith("RUN")){
                    String[] args = line.substring(4).split(" ");
                    
                    //ArgParser ap = new ArgParser(args);

                    String urlStrDy = null;
                    for (int i = 0; i < args.length; i++){
                        if (Osu.isVaildBeatMapUrl(args[i])){
                            urlStrDy = args[i];
                            break;
                        }
                    }
                    
                    final String urlStr = urlStrDy;
                    
                    if (urlStr != null){
                        new Thread(){
                            public void run(){
                                sockThread.getFrame().addBtQueue(urlStr, false, true);
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
