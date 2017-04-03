package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DumpManager;
import com.github.mob41.osumer.io.Downloader;
import com.github.mob41.osumer.io.OsuDownloader;

public class UpdaterDownloadDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1362305880922451848L;

	private final JPanel contentPanel = new JPanel();

	private final Thread thread;
	
	private Downloader dwn;
	private JButton cancelButton;
	
	/**
	 * Create the dialog.
	 */
	public UpdaterDownloadDialog(URL url) {
		setBounds(100, 100, 450, 159);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel lblStatus = new JLabel("Status: Awaiting");
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
						.addComponent(lblStatus, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblStatus)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(159, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						askCancel();
					}
				});
				cancelButton.setEnabled(false);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		thread = new Thread(new Runnable(){

			@Override
			public void run() {
				
				progressBar.setIndeterminate(false);
				progressBar.setStringPainted(true);
				
				final String folder = System.getProperty("java.io.tmpdir");
				final String fileName = "osumer_updater_" + Calendar.getInstance().getTimeInMillis() + ".exe";
				
				dwn = new Downloader(folder, fileName, url);
				
				cancelButton.setEnabled(true);
				
				lblStatus.setText("Status: Downloading updater...");
				System.out.println("Download started.");
				while (dwn.getStatus() == OsuDownloader.DOWNLOADING){
					if (thread.isInterrupted()){
						return;
					}
					
					int progress = (int) dwn.getProgress();
					
					System.out.print(""); //If not doing this, problems will happen in .exe version :(
					
					progressBar.setValue(progress);
				}
				cancelButton.setEnabled(false);
				
				progressBar.setIndeterminate(true);
				progressBar.setStringPainted(true);
				
				if (dwn.getStatus() == OsuDownloader.ERROR){
					lblStatus.setForeground(Color.RED);
					lblStatus.setText("Status: Error when downloading");
					
					cancelButton.setEnabled(true);
					
					System.out.println("Download failed.");
				} else if (dwn.getStatus() == OsuDownloader.COMPLETED){
					String loc = folder + "\\" + fileName;

					lblStatus.setText("Status: Download completed. Opening...");
					System.out.println("Download completed. Importing...");
					
					try {
						Runtime.getRuntime().exec("cmd /c " + loc + " -install");
					} catch (IOException e1) {
						e1.printStackTrace();
						DumpManager.getInstance()
						.addDump(new DebugDump(
								null,
								"(If[openFile] scope) (UI) Set status to lblStatus",
								"(Try scope) Open file loc using Desktop.getDesktop.open()",
								"(Try scope) Sleep 2000 ms (2 sec)",
								"Unable to open file",
								false,
								e1));
					}
					
					System.exit(0);
					return;
				}
				
				
			}
			
		});
		thread.start();
	}
	
	private void askCancel(){
		int option = JOptionPane.showOptionDialog(UpdaterDownloadDialog.this, "Are you sure?", "Cancelling", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 1);
		if (option == JOptionPane.YES_OPTION){
			if (dwn != null){
				dwn.cancel();
			}
			thread.interrupt();
			
			dispose();
		}
	}
}
