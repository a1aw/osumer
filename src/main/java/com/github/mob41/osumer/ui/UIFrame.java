package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.io.beatmap.Osu;
import com.github.mob41.osumer.io.beatmap.OsuBeatmap;
import com.github.mob41.osumer.io.beatmap.OsuDownloader;
import com.github.mob41.osumer.io.legacy.URLDownloader;
import com.github.mob41.osumer.io.queue.BeatmapImportAction;
import com.github.mob41.osumer.io.queue.Queue;
import com.github.mob41.osumer.io.queue.QueueAction;
import com.github.mob41.osumer.io.queue.QueueManager;
import com.github.mob41.osumer.ui.old.ViewDumpDialog;

public class UIFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4742856302707419966L;
	private final QueueManager mgr;
	private final Osu osu;
	private final Config config;
	
	private final Timer timer;

	private JPanel contentPane;
	private QueueCellTableModel tableModel;
	private JTable table;
	private JTextField beatmapField;
	private JCheckBox chckbxShowBeatmapPreview;

	/**
	 * Create the frame.
	 */
	public UIFrame(Config config, QueueManager mgr) {
		this.mgr = mgr;
		this.config = config;
		this.osu = new Osu();
		mgr.startQueuing();
		
		setTitle("osumer");
		setIconImage(Toolkit.getDefaultToolkit().getImage(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_32px.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 796, 541);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnOsumer2 = new JMenu("osumer2");
		menuBar.add(mnOsumer2);
		
		JMenuItem mntmViewDumps = new JMenuItem("View dumps");
		mntmViewDumps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ViewDumpDialog dialog = new ViewDumpDialog();
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
		mnOsumer2.add(mntmViewDumps);
		
		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mnOsumer2.add(mntmPreferences);
		
		JMenu mnQueue = new JMenu("Queue");
		menuBar.add(mnQueue);
		
		JMenuItem mntmRefreshQueueList = new JMenuItem("Refresh queue list");
		mntmRefreshQueueList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tableModel.fireTableDataChanged();
			}
		});
		
		JMenuItem mntmAddLegacyQueue = new JMenuItem("Add legacy queue");
		mntmAddLegacyQueue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Specify URL to download:");
				
				URL url = null;
				try {
					url = new URL(input);
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(UIFrame.this, "Error: Invalid URL", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				mgr.addQueue(new Queue("Legacy download", new URLDownloader("C:\\Users\\Anthony\\Desktop\\Unknown", "legacy.test", url), null, null, null));
				tableModel.fireTableDataChanged();
			}
		});
		mnQueue.add(mntmAddLegacyQueue);
		
		JMenuItem mntmEditQueues = new JMenuItem("Edit queues");
		mntmEditQueues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditQueueDialog dialog = new EditQueueDialog(mgr);
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
		mnQueue.add(mntmEditQueues);
		mnQueue.add(mntmRefreshQueueList);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblImg = new JLabel("");
		lblImg.setIcon(new ImageIcon(UIFrame.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_64px.png")));
		
		JLabel lblTitle = new JLabel("osumer2");
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
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(tab)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblImg)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblTitle)
									.addContainerGap())
								.addComponent(lblSubtitle, GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblCopyright, GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
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
					.addComponent(tab, GroupLayout.PREFERRED_SIZE, 366, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblCopyright))
		);
		
		JPanel panel = new JPanel();
		tab.addTab("Quick start", null, panel, null);
		
		JLabel lblBeatmapUrl = new JLabel("Beatmap URL:");
		lblBeatmapUrl.setFont(new Font("Tahoma", Font.PLAIN, 21));
		
		beatmapField = new JTextField();
		beatmapField.setFont(new Font("Tahoma", Font.PLAIN, 18));
		beatmapField.setColumns(10);
		
		JButton btnAddToQueue = new JButton("Add to queue");
		btnAddToQueue.addActionListener(new ActionListener() {
			
			private OsuBeatmap map = null;
			private BufferedImage thumb = null;
			private ProgressDialog pbd;
			
			public void actionPerformed(ActionEvent e) {
				pbd = new ProgressDialog();
				map = null;
				
				new Thread(){
					public void run(){
						pbd.getProgressBar().setIndeterminate(true);
						pbd.getLabel().setText("Status: Getting configuration...");
						String user = config.getUser();
						String pass = config.getPass();
						
						if (user == null || user.isEmpty() || pass == null || pass.isEmpty()){
							pbd.getLabel().setText("Status: Prompting username and password...");
							LoginPanel loginPanel = new LoginPanel();
							int option = JOptionPane.showOptionDialog(UIFrame.this, loginPanel, "Login to osu!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.CANCEL_OPTION);
							
							if (option == JOptionPane.OK_OPTION){
								if (loginPanel.getUsername().isEmpty() || loginPanel.getPassword().isEmpty()){
									JOptionPane.showMessageDialog(UIFrame.this, "Username or password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
									pbd.dispose();
									return;
								}
								
								user = loginPanel.getUsername();
								pass = loginPanel.getPassword();
							} else {
								return;
							}
						}
						
						pbd.getLabel().setText("Status: Logging in...");
						try {
							osu.login(user, pass);
						} catch (DebuggableException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(UIFrame.this, "Error logging in:\n" + e.getDump().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							pbd.dispose();
							return;
						}
						
						String beatmapLink = beatmapField.getText();
						
						pbd.getLabel().setText("Status: Obtaining beatmap information...");
						try {
							map = osu.getBeatmapInfo(beatmapLink);
						} catch (DebuggableException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(UIFrame.this, "Error getting beatmap info:\n" + e.getDump().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							pbd.dispose();
							return;
						}
						
						pbd.dispose();
					}
				}.start();
				
				pbd.setLocationRelativeTo(UIFrame.this);
				pbd.setModal(true);
				pbd.setVisible(true);
				
				if (map == null){
					return;
				}
				
				boolean preview = chckbxShowBeatmapPreview.isSelected();
				
				boolean stillDwn = true;
				
				if (preview){
					BeatmapPreviewDialog bpd = new BeatmapPreviewDialog(map);
					bpd.setLocationRelativeTo(UIFrame.this);
					bpd.setModal(true);
					bpd.setVisible(true);
					
					stillDwn = bpd.isSelectedYes();
					thumb = bpd.getDownloadedImage();
				}
				
				pbd = new ProgressDialog();
				
				new Thread(){
					public void run(){
						pbd.getProgressBar().setIndeterminate(true);
						pbd.getLabel().setText("Status: Downloading thumb image...");
						URL url = null;
						try {
							url = new URL("http:" + map.getThumbUrl());
						} catch (MalformedURLException e) {
							e.printStackTrace();
							
							return;
						}
						
						URLConnection conn = null;
						try {
							conn = url.openConnection();
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
						
						conn.setConnectTimeout(5000);
						conn.setReadTimeout(5000);
						
						try {
							thumb = ImageIO.read(conn.getInputStream());
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
						
						pbd.dispose();
					}
				}.start();
				
				pbd.setLocationRelativeTo(UIFrame.this);
				pbd.setModal(true);
				pbd.setVisible(true);
				
				if (stillDwn){
					URL downloadUrl = null;
					try {
						downloadUrl = new URL("http://osu.ppy.sh" + map.getDwnUrl());
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(UIFrame.this, "Error validating download URL:\n" + e1, "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					String tmpdir = System.getProperty("java.io.tmpdir");
					
					OsuDownloader dwn = new OsuDownloader(tmpdir, map.getDwnUrl().substring(3, map.getDwnUrl().length()) + " " + map.getName(), osu, downloadUrl);
					mgr.addQueue(new Queue(map.getName(), dwn, thumb, null, new QueueAction[]{new BeatmapImportAction()}));
					tableModel.fireTableDataChanged();
					tab.setSelectedIndex(1);
				}
			}
		});
		btnAddToQueue.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		JLabel lblSpecifyYourDesired = new JLabel("Specify your desired osu! beatmap URL to download. (e.g. http://osu.ppy.sh/s/320118)");
		lblSpecifyYourDesired.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JLabel lblNewWebpageUrls = new JLabel("New webpage URLs (e.g. http://new.ppy.sh/) are currently not supported.");
		lblNewWebpageUrls.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JLabel lblYouWillBe = new JLabel("WARNING: You haven't setup an osu! account for beatmap downloading. Go to \"Preferences\" to configure.");
		lblYouWillBe.setForeground(Color.RED);
		lblYouWillBe.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JRadioButton rdbtnDownloadAndImport = new JRadioButton("Download and import");
		rdbtnDownloadAndImport.setSelected(true);
		rdbtnDownloadAndImport.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JRadioButton rdbtnDownloadToFile = new JRadioButton("Download to file...");
		rdbtnDownloadToFile.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JButton btnSelectFile = new JButton("Select file");
		btnSelectFile.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JRadioButton rdbtnDownloadToFolder = new JRadioButton("Download to folder...");
		rdbtnDownloadToFolder.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JButton btnSelectFolder = new JButton("Select folder");
		btnSelectFolder.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JButton btnOsumerPreferences = new JButton("osumer2 Preferences");
		btnOsumerPreferences.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		chckbxShowBeatmapPreview = new JCheckBox("Show beatmap preview");
		chckbxShowBeatmapPreview.setSelected(true);
		chckbxShowBeatmapPreview.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(rdbtnDownloadToFolder)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSelectFolder))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(rdbtnDownloadToFile)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSelectFile))
						.addComponent(lblSpecifyYourDesired, GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblBeatmapUrl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(beatmapField, GroupLayout.PREFERRED_SIZE, 441, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAddToQueue, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
						.addComponent(lblNewWebpageUrls, GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
						.addComponent(lblYouWillBe, GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
						.addComponent(rdbtnDownloadAndImport, GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
						.addComponent(chckbxShowBeatmapPreview, GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
						.addComponent(btnOsumerPreferences))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnAddToQueue, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
						.addComponent(beatmapField, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBeatmapUrl, Alignment.LEADING))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSpecifyYourDesired)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewWebpageUrls)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblYouWillBe)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnDownloadAndImport)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(rdbtnDownloadToFile)
						.addComponent(btnSelectFile))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(rdbtnDownloadToFolder)
						.addComponent(btnSelectFolder))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxShowBeatmapPreview)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOsumerPreferences)
					.addGap(99))
		);
		panel.setLayout(gl_panel);
		
		JPanel queuePanel = new JPanel();
		tab.addTab("Queue", null, queuePanel, null);
		queuePanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		queuePanel.add(scrollPane, BorderLayout.CENTER);
		
		tableModel = new QueueCellTableModel(mgr);
		
		table = new JTable(tableModel);
		table.setDefaultRenderer(Queue.class, new QueueCellRenderer(tableModel));
		table.setDefaultEditor(Queue.class, new QueueCellEditor(tableModel));
		table.setRowHeight(153);
		
		timer = new Timer(100, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.fireTableDataChanged();
				
			}
			
		});
		timer.start();
		
		JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem mntmStart = new JMenuItem("Start");
		popupMenu.add(mntmStart);
		
		JMenuItem mntmPause = new JMenuItem("Pause");
		popupMenu.add(mntmPause);
		
		JMenuItem mntmResume = new JMenuItem("Resume");
		popupMenu.add(mntmResume);
		
		JMenuItem mntmCancel = new JMenuItem("Cancel");
		mntmCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				if (index != -1){
					Queue queue = (Queue) table.getValueAt(index, 0);
					mgr.removeQueue(queue);
					tableModel.fireTableDataChanged();
				}
			}
		});
		popupMenu.add(mntmCancel);
		//timer.start();
		
		scrollPane.setViewportView(table);
		contentPane.setLayout(gl_contentPane);
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
