package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Color;
import java.awt.Desktop;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class GettingStartedDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public GettingStartedDialog() {
        setTitle("Getting Started");
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        JLabel lblWelcomeToV = new JLabel("Welcome to v2!");
        lblWelcomeToV.setFont(new Font("Tahoma", Font.PLAIN, 21));
        lblWelcomeToV.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lblClickHereFor = new JLabel("[ Click here for tutorial/more details. ]");
        lblClickHereFor.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                try {
                    Desktop.getDesktop().browse(new URL("https://github.com/mob41/osumer/wiki/osumer-tutorial").toURI());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        lblClickHereFor.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblClickHereFor.setForeground(Color.BLUE);
        lblClickHereFor.setHorizontalAlignment(SwingConstants.CENTER);
        
        JScrollPane scrollPane = new JScrollPane();
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                        .addComponent(lblClickHereFor, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                        .addComponent(lblWelcomeToV, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                    .addContainerGap())
        );
        gl_contentPanel.setVerticalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblWelcomeToV)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblClickHereFor))
        );
        
        JTextArea txtrWelcomeToOsumer = new JTextArea();
        txtrWelcomeToOsumer.setEditable(false);
        txtrWelcomeToOsumer.setFont(new Font("Tahoma", Font.PLAIN, 13));
        txtrWelcomeToOsumer.setLineWrap(true);
        txtrWelcomeToOsumer.setText("Welcome to osumer2! The more powerful version of osumer comparing to v1.\r\n\r\nThis \"Getting Started\" is not really a getting started. In the future, this will be panels with pictures as tutorials.\r\n\r\nYou are running a pretty experimental SNAPSHOT version 2.x.x-snapshot-bx. It is only for testing features purpose. Please keep updating to the latest version. Bugs are expected, and do not hesitate to help reporting them at:\r\nhttps://github.com/mob41/osumer/issues/new\r\n\r\nThis version of osumer mainly adds queuing, background downloading, download sound effects, beatmap searching and managing. As offline guide is not implemented, please refer to the online tutorial page for more details:\r\nhttps://github.com/mob41/osumer/wiki/osumer-tutorial\r\n\r\nThank you for using this software! I would be happy if you can put a star in my GitHub project page:\r\nhttps://github.com/mob41/osumer\r\n\r\nRegards,\r\nmob41");
        txtrWelcomeToOsumer.setCaretPosition(0);
        scrollPane.setViewportView(txtrWelcomeToOsumer);
        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            
            JLabel lblYouCanDisable = new JLabel("You can disable this getting started in Preferences.");
            buttonPane.add(lblYouCanDisable);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }
    }
}
