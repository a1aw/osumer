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
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import javax.swing.ImageIcon;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Legacy_OldSiteParsingDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public Legacy_OldSiteParsingDialog() {
        setTitle("osumer Legacy Mode Warning");
        setBounds(100, 100, 616, 538);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        JLabel lblWarning = new JLabel("- Warning -");
        lblWarning.setForeground(Color.RED);
        lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
        lblWarning.setFont(new Font("Tahoma", Font.BOLD, 16));
        
        JScrollPane scrollPane = new JScrollPane();
        
        JLabel lblPleaseFollowThe = new JLabel("Please follow the following steps to enable legacy old-site support on your osumer.");
        lblPleaseFollowThe.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblOpenpreferences = new JLabel("1. Open \"Preferences\" and go to \"(*) Legacy Old-site Beatmap\"");
        
        JPanel panel = new JPanel();
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        JLabel lblTickenable = new JLabel("2. Tick \"Enable redirect to old-site automatically\"");
        
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        
        JLabel lblYouCan = new JLabel("3. You can disable this message to show in startup by ticking the following.");
        
        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
            gl_contentPanel.createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(scrollPane, Alignment.TRAILING)
                        .addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                        .addComponent(lblWarning, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                        .addComponent(lblPleaseFollowThe, GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                        .addComponent(lblOpenpreferences)
                        .addComponent(panel, GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                        .addComponent(lblTickenable, GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                        .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 558, Short.MAX_VALUE)
                        .addComponent(lblYouCan, GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE))
                    .addContainerGap())
        );
        gl_contentPanel.setVerticalGroup(
            gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblWarning)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblPleaseFollowThe)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblOpenpreferences)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblTickenable)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblYouCan)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(37, Short.MAX_VALUE))
        );
        
        JLabel lblImg3 = new JLabel("");
        lblImg3.setIcon(new ImageIcon(Legacy_OldSiteParsingDialog.class.getResource("/com/github/mob41/osumer/ui/legacyoldsite_3.PNG")));
        lblImg3.setHorizontalAlignment(SwingConstants.CENTER);
        panel_2.add(lblImg3);
        
        JLabel lblImg2 = new JLabel("");
        lblImg2.setHorizontalAlignment(SwingConstants.CENTER);
        lblImg2.setIcon(new ImageIcon(Legacy_OldSiteParsingDialog.class.getResource("/com/github/mob41/osumer/ui/legacyoldsite_2.PNG")));
        panel_1.add(lblImg2);
        
        JLabel lblImg1 = new JLabel("");
        lblImg1.setHorizontalAlignment(SwingConstants.CENTER);
        lblImg1.setIcon(new ImageIcon(Legacy_OldSiteParsingDialog.class.getResource("/com/github/mob41/osumer/ui/legacyoldsite_1.PNG")));
        panel.add(lblImg1);
        
        JTextArea txtrAsTheOsu = new JTextArea();
        txtrAsTheOsu.setFont(new Font("Tahoma", Font.BOLD, 14));
        txtrAsTheOsu.setLineWrap(true);
        txtrAsTheOsu.setText("As the osu! new-site is going to be used, the existing old-site parser in osumer WILL NOT WORK since 2.0.0-snapshot-b3 and leading the application to stop working. This is an emergency update to redirect beatmaps back to old-site URLs.\r\n\r\nYou will see this message if legacy old-site is disabled\r\nThis message will show in every startup. You can disable this in Preferences.");
        txtrAsTheOsu.setBackground(SystemColor.control);
        txtrAsTheOsu.setEditable(false);
        scrollPane.setViewportView(txtrAsTheOsu);
        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        dispose();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }
    }
}
