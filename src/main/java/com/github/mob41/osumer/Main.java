package com.github.mob41.osumer;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.io.Installer;
import com.github.mob41.osumer.io.Osu;
import com.github.mob41.osumer.ui.DownloadDialog;
import com.github.mob41.osumer.ui.UIFrame;

public class Main {
	
	public static final String INTRO = 
			"osumer (osu! beatMap downloadEr) by mob41\n" +
			"Licenced under MIT Licence\n" +
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
		System.out.println(INTRO);
		if (!GraphicsEnvironment.isHeadless()){
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e){
				e.printStackTrace();
			} 
		}
		
		if (args.length == 1){
			Installer installer = new Installer();
			if (args[0].equals("-hideicons")){
				installer.hideIcons();
				
				System.exit(0);
				return;
			} else if (args[0].equals("-showicons")){
				installer.showIcons();

				System.exit(0);
				return;
			} else if (args[0].equals("-reinstall")){
				installer.reinstall();

				System.exit(0);
				return;
			}
		}
		
		String configPath = Osu.isWindows() ? System.getenv("localappdata") + "\\osumerExpress" : "";
		
		Config config = new Config(configPath, Config.DEFAULT_DATA_FILE_NAME);
		
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
						
						DownloadDialog dialog = new DownloadDialog(config, url);
						dialog.setLocationRelativeTo(null);
						dialog.setVisible(true);
						dialog.setAlwaysOnTop(true);
					}
				} else {
					canAccess = false;
					index++;
				}
			}
			
			if (!canAccess){
				if (config.isSwitchToBrowserIfWithoutUiArg()){
					System.out.println("Configuration specified that switch to browser if an \"-ui\" arugment wasn't specified.");
					boolean containUiArg = false;
					
					for (int i = 0; i < args.length; i++){
						if (args[i].equals("-ui")){
							containUiArg = true;
							break;
						}
					}
					
					if (containUiArg){
						System.out.println("An \"-ui\" argument was specified. Launching UI.");
						UIFrame frame = new UIFrame(config);
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
				UIFrame frame = new UIFrame(config);
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
