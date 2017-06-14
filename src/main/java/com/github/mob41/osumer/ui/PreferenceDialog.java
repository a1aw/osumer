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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import javax.swing.JTable;

public class PreferenceDialog extends JDialog {

    private final UIManager.LookAndFeelInfo[] infos;
    private final JPanel contentPanel = new JPanel();
    private JComboBox<String> uiSkinBox;
    private JRadioButton rdbtnStable;
    private JRadioButton rdbtnBeta;
    private JRadioButton rdbtnSnapshot;

    /**
     * Create the dialog.
     */
    public PreferenceDialog() {
        setTitle("osumer2 Preferences");
        setBounds(100, 100, 811, 545);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP);
            contentPanel.add(tab, BorderLayout.CENTER);

            JPanel mainPanel = new JPanel();
            tab.addTab("Main", null, mainPanel, null);

            JPanel panel_8 = new JPanel();
            panel_8.setBorder(
                    new TitledBorder(null, "Getting started", TitledBorder.LEADING, TitledBorder.TOP, null, null));

            JLabel lblUiSkin = new JLabel("UI Skin:");

            uiSkinBox = new JComboBox<String>();

            infos = UIManager.getInstalledLookAndFeels();
            for (int i = 0; i < infos.length; i++) {
                UIManager.LookAndFeelInfo info = infos[i];
                uiSkinBox.addItem(info.getName());
            }

            JButton btnApply = new JButton("Apply");
            btnApply.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int index = uiSkinBox.getSelectedIndex();

                    if (index != -1) {
                        try {
                            UIManager.setLookAndFeel(infos[index].getClassName());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(PreferenceDialog.this, "Unable to set look and feel:\n" + e1,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            });
            GroupLayout gl_mainPanel = new GroupLayout(mainPanel);
            gl_mainPanel.setHorizontalGroup(gl_mainPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_mainPanel.createSequentialGroup().addContainerGap()
                            .addGroup(gl_mainPanel.createParallelGroup(Alignment.LEADING)
                                    .addComponent(panel_8, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                    .addGroup(gl_mainPanel.createSequentialGroup().addComponent(lblUiSkin)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(uiSkinBox, GroupLayout.PREFERRED_SIZE, 215,
                                                    GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnApply)))
                            .addContainerGap()));
            gl_mainPanel.setVerticalGroup(gl_mainPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_mainPanel.createSequentialGroup().addContainerGap()
                            .addComponent(panel_8, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_mainPanel.createParallelGroup(Alignment.BASELINE).addComponent(lblUiSkin)
                                    .addComponent(uiSkinBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                            GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnApply))
                            .addContainerGap(255, Short.MAX_VALUE)));

            JLabel lblWelcomeToOsumer = new JLabel("Welcome to osumer2!");
            lblWelcomeToOsumer.setHorizontalAlignment(SwingConstants.CENTER);
            lblWelcomeToOsumer.setFont(new Font("Tahoma", Font.BOLD, 25));

            JLabel lblIfYouAre = new JLabel(
                    "If you are new to here, or old users from v1, please take a short look of the Getting Started guide!");
            lblIfYouAre.setHorizontalAlignment(SwingConstants.CENTER);
            lblIfYouAre.setFont(new Font("Tahoma", Font.PLAIN, 11));

            JCheckBox chckbxShowGettingStarted = new JCheckBox("Show getting started v2.0.0 on startup");

            JButton btnShowGuide_1 = new JButton("Show Guide");
            GroupLayout gl_panel_8 = new GroupLayout(panel_8);
            gl_panel_8.setHorizontalGroup(gl_panel_8.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_8.createSequentialGroup().addContainerGap()
                            .addGroup(gl_panel_8.createParallelGroup(Alignment.LEADING)
                                    .addComponent(lblIfYouAre, GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
                                    .addComponent(lblWelcomeToOsumer, GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
                                    .addComponent(chckbxShowGettingStarted, GroupLayout.DEFAULT_SIZE,
                                            728, Short.MAX_VALUE)
                                    .addComponent(btnShowGuide_1, GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE))
                            .addContainerGap()));
            gl_panel_8.setVerticalGroup(gl_panel_8.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_8.createSequentialGroup()
                            .addComponent(lblWelcomeToOsumer, GroupLayout.PREFERRED_SIZE, 52,
                                    GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(lblIfYouAre)
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxShowGettingStarted)
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnShowGuide_1)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
            panel_8.setLayout(gl_panel_8);
            mainPanel.setLayout(gl_mainPanel);
            {
                JPanel panel = new JPanel();
                tab.addTab("osumerExpress", null, panel, null);

                JPanel panel_1 = new JPanel();
                panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                        "Server Priority (osumsSrv or plugins)", TitledBorder.LEADING, TitledBorder.TOP, null,
                        new Color(0, 0, 0)));

                JPanel panel_2 = new JPanel();
                panel_2.setBorder(new TitledBorder(null, "osu! Account Credentials", TitledBorder.LEADING,
                        TitledBorder.TOP, null, null));

                JPanel panel_3 = new JPanel();
                panel_3.setBorder(new TitledBorder(null, "osumerExpress Installation Status (Browser Hooker)",
                        TitledBorder.LEADING, TitledBorder.TOP, null, null));

                JCheckBox chckbxDisabledredirectAll = new JCheckBox("Disabled (Redirect all URLs to browser directly)");

                JCheckBox chckbxRunAsDaemon = new JCheckBox("Run as daemon on Windows startup (-daemon)");

                JCheckBox chckbxDisabledDaemon = new JCheckBox(
                        "Daemon Disabled (Uses traditional v1 start-on-command instead of daemon socket call, comparatively slower)");
                GroupLayout gl_panel = new GroupLayout(panel);
                gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel.createSequentialGroup().addContainerGap()
                                .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                                        .addComponent(panel_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 760,
                                                Short.MAX_VALUE)
                                        .addGroup(gl_panel.createSequentialGroup()
                                                .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 289,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addGap(18)
                                                .addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE))
                                        .addComponent(chckbxDisabledredirectAll, GroupLayout.DEFAULT_SIZE, 760,
                                                Short.MAX_VALUE)
                                        .addComponent(chckbxRunAsDaemon, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                        .addComponent(chckbxDisabledDaemon, GroupLayout.DEFAULT_SIZE, 760,
                                                Short.MAX_VALUE))
                                .addContainerGap()));
                gl_panel.setVerticalGroup(
                        gl_panel.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel.createSequentialGroup().addContainerGap()
                                        .addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
                                                .addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 150,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(chckbxDisabledredirectAll)
                                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxRunAsDaemon)
                                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxDisabledDaemon)
                                        .addGap(76)));

                JLabel lblStatus = new JLabel("Status:");

                JLabel lblUnknown = new JLabel("Unknown");

                JLabel lblPleaseSetosumerexpress = new JLabel(
                        "Hint: Please set \"osumerExpress\" as default browser in order to receive beatmap URLs.");

                JButton btnShowGuide = new JButton("Show Guide");

                JLabel lblPleaseRestartOsumer = new JLabel(
                        "Please restart osumer2 with administrative priviledges to have un/installations ");
                lblPleaseRestartOsumer.setForeground(Color.RED);
                lblPleaseRestartOsumer.setHorizontalAlignment(SwingConstants.CENTER);

                JButton btnInstallOsumerexpress = new JButton("Install osumerExpress, osumer2 daemon");
                GroupLayout gl_panel_3 = new GroupLayout(panel_3);
                gl_panel_3.setHorizontalGroup(gl_panel_3.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel_3
                        .createSequentialGroup().addContainerGap()
                        .addGroup(gl_panel_3.createParallelGroup(Alignment.TRAILING)
                                .addComponent(btnInstallOsumerexpress, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 722,
                                        Short.MAX_VALUE)
                                .addComponent(lblPleaseRestartOsumer, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 722,
                                        Short.MAX_VALUE)
                                .addGroup(Alignment.LEADING,
                                        gl_panel_3.createSequentialGroup()
                                                .addComponent(lblPleaseSetosumerexpress, GroupLayout.DEFAULT_SIZE, 609,
                                                        Short.MAX_VALUE)
                                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnShowGuide))
                                .addGroup(Alignment.LEADING,
                                        gl_panel_3.createSequentialGroup().addComponent(lblStatus)
                                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(lblUnknown,
                                                        GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)))
                        .addContainerGap()));
                gl_panel_3.setVerticalGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_3.createSequentialGroup()
                                .addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE).addComponent(lblStatus)
                                        .addComponent(lblUnknown))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblPleaseSetosumerexpress).addComponent(btnShowGuide))
                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(lblPleaseRestartOsumer)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(btnInstallOsumerexpress, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                                .addContainerGap()));
                panel_3.setLayout(gl_panel_3);

                JButton btnAdd = new JButton("Add");

                JButton btnRmv = new JButton("Rmv");

                JButton btnUp = new JButton("Up");

                JButton btnDown = new JButton("Down");

                JScrollPane scrollPane = new JScrollPane();
                GroupLayout gl_panel_1 = new GroupLayout(panel_1);
                gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_1.createSequentialGroup().addContainerGap()
                                .addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING, false)
                                        .addComponent(btnAdd, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnRmv, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING, false)
                                        .addComponent(btnUp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(btnDown, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE))
                                .addContainerGap()));
                gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_1.createSequentialGroup()
                                .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE).addComponent(btnAdd)
                                        .addComponent(btnUp))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE).addComponent(btnRmv)
                                        .addComponent(btnDown))
                                .addContainerGap(45, Short.MAX_VALUE))
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE));

                JList list = new JList();
                list.setModel(new AbstractListModel() {
                    String[] values = new String[] { "osu! forum" };

                    public int getSize() {
                        return values.length;
                    }

                    public Object getElementAt(int index) {
                        return values[index];
                    }
                });
                scrollPane.setViewportView(list);
                panel_1.setLayout(gl_panel_1);

                JLabel lblLoggedInAs = new JLabel("Will be logged in as: Not logged in");

                JButton btnClickHereTo = new JButton("Add/Change credentials");
                btnClickHereTo.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        ChgCredentialsDialog dialog = new ChgCredentialsDialog();
                        dialog.setLocationRelativeTo(PreferenceDialog.this);
                        dialog.setModal(true);
                        dialog.setVisible(true);

                        if (dialog.isUseCredentials()) {
                            lblLoggedInAs.setForeground(Color.BLUE);
                            lblLoggedInAs.setText("Will be logged in as: " + dialog.getUsername());
                        }
                    }
                });

                JButton btnRemoveCredentials = new JButton("Remove credentials");
                GroupLayout gl_panel_2 = new GroupLayout(panel_2);
                gl_panel_2.setHorizontalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
                        .addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup().addContainerGap()
                                .addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(lblLoggedInAs, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 262,
                                                Short.MAX_VALUE)
                                        .addComponent(btnRemoveCredentials, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
                                                262, Short.MAX_VALUE)
                                        .addComponent(btnClickHereTo, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 262,
                                                Short.MAX_VALUE))
                                .addContainerGap()));
                gl_panel_2
                        .setVerticalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panel_2.createSequentialGroup().addContainerGap()
                                        .addComponent(lblLoggedInAs, GroupLayout.PREFERRED_SIZE, 19,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(btnClickHereTo, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnRemoveCredentials,
                                                GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                                        .addContainerGap()));
                panel_2.setLayout(gl_panel_2);
                panel.setLayout(gl_panel);
            }

            JPanel panel = new JPanel();
            tab.addTab("Queuing", null, panel, null);

            JLabel lblMaximumDownloadingThread = new JLabel("Maximum Downloading Thread:");
            lblMaximumDownloadingThread.setHorizontalAlignment(SwingConstants.RIGHT);

            JSpinner spinner = new JSpinner();

            JLabel lblNextQueuingCheck = new JLabel("Next Queuing Check Delay:");
            lblNextQueuingCheck.setHorizontalAlignment(SwingConstants.RIGHT);

            JSpinner spinner_1 = new JSpinner();

            JLabel lblMs = new JLabel("ms");

            JLabel lblThreads = new JLabel("thread(s)");

            JCheckBox chckbxEnableMassiveDownloading = new JCheckBox(
                    "Enable massive downloading threads of having more than 8 threads (Requires liability agreement)");
            chckbxEnableMassiveDownloading.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (chckbxEnableMassiveDownloading.isSelected()) {
                        int option = JOptionPane.showOptionDialog(PreferenceDialog.this,
                                "By enabling this, it is believed that you have known,\n"
                                        + " this program, using a liability-holdless MIT License,\n"
                                        + " agreed with holding no liability with the developer.\n"
                                        + "And, moreover, not to abuse this feature to affect the\n"
                                        + " service quality of \"osu!\" company. Any violation may\n"
                                        + " get yourself into a lawsuit.\n\nAre you sure to continue?",
                                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, 0);
                        if (option != JOptionPane.YES_OPTION) {
                            chckbxEnableMassiveDownloading.setSelected(false);
                        }
                    }
                }
            });
            GroupLayout gl_panel = new GroupLayout(panel);
            gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(
                    Alignment.LEADING)
                    .addGroup(gl_panel.createSequentialGroup().addContainerGap().addGroup(gl_panel
                            .createParallelGroup(Alignment.LEADING).addGroup(gl_panel
                                    .createSequentialGroup().addGroup(gl_panel
                                            .createParallelGroup(Alignment.TRAILING, false)
                                            .addComponent(lblNextQueuingCheck, Alignment.LEADING,
                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblMaximumDownloadingThread, Alignment.LEADING,
                                                    GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE))
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
                                            .addComponent(spinner).addComponent(spinner_1, GroupLayout.DEFAULT_SIZE, 82,
                                                    Short.MAX_VALUE))
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING).addComponent(lblMs)
                                            .addComponent(lblThreads)))
                            .addComponent(chckbxEnableMassiveDownloading)).addContainerGap(486, Short.MAX_VALUE)));
            gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
                    .createSequentialGroup().addContainerGap()
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(lblMaximumDownloadingThread)
                            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblThreads))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(lblNextQueuingCheck)
                            .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                    GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMs))
                    .addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxEnableMassiveDownloading)
                    .addContainerGap(349, Short.MAX_VALUE)));
            panel.setLayout(gl_panel);

            JPanel panel_1 = new JPanel();
            tab.addTab("Updater", null, panel_1, null);

            JPanel panel_2 = new JPanel();
            panel_2.setBorder(new TitledBorder(null, "Frequency on checking updates", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));

            JPanel panel_3 = new JPanel();
            panel_3.setBorder(
                    new TitledBorder(null, "Update branch", TitledBorder.LEADING, TitledBorder.TOP, null, null));

            JPanel panel_4 = new JPanel();
            panel_4.setBorder(
                    new TitledBorder(null, "Comparison algorithm", TitledBorder.LEADING, TitledBorder.TOP, null, null));

            JCheckBox chckbxAutomaticallyAcceptCritical = new JCheckBox("Automatically accept critical updates");

            JCheckBox chckbxDownloadAndApply = new JCheckBox("Download and apply patches automatically");

            JButton btnCheckUpdateNow = new JButton("Check update now");

            JPanel panel_5 = new JPanel();
            panel_5.setBorder(
                    new TitledBorder(null, "Down-grade software", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GroupLayout gl_panel_1 = new GroupLayout(panel_1);
            gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
                    gl_panel_1.createSequentialGroup().addContainerGap().addGroup(gl_panel_1
                            .createParallelGroup(Alignment.TRAILING)
                            .addComponent(panel_5, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                            .addComponent(chckbxAutomaticallyAcceptCritical, Alignment.LEADING,
                                    GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
                            .addComponent(panel_4, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                            .addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                            .addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                            .addComponent(chckbxDownloadAndApply, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760,
                                    Short.MAX_VALUE)
                            .addComponent(btnCheckUpdateNow, Alignment.LEADING)).addContainerGap()));
            gl_panel_1
                    .setVerticalGroup(
                            gl_panel_1.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_panel_1.createSequentialGroup().addContainerGap()
                                            .addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 55,
                                                    GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                    GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 52,
                                                    GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(chckbxAutomaticallyAcceptCritical)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(chckbxDownloadAndApply)
                                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnCheckUpdateNow)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(panel_5, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                                            .addContainerGap()));

            JLabel lblWarningDowngradingOsumer = new JLabel(
                    "WARNING: Downgrading osumer may result in function loss or ocurrance of bugs.");
            lblWarningDowngradingOsumer.setForeground(Color.RED);
            lblWarningDowngradingOsumer.setFont(new Font("PMingLiU", Font.BOLD, 12));

            JLabel lblSelectVersion = new JLabel("Select version:");

            JComboBox comboBox = new JComboBox();

            JButton btnShowChangelog = new JButton("Show change-log");

            JButton btnDowngrade = new JButton("Down-grade");

            JLabel lbldowngradingToVersions = new JLabel(
                    "*Downgrading to versions older than 1.0.0-snapshot-b1 will loss updater functions.");

            JLabel lbldisableAutoInstall = new JLabel(
                    "***Disable auto install patches and critical updates if install older v2.x.x series");
            GroupLayout gl_panel_5 = new GroupLayout(panel_5);
            gl_panel_5.setHorizontalGroup(gl_panel_5.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_5
                    .createSequentialGroup().addContainerGap()
                    .addGroup(gl_panel_5.createParallelGroup(Alignment.LEADING)
                            .addComponent(btnDowngrade, GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
                            .addComponent(lbldowngradingToVersions, GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
                            .addComponent(lblWarningDowngradingOsumer, GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
                            .addGroup(gl_panel_5.createSequentialGroup().addComponent(lblSelectVersion)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 501, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnShowChangelog, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                            .addComponent(lbldisableAutoInstall, GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE))
                    .addContainerGap()));
            gl_panel_5.setVerticalGroup(gl_panel_5.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_5.createSequentialGroup().addContainerGap()
                            .addComponent(lblWarningDowngradingOsumer).addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_panel_5.createParallelGroup(Alignment.BASELINE).addComponent(lblSelectVersion)
                                    .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                            GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnShowChangelog))
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnDowngrade)
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(lbldowngradingToVersions)
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(lbldisableAutoInstall)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
            panel_5.setLayout(gl_panel_5);

            JRadioButton rdbtnPerVersion = new JRadioButton("Per version in selected branch");
            panel_4.add(rdbtnPerVersion);

            JRadioButton rdbtnLatestVersion = new JRadioButton("Latest version (per branch)");
            panel_4.add(rdbtnLatestVersion);

            JRadioButton rdbtnLatestVersionoverall = new JRadioButton("Latest version (overall)");
            panel_4.add(rdbtnLatestVersionoverall);

            JRadioButton rdbtnStablity = new JRadioButton("Stablity");
            panel_4.add(rdbtnStablity);

            ButtonGroup updateCompareAlgoBtnGroup = new ButtonGroup();
            updateCompareAlgoBtnGroup.add(rdbtnPerVersion);
            updateCompareAlgoBtnGroup.add(rdbtnLatestVersion);
            updateCompareAlgoBtnGroup.add(rdbtnLatestVersionoverall);
            updateCompareAlgoBtnGroup.add(rdbtnStablity);

            rdbtnStable = new JRadioButton("Stable");
            panel_3.add(rdbtnStable);

            rdbtnBeta = new JRadioButton("Beta");
            panel_3.add(rdbtnBeta);

            rdbtnSnapshot = new JRadioButton("Snapshot");
            panel_3.add(rdbtnSnapshot);

            ButtonGroup updateBranchBtnGroup = new ButtonGroup();
            updateBranchBtnGroup.add(rdbtnStable);
            updateBranchBtnGroup.add(rdbtnBeta);
            updateBranchBtnGroup.add(rdbtnSnapshot);

            JRadioButton rdbtnEveryStartup = new JRadioButton("Every startup");
            panel_2.add(rdbtnEveryStartup);

            JRadioButton rdbtnEveryAct = new JRadioButton("Every download/request/activation");
            panel_2.add(rdbtnEveryAct);

            JRadioButton rdbtnNever = new JRadioButton("Never");
            panel_2.add(rdbtnNever);
            panel_1.setLayout(gl_panel_1);

            ButtonGroup updateFreqBtnGroup = new ButtonGroup();
            updateFreqBtnGroup.add(rdbtnEveryStartup);
            updateFreqBtnGroup.add(rdbtnEveryAct);
            updateFreqBtnGroup.add(rdbtnNever);

            JPanel panel_6 = new JPanel();
            tab.addTab("Security", null, panel_6, null);

            JPanel panel_7 = new JPanel();
            panel_7.setBorder(
                    new TitledBorder(null, "On-login password", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GroupLayout gl_panel_6 = new GroupLayout(panel_6);
            gl_panel_6
                    .setHorizontalGroup(gl_panel_6.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_panel_6.createSequentialGroup().addContainerGap()
                                    .addComponent(panel_7, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                    .addContainerGap()));
            gl_panel_6.setVerticalGroup(gl_panel_6.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_6.createSequentialGroup()
                            .addContainerGap().addComponent(panel_7, GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(340, Short.MAX_VALUE)));

            JCheckBox chckbxEnabledusedAs = new JCheckBox(
                    "Enabled (Used as encryption with account osu! username and password)");

            JCheckBox chckbxAskOnStartup = new JCheckBox("Ask on startup");
            GroupLayout gl_panel_7 = new GroupLayout(panel_7);
            gl_panel_7.setHorizontalGroup(gl_panel_7.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
                    gl_panel_7.createSequentialGroup().addContainerGap()
                            .addGroup(gl_panel_7.createParallelGroup(Alignment.TRAILING)
                                    .addComponent(chckbxAskOnStartup, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 736,
                                            Short.MAX_VALUE)
                                    .addComponent(chckbxEnabledusedAs, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 736,
                                            Short.MAX_VALUE))
                            .addContainerGap()));
            gl_panel_7.setVerticalGroup(gl_panel_7.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_7.createSequentialGroup().addContainerGap().addComponent(chckbxEnabledusedAs)
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxAskOnStartup)
                            .addContainerGap(8, Short.MAX_VALUE)));
            panel_7.setLayout(gl_panel_7);
            panel_6.setLayout(gl_panel_6);

            JPanel panel_9 = new JPanel();
            tab.addTab("Plugins", null, panel_9, null);

            JLabel lblWarningOnlyAdd = new JLabel(
                    "Warning: Only add plugins that you trust, or officially trusted by the developer.");
            lblWarningOnlyAdd.setForeground(Color.RED);
            lblWarningOnlyAdd.setFont(new Font("Tahoma", Font.BOLD, 12));
            GroupLayout gl_panel_9 = new GroupLayout(panel_9);
            gl_panel_9.setHorizontalGroup(gl_panel_9.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_9.createSequentialGroup().addContainerGap()
                            .addComponent(lblWarningOnlyAdd, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                            .addContainerGap()));
            gl_panel_9.setVerticalGroup(
                    gl_panel_9.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_9.createSequentialGroup()
                            .addContainerGap().addComponent(lblWarningOnlyAdd).addContainerGap(409, Short.MAX_VALUE)));
            panel_9.setLayout(gl_panel_9);

            JPanel panel_11 = new JPanel();
            tab.addTab("Miscellaneous", null, panel_11, null);

            JPanel panel_12 = new JPanel();
            panel_12.setBorder(new TitledBorder(null, "Event Handler Profile", TitledBorder.LEADING, TitledBorder.TOP,
                    null, null));
            GroupLayout gl_panel_11 = new GroupLayout(panel_11);
            gl_panel_11
                    .setHorizontalGroup(gl_panel_11.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_panel_11.createSequentialGroup().addContainerGap()
                                    .addComponent(panel_12, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                    .addContainerGap()));
            gl_panel_11.setVerticalGroup(gl_panel_11.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_11.createSequentialGroup()
                            .addContainerGap().addComponent(panel_12, GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(314, Short.MAX_VALUE)));

            JCheckBox chckbxEnableToneBefore = new JCheckBox("Enable tone before download");

            JCheckBox chckbxEnableToneOn = new JCheckBox("Enable tone on download started");

            JCheckBox chckbxEnableToneAfter = new JCheckBox("Enable tone after download");

            JButton btnSelect = new JButton("Select");

            JButton btnSelect_1 = new JButton("Select");

            JButton btnSelect_2 = new JButton("Select");
            GroupLayout gl_panel_12 = new GroupLayout(panel_12);
            gl_panel_12.setHorizontalGroup(gl_panel_12.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_12
                    .createSequentialGroup().addContainerGap()
                    .addGroup(gl_panel_12.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_panel_12.createSequentialGroup().addComponent(chckbxEnableToneBefore)
                                    .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnSelect))
                            .addGroup(gl_panel_12.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
                                    gl_panel_12.createSequentialGroup().addComponent(chckbxEnableToneAfter)
                                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnSelect_2))
                                    .addGroup(Alignment.LEADING,
                                            gl_panel_12.createSequentialGroup().addComponent(chckbxEnableToneOn)
                                                    .addPreferredGap(ComponentPlacement.RELATED)
                                                    .addComponent(btnSelect_1))))
                    .addGap(476)));
            gl_panel_12.setVerticalGroup(gl_panel_12.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_12.createSequentialGroup().addContainerGap()
                            .addGroup(gl_panel_12.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(chckbxEnableToneBefore).addComponent(btnSelect))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_panel_12.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(chckbxEnableToneOn).addComponent(btnSelect_1))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_panel_12.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(chckbxEnableToneAfter).addComponent(btnSelect_2))
                            .addContainerGap(11, Short.MAX_VALUE)));
            panel_12.setLayout(gl_panel_12);
            panel_11.setLayout(gl_panel_11);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("Save");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Apply");
                buttonPane.add(cancelButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }
}
