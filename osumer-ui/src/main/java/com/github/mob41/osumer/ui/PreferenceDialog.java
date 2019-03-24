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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.mob41.organdebug.DebugDump;
import com.github.mob41.organdebug.DumpManager;
import com.github.mob41.organdebug.exceptions.DebuggableException;
import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.Osumer;
import com.github.mob41.osumer.exceptions.NoBuildsForVersionException;
import com.github.mob41.osumer.exceptions.NoSuchBuildNumberException;
import com.github.mob41.osumer.exceptions.NoSuchVersionException;
import com.github.mob41.osumer.installer.Installer;
import com.github.mob41.osumer.updater.UpdateInfo;
import com.github.mob41.osumer.updater.Updater;

public class PreferenceDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 3143276952707175093L;
    
    private final Installer installer;
    private final Configuration config;
    private final UIManager.LookAndFeelInfo[] infos;
    private final JPanel contentPanel = new JPanel();
    private final DefaultListModel<String> serverProrityListModel;
    private final DefaultComboBoxModel<String> browserComboBoxModel;
    private final DefaultComboBoxModel<String> versionsComboBoxModel;
    private final SpinnerNumberModel queuingMaxThreadsSpinnerModel;
    private final SpinnerNumberModel queuingNextQueueCheckSpinnerModel;
    
    private JComboBox<String> uiSkinBox;
    private JCheckBox chckbxShowGettingStarted;
    private JList<String> serverProrityList;
    private JLabel lblLoggedInAs;
    private JCheckBox chckbxOeDisabled;
    private JCheckBox chckbxRunAsDaemon;
    private JCheckBox chckbxDisabledDaemon;
    private JComboBox<String> browsersBox;
    private JSpinner maxDwnThreadsSpinner;
    private JSpinner nextQueuingCheckDelaySpinner;
    private JCheckBox chckbxEnableMassiveDownloading;
    private JRadioButton rdbtnEveryStartup;
    private JRadioButton rdbtnEveryAct;
    private JRadioButton rdbtnNever;
    private JRadioButton rdbtnPerVersion;
    private JRadioButton rdbtnLatestVersion;
    private JRadioButton rdbtnLatestVersionoverall;
    private JRadioButton rdbtnStablity;
    private JCheckBox chckbxAutoPatches;
    private JCheckBox chckbxAutoCriticalUpdate;
    private JComboBox<String> versionsBox;
    private JCheckBox chckbxEnabledusedAs;
    private JCheckBox chckbxAskOnStartup;
    private JCheckBox chckbxToneBeforeDwn;
    private JCheckBox chckbxToneAfterDwn;
    private JRadioButton rdbtnStable;
    private JRadioButton rdbtnBeta;
    private JRadioButton rdbtnSnapshot;
    
    private final Updater updater;
    private JButton btnDowngrade;
    private JButton btnShowChangelog;
    private JTabbedPane tab;
    private JLabel lblStatusValue;
    private JButton btnInstallOsumerexpress;

    private UIFrame uiFrame;
    private JTextField downloadFolderField;
    private JRadioButton rdbtnPutDownloadsTo_1;
    private JRadioButton rdbtnPutDownloadsTo;
    private JRadioButton rdbtnLaunchOsuAutomatically;
    private JLabel lblDownloadFolder;
    private JButton btnDownloadLocSelect;
    private JLabel lblPleaseRestartOsumer;
    private JTextField toneBeforeField;
    private JTextField toneAfterField;
    private JLabel lblFileToneBefore;
    private JLabel lblFileToneAfter;
    private JButton btnSelectToneBefore;
    private JButton btnSelectToneAfter;
    private JCheckBox chckbxEnableRedirectTo;
    private JCheckBox chckbxDisableStartupNotification;
    
    /**
     * Create the dialog.
     */
    public PreferenceDialog(Configuration config, UIFrame uiFrame) {
        this.config = config;
        this.updater = new Updater(config);
        this.installer = new Installer();
        this.uiFrame = uiFrame;
        
        setTitle("osumer2 Preferences");
        setBounds(100, 100, 811, 545);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            tab = new JTabbedPane(JTabbedPane.TOP);
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

            chckbxShowGettingStarted = new JCheckBox("Show getting started v2.0.0 on startup");

            JButton btnShowGuide_1 = new JButton("Show Guide");
            btnShowGuide_1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    GettingStartedDialog gsd = new GettingStartedDialog();
                    gsd.setLocationRelativeTo(PreferenceDialog.this);
                    gsd.setModal(true);
                    gsd.setVisible(true);
                }
            });
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
                JPanel oePanel = new JPanel();
                tab.addTab("osumerExpress", null, oePanel, null);

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

                chckbxOeDisabled = new JCheckBox("Disabled (Redirect all URLs to browser directly)");

                chckbxRunAsDaemon = new JCheckBox("Run as daemon on Windows startup (-daemon)");
                chckbxRunAsDaemon.setEnabled(false);

                chckbxDisabledDaemon = new JCheckBox(
                        "Daemon Disabled (Uses traditional v1 start-on-command instead of daemon socket call, comparatively slower)");
                chckbxDisabledDaemon.setEnabled(false);
                
                JLabel lblSelectYourDefault = new JLabel("Select your default browser:");
                
                browserComboBoxModel = new DefaultComboBoxModel<String>();
                browsersBox = new JComboBox<String>(browserComboBoxModel);
                GroupLayout gl_oePanel = new GroupLayout(oePanel);
                gl_oePanel.setHorizontalGroup(
                    gl_oePanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_oePanel.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(gl_oePanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(chckbxDisabledDaemon, GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                                .addComponent(panel_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                .addGroup(gl_oePanel.createSequentialGroup()
                                    .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18)
                                    .addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE))
                                .addComponent(chckbxOeDisabled, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                .addComponent(chckbxRunAsDaemon, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                .addGroup(gl_oePanel.createSequentialGroup()
                                    .addComponent(lblSelectYourDefault)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(browsersBox, 0, 626, Short.MAX_VALUE)))
                            .addContainerGap())
                );
                gl_oePanel.setVerticalGroup(
                    gl_oePanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_oePanel.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(gl_oePanel.createParallelGroup(Alignment.TRAILING, false)
                                .addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(chckbxOeDisabled)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(chckbxRunAsDaemon)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(chckbxDisabledDaemon)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_oePanel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblSelectYourDefault)
                                .addComponent(browsersBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(59))
                );

                JLabel lblStatus = new JLabel("Status:");

                lblStatusValue = new JLabel("Unknown");

                JLabel lblPleaseSetosumerexpress = new JLabel(
                        "Hint: Please set \"osumerExpress\" as default browser in order to receive beatmap URLs.");

                JButton btnShowGuide = new JButton("Show Guide");
                btnShowGuide.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        GettingStartedDialog gsd = new GettingStartedDialog();
                        gsd.setLocationRelativeTo(PreferenceDialog.this);
                        gsd.setModal(true);
                        gsd.setVisible(true);
                    }
                });

                lblPleaseRestartOsumer = new JLabel(
                        "Please restart osumer2 with administrative priviledges to have un/installations ");
                lblPleaseRestartOsumer.setForeground(Color.RED);
                lblPleaseRestartOsumer.setHorizontalAlignment(SwingConstants.CENTER);

                btnInstallOsumerexpress = new JButton("Install osumerExpress, osumer2 daemon");
                btnInstallOsumerexpress.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        boolean installed = Installer.isInstalled();
                        
                        if (!installed) {
                            int option = JOptionPane.showOptionDialog(PreferenceDialog.this, "Are you sure to install?", "Install osumerExpress", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 1);
                            
                            if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.NO_OPTION) {
                                return;
                            }
                            
                            boolean useWebUpdater = false;
                            
                            if (!Osumer.isWindowsElevated()){
                                int option2 = JOptionPane.showOptionDialog(null, "Installation requires osumer to be elevated.\nosumer will use the web updater instead.\nAre you sure?", "Not elevated", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.YES_OPTION);
                                
                                if (option2 == JOptionPane.NO_OPTION){
                                    return;
                                }
                                
                                useWebUpdater = true;
                            }
                            
                            if (!useWebUpdater){
                                try {
                                    if (updater.isUpdateAvailable()){
                                        int option2 = JOptionPane.showOptionDialog(null, "osumer Update available!\nDo you want to launch the web updater\ninstead of installing the current version?", "Update available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.YES_OPTION);
                                        
                                        if (option2 == JOptionPane.YES_OPTION){
                                            useWebUpdater = true;
                                        }
                                    }
                                } catch (DebuggableException e1) {
                                    int option2 = JOptionPane.showOptionDialog(null, "osumer cannot check for updates for latest version.\nDo you still want to install the current version?", "Cannot check update", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.YES_OPTION);
                                    
                                    if (option2 != JOptionPane.YES_OPTION){
                                        return;
                                    }
                                }
                            }
                            
                            if (useWebUpdater){
                                try {
                                    String updaterLink = Updater.getUpdaterLink();
                                    
                                    if (updaterLink == null){
                                        System.out.println("No latest updater .exe defined! Falling back to legacy updater!");
                                        updaterLink = Updater.LEGACY_UPDATER_JAR;
                                    }
                                    
                                    URL url;
                                    try {
                                        url = new URL(updaterLink);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                        JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    
                                    final String folder = System.getProperty("java.io.tmpdir");
                                    final String fileName = "osumer_updater_" + Calendar.getInstance().getTimeInMillis() + ".exe";
                                    
                                    //TODO
                                    /*
                                    QueueManager mgr = uiFrame.getQueueManager();
                                    mgr.addQueue(new Queue(
                                            "osumer Updater",
                                            new URLDownloader(folder, fileName, url),
                                            null,
                                            null,
                                            new QueueAction[] {
                                                    new UpdaterRunAction(folder + fileName)
                                            })
                                     );
                                    uiFrame.getTab().setSelectedIndex(1);
                                    new Thread() {
                                        public void run() {
                                            JOptionPane.showMessageDialog(uiFrame, "The web updater will be downloaded and started very soon.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    }.start();
                                    */
                                    dispose();
                                } catch (DebuggableException e){
                                    e.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                try {
                                    installer.install();
                                    JOptionPane.showMessageDialog(null, "Installation success.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                } catch (DebuggableException e){
                                    JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                                }
                                
                                updateConfigUi();
                            }
                        } else {
                            String runningFilePath = null;
                            try {
                                runningFilePath = URLDecoder.decode(Installer.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
                            } catch (UnsupportedEncodingException e1) {
                                e1.printStackTrace();
                                DebugDump dump = new DebugDump(runningFilePath, "None", "Decoding running file path in UninstallOsumer", "Ask for option", "Error decoding?", false, e1);
                                DumpManager.getInstance().addDump(dump);
                                ErrorDumpDialog dialog = new ErrorDumpDialog(dump);
                                dialog.setModal(true);
                                dialog.setVisible(true);
                                return;
                            }
                            
                            File file = new File(runningFilePath);
                            String path = file.toPath().toString();
                            System.out.println(path);
                            
                            if (path.contains(Installer.winPath)){
                                JOptionPane.showMessageDialog(null, "osumer cannot be uninstalled if launched from \"Program Files\".\nYou have to use an external osumer downloaded from the releases.\nAnd press \"Uninstall osumerExpress\" there.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            
                            int option = JOptionPane.showOptionDialog(null, "Are you sure to uninstall? osumerExpress will no longer act as a browser.", "Uninstall osumerExpress", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 1);
                            
                            if (option == JOptionPane.CLOSED_OPTION || option == JOptionPane.NO_OPTION){
                                return;
                            }
                            
                            try {
                                installer.uninstall();
                                JOptionPane.showMessageDialog(null, "Uninstallation success.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            } catch (DebuggableException e){
                                updateConfigUi();
                                e.printStackTrace();
                                ErrorDumpDialog dialog = new ErrorDumpDialog(e.getDump());
                                dialog.setModal(true);
                                dialog.setVisible(true);
                                return;
                            }
                            
                            updateConfigUi();
                        }
                    }
                });
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
                                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(lblStatusValue,
                                                        GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)))
                        .addContainerGap()));
                gl_panel_3.setVerticalGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_3.createSequentialGroup()
                                .addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE).addComponent(lblStatus)
                                        .addComponent(lblStatusValue))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblPleaseSetosumerexpress).addComponent(btnShowGuide))
                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(lblPleaseRestartOsumer)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(btnInstallOsumerexpress, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                                .addContainerGap()));
                panel_3.setLayout(gl_panel_3);

                JButton btnAdd = new JButton("Add");
                btnAdd.setEnabled(false);

                JButton btnRmv = new JButton("Rmv");
                btnRmv.setEnabled(false);

                JButton btnUp = new JButton("Up");
                btnUp.setEnabled(false);

                JButton btnDown = new JButton("Down");
                btnDown.setEnabled(false);

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

                serverProrityListModel = new DefaultListModel<String>();
                serverProrityList = new JList<String>();
                serverProrityList.setModel(serverProrityListModel);
                
                scrollPane.setViewportView(serverProrityList);
                panel_1.setLayout(gl_panel_1);

                lblLoggedInAs = new JLabel("Will be logged in as: Not logged in");

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
                            
                            config.setUser(Base64.encodeBase64String(dialog.getUsername().getBytes(StandardCharsets.UTF_8)));
                            config.setPass(Base64.encodeBase64String(dialog.getPassword().getBytes(StandardCharsets.UTF_8)));
                        }
                    }
                });

                JButton btnRemoveCredentials = new JButton("Remove credentials");
                btnRemoveCredentials.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (JOptionPane.showOptionDialog(PreferenceDialog.this, "Are you sure?", "Removing credentials", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                            config.setUser("");
                            config.setPass("");
                        }
                    }
                });
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
                oePanel.setLayout(gl_oePanel);
            }
            
            JPanel panel = new JPanel();
            tab.addTab("Downloading", null, panel, null);
            
            JPanel panel_1 = new JPanel();
            panel_1.setBorder(new TitledBorder(null, "Default After-Download Action and Location", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GroupLayout gl_panel = new GroupLayout(panel);
            gl_panel.setHorizontalGroup(
                gl_panel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                        .addContainerGap())
            );
            gl_panel.setVerticalGroup(
                gl_panel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(267, Short.MAX_VALUE))
            );
            
            rdbtnLaunchOsuAutomatically = new JRadioButton("Launch osu! automatically to import beatmap (Default & recommended)");
            
            rdbtnPutDownloadsTo = new JRadioButton("Put downloads to osu! Songs folder (Then press F5 in osu! to import)");
            
            rdbtnPutDownloadsTo_1 = new JRadioButton("Put downloads to a folder");
            rdbtnPutDownloadsTo_1.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    lblDownloadFolder.setEnabled(rdbtnPutDownloadsTo_1.isSelected());
                    downloadFolderField.setEnabled(rdbtnPutDownloadsTo_1.isSelected());
                    btnDownloadLocSelect.setEnabled(rdbtnPutDownloadsTo_1.isSelected());
                }
            });
            
            ButtonGroup rdbtnDefaultDownloadGp = new ButtonGroup();
            rdbtnDefaultDownloadGp.add(rdbtnLaunchOsuAutomatically);;
            rdbtnDefaultDownloadGp.add(rdbtnPutDownloadsTo);
            rdbtnDefaultDownloadGp.add(rdbtnPutDownloadsTo_1);
            
            lblDownloadFolder = new JLabel("Folder:");
            lblDownloadFolder.setEnabled(false);
            
            downloadFolderField = new JTextField();
            downloadFolderField.setEnabled(false);
            downloadFolderField.setColumns(10);
            
            btnDownloadLocSelect = new JButton("Select");
            btnDownloadLocSelect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser c = new JFileChooser();
                    c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    
                    File file = new File(downloadFolderField.getText());
                    if (file.exists() && file.isDirectory()) {
                        c.setSelectedFile(file);
                    }
                    
                    int opt = c.showOpenDialog(PreferenceDialog.this);
                    if (opt == JFileChooser.APPROVE_OPTION) {
                        File sf = c.getSelectedFile();
                        if (sf != null && sf.exists() && sf.isDirectory()) {
                            downloadFolderField.setText(sf.getAbsolutePath());
                        } else {
                            JOptionPane.showMessageDialog(PreferenceDialog.this, "You must select a folder that exists.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            btnDownloadLocSelect.setEnabled(false);
            GroupLayout gl_panel_1 = new GroupLayout(panel_1);
            gl_panel_1.setHorizontalGroup(
                gl_panel_1.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_1.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_panel_1.createSequentialGroup()
                                .addGap(21)
                                .addComponent(lblDownloadFolder)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(downloadFolderField, GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(btnDownloadLocSelect))
                            .addComponent(rdbtnPutDownloadsTo, GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE)
                            .addComponent(rdbtnLaunchOsuAutomatically, GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE)
                            .addComponent(rdbtnPutDownloadsTo_1, GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE))
                        .addContainerGap())
            );
            gl_panel_1.setVerticalGroup(
                gl_panel_1.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_1.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rdbtnLaunchOsuAutomatically)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(rdbtnPutDownloadsTo)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(rdbtnPutDownloadsTo_1)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                            .addComponent(downloadFolderField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDownloadFolder)
                            .addComponent(btnDownloadLocSelect))
                        .addContainerGap(53, Short.MAX_VALUE))
            );
            panel_1.setLayout(gl_panel_1);
            panel.setLayout(gl_panel);

            JPanel queuingPanel = new JPanel();
            tab.addTab("Queuing", null, queuingPanel, null);

            JLabel lblMaximumDownloadingThread = new JLabel("Maximum Downloading Thread:");
            lblMaximumDownloadingThread.setHorizontalAlignment(SwingConstants.RIGHT);

            queuingMaxThreadsSpinnerModel = new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1));
            maxDwnThreadsSpinner = new JSpinner(queuingMaxThreadsSpinnerModel);

            JLabel lblNextQueuingCheck = new JLabel("Next Queuing Check Delay:");
            lblNextQueuingCheck.setHorizontalAlignment(SwingConstants.RIGHT);

            queuingNextQueueCheckSpinnerModel = new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(250));
            nextQueuingCheckDelaySpinner = new JSpinner(queuingNextQueueCheckSpinnerModel);

            JLabel lblMs = new JLabel("ms");

            JLabel lblThreads = new JLabel("thread(s)");

            chckbxEnableMassiveDownloading = new JCheckBox(
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
            
            JLabel lblifMassiveDownloading = new JLabel("*If massive downloading not enabled, setting higher than 8 threads have no effect.");
            lblifMassiveDownloading.setForeground(Color.RED);
            
            JLabel lbldoNotSet = new JLabel("*Do not set too low. Otherwise it may cause high CPU usage.");
            lbldoNotSet.setForeground(Color.RED);
            GroupLayout gl_queuingPanel = new GroupLayout(queuingPanel);
            gl_queuingPanel.setHorizontalGroup(
                gl_queuingPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_queuingPanel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_queuingPanel.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_queuingPanel.createSequentialGroup()
                                .addGroup(gl_queuingPanel.createParallelGroup(Alignment.TRAILING, false)
                                    .addComponent(lblNextQueuingCheck, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblMaximumDownloadingThread, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_queuingPanel.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(maxDwnThreadsSpinner)
                                    .addComponent(nextQueuingCheckDelaySpinner, GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_queuingPanel.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_queuingPanel.createSequentialGroup()
                                        .addComponent(lblMs)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(lbldoNotSet, GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE))
                                    .addGroup(gl_queuingPanel.createSequentialGroup()
                                        .addComponent(lblThreads)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(lblifMassiveDownloading, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))))
                            .addComponent(chckbxEnableMassiveDownloading))
                        .addContainerGap())
            );
            gl_queuingPanel.setVerticalGroup(
                gl_queuingPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_queuingPanel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_queuingPanel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(lblMaximumDownloadingThread)
                            .addComponent(maxDwnThreadsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblThreads)
                            .addComponent(lblifMassiveDownloading))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_queuingPanel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(lblNextQueuingCheck)
                            .addComponent(nextQueuingCheckDelaySpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMs)
                            .addComponent(lbldoNotSet))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(chckbxEnableMassiveDownloading)
                        .addContainerGap(349, Short.MAX_VALUE))
            );
            queuingPanel.setLayout(gl_queuingPanel);

            JPanel updaterPanel = new JPanel();
            tab.addTab("Updater", null, updaterPanel, null);

            JPanel panel_2 = new JPanel();
            panel_2.setBorder(new TitledBorder(null, "Frequency on checking updates", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));

            JPanel panel_3 = new JPanel();
            panel_3.setBorder(
                    new TitledBorder(null, "Update branch", TitledBorder.LEADING, TitledBorder.TOP, null, null));

            JPanel panel_4 = new JPanel();
            panel_4.setBorder(
                    new TitledBorder(null, "Comparison algorithm", TitledBorder.LEADING, TitledBorder.TOP, null, null));

            chckbxAutoCriticalUpdate = new JCheckBox("Automatically accept critical updates");
            chckbxAutoCriticalUpdate.setEnabled(false);

            chckbxAutoPatches = new JCheckBox("Download and apply patches automatically");
            chckbxAutoPatches.setEnabled(false);

            JButton btnCheckUpdateNow = new JButton("Check update now");
            btnCheckUpdateNow.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ProgressDialog dialog = new ProgressDialog();
                    dialog.setLocationRelativeTo(PreferenceDialog.this);
                    
                    Thread thread = new Thread() {
                        public void run() {
                            dialog.getLabel().setText("Status: Checking for updates...");
                            dialog.getProgressBar().setIndeterminate(true);
                            
                            UpdateInfo verInfo = null;
                            try {
                                verInfo = getUpdateInfoByConfig();
                            } catch (NoBuildsForVersionException e){
                                dialog.dispose();
                                JOptionPane.showMessageDialog(PreferenceDialog.this, "No builds availiable for the new version. See dump for more details", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            } catch (NoSuchVersionException e){
                                dialog.dispose();
                                JOptionPane.showMessageDialog(PreferenceDialog.this, "We don't have version " + Osumer.OSUMER_VERSION + " in the current update branch\n\nPlease try another update branch (snapshot, beta, stable).", "Version not available", JOptionPane.INFORMATION_MESSAGE);
                                return;
                            } catch (NoSuchBuildNumberException e){
                                dialog.dispose();
                                JOptionPane.showMessageDialog(PreferenceDialog.this, 
                                        "We don't have build number greater or equal to " + Osumer.OSUMER_BUILD_NUM + " in version " + Osumer.OSUMER_VERSION + ".\n" +
                                        "If you are using a modified/development osumer,\n"
                                        + " you can just ignore this message.\n" +
                                        "If not, this might be the versions.json in GitHub goes wrong,\n"
                                        + " post a new issue about this.", "Build not available", JOptionPane.WARNING_MESSAGE);
                                return;
                            } catch (DebuggableException e){
                                dialog.dispose();
                                JOptionPane.showMessageDialog(PreferenceDialog.this, "Could not connect to update server.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            
                            if (verInfo == null) {
                                dialog.dispose();
                                JOptionPane.showMessageDialog(PreferenceDialog.this, "Could not obtain update info.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            
                            dialog.dispose();
                            
                            if (verInfo.isThisVersion()) {
                                JOptionPane.showMessageDialog(PreferenceDialog.this, "You are running the latest version of osumer"
                                        + " (" + verInfo.getVersion() + "-" + Updater.getBranchStr(verInfo.getBranch()) + "-" + verInfo.getBuildNum() + ")", "Info", JOptionPane.INFORMATION_MESSAGE);
                                return;
                            }
                            
                            int option;
                            String desc = verInfo.getDescription();
                            if (desc == null){
                                option = JOptionPane.showOptionDialog(PreferenceDialog.this,
                                        "New " +
                                        (verInfo.isUpgradedVersion() ? "upgrade" : "update") +
                                        " available! New version:\n" + verInfo.getVersion() +
                                        "-" + Updater.getBranchStr(verInfo.getBranch()) +
                                        "-b" + verInfo.getBuildNum() + "\n\n" +
                                        "Do you want to update it now?", "Update available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.NO_OPTION);
                            } else {
                                option = JOptionPane.showOptionDialog(PreferenceDialog.this,
                                        "New " +
                                        (verInfo.isUpgradedVersion() ? "upgrade" : "update") +
                                        " available! New version:\n" + verInfo.getVersion() +
                                        "-" + Updater.getBranchStr(verInfo.getBranch()) +
                                        "-b" + verInfo.getBuildNum() + "\n\n" +
                                        "Do you want to update it now?", "Update available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "No", "Description/Changelog"}, JOptionPane.NO_OPTION);
                                
                                if (option == 2){
                                    option = JOptionPane.showOptionDialog(PreferenceDialog.this, new TextPanel(desc), "Update description/change-log", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, 0);
                                }
                            }
                            
                            if (option == JOptionPane.YES_OPTION){
                                /*
                                try {
                                    Desktop.getDesktop().browse(new URI(verInfo.getWebLink()));
                                } catch (IOException | URISyntaxException e) {
                                    DebugDump dump = new DebugDump(
                                            verInfo.getWebLink(),
                                            "Show option dialog of updating osumer or not",
                                            "Set checkingUpdate to false",
                                            "(End of function / thread)",
                                            "Error when opening the web page",
                                            false,
                                            e);
                                    DumpManager.getInstance().addDump(dump);
                                    DebugDump.showDebugDialog(dump);
                                }
                                */
                                try {
                                    String updaterLink = Updater.getUpdaterLink();
                                    
                                    if (updaterLink == null){
                                        System.out.println("No latest updater .exe defined! Falling back to legacy updater!");
                                        updaterLink = Updater.LEGACY_UPDATER_JAR;
                                    }
                                    
                                    URL url;
                                    try {
                                        url = new URL(updaterLink);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                        JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    
                                    final String folder = System.getProperty("java.io.tmpdir");
                                    final String fileName = "osumer_updater_" + Calendar.getInstance().getTimeInMillis() + ".exe";
                                    
                                    //TODO
                                    /*
                                    QueueManager mgr = uiFrame.getQueueManager();
                                    mgr.addQueue(new Queue(
                                            "osumer Updater",
                                            new URLDownloader(folder, fileName, url),
                                            null,
                                            null,
                                            new QueueAction[] {
                                                    new UpdaterRunAction(folder + fileName)
                                            })
                                     );
                                    uiFrame.getTab().setSelectedIndex(1);
                                    new Thread() {
                                        public void run() {
                                            JOptionPane.showMessageDialog(uiFrame, "The web updater will be downloaded and started very soon.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    }.start();
                                    */
                                    dispose();
                                } catch (DebuggableException e){
                                    e.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    };
                    thread.start();
                    
                    dialog.setModal(true);
                    dialog.setVisible(true);
                }
            });

            JPanel panel_5 = new JPanel();
            panel_5.setBorder(
                    new TitledBorder(null, "Down-grade software", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GroupLayout gl_updaterPanel = new GroupLayout(updaterPanel);
            gl_updaterPanel.setHorizontalGroup(gl_updaterPanel.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
                    gl_updaterPanel.createSequentialGroup().addContainerGap().addGroup(gl_updaterPanel
                            .createParallelGroup(Alignment.TRAILING)
                            .addComponent(panel_5, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                            .addComponent(chckbxAutoCriticalUpdate, Alignment.LEADING,
                                    GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
                            .addComponent(panel_4, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                            .addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                            .addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                            .addComponent(chckbxAutoPatches, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760,
                                    Short.MAX_VALUE)
                            .addComponent(btnCheckUpdateNow, Alignment.LEADING)).addContainerGap()));
            gl_updaterPanel
                    .setVerticalGroup(
                            gl_updaterPanel.createParallelGroup(Alignment.LEADING)
                                    .addGroup(gl_updaterPanel.createSequentialGroup().addContainerGap()
                                            .addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 55,
                                                    GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                    GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 52,
                                                    GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(chckbxAutoCriticalUpdate)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(chckbxAutoPatches)
                                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnCheckUpdateNow)
                                            .addPreferredGap(ComponentPlacement.RELATED)
                                            .addComponent(panel_5, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                                            .addContainerGap()));

            JLabel lblWarningDowngradingOsumer = new JLabel(
                    "WARNING: Downgrading osumer may result in function loss or ocurrance of bugs.");
            lblWarningDowngradingOsumer.setForeground(Color.RED);
            lblWarningDowngradingOsumer.setFont(new Font("PMingLiU", Font.BOLD, 12));

            JLabel lblSelectVersion = new JLabel("Select version:");

            versionsComboBoxModel = new DefaultComboBoxModel<String>();
            versionsBox = new JComboBox<String>(versionsComboBoxModel);
            versionsBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            });

            btnShowChangelog = new JButton("Show change-log");
            btnShowChangelog.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String str = (String) versionsBox.getSelectedItem();
                    if (str != null){
                        ProgressDialog dialog = new ProgressDialog();
                        dialog.setTitle("Downloading data...");
                        dialog.getProgressBar().setIndeterminate(true);
                        
                        new Thread(){
                            public void run(){
                                String[] strs = str.split("-");
                                if (strs.length < 3){
                                    JOptionPane.showMessageDialog(PreferenceDialog.this, "The selected version name is in a wrong format. Please ignore this error.", "Error", JOptionPane.ERROR_MESSAGE);
                                    dialog.dispose();
                                    return;
                                }
                                JSONObject versionsJson = null;
                                try {
                                    versionsJson = updater.getVersions();
                                } catch (DebuggableException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(PreferenceDialog.this, "Could not download update data! Check Debug Dump.", "Error", JOptionPane.ERROR_MESSAGE);
                                    dialog.dispose();
                                    return;
                                }
                                JSONObject sourcesJson = versionsJson.getJSONObject("sources");
                                JSONObject sourceJson = sourcesJson.getJSONObject(strs[1]);
                                JSONArray verJson = sourceJson.getJSONArray(strs[0]);
                                JSONObject buildJson = verJson.getJSONObject(Integer.parseInt(strs[2]) - 1);
                                String desc = buildJson.isNull("desc") ? null : buildJson.getString("desc");
                                dialog.dispose();
                                if (desc == null){
                                    JOptionPane.showMessageDialog(PreferenceDialog.this, "This version does not have an update description / change-log.\nThis version might be too old to install.", "Info", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(PreferenceDialog.this, desc, str + " Change-log / Description", JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                        }.start();

                        dialog.setLocationRelativeTo(PreferenceDialog.this);
                        dialog.getLabel().setText("Versions' data might take some while to download...");
                        dialog.setModal(true);
                        dialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(PreferenceDialog.this, "No version selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            btnDowngrade = new JButton("Down-grade (Feature currently not available)");
            btnDowngrade.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                }
            });
            btnDowngrade.setEnabled(false);

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
                                    .addComponent(versionsBox, GroupLayout.PREFERRED_SIZE, 501, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(btnShowChangelog, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                            .addComponent(lbldisableAutoInstall, GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE))
                    .addContainerGap()));
            gl_panel_5.setVerticalGroup(gl_panel_5.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_5.createSequentialGroup().addContainerGap()
                            .addComponent(lblWarningDowngradingOsumer).addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_panel_5.createParallelGroup(Alignment.BASELINE).addComponent(lblSelectVersion)
                                    .addComponent(versionsBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                            GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnShowChangelog))
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnDowngrade)
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(lbldowngradingToVersions)
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(lbldisableAutoInstall)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
            panel_5.setLayout(gl_panel_5);

            rdbtnPerVersion = new JRadioButton("Per version in selected branch");
            panel_4.add(rdbtnPerVersion);

            rdbtnLatestVersion = new JRadioButton("Latest version (per branch)");
            panel_4.add(rdbtnLatestVersion);

            rdbtnLatestVersionoverall = new JRadioButton("Latest version (overall)");
            rdbtnLatestVersionoverall.setEnabled(false);
            panel_4.add(rdbtnLatestVersionoverall);

            rdbtnStablity = new JRadioButton("Stablity");
            rdbtnStablity.setEnabled(false);
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

            rdbtnEveryStartup = new JRadioButton("Every startup");
            rdbtnEveryStartup.setEnabled(false);
            panel_2.add(rdbtnEveryStartup);

            rdbtnEveryAct = new JRadioButton("Every download/request/activation");
            rdbtnEveryAct.setEnabled(false);
            panel_2.add(rdbtnEveryAct);

            rdbtnNever = new JRadioButton("Never");
            rdbtnNever.setEnabled(false);
            panel_2.add(rdbtnNever);
            updaterPanel.setLayout(gl_updaterPanel);

            ButtonGroup updateFreqBtnGroup = new ButtonGroup();
            updateFreqBtnGroup.add(rdbtnEveryStartup);
            updateFreqBtnGroup.add(rdbtnEveryAct);
            updateFreqBtnGroup.add(rdbtnNever);

            JPanel securityPanel = new JPanel();
            tab.addTab("Security", null, securityPanel, null);
            tab.setEnabledAt(5, false);

            JPanel panel_7 = new JPanel();
            panel_7.setBorder(
                    new TitledBorder(null, "On-login password", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            GroupLayout gl_securityPanel = new GroupLayout(securityPanel);
            gl_securityPanel
                    .setHorizontalGroup(gl_securityPanel.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_securityPanel.createSequentialGroup().addContainerGap()
                                    .addComponent(panel_7, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                    .addContainerGap()));
            gl_securityPanel.setVerticalGroup(gl_securityPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_securityPanel.createSequentialGroup()
                            .addContainerGap().addComponent(panel_7, GroupLayout.PREFERRED_SIZE,
                                    GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(340, Short.MAX_VALUE)));

            chckbxEnabledusedAs = new JCheckBox(
                    "Enabled (Used as encryption with account osu! username and password)");

            chckbxAskOnStartup = new JCheckBox("Ask on startup");
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
            securityPanel.setLayout(gl_securityPanel);

            JPanel pluginsPanel = new JPanel();
            tab.addTab("Plugins", null, pluginsPanel, null);
            tab.setEnabledAt(6, false);

            JLabel lblWarningOnlyAdd = new JLabel(
                    "Warning: Only add plugins that you trust, or officially trusted by the developer.");
            lblWarningOnlyAdd.setForeground(Color.RED);
            lblWarningOnlyAdd.setFont(new Font("Tahoma", Font.BOLD, 12));
            
            JLabel lblThisFeatureIs = new JLabel("This feature is currently not implemented.");
            GroupLayout gl_pluginsPanel = new GroupLayout(pluginsPanel);
            gl_pluginsPanel.setHorizontalGroup(
                gl_pluginsPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(Alignment.TRAILING, gl_pluginsPanel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_pluginsPanel.createParallelGroup(Alignment.TRAILING)
                            .addComponent(lblThisFeatureIs, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                            .addComponent(lblWarningOnlyAdd, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE))
                        .addContainerGap())
            );
            gl_pluginsPanel.setVerticalGroup(
                gl_pluginsPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_pluginsPanel.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblWarningOnlyAdd)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(lblThisFeatureIs)
                        .addContainerGap(388, Short.MAX_VALUE))
            );
            pluginsPanel.setLayout(gl_pluginsPanel);

            JPanel miscPanel = new JPanel();
            tab.addTab("Miscellaneous", null, miscPanel, null);

            JPanel soundTonePanel = new JPanel();
            soundTonePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Sound/Tone", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
            GroupLayout gl_miscPanel = new GroupLayout(miscPanel);
            gl_miscPanel.setHorizontalGroup(
                gl_miscPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_miscPanel.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(soundTonePanel, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                        .addContainerGap())
            );
            gl_miscPanel.setVerticalGroup(
                gl_miscPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_miscPanel.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(soundTonePanel, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(273, Short.MAX_VALUE))
            );

            chckbxToneBeforeDwn = new JCheckBox("Enable tone before download");
            chckbxToneBeforeDwn.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent arg0) {
                    lblFileToneBefore.setEnabled(chckbxToneBeforeDwn.isSelected());
                    toneBeforeField.setEnabled(chckbxToneBeforeDwn.isSelected());
                    btnSelectToneBefore.setEnabled(chckbxToneBeforeDwn.isSelected());
                }
            });
            
            lblFileToneBefore = new JLabel("File:");
            lblFileToneBefore.setEnabled(false);
            
            toneBeforeField = new JTextField();
            toneBeforeField.setEnabled(false);
            toneBeforeField.setColumns(10);
            
            btnSelectToneBefore = new JButton("Select");
            btnSelectToneBefore.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser c = new JFileChooser();
                    c.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    
                    File file = new File(toneBeforeField.getText());
                    if (file.exists() && file.isFile()) {
                        c.setSelectedFile(file);
                    }
                    
                    int opt = c.showOpenDialog(PreferenceDialog.this);
                    if (opt == JFileChooser.APPROVE_OPTION) {
                        File sf = c.getSelectedFile();
                        if (sf != null && sf.exists() && sf.isFile()) {
                            toneBeforeField.setText(sf.getAbsolutePath());
                        } else {
                            JOptionPane.showMessageDialog(PreferenceDialog.this, "You must select a file that exists.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            btnSelectToneBefore.setEnabled(false);
            
                        chckbxToneAfterDwn = new JCheckBox("Enable tone after download");
                        chckbxToneAfterDwn.addChangeListener(new ChangeListener() {
                            public void stateChanged(ChangeEvent e) {
                                lblFileToneAfter.setEnabled(chckbxToneAfterDwn.isSelected());
                                toneAfterField.setEnabled(chckbxToneAfterDwn.isSelected());
                                btnSelectToneAfter.setEnabled(chckbxToneAfterDwn.isSelected());
                            }
                        });
            
            lblFileToneAfter = new JLabel("File:");
            lblFileToneAfter.setEnabled(false);
            
            toneAfterField = new JTextField();
            toneAfterField.setEnabled(false);
            toneAfterField.setColumns(10);
            
            btnSelectToneAfter = new JButton("Select");
            btnSelectToneAfter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser c = new JFileChooser();
                    c.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    
                    File file = new File(toneAfterField.getText());
                    if (file.exists() && file.isFile()) {
                        c.setSelectedFile(file);
                    }
                    
                    int opt = c.showOpenDialog(PreferenceDialog.this);
                    if (opt == JFileChooser.APPROVE_OPTION) {
                        File sf = c.getSelectedFile();
                        if (sf != null && sf.exists() && sf.isFile()) {
                            toneAfterField.setText(sf.getAbsolutePath());
                        } else {
                            JOptionPane.showMessageDialog(PreferenceDialog.this, "You must select a file that exists.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            btnSelectToneAfter.setEnabled(false);
            GroupLayout gl_soundTonePanel = new GroupLayout(soundTonePanel);
            gl_soundTonePanel.setHorizontalGroup(
                gl_soundTonePanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_soundTonePanel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_soundTonePanel.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_soundTonePanel.createSequentialGroup()
                                .addGap(21)
                                .addComponent(lblFileToneAfter)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(toneAfterField, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(btnSelectToneAfter))
                            .addComponent(chckbxToneAfterDwn, GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE)
                            .addGroup(Alignment.TRAILING, gl_soundTonePanel.createSequentialGroup()
                                .addGap(21)
                                .addComponent(lblFileToneBefore, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(toneBeforeField, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(btnSelectToneBefore))
                            .addComponent(chckbxToneBeforeDwn, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE))
                        .addContainerGap())
            );
            gl_soundTonePanel.setVerticalGroup(
                gl_soundTonePanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_soundTonePanel.createSequentialGroup()
                        .addGap(11)
                        .addComponent(chckbxToneBeforeDwn)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_soundTonePanel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(toneBeforeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSelectToneBefore)
                            .addComponent(lblFileToneBefore))
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(chckbxToneAfterDwn)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_soundTonePanel.createParallelGroup(Alignment.LEADING)
                            .addComponent(btnSelectToneAfter)
                            .addGroup(gl_soundTonePanel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(toneAfterField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblFileToneAfter)))
                        .addGap(82))
            );
            soundTonePanel.setLayout(gl_soundTonePanel);
            miscPanel.setLayout(gl_miscPanel);
            
            JPanel panel_6 = new JPanel();
            tab.addTab("(*) Legacy Old-site Beatmap", null, panel_6, null);
            
            JLabel lblThisIsAn = new JLabel("This is an emergency update for legacy reading and downloading old-site beatmap urls.");
            
            JLabel lblTheNewsiteWebpage = new JLabel("The new-site web-page parser is not implemented in this version.");
            
            chckbxEnableRedirectTo = new JCheckBox("Enable redirect to old-site automatically (Legacy)");
            chckbxEnableRedirectTo.setFont(new Font("Tahoma", Font.PLAIN, 14));
            chckbxEnableRedirectTo.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel lblPleaseEnableThe = new JLabel("Please enable the following option to temporarily fix the problem.");
            lblPleaseEnableThe.setFont(new Font("Tahoma", Font.BOLD, 14));
            lblPleaseEnableThe.setHorizontalAlignment(SwingConstants.CENTER);
            
            chckbxDisableStartupNotification = new JCheckBox("Disable startup notification, warning");
            
            JLabel lblThisOptionWill = new JLabel("This option will be automatically DISABLED when the parser is implemented in next version.");
            lblThisOptionWill.setHorizontalAlignment(SwingConstants.CENTER);
            GroupLayout gl_panel_6 = new GroupLayout(panel_6);
            gl_panel_6.setHorizontalGroup(
                gl_panel_6.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_6.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_panel_6.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_panel_6.createSequentialGroup()
                                .addGroup(gl_panel_6.createParallelGroup(Alignment.TRAILING)
                                    .addComponent(lblThisOptionWill, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                    .addComponent(lblTheNewsiteWebpage, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                    .addComponent(lblThisIsAn, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                    .addComponent(lblPleaseEnableThe, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                    .addComponent(chckbxEnableRedirectTo, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE))
                                .addContainerGap())
                            .addGroup(gl_panel_6.createSequentialGroup()
                                .addComponent(chckbxDisableStartupNotification, GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
                                .addGap(18))))
            );
            gl_panel_6.setVerticalGroup(
                gl_panel_6.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_panel_6.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblThisIsAn)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(lblTheNewsiteWebpage)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(lblPleaseEnableThe)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(chckbxEnableRedirectTo)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(lblThisOptionWill)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(chckbxDisableStartupNotification)
                        .addContainerGap(299, Short.MAX_VALUE))
            );
            panel_6.setLayout(gl_panel_6);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton saveButton = new JButton("Save");
                saveButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        applyChanges();
                        dispose();
                    }
                });
                buttonPane.add(saveButton);
                getRootPane().setDefaultButton(saveButton);
            }
            {
                JButton applyButton = new JButton("Apply");
                applyButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        applyChanges();
                    }
                });
                buttonPane.add(applyButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                buttonPane.add(cancelButton);
            }
        }
        updateConfigUi();
    }


    private UpdateInfo getUpdateInfoByConfig() throws DebuggableException {
        String algo = config.getCheckUpdateAlgo();
        if (algo.equals(Configuration.CHECK_UPDATE_ALGO_PER_VER_PER_BRANCH)) {
            return updater.getPerVersionPerBranchLatestVersion();
        } else if (algo.equals(Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH)) {
            return updater.getLatestVersion();
        } else { //TODO: Implement other algo
            return updater.getLatestVersion();
        }
    }
    
    private void updateConfigUi(){
        //Main Panel
        chckbxShowGettingStarted.setSelected(config.isShowGettingStartedOnStartup());
        
        //OE
        String[] serverProrityStrs = config.getServerPrority();
        serverProrityListModel.clear();
        for (int i = 0; i < serverProrityStrs.length; i++){
            serverProrityListModel.addElement(serverProrityStrs[i]);
        }
        
        String user = config.getUser();
        if (config.isUserPassEncrypted()){
            lblLoggedInAs.setForeground(Color.RED);
            lblLoggedInAs.setText("Please enter encryption unlock key in order to manage credentials!");
        } else if (user.isEmpty()){
            lblLoggedInAs.setForeground(Color.BLACK);
            lblLoggedInAs.setText("No credentials entered. Please save your credentials for a faster osumerExpress experience.");
        } else {
            lblLoggedInAs.setForeground(Color.BLUE);
            lblLoggedInAs.setText("Will be logged in as: " + user);
        }
        chckbxDisabledDaemon.setSelected(config.isDaemonDisabled());
        
        browserComboBoxModel.removeAllElements();
        browserComboBoxModel.addElement("... relieving a list of available browsers ...");
        browsersBox.setEnabled(false);
        
        boolean installed = Installer.isInstalled();
        if (installed) {
            lblStatusValue.setText("Installed");
            lblStatusValue.setForeground(Color.GREEN);
            btnInstallOsumerexpress.setText("Uninstall osumerExpress, osumer2 daemon");
        } else {
            lblStatusValue.setText("Not installed");
            lblStatusValue.setForeground(Color.RED);
            btnInstallOsumerexpress.setText("Install osumerExpress, osumer2 daemon");
        }
        
        boolean admin = Osumer.isWindowsElevated();
        lblPleaseRestartOsumer.setVisible(!admin);
        btnInstallOsumerexpress.setEnabled(!installed || admin);
        
        new Thread(){
            public void run(){
                String[] browsers = null;
                try {
                    browsers = Installer.getAvailableBrowsers();
                } catch (DebuggableException e) {
                    e.printStackTrace();
                }
                browserComboBoxModel.removeAllElements();
                if (browsers == null){
                    browsersBox.setEnabled(false);
                    browsers = new String[]{"-!- Could not relieve available browsers, check Debug Dumps. -!-"};
                } else {
                    int selectedIndex = -1;
                    browsersBox.setEnabled(true);
                    final String configBrowser = config.getDefaultBrowser();
                    browserComboBoxModel.addElement("--- Select ---");
                    for (int i = 0; i < browsers.length; i++){
                        if (browsers[i].equals(configBrowser)){
                            selectedIndex = i + 1;
                        }
                        browserComboBoxModel.addElement(browsers[i]);
                    }
                    browsersBox.setSelectedIndex(selectedIndex);
                }
            }
        }.start();
        
        //Downloading
        int action = config.getDefaultOpenBeatmapAction();
        if (action < 0 || action > 2) {
            action = 0;
            config.setDefaultOpenBeatmapAction(0);
            try {
                config.write();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        rdbtnLaunchOsuAutomatically.setSelected(action == 0);
        rdbtnPutDownloadsTo.setSelected(action == 1);
        rdbtnPutDownloadsTo_1.setSelected(action == 2);
        downloadFolderField.setText(config.getDefaultBeatmapSaveLocation());
        
        //Queuing
        maxDwnThreadsSpinner.setValue(config.getMaxThreads());
        nextQueuingCheckDelaySpinner.setValue(config.getNextCheckDelay());
        chckbxEnableMassiveDownloading.setSelected(config.isMassiveDownloadingThreads());
        
        //Updater
        int configUpdateSource = config.getUpdateSource();
        if (configUpdateSource < 0 || configUpdateSource > 2){
            configUpdateSource = 2;
        }
        final int updateSource = configUpdateSource;
        
        rdbtnStable.setSelected(updateSource == 0);
        rdbtnBeta.setSelected(updateSource == 1);
        rdbtnSnapshot.setSelected(updateSource == 2);
        
        String updateFreq = config.getCheckUpdateFreq();
        
        if (!updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_EVERY_STARTUP) &&
            !updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_EVERY_ACT) &&
            !updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_NEVER)){
            updateFreq = Configuration.CHECK_UPDATE_FREQ_EVERY_ACT;
        }
        
        rdbtnEveryStartup.setSelected(updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_EVERY_STARTUP)); //TDOO Change to constant
        rdbtnEveryAct.setSelected(updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_EVERY_ACT));
        rdbtnNever.setSelected(updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_NEVER));
        
        String updateAlgo = config.getCheckUpdateAlgo();
        
        if (!updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_PER_VER_PER_BRANCH) &&
                !updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH) &&
                !updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_LATEST_VER_OVERALL) &&
                !updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_STABLITY)){
            updateAlgo = Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH;
        }
        
        rdbtnPerVersion.setSelected(updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_PER_VER_PER_BRANCH));
        rdbtnLatestVersion.setSelected(updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH));
        rdbtnLatestVersionoverall.setSelected(updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_LATEST_VER_OVERALL));
        rdbtnStablity.setSelected(updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_STABLITY));
        
        chckbxAutoCriticalUpdate.setSelected(config.isAutoAcceptCriticalUpdates());
        chckbxAutoPatches.setSelected(config.isAutoDownloadApplyPatches());

        versionsComboBoxModel.removeAllElements();
        versionsBox.setEnabled(false);
        btnShowChangelog.setEnabled(false);
        versionsComboBoxModel.addElement("... relieving versions list ...");
        new Thread(){
            public void run(){
                JSONObject json = null;
                try {
                    json = updater.getVersions();
                } catch (DebuggableException e) {
                    e.printStackTrace();
                }
                versionsComboBoxModel.removeAllElements();
                
                if (json == null){
                    versionsComboBoxModel.addElement("-!- Cannot relieve versions data. Check Debug Dump. -!-");
                    versionsBox.setEnabled(false);
                    btnDowngrade.setEnabled(false);
                    btnShowChangelog.setEnabled(false);
                } else {
                    String sourceStr = Updater.getBranchStr(updateSource);
                    JSONObject sourcesJson = json.getJSONObject("sources");
                    
                    JSONObject sourceJson = sourcesJson.getJSONObject(sourceStr);
                    
                    Iterator<String> it = sourceJson.keys();
                    
                    List<String> strlist = new ArrayList<String>(50);
                    while(it.hasNext()){
                        strlist.add(it.next());
                    }
                    
                    Collections.sort(strlist, new Comparator<String>(){

                        @Override
                        public int compare(String arg0, String arg1) {
                            int result = Updater.compareVersion(arg0, arg1);
                            if (result == -2){
                                return -1;
                            }
                            return result * -1;
                        }
                        
                    });
                    
                    for (int i = 0; i < strlist.size(); i++){
                        String str = strlist.get(i);
                        JSONArray arr = sourceJson.getJSONArray(str);
                        //int inc = arr.length() - 1;
                        
                        strlist.set(i, str + "-" + sourceStr + "-1");
                        
                        for (int j = 1; j < arr.length(); j++){
                            strlist.add(++i, str + "-" + sourceStr + "-" + (j + 1));
                        }
                    }
                    
                    for (int i = 0; i < strlist.size(); i++){
                        versionsComboBoxModel.addElement(strlist.get(i));
                    }
                    
                    versionsBox.setEnabled(true);
                    //btnDowngrade.setEnabled(true);
                    btnShowChangelog.setEnabled(true);
                }
            }
        }.start();
        
        //Security
        chckbxEnabledusedAs.setSelected(config.isUserPassEncrypted());
        chckbxAskOnStartup.setSelected(config.isEncPassAskOnStartup());
        
        //Plugin
        //
        
        //Misc
        chckbxToneBeforeDwn.setSelected(config.isEnableToneBeforeDownload());
        toneBeforeField.setText(config.getToneBeforeDownloadPath());
        chckbxToneAfterDwn.setSelected(config.isEnableToneAfterDownload());
        toneAfterField.setText(config.getToneAfterDownloadPath());
        
        //(*) Legacy Old-site Beatmap
        chckbxEnableRedirectTo.setSelected(config.isLegacyEnableOldSiteBeatmapRedirecting());
        chckbxDisableStartupNotification.setSelected(config.isLegacyDisableOldSiteBeatmapRedirectingStartupNotification());
    }
    
    private void applyChanges(){
        //Main
        config.setShowGettingStartedOnStartup(this.chckbxShowGettingStarted.isSelected());
        
        //OE
        //TODO ServerPrority
        config.setOEEnabled(!chckbxOeDisabled.isSelected());
        config.setRunDaemonOnWinStartup(!chckbxRunAsDaemon.isSelected());
        config.setDaemonDisabled(chckbxDisabledDaemon.isSelected());
        
        String selectedItem = (String) browsersBox.getSelectedItem();
        if (!selectedItem.equals("--- Select ---")){
            config.setDefaultBrowser(selectedItem);
        }
        
        //Downloading
        int action = 0;
        if (rdbtnLaunchOsuAutomatically.isSelected()) {
            action = 0;
        } else if (rdbtnPutDownloadsTo.isSelected()) {
            action = 1;
        } else if (rdbtnPutDownloadsTo_1.isSelected()) {
            action = 2;
        }
        config.setDefaultOpenBeatmapAction(action);
        config.setDefaultBeatmapSaveLocation(downloadFolderField.getText());
        
        //Queuing
        config.setMaxThreads((int) maxDwnThreadsSpinner.getValue());
        config.setNextCheckDelay((int) nextQueuingCheckDelaySpinner.getValue());
        config.setMassiveDownloadingThreads(chckbxEnableMassiveDownloading.isSelected());
        
        //Updater
        int updateSource = -1;
        if (rdbtnStable.isSelected()){
            updateSource = 0;
        } else if (rdbtnBeta.isSelected()){
            updateSource = 1;
        } else if (rdbtnNever.isSelected()){
            updateSource = 2;
        } else { //Default
            updateSource = 2;
        }
        config.setUpdateSource(updateSource);
        
        String checkFreq = null;
        if (rdbtnEveryStartup.isSelected()){
            checkFreq = Configuration.CHECK_UPDATE_FREQ_EVERY_STARTUP;
        } else if (rdbtnEveryAct.isSelected()){
            checkFreq = Configuration.CHECK_UPDATE_FREQ_EVERY_ACT;
        } else if (rdbtnNever.isSelected()){
            checkFreq = Configuration.CHECK_UPDATE_FREQ_NEVER;
        } else { //Default
            checkFreq = Configuration.CHECK_UPDATE_FREQ_EVERY_ACT;
        }
        config.setCheckUpdateFreq(checkFreq);
        
        String checkAlgo = null;
        if (rdbtnPerVersion.isSelected()){
            checkAlgo = Configuration.CHECK_UPDATE_ALGO_PER_VER_PER_BRANCH;
        } else if (rdbtnLatestVersion.isSelected()){
            checkAlgo = Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH;
        } else if (rdbtnLatestVersionoverall.isSelected()){
            checkAlgo = Configuration.CHECK_UPDATE_ALGO_LATEST_VER_OVERALL;
        } else if (rdbtnStablity.isSelected()){
            checkAlgo = Configuration.CHECK_UPDATE_ALGO_STABLITY;
        } else { //Default
            checkAlgo = Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH;
        }
        config.setCheckUpdateAlgo(checkAlgo);
        
        config.setAutoAcceptCriticalUpdates(chckbxAutoCriticalUpdate.isSelected());
        config.setAutoDownloadApplyPatches(chckbxAutoPatches.isSelected());
        
        //Security
        config.setUserPassEncrypted(chckbxEnabledusedAs.isSelected());
        config.setEncPassAskOnStartup(chckbxAskOnStartup.isSelected());
        
        //Plugins
        //
        
        //Misc
        config.setEnableToneBeforeDownload(chckbxToneBeforeDwn.isSelected());
        config.setToneBeforeDownloadPath(toneBeforeField.getText());
        config.setEnableToneAfterDownload(chckbxToneAfterDwn.isSelected());
        config.setToneAfterDownloadPath(toneAfterField.getText());
        
        //(*) Legacy Old-site Beatmap
        config.setLegacyEnableOldSiteBeatmapRedirecting(chckbxEnableRedirectTo.isSelected());
        config.setLegacyDisableOldSiteBeatmapRedirectingStartupNotification(chckbxDisableStartupNotification.isSelected());
        
        try {
            config.write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
