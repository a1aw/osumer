package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.exceptions.OsuException;
import com.github.mob41.osumer.io.Downloader;
import com.github.mob41.osumer.io.Osu;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DownloadDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2096061865175333592L;
	private final JPanel contentPanel = new JPanel();
	
	private Osu osu;
	private Thread thread;
	private JLabel lblStatus;
	private JLabel lblOsumer;
	private JProgressBar progressBar;
	private JButton cancelButton;
	private Downloader dwn;
	
	public DownloadDialog(Config config, URL url){
		this(config, url, true);
	}

	/**
	 * Create the dialog.
	 */
	public DownloadDialog(Config config, URL url, boolean systemExit) {
		osu = new Osu();
		
		setTitle("Downloading beatmap...");
		setUndecorated(true);
		setBounds(100, 100, 471, 167);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		lblOsumer = new JLabel("osumer - beatmaps express");
		lblOsumer.setFont(new Font("Tahoma", Font.PLAIN, 24));
		
		lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(lblOsumer, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
				.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblOsumer)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblStatus)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setEnabled(false);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						int option = JOptionPane.showOptionDialog(DownloadDialog.this, "Are you sure?", "Cancelling", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 1);
						if (option == JOptionPane.YES_OPTION){
							dwn.cancel();
							if (systemExit){
								System.exit(0);
								return;
							} else {
								dispose();
								return;
							}
						}
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		thread = new Thread(new Runnable(){

			@Override
			public void run() {
				String usr = config.getUser();
				String pwd = config.getPass();
				
				lblStatus.setText("Status: Getting account information from configuration...");
				
				if (usr == null || pwd == null || usr.isEmpty() || pwd.isEmpty()){
					JOptionPane.showMessageDialog(DownloadDialog.this,
							"No osu! username or password is\n" + 
							"specified in the configuration\n" +
							"Download cannot be started without\n" +
							"osu! user account.", "Error", JOptionPane.ERROR_MESSAGE);
					if (systemExit){
						System.exit(-1);
						return;
					} else {
						dispose();
						return;
					}
				}
				
				lblStatus.setText("Status: Logging in with account \"" + usr + "\"...");
				
				try {
					osu.login(usr, pwd);
				} catch (OsuException e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(DownloadDialog.this,
							"Login unsuccessful.\n\n" +
							"Check your login information in the\n" +
							"configuration, or the network connection.\n" +
							"\nException: \n" + e, "Error", JOptionPane.ERROR_MESSAGE);
					if (systemExit){
						System.exit(-1);
						return;
					} else {
						dispose();
						return;
					}
				}
				
				lblStatus.setText("Status: Obtaining beatmap download link...");
				
				String maplink = null;
				
				try {
					maplink = osu.getBeatmapDownloadLink(url.toString());
				} catch (OsuException e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(DownloadDialog.this,
							"Could not obtain beatmap download link.\n" +
							"Please check whether the beatmap URL link\n" +
							"is valid. or thet network connection.\n" + 
							"\nException: \n" + e, "Error", JOptionPane.ERROR_MESSAGE);
					if (systemExit){
						System.exit(-1);
						return;
					} else {
						dispose();
						return;
					}
				}
				
				URL url = null;
				
				try {
					url = new URL("http://osu.ppy.sh" + maplink);
				} catch (MalformedURLException e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(DownloadDialog.this,
							"Invalid beatmap URL download link received.\n" +
							"Please check whether the beatmap URL link\n" +
							"is valid. or thet network connection.\n" + 
							"\nException: \n" + e, "Error", JOptionPane.ERROR_MESSAGE);
					if (systemExit){
						System.exit(-1);
						return;
					} else {
						dispose();
						return;
					}
				}
				
				progressBar.setIndeterminate(false);
				progressBar.setStringPainted(true);
				
				lblOsumer.setText("osuming...");
				lblStatus.setText("Status: Downloading beatmap " + maplink.substring(3));
				
				String folder = System.getProperty("java.io.tmpdir");
				dwn = new Downloader(folder, osu, url);
				
				cancelButton.setEnabled(true);
				
				System.out.println("Download started.");
				while (dwn.getStatus() == Downloader.DOWNLOADING){
					int progress = (int) dwn.getProgress();
					
					System.out.print(""); //If not doing this, problems will happen in .exe version :(
					
					progressBar.setValue(progress);
				}
				cancelButton.setEnabled(false);
				
				lblOsumer.setText("osumed! Here you go!");
				progressBar.setIndeterminate(true);
				progressBar.setStringPainted(true);
				
				if (dwn.getStatus() == Downloader.ERROR){
					lblStatus.setText("Status: Error occurred downloading");
					cancelButton.setEnabled(true);
					System.out.println("Download failed.");
				} else if (dwn.getStatus() == Downloader.COMPLETED){
					lblStatus.setText("Status: Download completed. Opening...");
					System.out.println("Download completed. Importing...");
					
					try {
						Desktop.getDesktop().open(new File(folder + "\\" + maplink.substring(3) + ".osz"));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {}
					
					if (systemExit){
						System.exit(0);
						return;
					} else {
						dispose();
						return;
					}
				}
				
				
			}
			
		});
		thread.start();
	}
}
