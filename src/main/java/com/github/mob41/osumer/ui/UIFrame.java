package com.github.mob41.osumer.ui;

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
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.io.Osu;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Desktop;

import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

public class UIFrame extends JFrame {

	private JPanel contentPane;
	private JTextField mapUrlFld;

	/**
	 * Create the frame.
	 */
	public UIFrame(Config config) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_32px.png")));
		setResizable(false);
		setTitle("osumer UI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 462);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblOsumer = new JLabel("osumer");
		lblOsumer.setHorizontalAlignment(SwingConstants.LEFT);
		lblOsumer.setFont(new Font("Tahoma", Font.PLAIN, 24));
		
		JLabel lblBeatmapUrl = new JLabel("Beatmap URL:");
		lblBeatmapUrl.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		
		mapUrlFld = new JTextField();
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
		
		JMenuItem mntmDwnFolder = new JMenuItem("Download to folder");
		mntmDwnFolder.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				
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
				
				//Allow user to choose folder
				JFileChooser chooser = new JFileChooser();
				
				chooser.addChoosableFileFilter(new FileFilter(){

					@Override
					public boolean accept(File arg0) {
						if (arg0 == null){
							return false;
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
		popupMenu.add(mntmDwnAs);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblCopyrightc, GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 563, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(label, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblOsumer, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 383, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblBeatmapUrl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(mapUrlFld, GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDownloadImport)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDownload))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblHttpsgithubcommobosumer, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
						.addComponent(label, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
						.addComponent(lblOsumer, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
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
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblCopyrightc)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblHttpsgithubcommobosumer, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
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
	}
}
