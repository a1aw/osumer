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
package com.github.mob41.osumer.io.queue.actions;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.io.queue.Queue;
import com.github.mob41.osumer.io.queue.QueueAction;
import com.github.mob41.osums.io.Downloader;

public class CustomImportAction implements QueueAction {
    
    private final int action;
    
    private final String targetFileOrFolder;
    
    public CustomImportAction(int action, String targetFileOrFolder) {
        this.action = action;
        this.targetFileOrFolder = targetFileOrFolder;
    }

    @Override
    public void run(Queue queue) {
        Downloader dwn = queue.getDownloader();
        String path = dwn.getDownloadFolder() + dwn.getFileName();

        File file = new File(path + ".osz");

        if (!file.exists()) {
            System.out.println("File not exists: " + path + ".osz");
            return;
        }
        
        if (action == 1 || action == 2 || action == 3) {
            String loc = null;
            if (action == 1) {
                loc = System.getenv("LOCALAPPDATA") + "\\osu!\\Songs";
            } else {
                loc = targetFileOrFolder;
            }
            
            if (action == 3) {
                File songsFolder = new File(loc);
                
                if (!songsFolder.exists()) {
                    songsFolder.mkdirs();   
                }
            }
            
            String filePath;
            if (action == 2) {
                if (!loc.endsWith(".osz")) {
                    loc = loc + ".osz";
                }
                filePath = loc;
            } else {
                filePath = loc + "\\" + dwn.getFileName() + ".osz";
            }
            
            File toFile = new File(filePath);
            
            if (toFile.exists()) {
                toFile.delete();
            }

            FileOutputStream toFileOut;
            try {
                toFileOut = new FileOutputStream(toFile);
                Files.copy(file.toPath(), toFileOut);
                toFileOut.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "osumer Song Copy failed:\n\nFrom: " + path + "\nTo: " + loc + "\n\nMake sure you have access to that folder.\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
