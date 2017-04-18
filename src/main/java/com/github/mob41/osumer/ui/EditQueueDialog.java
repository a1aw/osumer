package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.github.mob41.osumer.io.queue.Queue;
import com.github.mob41.osumer.io.queue.QueueManager;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;

public class EditQueueDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private DefaultTableModel tableModel;
	private QueueManager mgr;
	private JButton btnCancelAndRemove;

	/**
	 * Create the dialog.
	 */
	public EditQueueDialog(QueueManager mgr) {
		this.mgr = mgr;
		setBounds(100, 100, 464, 326);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane();
		btnCancelAndRemove = new JButton("Cancel and remove queue");
		btnCancelAndRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				if (index != -1){
					String name = (String) table.getValueAt(index, 0);
					Queue queue = mgr.getQueue(name);
					
					if (queue != null){
						mgr.removeQueue(queue);
					}
					refresh();
				} else {
					btnCancelAndRemove.setEnabled(false);
				}
			}
		});
		btnCancelAndRemove.setEnabled(false);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnCancelAndRemove, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnCancelAndRemove))
		);
		{
			tableModel = new DefaultTableModel();
			tableModel.setColumnIdentifiers(new String[]{"Name", "File-name", "Progress"});
			table = new JTable(tableModel){
				public boolean isCellEditable(int row, int column){
					return false;
				}
			};
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					int index = table.getSelectedRow();
					btnCancelAndRemove.setEnabled(index != -1);
				}
			});
			
			refresh();
			scrollPane.setViewportView(table);
		}
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Done");
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	private void refresh(){
		btnCancelAndRemove.setEnabled(false);
		List<Queue> queues = mgr.getList();
		tableModel.setRowCount(0);
		for (int i = 0; i < queues.size(); i++){
			Queue queue = queues.get(i);
			tableModel.addRow(new String[]{queue.getName(), queue.getDownloader().getFileName(), ((int)queue.getDownloader().getProgress()) + "%"});
		}
		tableModel.fireTableDataChanged();
	}

}
