package com.github.mob41.osumer.ui;

import javax.swing.JPanel;

import com.github.mob41.osumer.io.Downloader;
import com.github.mob41.osumer.io.Queue;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.swing.JProgressBar;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class QueueCell extends JPanel {
	private JProgressBar pb;
	private JLabel lblEta;
	private JLabel lblFilename;
	private JLabel label;
	private JLabel lblThumb;
	private JLabel lblStatus;
	private long startTime = 0;
	private Queue queue;

	/**
	 * Create the panel.
	 */
	public QueueCell(Queue queue) {
		this.queue = queue;
		lblThumb = new JLabel("Loading thumb...");
		lblThumb.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel detailsPanel = new JPanel();
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(this, popupMenu);
		
		JMenuItem mntmStart = new JMenuItem("Start");
		popupMenu.add(mntmStart);
		
		JSeparator separator = new JSeparator();
		popupMenu.add(separator);
		
		JMenuItem mntmPause = new JMenuItem("Pause");
		popupMenu.add(mntmPause);
		
		JMenuItem mntmResume = new JMenuItem("Resume");
		popupMenu.add(mntmResume);
		
		JSeparator separator_1 = new JSeparator();
		popupMenu.add(separator_1);
		
		JMenuItem mntmCancel = new JMenuItem("Cancel");
		popupMenu.add(mntmCancel);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblThumb, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(detailsPanel, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(detailsPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
						.addComponent(lblThumb, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		Downloader downloader = queue.getDownloader();
		downloader.addObserver(new Observer() {
			
			@Override
			public void update(Observable o, Object arg) {
				if (downloader.getStatus() == Downloader.DOWNLOADING){
					pb.setValue((int) downloader.getProgress());
					long elapsedTime = System.nanoTime() - startTime;
					long allTimeForDownloading = (elapsedTime * downloader.getSize() / downloader.getDownloaded());
					long eta = allTimeForDownloading - elapsedTime;
					lblEta.setText("ETA: " + TimeUnit.NANOSECONDS.toSeconds(eta) + " sec");
					lblStatus.setText("Status: Downloading...");
				} else {
					lblStatus.setText("Status: Stopped.");
					pb.setIndeterminate(true);
				}
			}
		});
		
		label = new JLabel(queue.getName());
		label.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		lblFilename = new JLabel("File-name: " + downloader.getFileName());
		
		lblEta = new JLabel("ETA:");
		
		pb = new JProgressBar();
		pb.setStringPainted(true);
		
		lblStatus = new JLabel("Status:");
		GroupLayout gl_detailsPanel = new GroupLayout(detailsPanel);
		gl_detailsPanel.setHorizontalGroup(
			gl_detailsPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(label, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
				.addComponent(lblFilename, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
				.addComponent(lblEta, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
				.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
				.addComponent(pb, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
		);
		gl_detailsPanel.setVerticalGroup(
			gl_detailsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_detailsPanel.createSequentialGroup()
					.addComponent(label)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblFilename)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblEta)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblStatus)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pb, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
		);
		detailsPanel.setLayout(gl_detailsPanel);
		setLayout(groupLayout);
		
		
	}
	
	private void start(){
		startTime = System.nanoTime();
		queue.start();
	}
	
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
