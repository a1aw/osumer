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
package com.github.mob41.osumer;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.github.mob41.organdebug.DebugDump;
import com.github.mob41.organdebug.DumpManager;
import com.github.mob41.organdebug.exceptions.DebuggableException;
import com.github.mob41.osumer.daemon.IDaemon;
import com.github.mob41.osumer.installer.Installer;
import com.github.mob41.osums.io.beatmap.Osums;

public class Main {

    public static final String INTRO = "osumer2 (osuMapDownloadEr) by mob41\n" + "Licensed under MIT License\n" + "\n"
            + "https://github.com/mob41/osumer\n" + "\n" + "This is a unoffical software to download beatmaps.\n" + "\n"
            + "Disclaimer:\n" + "This software does not contain malicious code to send\n"
            + "username and password to another server other than\n"
            + "osu!'s login server. This is a Open Source software.\n"
            + "You can feel free to look through the code. If you still\n"
            + "feel uncomfortable with this software, you can simply\n" + "stop using it. Thank you!\n";;

    public static void main(String[] args) {
        ArgParser ap = new ArgParser(args);

        if (ap.isVersionFlag()) {
            //TODO: Version via RMI
            System.out.println("TODO: Unimplemented: Version reading via Arguments");
            //System.out.println(Osumer.OSUMER_VERSION + "-" + Osumer.OSUMER_BRANCH + "-" + Osumer.OSUMER_BUILD_NUM);
            return;
        }

        //TODO: Still make it here?
        if (!GraphicsEnvironment.isHeadless()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //TODO: Print the INTRO if in cmd mode
        // System.out.println(INTRO);
        
        //TODO: Move installer to osumer-installer standalone!
        
        // These are called by Windows when setting Default Programs
        if (ap.isHideIconsFlag() || ap.isShowIconsFlag() || ap.isReinstallFlag() || ap.isInstallFlag()
                || ap.isUninstallFlag()) {
            Installer installer = new Installer();

            if (ap.isHideIconsFlag()) {
                installer.hideIcons();

            } else if (ap.isShowIconsFlag()) {
                installer.showIcons();

            } else if (ap.isReinstallFlag()) {
                installer.reinstall();

            } else if (ap.isInstallFlag()) {
                if (!ap.isQuietFlag() && !ap.isForceFlag()) {
                    int option = JOptionPane.showOptionDialog(null,
                            "You are installing osumer " + Osumer.OSUMER_VERSION + "-" + Osumer.OSUMER_BRANCH + "-"
                                    + Osumer.OSUMER_BUILD_NUM + ".\n" + "Are you sure?",
                            "Installing osumer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null,
                            JOptionPane.NO_OPTION);

                    if (option != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                try {
                    long startTime = System.currentTimeMillis();
                    installer.install();

                    if (!(ap.isQuietFlag() && ap.isForceFlag())) {
                        System.out.println("Info@U$\nInstallation success within "
                                + (System.currentTimeMillis() - startTime) + " ms\nInfo@D$");
                    }
                } catch (DebuggableException e) {
                    if (!ap.isNoUiFlag() && !GraphicsEnvironment.isHeadless()) {
                        //TODO: Error Dump Dialog Control
                        /*
                        ErrorDumpDialog dialog = new ErrorDumpDialog(e.getDump());
                        dialog.setModal(true);
                        dialog.setVisible(true);
                        */
                    }

                    if (!(ap.isQuietFlag() && ap.isForceFlag())) {
                        System.out.println("Error@U$\n" + e.getDump().toString() + "Error@D$");
                    }
                }
            } else if (ap.isUninstallFlag()) {
                if (!ap.isQuietFlag() && !ap.isForceFlag()) {
                    int option = JOptionPane.showOptionDialog(null,
                            "You are uninstalling osumer " + Osumer.OSUMER_VERSION + "-" + Osumer.OSUMER_BRANCH + "-"
                                    + Osumer.OSUMER_BUILD_NUM + ".\n" + "Are you sure?",
                            "Uninstalling osumer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null,
                            JOptionPane.NO_OPTION);

                    if (option != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                try {
                    long startTime = System.currentTimeMillis();
                    installer.uninstall();

                    if (!(ap.isQuietFlag() && ap.isForceFlag())) {
                        System.out.println("Info@U$\nUninstallation success within "
                                + (System.currentTimeMillis() - startTime) + " ms\nInfo@D$");
                    }
                } catch (DebuggableException e) {
                    if (!ap.isNoUiFlag() && !GraphicsEnvironment.isHeadless()) {
                        //TODO: Error Dump Dialog Control
                        /*
                        ErrorDumpDialog dialog = new ErrorDumpDialog(e.getDump());
                        dialog.setModal(true);
                        dialog.setVisible(true);
                        */
                    }

                    if (!(ap.isQuietFlag() && ap.isForceFlag())) {
                        System.out.println("Error@U$\n" + e.getDump().toString() + "Error@D$");
                    }
                }
            }

            System.exit(0);
            return;
        }
        
        //
        //Launch osumer system right here if no installation arguments
        //
        
        String configPath = Osumer.isWindows() ? System.getenv("localappdata") + "\\osumerExpress" : "";
        
        Configuration config = new Configuration(configPath, Configuration.DEFAULT_DATA_FILE_NAME);
        //QueueManager mgr = new QueueManager();

        try {
            config.load();
        } catch (IOException e1) {
            System.err.println("Unable to load configuration");
            e1.printStackTrace();

            if (!GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(null, "Could not load configuration: " + e1, "Configuration Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            System.exit(-1);
            return;
        }

        String urlStr = null;
        for (int i = 0; i < args.length; i++) {
            if (Osums.isVaildBeatMapUrl(args[i])) {
                urlStr = args[i];
                break;
            }
        }

        //TODO: Remove config's daemon flag and deprecate switch to browser if no UI flag
        //TODO: Too complicated!! Remove isUiFlag or isNoUiFlag!!
        
        if ((config.isSwitchToBrowserIfWithoutUiArg() && !ap.isUiFlag() && ap.isNoUiFlag()) || //Configuration
                (args != null && args.length > 0 && !config.isOEEnabled())) { //Browser if disabled OE
            runBrowser(config, args);

            System.exit(0);
            return;
        } else {
            IDaemon d = null;
            
            try {
                d = (IDaemon) Naming.lookup("rmi://localhost:46726/daemon"); //Contact the daemon via RMI
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (d == null) {
                System.out.println("Failed");
                //TODO: Launch daemon before continue
            } else if (urlStr != null) {
                System.out.println("Add queue : [" + urlStr + "]");
                try {
                    d.addQueue(urlStr);
                    
                    System.exit(0);
                    return;
                } catch (RemoteException e) {
                    e.printStackTrace();
                    DebugDump dump = new DebugDump(null, null, "Opening connection to BG osumer socket", null,
                            "Could not open socket at 46725 for BG call. Not osumer running at that port?", false, e);
                    DumpManager.getInstance().addDump(dump);
                    //ErrorDumpDialog dialog = new ErrorDumpDialog(dump);
                    //dialog.setModal(true);
                    //dialog.setVisible(true);
                    System.exit(-1);
                    return;
                }
            }
            
            if (GraphicsEnvironment.isHeadless()) {
                System.out.println(
                        "Error: Arguments are required to use this application. Otherwise, a graphics environment is required to show the downloader UI.");
                System.exit(0);
                return;
            }
            System.out.println("Connected success");
            runUi(config, args, ap, d);
        }
    }

    private static void runUi(Configuration config, String[] args, ArgParser ap, IDaemon d) {
        //Initialize JFX toolkit
        //new JFXPanel();
        
        //UIFrame frame = new UIFrame(config, d);
        //frame.setVisible(true);
    }

    public static void runBrowser(Configuration config, String[] args) {
        String argstr = buildArgStr(args);
        // Run the default browser application
        if (!GraphicsEnvironment.isHeadless() && Osumer.isWindows()) {

            System.out.println(config.getDefaultBrowser());
            if (config.getDefaultBrowser() == null || config.getDefaultBrowser().isEmpty()) {
                System.out.println(config.getDefaultBrowser());
                JOptionPane.showInputDialog(null,
                        "No default browser path is specified. Please maunally launch the browser the following arguments:",
                        "osumer - Automatic browser switching", JOptionPane.INFORMATION_MESSAGE, null, null, argstr);
                System.exit(-1);
                return;
            }

            String browserPath = Installer.getBrowserExePath(config.getDefaultBrowser());
            System.out.println(browserPath);
            if (browserPath == null) {
                JOptionPane.showMessageDialog(null,
                        "Cannot read browser executable path in registry.\nCannot start default browser application for:\n"
                                + argstr,
                        "Configuration Error", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
                return;
            }

            File file = new File(browserPath.replaceAll("\"", ""));
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null,
                        "The specified browser application does not exist.\nCannot start default browser application for:\n"
                                + argstr,
                        "Configuration Error", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
                return;
            }

            try {
                Runtime.getRuntime().exec(browserPath + " " + argstr);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.exit(0);
            return;
        }
    }

    private static String buildArgStr(String[] args) {
        String out = "";
        for (int i = 0; i < args.length; i++) {
            out += args[i];
            if (i != args.length - 1) {
                out += " ";
            }
        }
        return out;
    }

    private static boolean isUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

}
