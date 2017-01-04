package com.github.mob41.osumer.ui;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.exceptions.OsuException;
import com.github.mob41.osumer.io.Installer;
import com.github.mob41.osumer.io.Osu;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.CardLayout;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;

public class ExpressSettingsPanel extends JPanel {
	private Config config;
	private Installer installer;
	private JCheckBox chckbxAutomaticallySwitchTo;
	private JCheckBox chckbxSwitchToBrowser;
	private JPanel notinstalledpanel;
	private JPanel settingspanel;
	private JPanel notsupportedpanel;
	private JLabel lblPleaseRestartOsumer;

	/**
	 * Create the panel.
	 */
	public ExpressSettingsPanel(Config conf) {
		settingspanel = new JPanel();
		
		this.config = conf;
		this.installer = new Installer();
		
		if (config.getDefaultBrowserPath() != null){
			//pathFld.setText(config.getDefaultBrowserPath());
		}
		
		JButton btnUninstallOsumer = new JButton("(*Admin) Uninstall osumerExpress");
		btnUninstallOsumer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					installer.uninstall();
				} catch (OsuException e){
					JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				
				if (installer.isInstalled()){
					panelSettings();
				} else {
					panelNotInstalled();
				}
			}
		});
		btnUninstallOsumer.setToolTipText("Not implemented");
		
		chckbxAutomaticallySwitchTo = new JCheckBox("Automatically switch to browser for non-beatmaps");
		chckbxAutomaticallySwitchTo.setSelected(config.isAutoSwitchBrowser());
		
		JButton btnRemoveConfiguration = new JButton("Remove configuration / Reset");
		btnRemoveConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//pathFld.setText("");
				chckbxAutomaticallySwitchTo.setSelected(false);
				config.removeDefaultBrowserPath();
				config.setAutoSwitchBrowser(true);
				config.setSwitchToBrowserIfWithoutUiArg(false);
				try {
					config.write();
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error occurred on writing configuration:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JButton btnSaveConfiguration = new JButton("Save configuration");
		btnSaveConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//config.setDefaultBrowserPath(pathFld.getText());
				config.setAutoSwitchBrowser(chckbxAutomaticallySwitchTo.isSelected());
				config.setSwitchToBrowserIfWithoutUiArg(chckbxSwitchToBrowser.isSelected());
				try {
					config.write();
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error occurred on writing configuration:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		setLayout(new CardLayout(0, 0));
		
		notinstalledpanel = new JPanel();
		add(notinstalledpanel, "name_169500745516563");
		
		JLabel lblOsumerexpressIsNot = new JLabel("osumerExpress is not installed.");
		lblOsumerexpressIsNot.setFont(new Font("PMingLiU", Font.PLAIN, 17));
		lblOsumerexpressIsNot.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnInstall = new JButton("(*Admin) Install osumerExpress");
		btnInstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					installer.install();
				} catch (OsuException e){
					JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				
				if (installer.isInstalled()){
					panelSettings();
				} else {
					panelNotInstalled();
				}
			}
		});
		
		JLabel lblNoadmin = new JLabel("Please restart osumer with administrative priviledges.");
		lblNoadmin.setForeground(Color.RED);
		lblNoadmin.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_notinstalledpanel = new GroupLayout(notinstalledpanel);
		gl_notinstalledpanel.setHorizontalGroup(
			gl_notinstalledpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_notinstalledpanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_notinstalledpanel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnInstall, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(lblOsumerexpressIsNot, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(lblNoadmin, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_notinstalledpanel.setVerticalGroup(
			gl_notinstalledpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_notinstalledpanel.createSequentialGroup()
					.addGap(78)
					.addComponent(lblOsumerexpressIsNot)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnInstall)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNoadmin)
					.addContainerGap(109, Short.MAX_VALUE))
		);
		notinstalledpanel.setLayout(gl_notinstalledpanel);
		
		chckbxSwitchToBrowser = new JCheckBox("Switch to browser if no \"-ui\" argument specified");
		chckbxSwitchToBrowser.setSelected(config.isSwitchToBrowserIfWithoutUiArg());
		
		JLabel lblSelectDefaultBrowser = new JLabel("Select default browser:");
		
		JComboBox comboBox = new JComboBox();
		
		lblPleaseRestartOsumer = new JLabel("Please restart osumer with administrative priviledges.");
		lblPleaseRestartOsumer.setForeground(Color.RED);
		lblPleaseRestartOsumer.setHorizontalAlignment(SwingConstants.CENTER);
		
		GroupLayout gl_settingspanel = new GroupLayout(settingspanel);
		gl_settingspanel.setHorizontalGroup(
			gl_settingspanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_settingspanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_settingspanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxSwitchToBrowser, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(chckbxAutomaticallySwitchTo, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(comboBox, 0, 269, Short.MAX_VALUE)
						.addComponent(btnRemoveConfiguration, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(btnSaveConfiguration, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(btnUninstallOsumer, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(lblSelectDefaultBrowser, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(lblPleaseRestartOsumer, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_settingspanel.setVerticalGroup(
			gl_settingspanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_settingspanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblSelectDefaultBrowser)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxAutomaticallySwitchTo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxSwitchToBrowser)
					.addPreferredGap(ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
					.addComponent(lblPleaseRestartOsumer)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnUninstallOsumer)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSaveConfiguration)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnRemoveConfiguration)
					.addContainerGap())
		);
		settingspanel.setLayout(gl_settingspanel);
		
		add(settingspanel, "name_169364947730057");
		
		notsupportedpanel = new JPanel();
		add(notsupportedpanel, "name_169799450579777");
		
		JLabel lblOsumerexpressDoesNot = new JLabel("Incompatible operating system");
		lblOsumerexpressDoesNot.setHorizontalAlignment(SwingConstants.CENTER);
		lblOsumerexpressDoesNot.setFont(new Font("PMingLiU", Font.PLAIN, 18));
		
		JLabel lblOsumerexpressCanOnly = new JLabel("osumerExpress can only be installed on Windows");
		lblOsumerexpressCanOnly.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_notsupportedpanel = new GroupLayout(notsupportedpanel);
		gl_notsupportedpanel.setHorizontalGroup(
			gl_notsupportedpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_notsupportedpanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_notsupportedpanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblOsumerexpressCanOnly, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(lblOsumerexpressDoesNot, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_notsupportedpanel.setVerticalGroup(
			gl_notsupportedpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_notsupportedpanel.createSequentialGroup()
					.addGap(85)
					.addComponent(lblOsumerexpressDoesNot)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblOsumerexpressCanOnly)
					.addContainerGap(130, Short.MAX_VALUE))
		);
		notsupportedpanel.setLayout(gl_notsupportedpanel);

		if (!Osu.isWindows()){
			panelNotSupported();
		}
		
		if (installer.isInstalled()){
			panelSettings();
		} else {
			panelNotInstalled();
		}
		
		boolean elevated = Osu.isWindowsElevated();
		
		lblNoadmin.setVisible(!elevated);
		lblPleaseRestartOsumer.setVisible(!elevated);
		btnUninstallOsumer.setEnabled(elevated);
		btnInstall.setEnabled(elevated);
	}
	
	private void panelNotSupported(){
		notsupportedpanel.setVisible(true);
		settingspanel.setVisible(false);
		notinstalledpanel.setVisible(false);
	}
	
	private void panelSettings(){
		notsupportedpanel.setVisible(false);
		settingspanel.setVisible(true);
		notinstalledpanel.setVisible(false);
	}
	
	private void panelNotInstalled(){
		notsupportedpanel.setVisible(false);
		settingspanel.setVisible(false);
		notinstalledpanel.setVisible(true);
	}
}
