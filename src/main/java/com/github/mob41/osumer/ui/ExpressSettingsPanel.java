package com.github.mob41.osumer.ui;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.github.mob41.osumer.Config;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class ExpressSettingsPanel extends JPanel {
	private JTextField pathFld;
	private Config config;

	/**
	 * Create the panel.
	 */
	public ExpressSettingsPanel(Config conf) {
		this.config = conf;
		
		JLabel lblDefaultBrowserApplication = new JLabel("Default browser application path:");
		
		pathFld = new JTextField();
		pathFld.setColumns(10);
		
		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int option = chooser.showOpenDialog(null);
				
				if (option == JFileChooser.CANCEL_OPTION){
					return;
				}
				
				File file = chooser.getSelectedFile();
				if (!file.exists()){
					JOptionPane.showMessageDialog(null, "The file chosen does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				pathFld.setText(file.getAbsolutePath());
			}
		});
		
		JButton btnInstallOsumerNext = new JButton("Install osumer next to browser");
		btnInstallOsumerNext.setToolTipText("Not implemented");
		btnInstallOsumerNext.setEnabled(false);
		
		JButton btnUninstallOsumer = new JButton("Uninstall osumer");
		btnUninstallOsumer.setToolTipText("Not implemented");
		btnUninstallOsumer.setEnabled(false);
		
		JCheckBox chckbxAutomaticallySwitchTo = new JCheckBox("Automatically switch to browser for non-beatmaps");
		chckbxAutomaticallySwitchTo.setSelected(true);
		
		JButton btnRemoveConfiguration = new JButton("Remove configuration / Reset");
		btnRemoveConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pathFld.setText("");
				chckbxAutomaticallySwitchTo.setSelected(false);
				config.removeDefaultBrowserPath();
				config.setAutoSwitchBrowser(true);
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
				config.setDefaultBrowserPath(pathFld.getText());
				config.setAutoSwitchBrowser(chckbxAutomaticallySwitchTo.isSelected());
				try {
					config.write();
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error occurred on writing configuration:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblDefaultBrowserApplication, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSelect, GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
						.addComponent(pathFld, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
						.addComponent(btnInstallOsumerNext, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
						.addComponent(btnUninstallOsumer, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
						.addComponent(chckbxAutomaticallySwitchTo, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(btnRemoveConfiguration, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
						.addComponent(btnSaveConfiguration, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDefaultBrowserApplication)
						.addComponent(btnSelect))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pathFld, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnInstallOsumerNext)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnUninstallOsumer)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxAutomaticallySwitchTo)
					.addPreferredGap(ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
					.addComponent(btnSaveConfiguration)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnRemoveConfiguration)
					.addContainerGap())
		);
		setLayout(groupLayout);

	}
}
