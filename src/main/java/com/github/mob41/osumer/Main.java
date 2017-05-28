/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Anthony Law
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

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.exceptions.DumpManager;
import com.github.mob41.osumer.io.Installer;
import com.github.mob41.osumer.io.beatmap.Osu;
import com.github.mob41.osumer.io.queue.Queue;
import com.github.mob41.osumer.io.queue.QueueManager;
import com.github.mob41.osumer.sock.SockThread;
import com.github.mob41.osumer.ui.LoginPanel;
import com.github.mob41.osumer.ui.UIFrame;
import com.github.mob41.osumer.ui.old.DownloadDialog;
import com.github.mob41.osumer.ui.old.ErrorDumpDialog;
//import com.github.mob41.osumer.ui.old.UIFrame_old;

public class Main {
	
	public static final String INTRO = 
			"osumer2 (osuMapDownloadEr) by mob41\n" +
			"Licensed under MIT License\n" +
			"\n" +
			"https://github.com/mob41/osumer\n" +
			"\n" +
			"This is a unoffical software to download beatmaps.\n" +
			"\n" +
			"Disclaimer:\n" + 
			"This software does not contain malicious code to send\n" +
			"username and password to another server other than\n" +
			"osu!'s login server. This is a Open Source software.\n" +
			"You can feel free to look through the code. If you still\n" +
			"feel uncomfortable with this software, you can simply\n" +
			"stop using it. Thank you!\n";
	;
	
	public static void main(String[] args){
		ArgParser ap = new ArgParser(args);
		
		if (ap.isVersionFlag()){
			System.out.println(Osu.OSUMER_VERSION + "-" + Osu.OSUMER_BRANCH + "-" + Osu.OSUMER_BUILD_NUM);
			return;
		}
		
		if (!GraphicsEnvironment.isHeadless()){
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e){
				e.printStackTrace();
			} 
		}

        //System.out.println(INTRO);
		//These are called by Windows when setting Default Programs
		if (ap.isHideIconsFlag() || ap.isShowIconsFlag() || ap.isReinstallFlag() || ap.isInstallFlag() || ap.isUninstallFlag()){
            Installer installer = new Installer();
            
		    if (ap.isHideIconsFlag()){
	            installer.hideIcons();
	            
	        } else if (ap.isShowIconsFlag()){
	            installer.showIcons();
	            
	        } else if (ap.isReinstallFlag()){
	            installer.reinstall();
	            
	        } else if (ap.isInstallFlag()){
	            if (!ap.isQuietFlag() && !ap.isForceFlag()){
	                int option = JOptionPane.showOptionDialog(null, 
	                        "You are installing osumer " + Osu.OSUMER_VERSION + "-" + Osu.OSUMER_BRANCH + "-" + Osu.OSUMER_BUILD_NUM + ".\n" +
	                        "Are you sure?",
	                        "Installing osumer",
	                        JOptionPane.YES_NO_OPTION,
	                        JOptionPane.QUESTION_MESSAGE,
	                        null, null, JOptionPane.NO_OPTION);
	                
	                if (option != JOptionPane.YES_OPTION){
	                    return;
	                }
	            }
	            
	            try {
	                long startTime = System.currentTimeMillis();
	                installer.install();
	                
	                if (!(ap.isQuietFlag() && ap.isForceFlag())){
	                    System.out.println("Info@U$\nInstallation success within " + (System.currentTimeMillis() - startTime) + " ms\nInfo@D$");
	                }
	            } catch (DebuggableException e) {
	                if (!ap.isNoUiFlag() && !GraphicsEnvironment.isHeadless()){
	                    ErrorDumpDialog d = DebugDump.showDebugDialog(e.getDump());
	                    d.setModal(true);
	                    d.setVisible(true);
	                }
	                
	                if (!(ap.isQuietFlag() && ap.isForceFlag())){
	                    System.out.println("Error@U$\n" + e.getDump().toString() + "Error@D$");
	                }
	            }
	        } else if (ap.isUninstallFlag()){
	            if (!ap.isQuietFlag() && !ap.isForceFlag()){
	                int option = JOptionPane.showOptionDialog(null, 
	                        "You are uninstalling osumer " + Osu.OSUMER_VERSION + "-" + Osu.OSUMER_BRANCH + "-" + Osu.OSUMER_BUILD_NUM + ".\n" +
	                        "Are you sure?",
	                        "Uninstalling osumer",
	                        JOptionPane.YES_NO_OPTION,
	                        JOptionPane.QUESTION_MESSAGE,
	                        null, null, JOptionPane.NO_OPTION);
	                
	                if (option != JOptionPane.YES_OPTION){
	                    return;
	                }
	            }
	            
	            try {
	                long startTime = System.currentTimeMillis();
	                installer.uninstall();
	                
	                if (!(ap.isQuietFlag() && ap.isForceFlag())){
	                    System.out.println("Info@U$\nUninstallation success within " + (System.currentTimeMillis() - startTime) + " ms\nInfo@D$");
	                }
	            } catch (DebuggableException e) {
	                if (!ap.isNoUiFlag() && !GraphicsEnvironment.isHeadless()){
	                    ErrorDumpDialog d = DebugDump.showDebugDialog(e.getDump());
	                    d.setModal(true);
	                    d.setVisible(true);
	                }
	                
	                if (!(ap.isQuietFlag() && ap.isForceFlag())){
	                    System.out.println("Error@U$\n" + e.getDump().toString() + "Error@D$");
	                }
	            }
	        }
            
            System.exit(0);
            return;
		}
		
		String configPath = Osu.isWindows() ? System.getenv("localappdata") + "\\osumerExpress" : "";
		
		Config config = new Config(configPath, Config.DEFAULT_DATA_FILE_NAME);
		//QueueManager mgr = new QueueManager();
		
		try {
			config.load();
		} catch (IOException e1) {
			System.err.println("Unable to load configuration");
			e1.printStackTrace();
			
			if (!GraphicsEnvironment.isHeadless()){
				JOptionPane.showMessageDialog(null, "Could not load configuration: " + e1, "Configuration Error", JOptionPane.ERROR_MESSAGE);
			}
			

			System.exit(-1);
			return;
		}
		
		if (args != null && args.length > 0){
			if (!config.isOEEnabled()){
				System.out.println("osumerExpress is disabled.");
				runBrowser(config, args);
				
				System.exit(0);
				return;
			}
			
			runUi(config, args, ap);
		} else {
			if (GraphicsEnvironment.isHeadless()){
				System.out.println("Error: Arguments are required to use this application. Otherwise, a graphics environment is required to show the downloader UI.");
				System.exit(0);
				return;
			}
			
			if (config.isSwitchToBrowserIfWithoutUiArg()){
				runBrowser(config, args);
			} else {
			    runUi(config, args, ap);
			}
		}
	}
	
	private static void runUi(Config config, String[] args, ArgParser ap){
        if (!SockThread.testPortFree(SockThread.PORT)){ //Call background osumer to work
            try {
                Socket socket = new Socket(InetAddress.getLoopbackAddress().getHostName(), SockThread.PORT);
                socket.setSoTimeout(5000);
                
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                writer.println("RUN " + buildArgStr(args));
                writer.flush();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                if (line == null || !line.equals("OK")){
                    reader.close();
                    socket.close();
                    
                    System.out.println("Not OK: " + line);
                    DebugDump dump = new DebugDump(null, null, "Asking BG osumer to run with args: \"" + buildArgStr(args) + "\"", null, false, "Could not start up BG osumer sucessfully. Destination did not response \"OK\": " + line);
                    DumpManager.getInstance().addDump(dump);
                    ErrorDumpDialog dialog = DebugDump.showDebugDialog(dump);
                    dialog.setModal(true);
                    dialog.setVisible(true);
                    return;
                }
                reader.close();
                socket.close();
                
                System.exit(0);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                DebugDump dump = new DebugDump(null, null, "Opening connection to BG osumer socket", null, "Could not open socket at 46725 for BG call. Not osumer running at that port?", false, e);
                DumpManager.getInstance().addDump(dump);
                DebugDump.showDebugDialog(dump);
                System.exit(-1);
                return;
            }
        } else {
            String urlStr = null;
            for (int i = 0; i < args.length; i++){
                if (Osu.isVaildBeatMapUrl(args[i])){
                    urlStr = args[i];
                    break;
                }
            }
            
            boolean runUi = true;
            if (args.length > 0 && urlStr == null && !ap.isDaemonFlag()){
                if (config.isSwitchToBrowserIfWithoutUiArg()){
                    System.out.println("Configuration specified that switch to browser if an \"-ui\" arugment wasn't specified.");
                    
                    if (runUi = ap.isUiFlag() && !ap.isNoUiFlag()){
                        System.out.println("An \"-ui\" argument was specified. Launching UI.");
                    } else {
                        System.out.println("An \"-ui\" argument wasn't specified. Opening the default browser instead.");
                        runBrowser(config, args);
                        return;
                    }
                } else {
                    System.out.println("Non-beatmap URL detected.");
                    
                    if (!config.isAutoSwitchBrowser()){
                        System.out.println("Auto switch to default browser is off. Nothing to do with such URL.");
                        return;
                    } else {
                        System.out.println("Switching to default browser with the URL.");
                        runBrowser(config, args);
                    }
                }
            }
            
            TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_16px.png")));
            UIFrame frame = new UIFrame(config, new QueueManager(), icon);
            
            if (ap.isDaemonFlag()){
                if (!SystemTray.isSupported()){
                    JOptionPane.showMessageDialog(null, "Your operating system does not support System Tray.\nAs a result, you are not able to start osumer from the tray.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                frame.setDaemonMode(true);
                
                SystemTray tray = SystemTray.getSystemTray();
                
                icon.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        frame.setVisible(!frame.isVisible());
                        
                        if (!frame.isVisible()){
                            icon.displayMessage("osumer2", "osumer2 is now running in background.", TrayIcon.MessageType.INFO);
                        }
                    }
                    
                });
                icon.setToolTip("osumer2");
                
                try {
                    tray.add(icon);
                    icon.displayMessage("osumer2", "osumer2 is now running in background.", TrayIcon.MessageType.INFO);
                } catch (AWTException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error when adding tray icon: " + e + "\nAs a result, you are not able to start osumer from the tray.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else {
                frame.setVisible(true);
                
                new Thread(){
                    public void run(){
                        JOptionPane.showMessageDialog(frame, "The osumer2 daemon (background process) is not running.\nThis might slow down further osumerExpress downloads.\nNow this process is listening for more queues till it is stopped.\n\nPlease check \"Help\" for more details.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }.start();
            }
            
            if (urlStr != null){
                frame.addBtQueue(urlStr, false);
            }
        }
	}
	
	private static void runBrowser(Config config, String[] args){
		String argstr = buildArgStr(args);
		//Run the default browser application
		if (!GraphicsEnvironment.isHeadless() && Osu.isWindows()){

			System.out.println(config.getDefaultBrowser());
			if (config.getDefaultBrowser() == null || config.getDefaultBrowser().isEmpty()){
				System.out.println(config.getDefaultBrowser());
				JOptionPane.showMessageDialog(null, "No default browser path is specified. Please maunally launch the browser the following arguments:\n" + argstr, "osumer - Automatic browser switching", JOptionPane.INFORMATION_MESSAGE);
				System.exit(-1);
				return;
			}
			
			String browserPath = Installer.getBrowserExePath(config.getDefaultBrowser());
			System.out.println(browserPath);
			if (browserPath == null){
				JOptionPane.showMessageDialog(null, "Cannot read browser executable path in registry.\nCannot start default browser application for:\n" + argstr, "Configuration Error", JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
				return;
			}
			
			File file = new File(browserPath.replaceAll("\"", ""));
			if (!file.exists()){
				JOptionPane.showMessageDialog(null, "The specified browser application does not exist.\nCannot start default browser application for:\n" + argstr, "Configuration Error", JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
				return;
			}
			
			try {
				Runtime.getRuntime().exec(browserPath + " " + argstr);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.exit(0);
			return;
		}
	}
	
	private static String buildArgStr(String[] args){
		String out = "";
		for (int i = 0; i < args.length; i++){
			out += args[i];
			if (i != args.length - 1){
				out += " ";
			}
		}
		return out;
	}
	
	private static boolean isUrl(String url){
		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
	
}
