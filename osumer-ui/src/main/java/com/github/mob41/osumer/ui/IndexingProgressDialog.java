package com.github.mob41.osumer.ui;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;

import com.github.mob41.osums.indexing.IndexingProgressHandler;

import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import javax.swing.JProgressBar;
import java.awt.Font;

public class IndexingProgressDialog extends JDialog {
    
    private final IndexingProgressHandler handler;
    private JLabel lblBiStatus;
    private JProgressBar pb0;
    private JLabel lblBifStatus;
    private JProgressBar pb1;
    
    /**
     * Create the dialog.
     */
    public IndexingProgressDialog() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        handler = new IndexingProgressHandler() {
            
            @Override
            public boolean onStart() {
                pb0.setIndeterminate(false);
                pb1.setIndeterminate(false);
                lblBiStatus.setText("Status: Indexing started. Waiting for response...");
                return true;
            }
            
            @Override
            public boolean onPause() {
                return false;
            }
            
            @Override
            public boolean onLoopStart() {
                return false;
            }
            
            @Override
            public boolean onLoopEnd() {
                int per = (int) ((float) this.getCompletedPages() / this.getTotalPages() * 100);
                if (this.getMode() == SEARCHING_MAPS){
                    if (per == 100){
                        pb0.setValue(100);
                        lblBiStatus.setText("Status: Indexing completed. Total indexes: " + this.getBeatmapIndexed());
                    } else {
                        pb0.setValue(per);
                        lblBiStatus.setText("Status: Indexing " + this.getCompletedPages() + "/" + this.getTotalPages() + ". Indexed: " + this.getBeatmapIndexed());
                    }
                } else if (this.getMode() == DOWNLOADING_INFO){
                    if (per == 100){
                        pb0.setValue(100);
                        lblBifStatus.setText("Status: Download info completed. Total downloads: " + this.getBeatmapIndexed());
                    } else {
                        pb0.setValue(100);
                        pb1.setValue(per);
                        lblBifStatus.setText("Status: Downloading info " + this.getCompletedPages() + "/" + this.getTotalPages() + ". Indexed: " + this.getBeatmapIndexed());
                    }
                }
                return false;
            }
            
            @Override
            public boolean onError() {
                return false;
            }
            
            @Override
            public boolean onComplete() {
                return false;
            }
        };
        setTitle("Indexing");
        setBounds(100, 100, 422, 256);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JPanel indexingPanel = new JPanel();
        indexingPanel.setBorder(new TitledBorder(null, "Beatmap Indexing", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        mainPanel.add(indexingPanel);
        indexingPanel.setLayout(new BorderLayout(0, 0));
        
        pb0 = new JProgressBar();
        pb0.setIndeterminate(true);
        pb0.setStringPainted(true);
        indexingPanel.add(pb0);
        
        lblBiStatus = new JLabel("Status: Waiting to start...");
        lblBiStatus.setFont(new Font("Tahoma", Font.PLAIN, 12));
        indexingPanel.add(lblBiStatus, BorderLayout.NORTH);
        
        JPanel dwnInfoPanel = new JPanel();
        dwnInfoPanel.setBorder(new TitledBorder(null, "Beatmap Information Fetching", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        mainPanel.add(dwnInfoPanel);
        dwnInfoPanel.setLayout(new BorderLayout(0, 0));
        
        pb1 = new JProgressBar();
        pb1.setIndeterminate(true);
        pb1.setStringPainted(true);
        dwnInfoPanel.add(pb1);
        
        lblBifStatus = new JLabel("Status: Waiting indexing to complete...");
        lblBifStatus.setFont(new Font("Tahoma", Font.PLAIN, 12));
        dwnInfoPanel.add(lblBifStatus, BorderLayout.NORTH);

    }

    public IndexingProgressHandler getHandler() {
        return handler;
    }
}
