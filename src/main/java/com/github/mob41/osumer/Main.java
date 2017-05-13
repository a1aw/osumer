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

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.io.Installer;
import com.github.mob41.osumer.io.beatmap.Osu;
import com.github.mob41.osumer.io.queue.QueueManager;
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

		System.out.println(INTRO);
		
		Installer installer = new Installer();
		
		//These are called by Windows when setting Default Programs
		if (ap.isHideIconsFlag()){
			installer.hideIcons();
			
			System.exit(0);
			return;
		} else if (ap.isShowIconsFlag()){
			installer.showIcons();

			System.exit(0);
			return;
		} else if (ap.isReinstallFlag()){
			installer.reinstall();

			System.exit(0);
			return;
		}
		
		if (ap.isInstallFlag()){
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
			
			System.exit(0);
			return;
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
			
			System.exit(0);
			return;
		}
		
		String configPath = Osu.isWindows() ? System.getenv("localappdata") + "\\osumerExpress" : "";
		
		Config config = new Config(configPath, Config.DEFAULT_DATA_FILE_NAME);
		QueueManager mgr = new QueueManager();
		
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
			boolean canAccess = false;
			
			int index = 0;
			while (!canAccess && index != args.length){
				String urlstr = args[index];
				System.out.println("Processing URL: " + urlstr);
				
				if (isUrl(urlstr) && Osu.isVaildBeatMapUrl(urlstr)){
					canAccess = true;
					
					if (GraphicsEnvironment.isHeadless()){
						System.out.println("Headless interface detected.");
						System.out.println("Error: Command-line interface is not implemented in this application currently.");
					} else {
						System.out.println("Non-headless interface detected.");
						System.out.println("Running download dialog.");
						URL url = null;
						try {
							url = new URL(urlstr);
						} catch (MalformedURLException e) {}
						//TODO We use the QueueManager instead, to run in background
						DownloadDialog dialog = new DownloadDialog(config, url);
						dialog.setLocationRelativeTo(null);
						dialog.setModal(true);
						dialog.setAutoRequestFocus(true);
						dialog.setAlwaysOnTop(true);
						dialog.setVisible(true);
					}
				} else {
					canAccess = false;
					index++;
				}
			}
			
			if (!canAccess){
				if (config.isSwitchToBrowserIfWithoutUiArg()){
					System.out.println("Configuration specified that switch to browser if an \"-ui\" arugment wasn't specified.");
					
					if (ap.isUiFlag() && !ap.isNoUiFlag()){
						System.out.println("An \"-ui\" argument was specified. Launching UI.");
						UIFrame frame = new UIFrame(config, mgr);
						frame.setVisible(true);
						return;
					}
					

					System.out.println("An \"-ui\" argument wasn't specified. Opening the default browser instead.");
				}
				
				System.out.println("Non-beatmap URL detected.");
				
				if (!config.isAutoSwitchBrowser()){
					System.out.println("Auto switch to default browser is off. Nothing to do with such URL.");
					return;
				} else {
					System.out.println("Switching to default browser with the URL.");
				}
				
				runBrowser(config, args);
			}
		} else {
			if (GraphicsEnvironment.isHeadless()){
				System.out.println("Error: Arguments are required to use this application. Otherwise, a graphics environment is required to show the downloader UI.");
				System.exit(0);
				return;
			}
			
			if (config.isSwitchToBrowserIfWithoutUiArg()){
				runBrowser(config, args);
			} else {
				UIFrame frame = new UIFrame(config, mgr);
				frame.setVisible(true);
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
