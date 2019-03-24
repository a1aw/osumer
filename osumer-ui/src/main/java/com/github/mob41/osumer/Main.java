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
package com.github.mob41.osumer;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.rmi.Naming;

import javax.swing.JOptionPane;

import com.github.mob41.osumer.daemon.IDaemon;
import com.github.mob41.osumer.daemon.IUI;
import com.github.mob41.osumer.ui.UIFrame;

public class Main {

    public static final String INTRO = "osumer2 (osuMapDownloadEr) by mob41\n" + "Licensed under MIT License\n" + "\n"
            + "https://github.com/mob41/osumer\n" + "\n" + "This is a unoffical software to download beatmaps.\n" + "\n"
            + "Disclaimer:\n" + "This software does not contain malicious code to send\n"
            + "username and password to another server other than\n"
            + "osu!'s login server. This is a Open Source software.\n"
            + "You can feel free to look through the code. If you still\n"
            + "feel uncomfortable with this software, you can simply\n" + "stop using it. Thank you!\n";
    
    private static final int RMI_DAEMON_PORT = 46726;
    
    private static final int RMI_UI_PORT = 46727;

    private static final String RMI_DAEMON_PATH = "daemon";
    
    private static final String RMI_UI_PATH = "ui";

    public static void main(String[] args) {
        //Arg is handled by osumer-launcher

        String configPath = Osumer.isWindows() ? System.getenv("localappdata") + "\\osumerExpress" : "";

        Configuration config = new Configuration(configPath, Configuration.DEFAULT_DATA_FILE_NAME);

        try {
            config.load();
        } catch (IOException e1) {
            System.err.println("Unable to load configuration");
            e1.printStackTrace();

            if (!GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(null, "Could not load configuration: " + e1, "Configuration Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            System.exit(-1);
            return;
        }

        String uiSuffix = RMI_UI_PORT + "/" + RMI_UI_PATH;
        String daemonSuffix = RMI_DAEMON_PORT + "/" + RMI_DAEMON_PATH;
        IUI ui = null;
        try {
            ui = (IUI) Naming.lookup("rmi://localhost:" + uiSuffix); //Find any running UI
        } catch (Exception e) {
            //e.printStackTrace();
        }
        
        if (ui == null) {
            IDaemon d = null;
            try {
                long startTime = System.currentTimeMillis();
                d = (IDaemon) Naming.lookup("rmi://localhost:" + daemonSuffix); //Contact the daemon via RMI
            } catch (Exception e) {
                e.printStackTrace();
                
                String msg = 
                        "Could not connect to daemon! Please ensure osumer-daemon is running properly.\n" +
                        "Instead of starting directly with \"osumer-ui.exe\", please use \"osumer.exe\" to launch osumer.";
                System.err.println(msg);

                if (!GraphicsEnvironment.isHeadless()) {
                    JOptionPane.showMessageDialog(null, msg, 
                            "osumer RMI Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                System.exit(-1);
                return;
            }
            
            UIFrame frame = new UIFrame(config, d);
            frame.setVisible(true);
        } else {
            //ui.wake();
        }
    }

}
