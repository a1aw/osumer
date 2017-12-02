package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.github.mob41.osums.io.beatmap.Osums;

import javax.swing.event.ListSelectionEvent;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.event.ActionEvent;

public class MultiDownloadDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextArea textArea;

    /**
     * Create the dialog.
     */
    public MultiDownloadDialog(UIFrame frame) {
        setTitle("Download multiple beatmaps");
        setBounds(100, 100, 459, 379);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JScrollPane scrollPane = new JScrollPane();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            {
                textArea = new JTextArea();
                scrollPane.setViewportView(textArea);
            }
            {
                JLabel lblPleasePasteBeatmap = new JLabel("Please paste beatmap URLs separated each line. (#@// for comment):");
                scrollPane.setColumnHeaderView(lblPleasePasteBeatmap);
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String text = textArea.getText();
                        String[] lines = text.split("\n");
                        boolean valid = true;
                        for (int i = 0; i < lines.length; i++) {
                            String line = lines[i];
                            if (line.startsWith("#@//")) {
                                continue;
                            }

                            System.out.println(i + ": [" + line + "]");
                            String urlText = null;
                            int commentIndex = line.indexOf("#@//");
                            if (commentIndex != -1) {
                                urlText = line.substring(0, commentIndex);
                            } else {
                                urlText = line;
                            }
                            System.out.println(i + ": [" + urlText + "]");
                            try {
                                new URL(urlText);
                            } catch (MalformedURLException e1) {
                                valid = false;
                                e1.printStackTrace();
                                System.out.println("Invalid URL");
                                break;
                            }
                            
                            if (!Osums.isVaildBeatMapUrl(urlText)) {
                                valid = false;
                                System.out.println("Invalid Beatmap");
                                break;
                            }
                        }
                        
                        if (!valid) {
                            JOptionPane.showMessageDialog(MultiDownloadDialog.this, "The URL syntax is invalid or the URL provided is not a song/beatmap URL.\nPlease correct it to continue.", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            int added = 0;
                            for (int i = 0; i < lines.length; i++) {
                                if (lines[i].startsWith("#@//")) {
                                    continue;
                                }
                                frame.addBtQueue(lines[i], false);
                                added++;
                            }
                            JOptionPane.showMessageDialog(MultiDownloadDialog.this, added + " songs/beatmaps have been added to queue.", "Info", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        }
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (!textArea.getText().isEmpty()) {
                            if (JOptionPane.showOptionDialog(MultiDownloadDialog.this, "You have data in the field.\nAre you sure to leave?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                                dispose();
                            }
                        } else {
                            dispose();
                        }
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

}
