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
			"don't use it. Thank you!\n";
	;

	public static final String URL_PREFIX = "http://osu.ppy.sh/";
	
	public static final String BEATMAP_DIR = "b/";
	
	public static final String SONG_DIR = "s/";
	
	public static void main(String[] args){
		System.out.println(INTRO);
		if (!GraphicsEnvironment.isHeadless()){
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e){
				e.printStackTrace();
			} 
		}
		
		Config config = new Config(Config.DEFAULT_DATA_FILE_NAME);
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
			String urlstr = args[0];
			System.out.println("Processing URL: " + urlstr);
			
			if (isUrl(urlstr) &&
				urlstr.length() > URL_PREFIX.length() + 2 &&
				(urlstr.substring(0, URL_PREFIX.length() + 2).equals(URL_PREFIX  + BEATMAP_DIR) ||
						urlstr.substring(0, URL_PREFIX.length() + 2).equals(URL_PREFIX  + SONG_DIR))
				){
				
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
				String argstr = buildArgStr(args);
				//Run the default browser application
				if (!GraphicsEnvironment.isHeadless()){
					if (config.getDefaultBrowserPath() == null){
						JOptionPane.showMessageDialog(null, "No default browser path is specified. Please maunally launch the following URL:\n" + urlstr, "osumer - Automatic browser switching", JOptionPane.INFORMATION_MESSAGE);
						System.exit(-1);
						return;
					}
					
					File file = new File(config.getDefaultBrowserPath());
					if (!file.exists()){
						JOptionPane.showMessageDialog(null, "The specified browser application does not exist.\nCannot start default browser application for:\n" + argstr, "Configuration Error", JOptionPane.ERROR_MESSAGE);
						System.exit(-1);
						return;
					}
					
					try {
						Runtime.getRuntime().exec(config.getDefaultBrowserPath() + " " + argstr);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					System.exit(0);
					return;
				}
			}
		}
		
		if (GraphicsEnvironment.isHeadless()){
			System.out.println("Error: Arguments are required to use this application. Otherwise, a graphics environment is required to show the downloader UI.");
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
