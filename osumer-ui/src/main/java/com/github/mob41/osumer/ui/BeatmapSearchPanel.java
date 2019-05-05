package com.github.mob41.osumer.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

import com.github.mob41.organdebug.exceptions.DebuggableException;
import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.ui.dialog.IndexingProgressDialog;
import com.github.mob41.osumer.ui.dialog.ProgressDialog;
import com.github.mob41.osumer.ui.dialog.SearchingProgressDialog;
import com.github.mob41.osums.indexing.OnlineIndexManager;
import com.github.mob41.osums.io.beatmap.Osums;
import com.github.mob41.osums.io.beatmap.ResultBeatmap;

public class BeatmapSearchPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 5382528481143622530L;
    private JTextField searchFld;
    private DefaultTableModel tableModel;
    private JTable table;
    
    private final OnlineIndexManager mgr;
    private JLabel lblResultDesc;
    private JRadioButton rdbtnUseOfflineIndexed;
    private JRadioButton rdbtnUseOnlineWeb;
    
    private UIFrame_old frame;
    private Osums osums;
    
    public BeatmapSearchPanel() {
        this(null, null);
    }

    /**
     * Create the panel.
     */
    public BeatmapSearchPanel(UIFrame_old frame, Osums osums) {
        this.osums = osums;
        this.frame = frame;
        if (osums != null) {
            mgr = osums.getOimgr();
        } else {
            mgr = null;
        }
        
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
        lblSortBy.setEnabled(false);
        lblSortBy.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        JComboBox<String> sortByBox = new JComboBox<String>();
        sortByBox.setEnabled(false);
        sortByBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
        sortByBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Title", "Artist", "Creator", "Difficulty", "Ranked", "Rating", "Plays"}));
        sortByBox.setSelectedIndex(4);
        
        JScrollPane scrollPane = new JScrollPane();
        
        rdbtnUseOfflineIndexed = new JRadioButton("Use offline indexed database (experimental)");
        rdbtnUseOfflineIndexed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rdbtnUseOfflineIndexed.isSelected()) {
                    JOptionPane.showMessageDialog(BeatmapSearchPanel.this, "This feature is still experimental. Please consider using web search instead.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        rdbtnUseOfflineIndexed.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        rdbtnUseOnlineWeb = new JRadioButton("Use online web search");
        rdbtnUseOnlineWeb.setSelected(true);
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
            /**
             * 
             */
            private static final long serialVersionUID = -5611726114479859094L;

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
                        frame.addQueue(map.getBeatmapUrl(), true);
                        //frame.addBtQueue(map.getBeatmapUrl(), true);
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
        if (searchFld.getText().isEmpty()) {
            int option = JOptionPane.showOptionDialog(BeatmapSearchPanel.this, "Please consider not to empty the search field.\nIt might cause a long time to do searching.\nAre you sure to continue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
            if (option == JOptionPane.NO_OPTION) {
                return;
            }
        }
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
                                Configuration config = frame.getConfig();
                                doUiLogin(config);
                                
                                try {
                                    maps = osums.getLinksOfBeatmapSearch(dialog.getHandler(), "https://osu.ppy.sh/p/beatmaplist?q=" + searchFld.getText());
                                } catch (DebuggableException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                dialog.dispose();
                                return;
                            }
                        }
                    } else {
                        Configuration config = frame.getConfig();
                        if (!osums.isLoggedIn()) {
                            doUiLogin(config);
                        }
                        
                        if (osums.isLoggedIn()) {
                            //TODO: Change to use NEW Parser
                            maps = osums.getLinksOfBeatmapSearch(dialog.getHandler(), "https://old.ppy.sh/p/beatmaplist?q=" + searchFld.getText());
                        } else {
                            dialog.dispose();
                            JOptionPane.showMessageDialog(BeatmapSearchPanel.this, "You must be logged into osu! forum to perform online search.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
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
    
    private ProgressDialog doUiLogin(Configuration config) {
        ProgressDialog pbd = new ProgressDialog();
        pbd.setTitle("osums Login Client");
        Thread th = new Thread() {
            public void run() {
                pbd.getProgressBar().setIndeterminate(true);
                pbd.getLabel().setText("Status: Getting configuration...");
                String user = config.getUser();
                String pass = config.getPass();

                if (user == null || user.isEmpty() || pass == null || pass.isEmpty()) {
                    pbd.getLabel().setText("Status: Prompting username and password...");
                    LoginPanel loginPanel = new LoginPanel();
                    int option = JOptionPane.showOptionDialog(frame, loginPanel, "Login to osu!",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
                            JOptionPane.CANCEL_OPTION);

                    if (option == JOptionPane.OK_OPTION) {
                        if (loginPanel.getUsername().isEmpty() || loginPanel.getPassword().isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "Username or password cannot be empty.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            pbd.dispose();
                            return;
                        }

                        user = loginPanel.getUsername();
                        pass = loginPanel.getPassword();
                    } else {
                        pbd.dispose();
                        return;
                    }
                }

                pbd.getLabel().setText("Status: Logging in...");
                try {
                    osums.login(user, pass);
                } catch (DebuggableException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error logging in:\n" + e.getDump().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    pbd.dispose();
                    return;
                }

                pbd.dispose();
            }
        };
        th.setDaemon(true);
        th.start();
        
        pbd.setLocationRelativeTo(BeatmapSearchPanel.this);
        pbd.setModal(true);
        pbd.setVisible(true);
        return pbd;
    }
}
