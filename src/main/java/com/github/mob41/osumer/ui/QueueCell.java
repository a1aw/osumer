package com.github.mob41.osumer.ui;

import javax.swing.JPanel;

import com.github.mob41.osumer.io.Downloader;
import com.github.mob41.osumer.io.Queue;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JButton;

public class QueueCell extends JPanel {
	private JProgressBar pb;
	private JLabel lblEta;
	private JLabel lblFilename;
	private JLabel lblThumb;
	private JLabel lblStatus;
	//private Queue queue;
	private QueueCellTableModel tableModel;
	//private Timer timer;
	private JLabel lblElapsed;
	private JLabel lblTitle;

	/**
	 * Create the panel.
	 */
	public QueueCell(QueueCellTableModel model) {
		//this.queue = queue;
		this.tableModel = model;
		/*
		this.timer = new Timer(100, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.fireTableDataChanged();
			}
		});
		*/
		
		//timer.start();
		
		lblThumb = new JLabel("Loading thumb...");
		lblThumb.setHorizontalAlignment(SwingConstants.CENTER);
		/*
		if (thumbIcon != null){
			lblThumb.setText("");
			lblThumb.setIcon(thumbIcon);
		} else {
			lblThumb.setText("No thumb");
		}
		*/
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(this, popupMenu);
		
		JMenuItem mntmStart = new JMenuItem("Start");
		mntmStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//start();
			}
		});
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
		
		lblFilename = new JLabel("File-name: ---");
		
		lblStatus = new JLabel("Status: ---");
		
		pb = new JProgressBar();
		pb.setStringPainted(true);
		
		lblEta = new JLabel("ETA: ---");
		
		lblElapsed = new JLabel("Elapsed: ---");
		lblTitle = new JLabel("---");
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 20));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblThumb, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTitle, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblFilename, GroupLayout.PREFERRED_SIZE, 428, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblEta, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(lblElapsed, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblStatus, GroupLayout.PREFERRED_SIZE, 428, GroupLayout.PREFERRED_SIZE)
						.addComponent(pb, GroupLayout.PREFERRED_SIZE, 428, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblThumb, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addComponent(lblTitle)
							.addGap(6)
							.addComponent(lblFilename)
							.addGap(6)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblEta)
								.addComponent(lblElapsed))
							.addGap(6)
							.addComponent(lblStatus)
							.addGap(6)
							.addComponent(pb, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		setLayout(groupLayout);
		
		//start();
	}
	
	public void updateData(Queue queue){
		Downloader dwn = queue.getDownloader();
		
		lblFilename.setText(dwn.getFileName());
		lblTitle.setText(queue.getName());
		
		lblThumb.setIcon(null);
		lblThumb.setText("Loading thumb...");
		lblStatus.setForeground(Color.BLACK);
		
		BufferedImage image = queue.getThumb();
		if (image != null){
			lblThumb.setText("");
			lblThumb.setIcon(new ImageIcon(image));
		} else {
			lblThumb.setText("No thumb");
		}
		
		int progress = (int) dwn.getProgress();
		pb.setValue(progress);
		
		switch (dwn.getStatus()){
		case Downloader.DOWNLOADING:
			pb.setIndeterminate(false);
			
			long elapsedTime = System.nanoTime() - queue.getStartTime();
			long allTimeForDownloading = dwn.getDownloaded() != 0 ? (elapsedTime * dwn.getSize() / dwn.getDownloaded()) : -1;
			
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
		case -1:
			lblEta.setText("ETA: ---");
			lblStatus.setForeground(Color.BLACK);
			lblStatus.setText("Status: Waiting for queuing...");
			break;
		default:
			lblEta.setText("ETA: ---");
			lblStatus.setForeground(Color.RED);
			lblStatus.setText("Status: Unknown status.");
		}
		/*
		if (this.queue != null){
			this.queue.getDownloader().deleteObservers();
		}
		
		this.queue = queue;
		
		Downloader downloader = queue.getDownloader();
		downloader.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				switch (downloader.getStatus()){
				case Downloader.DOWNLOADING:
					pb.setIndeterminate(false);
					int progress = (int) downloader.getProgress();
					pb.setValue(progress);
					
					long elapsedTime = System.nanoTime() - queue.getStartTime();
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
		queue.start();
		*/
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
	
	/*
	private void start(){
		queue.start();
	}
	*/
	
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
