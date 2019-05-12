package com.github.mob41.osumer.ui.old.dialog;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.github.mob41.osums.io.beatmap.SearchingProgressHandler;

public class SearchingProgressDialog extends JDialog {
    private JProgressBar pb;
    private JLabel lblStatus;
    private final SearchingProgressHandler handler;

    /**
     * Create the dialog.
     */
    public SearchingProgressDialog() {
        setTitle("Doing web search");
        setBounds(100, 100, 394, 124);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        handler = new SearchingProgressHandler() {
            
            @Override
            public boolean onStart() {
                lblStatus.setText("Status: Search started. Waiting request to response...");
                pb.setIndeterminate(false);
                return true;
            }
            
            @Override
            public boolean onPause() {
                return true;
            }
            
            @Override
            public boolean onLoopStart() {
                return true;
            }
            
            @Override
            public boolean onLoopEnd() {
                lblStatus.setText("Status: Relieving result. Completed: " + this.getCompletedPages() + "/" + this.getTotalPages() + " Total result: " + this.getBeatmapIndexed());
                pb.setValue((int) ((float) this.getCompletedPages() / this.getTotalPages() * 100));
                return true;
            }
            
            @Override
            public boolean onError() {
                lblStatus.setText("Status: Errored!");
                lblStatus.setForeground(Color.RED);
                JOptionPane.showMessageDialog(SearchingProgressDialog.this, "Error occurred! Please check error dumps under \"osumer2 -> View Dumps\"", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return false;
            }
            
            @Override
            public boolean onComplete() {
                lblStatus.setText("Status: Completed. Total result: " + this.getBeatmapIndexed());
                return true;
            }
        };
        
        lblStatus = new JLabel("Status: Waiting to start...");
        lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        pb = new JProgressBar();
        pb.setIndeterminate(true);
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(pb, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                        .addComponent(lblStatus, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblStatus)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(pb, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(43, Short.MAX_VALUE))
        );
        getContentPane().setLayout(groupLayout);

    }
    
    public JLabel getLabel(){
        return lblStatus;
    }

    public SearchingProgressHandler getHandler() {
        return handler;
    }
}
