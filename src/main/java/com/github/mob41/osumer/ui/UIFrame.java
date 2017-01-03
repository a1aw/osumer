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

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.io.Osu;

import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Desktop;

import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

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
		setBounds(100, 100, 574, 451);
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
		btnDownloadImport.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		
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
		label.setIcon(new ImageIcon(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_64px.png")));
		
		JLabel lblNewLabel = new JLabel("The easiest,express way to obtain beatmaps");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblCopyrightc, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblOsumer, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 383, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblBeatmapUrl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(mapUrlFld, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDownloadImport, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblHttpsgithubcommobosumer, GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
						.addComponent(label, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblOsumer, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBeatmapUrl)
						.addComponent(mapUrlFld, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnDownloadImport, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblCopyrightc)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblHttpsgithubcommobosumer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(3))
		);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		AccountSettingsPanel accPanel = new AccountSettingsPanel(config);
		accPanel.setBorder(new TitledBorder(null, "Using account", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(accPanel);
		
		ExpressSettingsPanel xpPanel = new ExpressSettingsPanel(config);
		xpPanel.setBorder(new TitledBorder(null, "osumerExpress settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(xpPanel);
		
		if (Osu.isWindows()){
			xpPanel.setEnabled(false);
			xpPanel.setToolTipText("osumerExpress does not support installation in non-Windows environment, currently not implemented.");
		}
		
		contentPane.setLayout(gl_contentPane);
	}
}
