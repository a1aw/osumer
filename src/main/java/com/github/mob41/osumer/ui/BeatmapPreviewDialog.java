package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.mob41.osumer.io.OsuBeatmap;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BeatmapPreviewDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextArea txtrLoadingInformation;
	private boolean selectedYes = false;
	private BufferedImage downloadedImage;

	/**
	 * Create the dialog.
	 */
	public BeatmapPreviewDialog(OsuBeatmap map) {
		setTitle("Beatmap information");
		setIconImage(Toolkit.getDefaultToolkit().getImage(BeatmapPreviewDialog.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_32px.png")));
		setBounds(100, 100, 589, 303);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel lblThumb = new JLabel("Loading thumb...");
		lblThumb.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblThumb.setHorizontalAlignment(SwingConstants.CENTER);
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblThumb, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
						.addComponent(lblThumb, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		txtrLoadingInformation = new JTextArea();
		txtrLoadingInformation.setEditable(false);
		txtrLoadingInformation.setText(
				"Name: " + map.getName() + "\n" +
						"Artist: " + map.getArtist() + "\n" +
						"Title: " + map.getTitle() + "\n" +
						"BPM: " + map.getBpm() + "\n" +
						"Creator: " + map.getCreator() + "\n" +
						"Source: " + map.getSource() + "\n" +
						"Genre: " + map.getGenre() + "\n" +
						"Rating: " + map.getRating() + "%\n" +
						"Good rating: " + map.getGoodRating() + "\n" +
						"Bad rating: " + map.getBadRating() + "\n" +
						"Success rate: " + map.getSuccessRate()
				);
		txtrLoadingInformation.setFont(new Font("Tahoma", Font.PLAIN, 13));
		scrollPane.setViewportView(txtrLoadingInformation);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			JLabel lblAreYouSure = new JLabel("Are you sure to download?");
			lblAreYouSure.setFont(new Font("Tahoma", Font.PLAIN, 12));
			buttonPane.add(lblAreYouSure);
			{
				JButton yesBtn = new JButton("Yes");
				yesBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectedYes = true;
						dispose();
						return;
					}
				});
				yesBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
				buttonPane.add(yesBtn);
				getRootPane().setDefaultButton(yesBtn);
			}
			{
				JButton noBtn = new JButton("No");
				noBtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectedYes = false;
						dispose();
						return;
					}
				});
				noBtn.setFont(new Font("Tahoma", Font.PLAIN, 12));
				buttonPane.add(noBtn);
			}
		}
		
		new Thread(){
			public void run(){
				URL url = null;
				try {
					url = new URL("http:" + map.getThumbUrl());
				} catch (MalformedURLException e) {
					e.printStackTrace();
					lblThumb.setForeground(Color.RED);
					lblThumb.setText("Thumb fetch error");
					return;
				}
				
				URLConnection conn = null;
				try {
					conn = url.openConnection();
				} catch (IOException e) {
					e.printStackTrace();
					lblThumb.setForeground(Color.RED);
					lblThumb.setText("Thumb fetch error");
					return;
				}
				
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				
				downloadedImage = null;
				try {
					downloadedImage = ImageIO.read(conn.getInputStream());
				} catch (IOException e) {
					e.printStackTrace();
					lblThumb.setForeground(Color.RED);
					lblThumb.setText("Thumb fetch error");
					return;
				}
				
				lblThumb.setText("");
				lblThumb.setIcon(new ImageIcon(downloadedImage));
			}
		}.start();
	}
	
	public BufferedImage getDownloadedImage(){
		return downloadedImage;
	}
	
	public boolean isSelectedYes(){
		return selectedYes;
	}
}
