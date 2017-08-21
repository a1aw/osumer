package com.github.mob41.osumer.ui;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Dialog.ModalityType;
import java.awt.Dialog;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;

import com.github.mob41.organdebug.exceptions.DebuggableException;
import com.github.mob41.osumer.io.queue.Queue;
import com.github.mob41.osums.indexing.IndexingProgressHandler;
import com.github.mob41.osums.indexing.OnlineIndexManager;
import com.github.mob41.osums.io.beatmap.OsuBeatmap;
import com.github.mob41.osums.io.beatmap.Osums;
import com.github.mob41.osums.io.beatmap.ResultBeatmap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class BeatmapSearchPanel extends JPanel {
    private JTextField searchFld;
    private DefaultTableModel tableModel;
    private JTable table;
    
    private final OnlineIndexManager mgr;

    /**
     * Create the panel.
     */
    public BeatmapSearchPanel(Osums osums) {
        mgr = osums.getOimgr();
        
        JLabel lblSearchBeatmap = new JLabel("Search beatmap:");
        lblSearchBeatmap.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        searchFld = new JTextField();
        searchFld.setColumns(10);
        
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                tableModel.setRowCount(0);
                SearchingProgressDialog dialog = new SearchingProgressDialog();
                
                Thread thread = new Thread(){
                    public void run(){
                        try {
                            sleep(250);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        
                        ResultBeatmap[] maps;
                        
                        try {
                            maps = osums.searchMaps(searchFld.getText());
                            if (maps == null) {
                                int option = JOptionPane.showOptionDialog(BeatmapSearchPanel.this, 
                                        "Online beatmap not indexed. Indexing can improve searching speed.\n"
                                        + "It can take some minutes to an hour.\n\n"
                                        + "Do you want to index now or just do web search?",
                                        "Not indexed", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                                if (option == JOptionPane.YES_OPTION){
                                    dialog.dispose();
                                    IndexingProgressDialog idialog = new IndexingProgressDialog();
                                    
                                    Thread t2 = new Thread(){
                                        public void run(){
                                            try {
                                                sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                mgr.startIndexing(idialog.getHandler());
                                                mgr.write();
                                            } catch (DebuggableException e) {
                                                e.printStackTrace();
                                                idialog.dispose();
                                            } catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                            
                                            idialog.dispose();
                                        }
                                    };
                                    t2.setDaemon(true);
                                    t2.start();
                                    
                                    idialog.setLocationRelativeTo(BeatmapSearchPanel.this);
                                    idialog.setModal(true);
                                    idialog.setVisible(true);
                                    
                                    JOptionPane.showMessageDialog(BeatmapSearchPanel.this, "Please make another search again.", "Info", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                } else if (option == JOptionPane.NO_OPTION){
                                    maps = osums.getLinksOfBeatmapSearch(dialog.getHandler(), "https://osu.ppy.sh/p/beatmaplist?q=" + searchFld.getText());
                                } else {
                                    dialog.dispose();
                                    return;
                                }
                            }
                        } catch (DebuggableException e) {
                            e.printStackTrace();
                            return;
                        }
                        
                        
                        dialog.getLabel().setText("Status: Updating UI...");
                    
                        for (int i = 0; i < maps.length; i++){
                            tableModel.addRow(new Object[]{maps[i]});
                        }
                        
                        dialog.dispose();
                    }
                };
                
                thread.setDaemon(true);
                thread.start();
                
                dialog.setLocationRelativeTo(BeatmapSearchPanel.this);
                dialog.setModal(true);
                dialog.setVisible(true);
            }
        });
        btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        JLabel lblSortBy = new JLabel("Sort by:");
        lblSortBy.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        JComboBox sortByBox = new JComboBox();
        sortByBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
        sortByBox.setModel(new DefaultComboBoxModel(new String[] {"Title", "Artist", "Creator", "Difficulty", "Ranked", "Rating", "Plays"}));
        sortByBox.setSelectedIndex(4);
        
        JScrollPane scrollPane = new JScrollPane();
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(10)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(lblSearchBeatmap)
                                    .addGap(4)
                                    .addComponent(searchFld, GroupLayout.PREFERRED_SIZE, 554, GroupLayout.PREFERRED_SIZE)
                                    .addGap(6)
                                    .addComponent(btnSearch))
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(lblSortBy)
                                    .addGap(4)
                                    .addComponent(sortByBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(10)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(4)
                            .addComponent(lblSearchBeatmap))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(2)
                            .addComponent(searchFld, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnSearch))
                    .addGap(6)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(3)
                            .addComponent(lblSortBy))
                        .addComponent(sortByBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                    .addContainerGap())
        );
        
        tableModel = new DefaultTableModel(){
            @Override
            public Class<?> getColumnClass(int colIndex){
                return ResultBeatmap.class;
            }
        };
        
        table = new JTable(tableModel);
        table.setDefaultRenderer(ResultBeatmap.class, new ResultBeatmapCellRenderer());
        table.setRowHeight(120);
        
        tableModel.setColumnIdentifiers(new String[]{"Result beatmaps"});
        tableModel.fireTableDataChanged();
        
        scrollPane.setViewportView(table);
        setLayout(groupLayout);

    }
}
