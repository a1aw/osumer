package com.github.mob41.osumer.ui;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.exceptions.DumpManager;
import com.github.mob41.osumer.exceptions.OsuException;
import com.github.mob41.osumer.io.Installer;
import com.github.mob41.osumer.io.Osu;
import com.github.mob41.osumer.io.VersionInfo;

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
import javax.swing.JTextPane;
import java.awt.SystemColor;

public class ExpressSettingsPanel extends JPanel {
	private Config config;
	private Installer installer;
	private JCheckBox chckbxAutomaticallySwitchTo;
	private JCheckBox chckbxSwitchToBrowser;
	private JPanel notinstalledpanel;
	private JPanel settingspanel;
	private JPanel notsupportedpanel;
	private JLabel lblPleaseRestartOsumer;
	private JComboBox browserBox;
	private JLabel lblPleaseRestartOsumer_1;
	private JButton btnReinstall;
	private JPanel thisvernewer;
	private JCheckBox chckbxDisableOsumerexpressdirect;
	private JTextPane txtpnUpdateInfo;
	private JPanel thisverold;

	/**
	 * Create the panel.
	 */
	public ExpressSettingsPanel(Config conf) {
		settingspanel = new JPanel();
		
		this.config = conf;
		this.installer = new Installer();
		
		JButton btnUninstallOsumer = new JButton("(*Admin) Uninstall osumerExpress");
		btnUninstallOsumer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int option = JOptionPane.showOptionDialog(null, "Are you sure to uninstall? osumerExpress will no longer act as a browser.", "Uninstall osumerExpress", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 1);
				
				if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.NO_OPTION){
					return;
				}
				
				try {
					installer.uninstall();
				} catch (OsuException e){
					JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
				}
				
				if (installer.isInstalled()){
					panelSettings();
				} else {
					panelNotInstalled();
				}
			}
		});
		
		chckbxAutomaticallySwitchTo = new JCheckBox("Automatically switch to browser for non-beatmaps");
		chckbxAutomaticallySwitchTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!chckbxAutomaticallySwitchTo.isSelected()){
					int option = JOptionPane.showOptionDialog(null, 
							"Are you sure?\n"
							+ "Non-beatmap URLs won't be redirected\n"
							+ "to the browser selected in the settings.\n\n"
							+ "It will show a dialog with the URL instead,\n"
							+ "which will lead to inconvenient."
							, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, 0);
					
					if (option == JOptionPane.NO_OPTION || option == JOptionPane.CLOSED_OPTION){
						chckbxAutomaticallySwitchTo.setSelected(true);
					}
				}
			}
		});
		chckbxAutomaticallySwitchTo.setSelected(config.isAutoSwitchBrowser());
		
		JButton btnRemoveConfiguration = new JButton("Remove configuration / Reset");
		btnRemoveConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//pathFld.setText("");
				chckbxAutomaticallySwitchTo.setSelected(false);
				chckbxSwitchToBrowser.setSelected(false);
				chckbxDisableOsumerexpressdirect.setSelected(false);
				
				config.removeDefaultBrowser();
				config.setAutoSwitchBrowser(true);
				config.setSwitchToBrowserIfWithoutUiArg(false);
				config.setOEEnabled(true);
				
				chckbxAutomaticallySwitchTo.setEnabled(true);
				chckbxSwitchToBrowser.setEnabled(true);
				try {
					config.write();
				} catch (IOException e1) {
					DumpManager.getInstance()
						.addDump(new DebugDump(
							null,
							"Reset configuration",
							"Writing configuration to file",
							"Show Error Dialog",
							"Error occurred on writing configuration",
							false,
							e1));
					JOptionPane.showMessageDialog(null, "Error occurred on writing configuration:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JButton btnSaveConfiguration = new JButton("Save configuration");
		btnSaveConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String defBrowser = (String) browserBox.getSelectedItem();
				if (!defBrowser.equals("--- Select ---") && !defBrowser.equals("-!- Not elevated -!-")){
					config.setDefaultBrowser(defBrowser);
				}
				config.setAutoSwitchBrowser(chckbxAutomaticallySwitchTo.isSelected());
				config.setSwitchToBrowserIfWithoutUiArg(chckbxSwitchToBrowser.isSelected());
				config.setOEEnabled(!chckbxDisableOsumerexpressdirect.isSelected());
				try {
					config.write();
				} catch (IOException e1) {
					DumpManager.getInstance()
						.addDump(new DebugDump(
						null,
						"Set via setSwitchToBrowserIfWithoutUiArg() in configuration",
						"Writing configuration to file",
						"Show Error Dialog",
						"Error occurred on writing configuration",
						false,
						e1));
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
				int option = JOptionPane.showOptionDialog(null, "Are you sure to install?", "Install osumerExpress", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 1);
				
				if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.NO_OPTION){
					return;
				}
				
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
		
		JLabel lblNoadmin = new JLabel("Please restart osumer with administrative privileges.");
		lblNoadmin.setForeground(Color.RED);
		lblNoadmin.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblInstallingCanEnable = new JLabel("Installing can enable one-click download!");
		lblInstallingCanEnable.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_notinstalledpanel = new GroupLayout(notinstalledpanel);
		gl_notinstalledpanel.setHorizontalGroup(
			gl_notinstalledpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_notinstalledpanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_notinstalledpanel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnInstall, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(lblNoadmin, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(lblOsumerexpressIsNot, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(lblInstallingCanEnable, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_notinstalledpanel.setVerticalGroup(
			gl_notinstalledpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_notinstalledpanel.createSequentialGroup()
					.addGap(58)
					.addComponent(lblOsumerexpressIsNot)
					.addGap(5)
					.addComponent(lblInstallingCanEnable)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnInstall)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNoadmin)
					.addContainerGap(109, Short.MAX_VALUE))
		);
		notinstalledpanel.setLayout(gl_notinstalledpanel);
		
		chckbxSwitchToBrowser = new JCheckBox("Switch to browser if no \"-ui\" argument specified");
		chckbxSwitchToBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxSwitchToBrowser.isSelected()){
					int option = JOptionPane.showOptionDialog(null, 
							"Are you sure?\n"
							+ "You won't be able to access this UI unless you\n"
							+ "run this with the following command:\n\n.exe version:\n"
							+ "[osumer.exe -ui]\n\n.jar version:\n[java -jar osumer.jar -ui]"
							, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, 0);
					
					if (option == JOptionPane.NO_OPTION || option == JOptionPane.CLOSED_OPTION){
						chckbxSwitchToBrowser.setSelected(false);
					}
				}
			}
		});
		chckbxSwitchToBrowser.setSelected(config.isSwitchToBrowserIfWithoutUiArg());
		
		JLabel lblSelectDefaultBrowser = new JLabel("Select default browser:");
		
		browserBox = new JComboBox();
		
		lblPleaseRestartOsumer = new JLabel("Please restart osumer with administrative privileges.");
		lblPleaseRestartOsumer.setForeground(Color.RED);
		lblPleaseRestartOsumer.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblPleaseSetosumerexpress = new JLabel("Please set \"osumerExpress\" as default browser in Control Panel");
		
		chckbxDisableOsumerexpressdirect = new JCheckBox("Disable osumerExpress (Direct all URLs to browser)");
		chckbxDisableOsumerexpressdirect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chckbxAutomaticallySwitchTo.setEnabled(!chckbxDisableOsumerexpressdirect.isSelected());
				chckbxSwitchToBrowser.setEnabled(!chckbxDisableOsumerexpressdirect.isSelected());
				chckbxSwitchToBrowser.setSelected(false);
			}
		});
		chckbxDisableOsumerexpressdirect.setSelected(!config.isOEEnabled());
		if (!config.isOEEnabled()){
			chckbxAutomaticallySwitchTo.setEnabled(false);
			chckbxSwitchToBrowser.setEnabled(false);
			chckbxSwitchToBrowser.setSelected(false);
		}
		
		GroupLayout gl_settingspanel = new GroupLayout(settingspanel);
		gl_settingspanel.setHorizontalGroup(
			gl_settingspanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_settingspanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_settingspanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxSwitchToBrowser, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(chckbxAutomaticallySwitchTo, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(browserBox, 0, 296, Short.MAX_VALUE)
						.addComponent(btnRemoveConfiguration, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(btnSaveConfiguration, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(btnUninstallOsumer, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(lblSelectDefaultBrowser, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(lblPleaseRestartOsumer, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(lblPleaseSetosumerexpress, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(chckbxDisableOsumerexpressdirect, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_settingspanel.setVerticalGroup(
			gl_settingspanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_settingspanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblSelectDefaultBrowser)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(browserBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxAutomaticallySwitchTo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxSwitchToBrowser)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxDisableOsumerexpressdirect)
					.addPreferredGap(ComponentPlacement.RELATED, 3, Short.MAX_VALUE)
					.addComponent(lblPleaseSetosumerexpress)
					.addPreferredGap(ComponentPlacement.RELATED)
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
		
		thisverold = new JPanel();
		add(thisverold, "name_64628164930953");
		
		JLabel lblThisIsA = new JLabel("This is a old version");
		lblThisIsA.setHorizontalAlignment(SwingConstants.CENTER);
		lblThisIsA.setFont(new Font("PMingLiU", Font.PLAIN, 24));
		lblThisIsA.setForeground(Color.RED);
		
		JLabel lblTheInstalledVersion = new JLabel("The installed version is newer than this running version.");
		lblTheInstalledVersion.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblRunFromCprogram = new JLabel("Run from: C:\\Program Files\\osumer\\osumer.exe");
		lblRunFromCprogram.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_thisverold = new GroupLayout(thisverold);
		gl_thisverold.setHorizontalGroup(
			gl_thisverold.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_thisverold.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_thisverold.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTheInstalledVersion, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(lblThisIsA, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(lblRunFromCprogram, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_thisverold.setVerticalGroup(
			gl_thisverold.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_thisverold.createSequentialGroup()
					.addGap(77)
					.addComponent(lblThisIsA, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblTheInstalledVersion)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblRunFromCprogram)
					.addContainerGap(96, Short.MAX_VALUE))
		);
		thisverold.setLayout(gl_thisverold);
		
		thisvernewer = new JPanel();
		add(thisvernewer, "name_83607154979029");
		
		JLabel lblReadyForUpdate = new JLabel("Ready for update");
		lblReadyForUpdate.setHorizontalAlignment(SwingConstants.CENTER);
		lblReadyForUpdate.setForeground(new Color(0, 204, 0));
		lblReadyForUpdate.setFont(new Font("PMingLiU", Font.BOLD, 20));
		
		txtpnUpdateInfo = new JTextPane();
		txtpnUpdateInfo.setText("<dynamic>\r\n");
		txtpnUpdateInfo.setBackground(SystemColor.control);
		txtpnUpdateInfo.setEditable(false);
		
		btnReinstall = new JButton("(*Admin) Update osumerExpress");
		btnReinstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int option = JOptionPane.showOptionDialog(null, "Are you sure to reinstall?", "Reinstall osumerExpress", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 1);
				
				if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.NO_OPTION){
					return;
				}
				
				try {
					installer.uninstall();
				} catch (OsuException e){
					JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
				}
				
				if (!installer.isInstalled()){
					try {
						installer.install();
					} catch (OsuException e){
						JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
					}
					
					if (installer.isSameVersionInstalled()){
						panelSettings();
					} else {
						JOptionPane.showMessageDialog(null, "Version still mismatch. Error occurred?\nPlease check error dumps.", "Error", JOptionPane.ERROR_MESSAGE);
						VersionInfo info = installer.getInstalledVersion();
						panelReadyUpdate(info == null ? null : info.getVersion() + "-" + info.getBranch() + "-b" + info.getBuildNum(),
								Osu.OSUMER_VERSION + "-" + Osu.OSUMER_BRANCH + "-b" + Osu.OSUMER_BUILD_NUM);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Uninstallation was not successful.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		lblPleaseRestartOsumer_1 = new JLabel("Please restart osumer with administrative privilege");
		lblPleaseRestartOsumer_1.setForeground(Color.RED);
		lblPleaseRestartOsumer_1.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_thisvernewer = new GroupLayout(thisvernewer);
		gl_thisvernewer.setHorizontalGroup(
			gl_thisvernewer.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_thisvernewer.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_thisvernewer.createParallelGroup(Alignment.LEADING)
						.addComponent(lblReadyForUpdate, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(txtpnUpdateInfo, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(btnReinstall, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
						.addComponent(lblPleaseRestartOsumer_1, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_thisvernewer.setVerticalGroup(
			gl_thisvernewer.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_thisvernewer.createSequentialGroup()
					.addGap(64)
					.addComponent(lblReadyForUpdate)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtpnUpdateInfo, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnReinstall)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPleaseRestartOsumer_1)
					.addContainerGap(38, Short.MAX_VALUE))
		);
		thisvernewer.setLayout(gl_thisvernewer);

		if (!Osu.isWindows()){
			panelNotSupported();
		} else {
			refreshBrowsers();
			
			if (installer.isInstalled()){
				VersionInfo info = installer.getInstalledVersion();
				
				if (info == null){
					//Old version don't have a version file
					panelReadyUpdate(null,
							Osu.OSUMER_VERSION + "-" + Osu.OSUMER_BRANCH + "-b" + Osu.OSUMER_BUILD_NUM);
				} else if (info.isEqualToRunning()){
					panelSettings();
				} else if (info.isNewerThanRunning()){
					panelOld();
				} else {
					panelReadyUpdate(info.getVersion() + "-" + info.getBranch() + "-b" + info.getBuildNum(),
							Osu.OSUMER_VERSION + "-" + Osu.OSUMER_BRANCH + "-b" + Osu.OSUMER_BUILD_NUM);
				}
			} else {
				panelNotInstalled();
			}
		}
		
		boolean elevated = Osu.isWindowsElevated();
		
		browserBox.setEnabled(elevated);
		lblNoadmin.setVisible(!elevated);
		lblPleaseRestartOsumer.setVisible(!elevated);
		lblPleaseRestartOsumer_1.setVisible(!elevated);
		btnUninstallOsumer.setEnabled(elevated);
		btnInstall.setEnabled(elevated);
		btnReinstall.setEnabled(elevated);
	}
	
	private void refreshBrowsers(){
		String[] browsers = null;
		if (Osu.isWindowsElevated()){
			try {
				browsers = Installer.getAvailableBrowsers();
			} catch (OsuException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Could not relieve registry data about available browsers!\n" + e, "Critical Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else {
			browsers = new String[]{};
		}
		
		browserBox.removeAllItems();
		browserBox.addItem(Osu.isWindowsElevated() ? "--- Select ---" : "-!- Not elevated -!-");
		for (int i = 0; i < browsers.length; i++){
			browserBox.addItem(browsers[i]);
		}
		
		if (config.getDefaultBrowser() != null){
			int index = -1;
			for (int i = 0; i < browsers.length; i++){
				if (browsers[i].equals(config.getDefaultBrowser())){
					index = i;
				}
			}
			
			if (index != -1){
				browserBox.setSelectedIndex(index + 1);
			}
		}
	}
	
	private void panelReadyUpdate(String fromVer, String toVer){
		if (fromVer == null){
			txtpnUpdateInfo.setText("Installed version's version file cannot be detected. (Version too old?) You are reinstalling this version \"" + toVer + "\".");
		} else {
			txtpnUpdateInfo.setText("This running version is newer than installed verison. You are updating from \"" + fromVer + "\" to \"" + toVer + "\". Press \"Update osumerExpress\" to continue!");
		}
		thisverold.setVisible(false);
		thisvernewer.setVisible(true);
		notsupportedpanel.setVisible(false);
		settingspanel.setVisible(false);
		notinstalledpanel.setVisible(false);
	}
	
	private void panelOld(){
		thisverold.setVisible(true);
		thisvernewer.setVisible(false);
		notsupportedpanel.setVisible(false);
		settingspanel.setVisible(false);
		notinstalledpanel.setVisible(false);
	}
	
	private void panelNotSupported(){
		thisverold.setVisible(false);
		thisvernewer.setVisible(false);
		notsupportedpanel.setVisible(true);
		settingspanel.setVisible(false);
		notinstalledpanel.setVisible(false);
	}
	
	private void panelSettings(){
		refreshBrowsers();
		thisverold.setVisible(false);
		thisvernewer.setVisible(false);
		notsupportedpanel.setVisible(false);
		settingspanel.setVisible(true);
		notinstalledpanel.setVisible(false);
	}
	
	private void panelNotInstalled(){
		thisverold.setVisible(false);
		thisvernewer.setVisible(false);
		notsupportedpanel.setVisible(false);
		settingspanel.setVisible(false);
		notinstalledpanel.setVisible(true);
	}
}
