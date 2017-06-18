/*******************************************************************************
 * Any modification, copies of sections of this file must be attached with this
 * license and shown clearly in the developer's project. The code can be used
 * as long as you state clearly you do not own it. Any violation might result in
 *  a take-down.
 *
 * MIT License
 *
 * Copyright (c) 2016, 2017 Anthony Law
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
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import com.github.mob41.osumer.exceptions.DebugDump;

public class ErrorDumpDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 6751710846617598242L;
    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public ErrorDumpDialog(DebugDump debugDump) {
        setTitle("Unexpected Error");
        setModal(true);
        setResizable(false);
        setBounds(100, 100, 505, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel lblOsumerHasJust = new JLabel("osumer has just encountered an unexpected error.");
        lblOsumerHasJust.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblPleasePostA = new JLabel("Please post a new issue in osumer's GitHub project page. ");
        JLabel lblPleaseAlsoInclude = new JLabel(
                "Please also include the following dump output or the saved dump file (.osmerdump)");
        lblPleaseAlsoInclude.setHorizontalAlignment(SwingConstants.CENTER);
        lblPleaseAlsoInclude.setFont(new Font("PMingLiU", Font.BOLD, 12));
        JLabel lblclickHereTo = new JLabel("[Click here to link to page]");
        lblclickHereTo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent arg0) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/mob41/osumer/issues/new"));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
        lblclickHereTo.setFont(new Font("PMingLiU", Font.BOLD | Font.ITALIC, 12));
        lblclickHereTo.setForeground(Color.BLUE);
        lblclickHereTo.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lblDebugDumpInfo = new JLabel("Debug dump info:");

        JScrollPane scrollPane = new JScrollPane();
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel
                .setHorizontalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPanel.createSequentialGroup().addContainerGap()
                                .addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                                        .addGroup(gl_contentPanel.createSequentialGroup()
                                                .addComponent(lblOsumerHasJust, GroupLayout.DEFAULT_SIZE, 414,
                                                        Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addComponent(lblPleaseAlsoInclude, GroupLayout.DEFAULT_SIZE, 467,
                                                Short.MAX_VALUE)
                                        .addGroup(gl_contentPanel.createSequentialGroup().addComponent(lblPleasePostA)
                                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(
                                                        lblclickHereTo, GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE))
                                        .addComponent(lblDebugDumpInfo, GroupLayout.DEFAULT_SIZE, 467,
                                                Short.MAX_VALUE))));
        gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPanel.createSequentialGroup().addContainerGap().addComponent(lblOsumerHasJust)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE).addComponent(lblPleasePostA)
                                .addComponent(lblclickHereTo))
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(lblPleaseAlsoInclude)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(lblDebugDumpInfo)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)));

        JTextArea textArea = new JTextArea();
        textArea.setText(debugDump != null ? debugDump.toString() : "Debug dump is null");
        textArea.setBackground(SystemColor.control);
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);
        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("Ignore");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Save dump");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        if (debugDump == null) {
                            JOptionPane.showMessageDialog(ErrorDumpDialog.this,
                                    "\"debugDump\" is null, cannot save dump with null", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        chooser.setFileFilter(new FileFilter() {

                            @Override
                            public boolean accept(File arg0) {
                                if (arg0 == null) {
                                    return false;
                                }

                                if (arg0.isDirectory()) {
                                    return true;
                                }

                                String str = arg0.getName();
                                final String ext = ".txt";

                                if (str.length() < ext.length()) {
                                    return false;
                                }

                                return str.endsWith(ext);
                            }

                            @Override
                            public String getDescription() {
                                return ".txt (osumer dump files)";
                            }

                        });
                        chooser.setSelectedFile(new File("osumer-debugDump-" + debugDump.getGenerated() + ".txt"));

                        int option = chooser.showSaveDialog(ErrorDumpDialog.this);

                        if (option == JFileChooser.CANCEL_OPTION) {
                            return;
                        }

                        File file = chooser.getSelectedFile();
                        if (file.exists()) {
                            file.delete();
                        }
                        try {
                            file.createNewFile();
                            FileOutputStream out = new FileOutputStream(file);
                            PrintWriter writer = new PrintWriter(out, true);
                            writer.println(debugDump.toString());
                            writer.flush();
                            writer.close();
                            out.close();
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(ErrorDumpDialog.this, "Error saving dump:\n" + e, "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        JOptionPane.showMessageDialog(ErrorDumpDialog.this,
                                "Saved dump to location:\n" + file.getAbsolutePath(), "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                buttonPane.add(cancelButton);
            }
        }
    }
}
