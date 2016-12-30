package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.JPasswordField;

public class UIFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Create the frame.
	 */
	public UIFrame() {
		setResizable(false);
		setTitle("osumer UI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 531, 351);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblOsumer = new JLabel("osumer");
		lblOsumer.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOsumer.setFont(new Font("Tahoma", Font.PLAIN, 24));
		
		JLabel lblTheEasiestexpressWay = new JLabel("The easiest,express way to obtain beatmaps");
		lblTheEasiestexpressWay.setFont(new Font("Tahoma", Font.PLAIN, 17));
		
		JLabel lblBeatmapUrl = new JLabel("Beatmap URL:");
		lblBeatmapUrl.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		
		textField = new JTextField();
		textField.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		textField.setColumns(10);
		
		JButton btnDownloadImport = new JButton("Download & Import");
		btnDownloadImport.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		
		JPanel panel = new JPanel();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addComponent(lblOsumer, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
							.addComponent(lblTheEasiestexpressWay, GroupLayout.PREFERRED_SIZE, 392, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addComponent(lblBeatmapUrl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDownloadImport, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOsumer, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTheEasiestexpressWay, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBeatmapUrl)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnDownloadImport, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))
		);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		AccountSettingsPanel accPanel = new AccountSettingsPanel();
		accPanel.setBorder(new TitledBorder(null, "Using account", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(accPanel);
		
		
		contentPane.setLayout(gl_contentPane);
	}
}
