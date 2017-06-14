/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DumpManager;

public class ViewDumpDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1176048886023608621L;

	private static final String[] col_ident = {"Time", "Time in ms", "Message", "Exception", "UID"};

	private static final String SELECT_DUMP_MSG = "Please select a dump from the table to see the full dump message.";
	
	private final JPanel contentPanel = new JPanel();
	private JButton btnExportSelectedDump;
	private JTable dumps;
	private DebugDump[] dumpsArr;
	private DefaultTableModel dumpsModel;
	private JTextArea dumpStr;

	/**
	 * Create the dialog.
	 */
	public ViewDumpDialog() {
		setModal(true);
		setTitle("Dumps manager");
		setBounds(100, 100, 597, 449);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JSplitPane splitUpDwPane = new JSplitPane();
			splitUpDwPane.setResizeWeight(0.5);
			splitUpDwPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			contentPanel.add(splitUpDwPane, BorderLayout.CENTER);
			{
				JPanel upPanel = new JPanel();
				splitUpDwPane.setLeftComponent(upPanel);
				upPanel.setLayout(new BorderLayout(0, 0));
				{
					JScrollPane dumpsScroll = new JScrollPane();
					upPanel.add(dumpsScroll, BorderLayout.CENTER);
					{
						dumpsModel = new DefaultTableModel();
						dumpsModel.setColumnIdentifiers(col_ident);
						dumps = new JTable(dumpsModel){
							/**
							 * 
							 */
							private static final long serialVersionUID = 2925652011194875393L;

							@Override
							public boolean isCellEditable(int row, int column){
								return false;
							}
						};
						dumps.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent arg0) {
								int index = dumps.getSelectedRow();
								
								if (index == -1){
									btnExportSelectedDump.setEnabled(false);
									dumpStr.setText(SELECT_DUMP_MSG);
									return;
								}
								
								DebugDump dump = dumpsArr[index];
								dumpStr.setText(dump.toString());
								btnExportSelectedDump.setEnabled(true);
							}
						});
						dumpsScroll.setViewportView(dumps);
					}
				}
			}
			{
				JPanel dwPanel = new JPanel();
				splitUpDwPane.setRightComponent(dwPanel);
				dwPanel.setLayout(new BorderLayout(0, 0));
				{
					JScrollPane dumpStrScroll = new JScrollPane();
					dwPanel.add(dumpStrScroll, BorderLayout.CENTER);
					{
						dumpStr = new JTextArea();
						dumpStr.setText(SELECT_DUMP_MSG);
						dumpStr.setEditable(false);
						dumpStrScroll.setViewportView(dumpStr);
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnExportSelectedDump = new JButton("Export selected dump");
				btnExportSelectedDump.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int index = dumps.getSelectedRow();
						
						if (index == -1){
							JOptionPane.showMessageDialog(ViewDumpDialog.this, "Please select a dump to export", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						DebugDump debugDump = dumpsArr[index];
						
						if (debugDump == null){
							JOptionPane.showMessageDialog(ViewDumpDialog.this, "\"debugDump\" is null, cannot save dump with null", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						final String fileName = "osumer-debugDump-" + debugDump.getGenerated() + ".txt";
						final String fileExt = ".txt";
						final String fileExtDesc = "osumer dump files";
						
						saveStrToFile(fileName, fileExt, fileExtDesc, debugDump.toString());
					}
				});
				{
					JButton btnCombineDumpsAnd = new JButton("Combine dumps and export");
					btnCombineDumpsAnd.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (dumpsArr == null || dumpsArr.length == 0){
								int option = JOptionPane.showOptionDialog(ViewDumpDialog.this, "There are no dumps to be exported.\nDo you want to still export?", "No dumps", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, "No");
								if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.NO_OPTION){
									return;
								}
							}
							
							String str = DumpManager.combineDumps(dumpsArr);
							final String fileName = "osumer-combinedDumps-" + Calendar.getInstance().getTimeInMillis() + ".txt";
							final String fileExt = ".txt";
							final String fileExtDesc = "osumer combined dump files";
							
							saveStrToFile(fileName, fileExt, fileExtDesc, str);
						}
					});
					buttonPane.add(btnCombineDumpsAnd);
				}
				btnExportSelectedDump.setEnabled(false);
				buttonPane.add(btnExportSelectedDump);
			}
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				{
					JButton btnRefresh = new JButton("Refresh");
					btnRefresh.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							updateDumpsTable();
						}
					});
					buttonPane.add(btnRefresh);
				}
				buttonPane.add(cancelButton);
			}
		}
		updateDumpsTable();
	}
	
	private void saveStrToFile(String fileName, String fileExt, String fileExtDesc, String str){
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				if (arg0 == null){
					return false;
				}

				if (arg0.isDirectory()){
					return true;
				}
				
				String str = arg0.getName();
				final String ext = fileExt;
				
				if (str.length() < ext.length()){
					return false;
				}
				
				return str.endsWith(ext);
			}

			@Override
			public String getDescription() {
				return fileExt + " (" + fileExtDesc + ")";
			}
			
		});
		chooser.setSelectedFile(new File(fileName));
		
		int option = chooser.showSaveDialog(ViewDumpDialog.this);
		
		if (option == JFileChooser.CANCEL_OPTION){
			return;
		}
		
		File file = chooser.getSelectedFile();
		if (file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			PrintWriter writer = new PrintWriter(out, true);
			writer.println(str);
			writer.flush();
			writer.close();
			out.close();
		} catch (IOException e1){
			JOptionPane.showMessageDialog(ViewDumpDialog.this, "Error saving:\n" + e1, "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		JOptionPane.showMessageDialog(ViewDumpDialog.this, "Saved to location:\n" + file.getAbsolutePath(), "Info", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void updateDumpsTable(){
		dumpStr.setText(SELECT_DUMP_MSG);
		btnExportSelectedDump.setEnabled(false);
		
		DumpManager mgr = DumpManager.getInstance();
		
		if (mgr == null){
			System.out.println("DumpManager not initialized");
			return;
		}
		
		dumpsModel.setRowCount(0);
		dumpsArr = mgr.getDumps();
		String[] rowData;
		DebugDump dump;
		for (int i = 0; i < dumpsArr.length; i++){
			dump = dumpsArr[i];
			rowData = new String[5];
			rowData[0] = dump.getGeneratedHuman();
			rowData[1] = Long.toString(dump.getGenerated());
			rowData[2] = dump.getMessage();
			rowData[3] = dump.getStacktrace();
			rowData[4] = dump.getUid();
			dumpsModel.addRow(rowData);
		}
		dumpsModel.fireTableDataChanged();
	}

}
