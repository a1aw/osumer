package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.github.mob41.osumer.io.Queue;
import com.github.mob41.osumer.io.QueueManager;
import com.github.mob41.osumer.io.URLDownloader;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTabbedPane;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UIFrame extends JFrame {

	private JPanel contentPane;
	private QueueCellTableModel tableModel;
	private JTable table;
	
	private final QueueManager mgr;

	/**
	 * Create the frame.
	 */
	public UIFrame(QueueManager mgr) {
		this.mgr = mgr;
		
		setTitle("osumer");
		setIconImage(Toolkit.getDefaultToolkit().getImage(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_32px.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 778, 546);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnQueue = new JMenu("Queue");
		menuBar.add(mnQueue);
		
		JMenuItem mntmRefreshQueueList = new JMenuItem("Refresh queue list");
		mntmRefreshQueueList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				syncUiQueues();
			}
		});
		mnQueue.add(mntmRefreshQueueList);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblImg = new JLabel("");
		lblImg.setIcon(new ImageIcon(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_64px.png")));
		
		JLabel lblTitle = new JLabel("osumer 2.0.0");
		lblTitle.setVerticalAlignment(SwingConstants.BOTTOM);
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 27));
		
		JLabel lblSubtitle = new JLabel("The easiest, express way to obtain beatmaps - Checking for updates...");
		lblSubtitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblSubtitle.setVerticalAlignment(SwingConstants.TOP);
		
		JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP);
		
		JLabel lblCopyright = new JLabel("Copyright (c) 2016-2017 Anthony Law. Licensed under MIT License.");
		lblCopyright.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCopyright.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(tab, GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(lblImg)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSubtitle, GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
								.addComponent(lblTitle, GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblCopyright, GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
							.addContainerGap())))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblTitle)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblSubtitle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(lblImg))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tab, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblCopyright))
		);
		
		JPanel queuePanel = new JPanel();
		tab.addTab("Queue", null, queuePanel, null);
		queuePanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		queuePanel.add(scrollPane, BorderLayout.CENTER);
		
		tableModel = new QueueCellTableModel();
		
		table = new JTable(tableModel);
		table.setDefaultRenderer(QueueCell.class, new QueueCellRenderer());
		table.setRowHeight(153);
		
		scrollPane.setViewportView(table);
		contentPane.setLayout(gl_contentPane);
	}
	
	private void syncUiQueues(){
		tableModel.removeAll();
		List<Queue> queues = mgr.getList();
		for (Queue queue : queues){
			tableModel.addCell(new QueueCell(tableModel, queue, new ImageIcon(queue.getThumb())));
		}
	}
}
