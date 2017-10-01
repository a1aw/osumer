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
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BeatmapSearchPanel extends JPanel {
    private JTextField searchFld;
    private DefaultTableModel tableModel;
    private JTable table;
    
    private final OnlineIndexManager mgr;
    private JLabel lblResultDesc;
    private JRadioButton rdbtnUseOfflineIndexed;
    private JRadioButton rdbtnUseOnlineWeb;
    
    private Osums osums;

    /**
     * Create the panel.
     */
    public BeatmapSearchPanel(UIFrame frame, Osums osums) {
        this.osums = osums;
        mgr = osums.getOimgr();
        
        JLabel lblSearchBeatmap = new JLabel("Search beatmap:");
        lblSearchBeatmap.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        searchFld = new JTextField();
        searchFld.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                doSearch();
            }
        });
        searchFld.setColumns(10);
        
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                doSearch();
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
        
        rdbtnUseOfflineIndexed = new JRadioButton("Use offline indexed database");
        rdbtnUseOfflineIndexed.setSelected(true);
        rdbtnUseOfflineIndexed.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        rdbtnUseOnlineWeb = new JRadioButton("Use online web search");
        rdbtnUseOnlineWeb.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        ButtonGroup rdGp = new ButtonGroup();
        rdGp.add(rdbtnUseOfflineIndexed);
        rdGp.add(rdbtnUseOnlineWeb);
        
        lblResultDesc = new JLabel("Please enter any search string in order to start searching!");
        lblResultDesc.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE))
                        .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                            .addGap(10)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(lblSearchBeatmap)
                                    .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(searchFld, GroupLayout.PREFERRED_SIZE, 554, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addComponent(lblSortBy)
                                    .addGap(4)
                                    .addComponent(sortByBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(rdbtnUseOfflineIndexed)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(rdbtnUseOnlineWeb))))
                        .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(lblResultDesc, GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(10)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addGap(4)
                                    .addComponent(lblSearchBeatmap))
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addGap(2)
                                    .addComponent(searchFld, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addGap(6)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addGap(3)
                                    .addComponent(lblSortBy))
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(sortByBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rdbtnUseOfflineIndexed)
                                    .addComponent(rdbtnUseOnlineWeb))))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(btnSearch)))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblResultDesc)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
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
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                int i = table.getSelectedRow();
                if (i != -1){
                    ResultBeatmap map = (ResultBeatmap) table.getValueAt(i, 0);
                    
                    if (frame != null){
                        frame.addBtQueue(map.getBeatmapUrl(), true);
                    }
                }
            }
        });
        
        tableModel.setColumnIdentifiers(new String[]{"Result beatmaps"});
        tableModel.fireTableDataChanged();
        
        scrollPane.setViewportView(table);
        setLayout(groupLayout);

    }

    private void doSearch() {
        boolean useDb = rdbtnUseOfflineIndexed.isSelected();
        lblResultDesc.setText("Searching string \"" + searchFld.getText() + "\"" + (useDb ? " in offline indexed database..." : " online..."));
        
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
                    if (useDb){
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
                    } else {
                        maps = osums.getLinksOfBeatmapSearch(dialog.getHandler(), "https://osu.ppy.sh/p/beatmaplist?q=" + searchFld.getText());
                    }
                } catch (DebuggableException e) {
                    e.printStackTrace();
                    return;
                }
                
                if (maps.length == 0){
                    lblResultDesc.setText("No beatmaps match with the search string. Try using another keyword!" + (useDb ? " Or please try a web search with this string." : ""));
                } if (maps.length <= 40){
                    lblResultDesc.setText("Only " + maps.length + " beatmaps match with the search string." + (useDb ? " Please try a web search with this string." : ""));
                } else {
                    lblResultDesc.setText("Total of " + maps.length + " beatmaps match with the search string.");
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
}
