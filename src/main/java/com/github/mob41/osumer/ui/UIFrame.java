package com.github.mob41.osumer.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.exceptions.NoBuildsForVersionException;
import com.github.mob41.osumer.exceptions.NoSuchBuildNumberException;
import com.github.mob41.osumer.exceptions.NoSuchVersionException;
import com.github.mob41.osumer.io.Osu;
import com.github.mob41.osumer.updater.Updater;
import com.github.mob41.osumer.updater.VersionInfo;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Desktop;

import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import java.awt.Button;
import javax.swing.JMenuItem;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import javax.swing.JRadioButtonMenuItem;

public class UIFrame extends JFrame {

	private JPanel contentPane;
	private JTextField mapUrlFld;
	private JFileChooser chooser;
	private Updater updater;
	
	private Thread thread;
	private boolean checkingUpdate = false;
	
	private static Image icon256px = null;
	private JLabel lblVersion;
	private JTextPane updateTxtPn;
	private JRadioButtonMenuItem rdbtnmntmSnapshot;
	private JRadioButtonMenuItem rdbtnmntmBeta;
	private JRadioButtonMenuItem rdbtnmntmStable;

	/**
	 * Create the frame.
	 */
	public UIFrame(Config config) {
		if (icon256px == null){
			icon256px = Toolkit.getDefaultToolkit().getImage(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_256px.png"));
		}
		setIconImage(Toolkit.getDefaultToolkit().getImage(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_32px.png")));
		setResizable(false);
		setTitle("osumer UI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 493);
		
		updater = new Updater(config);
		chooser = new JFileChooser();
		//Limit file format to .osz
		chooser.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				if (arg0 == null){
					return false;
				}

				if (arg0.isDirectory()){
					return true;
				}
				
				String str = arg0.getName();
				final String ext = ".osz";
				
				if (str.length() < ext.length()){
					return false;
				}
				
				return str.endsWith(ext);
			}

			@Override
			public String getDescription() {
				return "osu! beatmap";
			}
			
		});
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnOsuBeatmap = new JMenu("osu! beatmap");
		menuBar.add(mnOsuBeatmap);
		
		JMenuItem mntmSearchForBeatmaps = new JMenuItem("Search for beatmaps...");
		mntmSearchForBeatmaps.setEnabled(false);
		mntmSearchForBeatmaps.setToolTipText("This will be implemented if there're enough stars and download count for osumer XD");
		mnOsuBeatmap.add(mntmSearchForBeatmaps);
		
		JMenu mnDebug = new JMenu("Debug");
		menuBar.add(mnDebug);
		
		JMenuItem mntmOpenConfigurationLocation = new JMenuItem("Open configuration location");
		mntmOpenConfigurationLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(System.getenv("localappdata") + "\\osumerExpress"));
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(UIFrame.this, "Surprisely, failed to open folder. :(\n\n" + e1, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mnDebug.add(mntmOpenConfigurationLocation);
		
		JMenuItem mntmGenerateEventsDump = new JMenuItem("Generate events dump");
		mnDebug.add(mntmGenerateEventsDump);
		
		JMenu mnUpdate = new JMenu("Update");
		menuBar.add(mnUpdate);
		
		JMenuItem mntmRunUpdater = new JMenuItem("Check update");
		mntmRunUpdater.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				checkUpdate();
			}
		});
		mnUpdate.add(mntmRunUpdater);
		
		JSeparator separator = new JSeparator();
		mnUpdate.add(separator);
		
		JLabel lblUpdateBranch = new JLabel("Update branch");
		mnUpdate.add(lblUpdateBranch);
		
		rdbtnmntmSnapshot = new JRadioButtonMenuItem("Snapshot");
		rdbtnmntmSnapshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rdbtnmntmSnapshot.setSelected(rdbtnmntmSnapshot.isSelected());
				rdbtnmntmBeta.setSelected(!rdbtnmntmSnapshot.isSelected());
				rdbtnmntmStable.setSelected(!rdbtnmntmSnapshot.isSelected());
				saveUpdateBranchConfig(config);
			}
		});
		mnUpdate.add(rdbtnmntmSnapshot);
		
		rdbtnmntmBeta = new JRadioButtonMenuItem("Beta");
		rdbtnmntmBeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnmntmSnapshot.setSelected(!rdbtnmntmBeta.isSelected());
				rdbtnmntmBeta.setSelected(rdbtnmntmBeta.isSelected());
				rdbtnmntmStable.setSelected(!rdbtnmntmBeta.isSelected());
				saveUpdateBranchConfig(config);
			}
		});
		mnUpdate.add(rdbtnmntmBeta);
		
		rdbtnmntmStable = new JRadioButtonMenuItem("Stable");
		rdbtnmntmStable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnmntmSnapshot.setSelected(!rdbtnmntmStable.isSelected());
				rdbtnmntmBeta.setSelected(!rdbtnmntmStable.isSelected());
				rdbtnmntmStable.setSelected(rdbtnmntmStable.isSelected());
				saveUpdateBranchConfig(config);
			}
		});
		mnUpdate.add(rdbtnmntmStable);
		
		contentPane = new JPanel(){
			@Override
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				int width = getWidth();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
				g2.drawImage(icon256px, width / 3, 20, contentPane);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.repaint();
		
		JLabel lblOsumer = new JLabel("osumer");
		lblOsumer.setHorizontalAlignment(SwingConstants.LEFT);
		lblOsumer.setFont(new Font("Tahoma", Font.PLAIN, 24));
		
		JLabel lblBeatmapUrl = new JLabel("Beatmap URL:");
		lblBeatmapUrl.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		
		mapUrlFld = new JTextField();
		mapUrlFld.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				mapUrlFld.setBackground(Osu.isVaildBeatMapUrl(mapUrlFld.getText()) ? Color.WHITE : Color.PINK);
				mapUrlFld.setForeground(Osu.isVaildBeatMapUrl(mapUrlFld.getText()) ? Color.BLACK : Color.WHITE);
			}
		});
		mapUrlFld.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		mapUrlFld.setColumns(10);
		
		JButton btnDownloadImport = new JButton("Download & Import");
		btnDownloadImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String urlstr = mapUrlFld.getText();
				
				if (!Osu.isVaildBeatMapUrl(urlstr)){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a vaild osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				URL url = null;
				try {
					url = new URL(urlstr);
				} catch (MalformedURLException e1){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a vaild osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
					
				}
				
				DownloadDialog dialog = new DownloadDialog(config, url, false);
				dialog.setModal(true);
				dialog.setUndecorated(false);
				dialog.setVisible(true);
				dialog.setAlwaysOnTop(true);
				dialog.setLocationRelativeTo(UIFrame.this);
			}
		});
		btnDownloadImport.setFont(new Font("PMingLiU", Font.BOLD, 16));
		
		JPanel panel = new JPanel();
		
		JLabel lblCopyrightc = new JLabel("Copyright (c) 2016 mob41. Licenced under MIT Licence.");
		lblCopyrightc.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblHttpsgithubcommobosumer = new JLabel("https://github.com/mob41/osumer");
		lblHttpsgithubcommobosumer.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/mob41/osumer"));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		lblHttpsgithubcommobosumer.setForeground(Color.BLUE);
		lblHttpsgithubcommobosumer.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setIcon(new ImageIcon(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_64px.png")));
		
		JLabel lblNewLabel = new JLabel("The easiest,express way to obtain beatmaps");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));

		JPopupMenu popupMenu = new JPopupMenu();
		JButton btnDownload = new JButton("Download...");
		btnDownload.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				popupMenu.show(btnDownload, arg0.getX(), arg0.getY());
			}
		});
		
		JMenuItem mntmDwnBatch = new JMenuItem("Download batch");
		mntmDwnBatch.setEnabled(false);
		mntmDwnBatch.setToolTipText("This will be implemented if there're enough stars and download count for osumer XD");
		popupMenu.add(mntmDwnBatch);
		
		JMenuItem mntmDwnFolder = new JMenuItem("Download to folder");
		mntmDwnFolder.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//Validate URL
				String urlstr = mapUrlFld.getText();
				
				if (!Osu.isVaildBeatMapUrl(urlstr)){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a vaild osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				URL url = null;
				try {
					url = new URL(urlstr);
				} catch (MalformedURLException e1){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a vaild osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
					
				}
				
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				int option = chooser.showSaveDialog(UIFrame.this);
				
				if (option == JFileChooser.CANCEL_OPTION){
					return;
				}
				
				File folder = chooser.getSelectedFile();
				
				//Download
				DownloadDialog dialog = new DownloadDialog(config, url, false, false);
				dialog.setModal(true);
				dialog.setUndecorated(false);
				dialog.setVisible(true);
				dialog.setAlwaysOnTop(true);
				dialog.setLocationRelativeTo(UIFrame.this);
				
				//Move file
				String filePath = dialog.getFilePath();
				System.out.println(filePath);
				
				if (filePath == null){
					return;
				}
				
				File dwnFile = new File(filePath);
				
				if (!dwnFile.exists()){
					return;
				}
				
				File moveFile = new File(folder.getAbsolutePath() + "\\" + dwnFile.getName());
				System.out.println(moveFile.getAbsolutePath());
				
				try {
					FileOutputStream out = new FileOutputStream(moveFile);
					Files.copy(dwnFile.toPath(), out);
					out.flush();
					out.close();
				} catch (IOException e1){
					e1.printStackTrace();
				}
				
				dwnFile.delete();
				
				JOptionPane.showMessageDialog(UIFrame.this, "Download completed at location:\n" + moveFile.getAbsolutePath(), "Info", JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		popupMenu.add(mntmDwnFolder);
		
		JMenuItem mntmDwnAs = new JMenuItem("Download as...");
		mntmDwnAs.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//Validate URL
				String urlstr = mapUrlFld.getText();
				
				if (!Osu.isVaildBeatMapUrl(urlstr)){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a vaild osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				URL url = null;
				try {
					url = new URL(urlstr);
				} catch (MalformedURLException e1){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a vaild osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
					
				}
				
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				
				int option = chooser.showSaveDialog(UIFrame.this);
				
				if (option == JFileChooser.CANCEL_OPTION){
					return;
				}
				
				File targetFile = chooser.getSelectedFile();
				
				//Download
				DownloadDialog dialog = new DownloadDialog(config, url, false, false);
				dialog.setModal(true);
				dialog.setUndecorated(false);
				dialog.setVisible(true);
				dialog.setAlwaysOnTop(true);
				dialog.setLocationRelativeTo(UIFrame.this);
				
				//Move file
				String filePath = dialog.getFilePath();
				System.out.println(filePath);
				
				if (filePath == null){
					return;
				}
				
				File dwnFile = new File(filePath);
				
				if (!dwnFile.exists()){
					return;
				}
				
				String path = targetFile.getAbsolutePath();
				if (!path.endsWith(".osz")){
					path += ".osz";
				}
				
				File moveFile = new File(path);
				System.out.println(moveFile.getAbsolutePath());
				
				try {
					FileOutputStream out = new FileOutputStream(moveFile);
					Files.copy(dwnFile.toPath(), out);
					out.flush();
					out.close();
				} catch (IOException e1){
					e1.printStackTrace();
				}
				
				dwnFile.delete();
				
				JOptionPane.showMessageDialog(UIFrame.this, "Download completed at location:\n" + moveFile.getAbsolutePath(), "Info", JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		popupMenu.add(mntmDwnAs);
		
		lblVersion = new JLabel("Version: " + Osu.OSUMER_VERSION);
		
		JLabel lblUpdateBranchLabel = new JLabel("This branch: " + Osu.OSUMER_BRANCH);
		
		updateTxtPn = new JTextPane();
		updateTxtPn.setEditable(false);
		updateTxtPn.setBackground(SystemColor.control);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblCopyrightc, GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(label)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblOsumer)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 337, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(updateTxtPn, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
								.addComponent(lblUpdateBranchLabel, GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
								.addComponent(lblVersion, GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblBeatmapUrl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(mapUrlFld, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDownloadImport)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDownload))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblHttpsgithubcommobosumer, GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblOsumer, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
							.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
						.addComponent(label, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblVersion)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblUpdateBranchLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(updateTxtPn, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
									.addComponent(btnDownload, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
									.addComponent(btnDownloadImport, GroupLayout.PREFERRED_SIZE, 28, Short.MAX_VALUE))
								.addComponent(lblBeatmapUrl))
							.addGap(7))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(mapUrlFld, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblCopyrightc)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblHttpsgithubcommobosumer, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
					.addGap(3))
		);
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		AccountSettingsPanel accPanel = new AccountSettingsPanel(config);
		accPanel.setBorder(new TitledBorder(null, "Using account", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(accPanel);
		
		ExpressSettingsPanel xpPanel = new ExpressSettingsPanel(config);
		xpPanel.setBorder(new TitledBorder(null, "osumerExpress settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(xpPanel);
		
		if (!Osu.isWindows()){
			xpPanel.setEnabled(false);
			xpPanel.setToolTipText("osumerExpress does not support installation in non-Windows environment, currently not implemented.");
		}
		
		contentPane.setLayout(gl_contentPane);
		
		checkUpdate();
		updateUpdateSourceChoice(config);
	}
	
	private void checkUpdate(){
		if (!checkingUpdate){
			checkingUpdate = true;
			thread = new Thread(new Runnable(){
				public void run(){
					updateTxtPn.setForeground(Color.BLACK);
					updateTxtPn.setText("Checking update...");
					
					VersionInfo verInfo = null;
					try {
						verInfo = updater.getLatestVersion();
					} catch (NoBuildsForVersionException e){
						updateTxtPn.setForeground(Color.RED);
						updateTxtPn.setText("No builds available for the new version. See dump");
						checkingUpdate = false;
						return;
					} catch (NoSuchVersionException e){
						updateTxtPn.setForeground(Color.RED);
						updateTxtPn.setText("No current version in the selected branch. See dump");
						JOptionPane.showMessageDialog(UIFrame.this, "We don't have version " + Osu.OSUMER_VERSION + " in the current update branch\n\nPlease try another update branch (snapshot, beta, stable).", "Version not available", JOptionPane.INFORMATION_MESSAGE);
						checkingUpdate = false;
						return;
					} catch (NoSuchBuildNumberException e){
						updateTxtPn.setForeground(Color.RED);
						updateTxtPn.setText("This version has a invalid build number. See dump");
						JOptionPane.showMessageDialog(UIFrame.this, 
								"We don't have build number greater or equal to " + Osu.OSUMER_BUILD_NUM + " in version " + Osu.OSUMER_VERSION + ".\n" +
								"If you are using a modified/development osumer,\n"
								+ " you can just ignore this message.\n" +
								"If not, this might be the versions.json in GitHub goes wrong,\n"
								+ " post a new issue about this.", "Build not available", JOptionPane.WARNING_MESSAGE);
						checkingUpdate = false;
						return;
					} catch (DebuggableException e){
						e.printStackTrace();
						updateTxtPn.setForeground(Color.RED);
						updateTxtPn.setText("Could not connect to update server.");
						checkingUpdate = false;
						return;
					}
					
					if (verInfo == null){
						updateTxtPn.setForeground(Color.RED);
						updateTxtPn.setText("Could not get latest update information.");
					} else if (!verInfo.isThisVersion()){
						updateTxtPn.setForeground(new Color(0,153,0));
						updateTxtPn.setText(
								(verInfo.isUpgradedVersion() ? "Upgrade" : "Update") +
								" available! New version: " + verInfo.getVersion() +
								"-" + Updater.getBranchStr(verInfo.getBranch()) +
								"-b" + verInfo.getBuildNum());
						
						int option = JOptionPane.showOptionDialog(UIFrame.this,
								"New " +
								(verInfo.isUpgradedVersion() ? "upgrade" : "update") +
								" available! New version:\n" + verInfo.getVersion() +
								"-" + Updater.getBranchStr(verInfo.getBranch()) +
								"-b" + verInfo.getBuildNum() + "\n\n" +
								"Do you want to update it now?\n\n" +
								"(This version's updater is not fully\n implemented. A browser will open\n instead.)", "Update available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.NO_OPTION);
						
						if (option == JOptionPane.YES_OPTION){
							try {
								Desktop.getDesktop().browse(new URI("https://github.com/mob41/osumer/releases/latest"));
							} catch (IOException e) {
								e.printStackTrace();
							} catch (URISyntaxException e) {
								e.printStackTrace();
							}
						}
					} else {
						updateTxtPn.setText("You are running the latest version of osumer!");
					}
					
					checkingUpdate = false;
				}
			});
			thread.start();
		}
	}
	
	private void updateUpdateSourceChoice(Config config){
		switch(config.getUpdateSource()){
		case Updater.UPDATE_SOURCE_SNAPSHOT:
			rdbtnmntmSnapshot.setSelected(true);
			rdbtnmntmBeta.setSelected(false);
			rdbtnmntmStable.setSelected(false);
			break;
		case Updater.UPDATE_SOURCE_BETA:
			rdbtnmntmSnapshot.setSelected(false);
			rdbtnmntmBeta.setSelected(true);
			rdbtnmntmStable.setSelected(false);
			break;
		case Updater.UPDATE_SOURCE_STABLE:
			rdbtnmntmSnapshot.setSelected(false);
			rdbtnmntmBeta.setSelected(false);
			rdbtnmntmStable.setSelected(true);
			break;
		default:
			//TODO: Handle error?
			break;
		}
	}
	
	private void saveUpdateBranchConfig(Config config){
		int updateSource = -1;
		if (rdbtnmntmSnapshot.isSelected()){
			updateSource = Updater.UPDATE_SOURCE_SNAPSHOT;
		} else if (rdbtnmntmBeta.isSelected()){
			updateSource = Updater.UPDATE_SOURCE_BETA;
		} else if (rdbtnmntmStable.isSelected()){
			updateSource = Updater.UPDATE_SOURCE_STABLE;
		} else {
			return;
		}
		
		config.setUpdateSource(updateSource);
		try {
			config.write();
		} catch (IOException e) {
			//Handle error
			e.printStackTrace();
			JOptionPane.showMessageDialog(UIFrame.this, "Could not save update source configuration to file :(\n\nException:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
