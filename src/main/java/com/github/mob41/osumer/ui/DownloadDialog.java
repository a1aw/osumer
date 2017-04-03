package com.github.mob41.osumer.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.exceptions.DumpManager;
import com.github.mob41.osumer.exceptions.NoBuildsForVersionException;
import com.github.mob41.osumer.exceptions.NoSuchBuildNumberException;
import com.github.mob41.osumer.exceptions.NoSuchVersionException;
import com.github.mob41.osumer.exceptions.OsuException;
import com.github.mob41.osumer.io.OsuDownloader;
import com.github.mob41.osumer.io.Osu;
import com.github.mob41.osumer.io.OsuBeatmap;
import com.github.mob41.osumer.updater.Updater;
import com.github.mob41.osumer.updater.UpdateInfo;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DownloadDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2096061865175333592L;
	
	private Image icon256px = Toolkit.getDefaultToolkit().getImage(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_256px.png"));
	
	private final JPanel contentPanel = new JPanel(){
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			int width = getWidth();
			int height = getHeight();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
			g2.drawImage(icon256px, 0, height / 2, contentPanel);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
	};
	
	private Osu osu;
	private Thread thread;
	private JLabel lblStatus;
	private JLabel lblOsumer;
	private JProgressBar progressBar;
	private JButton cancelButton;
	private OsuDownloader dwn;
	private String loc = null;
	private JLabel lblThumbImg;
	
	private boolean checkingUpdate = false;
	private Updater updater;
	
	public DownloadDialog(Config config, URL url){
		this(config, url, true);
	}
	
	public DownloadDialog(Config config, URL url, boolean systemExit){
		this(config, url, true, systemExit, true);
	}
	
	public String getFilePath(){
		return loc;
	}

	/**
	 * Create the dialog.
	 * @wbp.parser.constructor
	 */
	public DownloadDialog(Config config, URL url, boolean checkOsumerUpdate, boolean systemExit, boolean openFile) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				askCancel(systemExit);
			}
		});
		updater = new Updater(config);
		osu = new Osu();
		setTitle("Downloading beatmap...");
		setModal(true);
		setUndecorated(true);
		setBounds(100, 100, 476, 279);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		lblOsumer = new JLabel("osumer - beatmaps express");
		lblOsumer.setFont(new Font("Tahoma", Font.PLAIN, 24));
		
		lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		lblThumbImg = new JLabel("...");
		lblThumbImg.setHorizontalAlignment(SwingConstants.CENTER);
		lblThumbImg.setFont(new Font("Tahoma", Font.PLAIN, 27));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(lblOsumer, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblThumbImg, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblOsumer)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
						.addComponent(lblThumbImg, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblStatus, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		JTextArea txtPn = new JTextArea();
		txtPn.setText(
				"Your download is starting!\n" + 
				"Thank you for using osumer!" +
				" If you like this software, please put a star on my GitHub project.");
		txtPn.setTabSize(3);
		txtPn.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtPn.setLineWrap(true);
		txtPn.setBackground(SystemColor.control);
		txtPn.setEditable(false);
		scrollPane.setViewportView(txtPn);
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
						askCancel(systemExit);
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
					/*
					JOptionPane.showMessageDialog(DownloadDialog.this,
							"No osu! username or password is\n" + 
							"specified in the configuration\n" +
							"Download cannot be started without\n" +
							"osu! user account.", "Error", JOptionPane.ERROR_MESSAGE);
					*/
					
					LoginPanel loginPanel = new LoginPanel();
					int option = JOptionPane.showOptionDialog(
							DownloadDialog.this, loginPanel, "Login to osu!",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
							null, null, 0);
					
					String paneluser = loginPanel.getUsername();
					String panelpwd = loginPanel.getPassword();
					
					if (option == JOptionPane.OK_OPTION && (paneluser.isEmpty() || panelpwd.isEmpty())){
						JOptionPane.showMessageDialog(DownloadDialog.this, "Username or password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
					if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.CANCEL_OPTION || paneluser.isEmpty() || panelpwd.isEmpty()){
						if (systemExit){
							System.exit(-1);
							return;
						} else {
							dispose();
							return;
						}
					}
					
					usr = paneluser;
					pwd = panelpwd;
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
				
				lblStatus.setText("Status: Obtaining beatmap information...");
				
				OsuBeatmap info = null;
				
				try {
					info = osu.getBeatmapInfo(url.toString());
				} catch (OsuException e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(DownloadDialog.this,
							"Could not obtain beatmap information.\n" +
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
				
				System.out.println(info.getThumbUrl());
				
				URL thumbUrl = null;
				
				try {
					thumbUrl = new URL("http:" + info.getThumbUrl());
					System.out.println(thumbUrl.toString());
				} catch (MalformedURLException e){
					DumpManager.getInstance()
						.addDump(new DebugDump(
								info.getThumbUrl(),
								"Assign thumbUrl with null",
								"(Try scope) Create new URL instance with \"http:" + info.getThumbUrl() + "\"",
								"(Try scope) Show Error Dialog",
								"Invalid thumb image URL received. Cannot create URL instance",
								false,
								e));
					JOptionPane.showMessageDialog(DownloadDialog.this,
							"Invalid thumb image URL received.\n" +
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
				
				if (thread.isInterrupted()){
					return;
				}
				
				lblStatus.setText("Status: Fetching beatmap thumb image...");
				try {
					URLConnection conn = thumbUrl.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					BufferedImage image = ImageIO.read(conn.getInputStream());
					lblThumbImg.setText("");
					lblThumbImg.setIcon(new ImageIcon(image.getScaledInstance(100, 75, Image.SCALE_DEFAULT)));
				} catch (IOException e2) {
					DumpManager.getInstance()
					.addDump(new DebugDump(
							url.toString(),
							"(UI) Set status to lblStatus",
							"(Try scope) (Code that throws IOException)",
							"(Catch scope) (UI) Set thumb img to \"Fetch failed\"",
							"Cannot fetch beatmap thumb image",
							false,
							e2));
					lblThumbImg.setText("Fetch failed");
				}
				
				if (thread.isInterrupted()){
					return;
				}
				
				txtPn.setText(
						"Name: " + info.getName() + "\n" +
						"Title: " + info.getTitle() + "\n" +
						"Artist: " + info.getArtist() + "\n" +
						"Creator: " + info.getCreator() + "\n" +
						"Genre: " + info.getGenre() + "\n" +
						"BPM: " + info.getBpm() + "\n" +
						"Rating (%): " + info.getRating() + "%\n" +
						"Bad rating: " + info.getBadRating() + "\n" +
						"Good rating: " + info.getGoodRating() + "\n" +
						"Success rate: " + info.getSuccessRate() + "\n"
						);
				
				String maplink = info.getDwnUrl();
				
				URL url = null;
				
				try {
					url = new URL("http://osu.ppy.sh" + maplink);
				} catch (MalformedURLException e){
					DumpManager.getInstance()
					.addDump(new DebugDump(
							maplink,
							"Assign url with null",
							"(Try scope) Create new URL instance with \"http:" + maplink + "\"",
							"(Try scope) Show Error Dialog",
							"Invalid download link received. Cannot create URL instance",
							false,
							e));
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
				lblStatus.setText("Status: Downloading \"" + info.getName() + "\"...");
				
				final String folder = System.getProperty("java.io.tmpdir");
				final String fileName = (toFilename(url) + " " + info.getName()).replaceAll("[-+.^:,]","");
				
				dwn = new OsuDownloader(folder, fileName, osu, url);
				
				cancelButton.setEnabled(true);
				
				System.out.println("Download started.");
				while (dwn.getStatus() == OsuDownloader.DOWNLOADING){
					if (thread.isInterrupted()){
						return;
					}
					
					int progress = (int) dwn.getProgress();
					
					System.out.print(""); //If not doing this, problems will happen in .exe version :(
					
					progressBar.setValue(progress);
				}
				cancelButton.setEnabled(false);
				
				lblOsumer.setText("osumed! Here you go!");
				progressBar.setIndeterminate(true);
				progressBar.setStringPainted(true);
				
				if (dwn.getStatus() == OsuDownloader.ERROR){
					lblStatus.setText("Status: Error occurred downloading");
					cancelButton.setEnabled(true);
					System.out.println("Download failed.");
				} else if (dwn.getStatus() == OsuDownloader.COMPLETED){
					loc = folder + "\\" + fileName + ".osz";
					
					if (openFile){
						lblStatus.setText("Status: Download completed. Opening...");
						System.out.println("Download completed. Importing...");
						
						try {
							Desktop.getDesktop().open(new File(loc));
						} catch (IOException e1) {
							DumpManager.getInstance()
							.addDump(new DebugDump(
									null,
									"(If[openFile] scope) (UI) Set status to lblStatus",
									"(Try scope) Open file loc using Desktop.getDesktop.open()",
									"(Try scope) Sleep 2000 ms (2 sec)",
									"Unable to open file",
									false,
									e1));
						}
						
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {}
					} else {
						lblStatus.setText("Status: Download completed.");
						System.out.println("Download completed.");
					}
					
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
		
		if (checkOsumerUpdate){
			checkUpdate();
		}
	}
	
	private void checkUpdate(){
		if (!checkingUpdate){
			checkingUpdate = true;
			thread = new Thread(new Runnable(){
				public void run(){
					
					UpdateInfo verInfo = null;
					try {
						verInfo = updater.getLatestVersion();
					} catch (NoBuildsForVersionException e){
						checkingUpdate = false;
						return;
					} catch (NoSuchVersionException e){
						JOptionPane.showMessageDialog(DownloadDialog.this, "We don't have version " + Osu.OSUMER_VERSION + " in the current update branch\n\nPlease try another update branch (snapshot, beta, stable).", "Version not available", JOptionPane.INFORMATION_MESSAGE);
						checkingUpdate = false;
						return;
					} catch (NoSuchBuildNumberException e){
						JOptionPane.showMessageDialog(DownloadDialog.this, 
								"We don't have build number greater or equal to " + Osu.OSUMER_BUILD_NUM + " in version " + Osu.OSUMER_VERSION + ".\n" +
								"If you are using a modified/development osumer,\n"
								+ " you can just ignore this message.\n" +
								"If not, this might be the versions.json in GitHub goes wrong,\n"
								+ " post a new issue about this.", "Build not available", JOptionPane.WARNING_MESSAGE);
						checkingUpdate = false;
						return;
					} catch (DebuggableException e){
						e.printStackTrace();
						checkingUpdate = false;
						return;
					}
					
					if (verInfo != null && !verInfo.isThisVersion()){
						int option;
						String desc = verInfo.getDescription();
						if (desc == null){
							option = JOptionPane.showOptionDialog(DownloadDialog.this,
									"New " +
									(verInfo.isUpgradedVersion() ? "upgrade" : "update") +
									" available! New version:\n" + verInfo.getVersion() +
									"-" + Updater.getBranchStr(verInfo.getBranch()) +
									"-b" + verInfo.getBuildNum() + "\n\n" +
									"Do you want to update it now?", "Update available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.NO_OPTION);
						} else {
							option = JOptionPane.showOptionDialog(DownloadDialog.this,
									"New " +
									(verInfo.isUpgradedVersion() ? "upgrade" : "update") +
									" available! New version:\n" + verInfo.getVersion() +
									"-" + Updater.getBranchStr(verInfo.getBranch()) +
									"-b" + verInfo.getBuildNum() + "\n\n" +
									"Do you want to update it now?", "Update available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "No", "Description/Changelog"}, JOptionPane.NO_OPTION);
							
							if (option == 2){
								option = JOptionPane.showOptionDialog(DownloadDialog.this, new TextPanel(desc), "Update description/change-log", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, 0);
							}
						}
						
						if (option == JOptionPane.YES_OPTION){
							/*
							try {
								Desktop.getDesktop().browse(new URI(verInfo.getWebLink()));
							} catch (IOException | URISyntaxException e) {
								DebugDump dump = new DebugDump(
										verInfo.getWebLink(),
										"Show option dialog of updating osumer or not",
										"Set checkingUpdate to false",
										"(End of function / thread)",
										"Error when opening the web page",
										false,
										e);
								DumpManager.getInstance().addDump(dump);
								DebugDump.showDebugDialog(dump);
							}
							*/
							try {
								String updaterLink = Updater.getUpdaterLink();
								
								if (updaterLink == null){
									System.out.println("No latest updater .exe defined! Falling back to legacy updater!");
									updaterLink = Updater.LEGACY_UPDATER_JAR;
								}
								
								URL url;
								try {
									url = new URL(updaterLink);
								} catch (MalformedURLException e) {
									e.printStackTrace();
									JOptionPane.showMessageDialog(DownloadDialog.this, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
									return;
								}
								
								UpdaterDownloadDialog dialog = new UpdaterDownloadDialog(url);
								dialog.setModal(true);
								dialog.setVisible(true);
							} catch (OsuException e){
								e.printStackTrace();
								JOptionPane.showMessageDialog(DownloadDialog.this, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					checkingUpdate = false;
				}
			});
			thread.start();
		}
	}
	
	private static String toFilename(URL url){
		String str = url.getFile();
		return str.substring(str.lastIndexOf('/') + 1);
	}
	
	private void askCancel(boolean systemExit){
		int option = JOptionPane.showOptionDialog(DownloadDialog.this, "Are you sure?", "Cancelling", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 1);
		if (option == JOptionPane.YES_OPTION){
			if (dwn != null){
				dwn.cancel();
			}
			thread.interrupt();
			if (systemExit){
				System.exit(0);
				return;
			} else {
				dispose();
				return;
			}
		}
	}
}
