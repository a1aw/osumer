package com.github.mob41.osumer.ui;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.apache.commons.codec.binary.Base64;

import com.github.mob41.osums.io.beatmap.ResultBeatmap;

import java.awt.Color;
import java.awt.Font;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import javax.swing.LayoutStyle.ComponentPlacement;

public class ResultBeatmapCell extends JPanel {
    private JLabel lblImg;
    private JLabel lblName;
    private JLabel lblCreator;
    private JLabel lblTags;
    private JLabel lblMoreDetails;
    private JLabel lblStatus;
    
    private ResultBeatmap lastMap;
    private Thread thread;
    
    private boolean alive = false;

    /**
     * Create the panel.
     */
    public ResultBeatmapCell() {
        
        lblImg = new JLabel("Loading thumb...");
        lblImg.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        
        lblName = new JLabel("---");
        lblName.setFont(new Font("Tahoma", Font.PLAIN, 18));
        
        lblCreator = new JLabel("---");
        lblCreator.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        lblTags = new JLabel("Tags:");
        lblTags.setFont(new Font("Tahoma", Font.PLAIN, 12));
        
        lblStatus = new JLabel("---");
        lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        
        lblMoreDetails = new JLabel("Click to see more details.");
        lblMoreDetails.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblMoreDetails.setHorizontalAlignment(SwingConstants.CENTER);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblImg, GroupLayout.PREFERRED_SIZE, 166, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblCreator, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                        .addComponent(lblName, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                        .addComponent(lblTags, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                        .addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                        .addComponent(lblMoreDetails, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(lblName)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblCreator)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(lblTags)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblStatus)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblMoreDetails))
                        .addComponent(lblImg, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
                    .addContainerGap())
        );
        setLayout(groupLayout);

    }

    public void updateData(ResultBeatmap rb) {
        if (!rb.equals(lastMap)){
            lastMap = rb;
            
            lblImg.setIcon(null);

            lblImg.setText("Waiting...");
            lblImg.setText("");
            
            lblName.setText(decode(rb.getArtist()) + " - " + decode(rb.getTitle()));
            lblCreator.setText(decode(rb.getCreator()));
            lblStatus.setText("Status not implemented");
            
            String tagsStr = "";
            String[] tags = rb.getTags();
            for (int i = 0; i < tags.length; i++){
                tagsStr += tags[i];
                
                if (i != tags.length - 1){
                    tagsStr += ", ";
                }
            }
            
            lblTags.setText("Tags: " + tagsStr);
            
            if (rb.getThumbData().isEmpty()){
                try {
                    lblImg.setForeground(Color.BLACK);
                    lblImg.setText("Loading thumb...");
                    
                    URL url = new URL(rb.getThumbUrl());
                    URLConnection conn = url.openConnection();
                    
                    conn.setConnectTimeout(100);
                    conn.setReadTimeout(100);
                    
                    lblImg.setText("");
                    lblImg.setIcon(new ImageIcon(ImageIO.read(conn.getInputStream())));
                } catch (Exception e){
                    lblImg.setForeground(Color.RED);
                    lblImg.setText("Thumb Fetch Error");
                }
            } else {
                lblImg.setIcon(new ImageIcon(Base64.decodeBase64(rb.getThumbData())));
            }
        }
    }
    
    private static String decode(String str){
        str = decodeUtf8(str);
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static String decodeUtf8(String str){
        int old = str.length();
        System.out.println("Old len: " + str.length() + ": " + str);
        for (int i = 0; i < str.length(); i++){
            if (str.substring(i).startsWith("%u")){
                String intStr = str.substring(i + 2, i + 6);
                int codePoint = -1;
                try {
                    codePoint = Integer.parseInt(intStr, 16);
                } catch (NumberFormatException e){
                    e.printStackTrace();
                    System.out.println("Error prasing UTF8!");
                    return null;
                }
                str = str.substring(0, i) + ((char) codePoint) + str.substring(i + 6, str.length());
            }
        }
        System.out.println("New len: " + str.length() + ": " + str);
        if (old != str.length()){
            System.out.println("Modified!");
        }
        return str;
    }

}
