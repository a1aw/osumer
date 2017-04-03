package com.github.mob41.osumer.ui;

import javax.swing.JPanel;

import com.github.mob41.osumer.io.Downloader;
import com.github.mob41.osumer.io.Queue;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.TableModel;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.swing.JProgressBar;
import javax.swing.JPopupMenu;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	private QueueCellTableModel tableModel;
	private Timer timer;
	private JLabel lblElapsed;

	/**
	 * Create the panel.
	 */
	public QueueCell(QueueCellTableModel model, Queue queue, Icon thumbIcon) {
		this.queue = queue;
		this.tableModel = model;
		
		this.timer = new Timer(100, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.fireTableDataChanged();
			}
		});
		
		timer.start();
		
		lblThumb = new JLabel("Loading thumb...");
		lblThumb.setHorizontalAlignment(SwingConstants.CENTER);
		
		if (thumbIcon != null){
			lblThumb.setText("");
			lblThumb.setIcon(thumbIcon);
		} else {
			lblThumb.setText("No thumb");
		}
		
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
			
			private int last = 0;
			
			@Override
			public void update(Observable o, Object arg) {
				switch (downloader.getStatus()){
				case Downloader.DOWNLOADING:
					pb.setIndeterminate(false);
					int progress = (int) downloader.getProgress();
					pb.setValue(progress);
					
					long elapsedTime = System.nanoTime() - startTime;
					long allTimeForDownloading = downloader.getDownloaded() != 0 ? (elapsedTime * downloader.getSize() / downloader.getDownloaded()) : -1;
					
					if (allTimeForDownloading == -1){
						lblStatus.setText("Status: Starting...");
					} else {
						long eta = allTimeForDownloading - elapsedTime;
						lblElapsed.setText("Elapsed: " + nanoSecToString(elapsedTime));
						lblEta.setText("ETA: " + nanoSecToString(eta));
						lblStatus.setText("Status: Downloading...");
					}
					break;
				case Downloader.COMPLETED:
					lblStatus.setForeground(Color.GREEN);
					
					lblEta.setText("ETA: ---");
					lblStatus.setText("Status: Completed.");
					break;
				case Downloader.ERROR:
					lblEta.setText("ETA: ---");
					lblStatus.setForeground(Color.RED);
					lblStatus.setText("Status: Error occurred while downloading.");
					break;
				case Downloader.PAUSED:
					lblEta.setText("ETA: ---");
					lblStatus.setForeground(Color.BLUE);
					lblStatus.setText("Status: Paused.");
					break;
				case Downloader.CANCELLED:
					lblEta.setText("ETA: ---");
					lblStatus.setForeground(Color.BLACK);
					lblStatus.setText("Status: Cancelled.");
					break;
				default:
					lblEta.setText("ETA: ---");
					lblStatus.setForeground(Color.RED);
					lblStatus.setText("Status: Unknown status.");
				}
			}
		});
		label = new JLabel(queue.getName());
		label.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		lblFilename = new JLabel("File-name: " + downloader.getFileName());
		
		lblEta = new JLabel("ETA: ---");
		
		pb = new JProgressBar();
		pb.setStringPainted(true);
		
		lblStatus = new JLabel("Status: Initializing...");
		
		lblElapsed = new JLabel("Elapsed: ---");
		GroupLayout gl_detailsPanel = new GroupLayout(detailsPanel);
		gl_detailsPanel.setHorizontalGroup(
			gl_detailsPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(label, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
				.addComponent(lblFilename, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
				.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
				.addComponent(pb, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
				.addGroup(gl_detailsPanel.createSequentialGroup()
					.addComponent(lblEta, GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblElapsed, GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_detailsPanel.setVerticalGroup(
			gl_detailsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_detailsPanel.createSequentialGroup()
					.addComponent(label)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblFilename)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_detailsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEta)
						.addComponent(lblElapsed))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblStatus)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pb, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
		);
		detailsPanel.setLayout(gl_detailsPanel);
		setLayout(groupLayout);
		
		start();
	}
	
	private String nanoSecToString(long ns){
		long sec = TimeUnit.NANOSECONDS.toSeconds(ns);
		
		long min = 0;
		if (sec >= 60){
			min = (long) ((float) sec / 60);
			sec -= min * 60;
		}
		
		long hr = 0;
		if (min >= 60){
			hr = (long) ((float) min / 60);
			min -= hr * 60;
		}
		
		return (hr != 0 ? (hr + " hr(s) ") : "") + (min != 0 ? (min + " min(s) ") : "") + sec + " sec(s)";
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
