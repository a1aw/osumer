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
package com.github.mob41.osumer.launcher;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

import com.codahale.metrics.MetricRegistry;
import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.Osumer;
import com.github.mob41.osumer.OsumerNative;
import com.github.mob41.osumer.debug.DebugDump;
import com.github.mob41.osumer.debug.DumpManager;
import com.github.mob41.osumer.installer.Installer;
import com.github.mob41.osumer.rmi.IDaemon;
import com.github.mob41.osumer.rmi.IUI;
import com.github.mob41.osums.Osums;

public class Main {

    public static final String INTRO = "osumer2 (osuMapDownloadEr) by mob41\n" + "Licensed under MIT License\n" + "\n"
            + "https://github.com/mob41/osumer\n" + "\n" + "This is a unoffical software to download beatmaps.\n" + "\n"
            + "Disclaimer:\n" + "This software does not contain malicious code to send\n"
            + "username and password to another server other than\n"
            + "osu!'s login server. This is a Open Source software.\n"
            + "You can feel free to look through the code. If you still\n"
            + "feel uncomfortable with this software, you can simply\n" + "stop using it. Thank you!\n";;

    public static void main(String[] args) {
		try {
			DumpManager.init(Osumer.getVersionString(), Osumer.getVersionString());
		} catch (IOException e2) {
			e2.printStackTrace();
			System.err.println("DumpManager: Error initializing dump manager");
		}
		
        DumpManager.getMetrics().meter(MetricRegistry.name("active", "launcher")).mark();
		
        ArgParser ap = new ArgParser(args);

        if (ap.isVersionFlag()) {
        	System.out.println(Osumer.OSUMER_VERSION + "-" + Osumer.OSUMER_BRANCH + "-" + Osumer.OSUMER_BUILD_NUM);
            return;
        }

        //TODO: Print the INTRO if in cmd mode
        // System.out.println(INTRO);
        
        //TODO: Move installer to osumer-installer standalone!
        
        // These are called by Windows when setting Default Programs
        if (ap.isHideIconsFlag() || ap.isShowIconsFlag() || ap.isReinstallFlag() || ap.isInstallFlag()
                || ap.isUninstallFlag()) {
            Installer installer = new Installer();

            if (ap.isHideIconsFlag()) {
                DumpManager.getMetrics().meter(MetricRegistry.name("event", "launcherHideIcons")).mark();
                installer.hideIcons();

            } else if (ap.isShowIconsFlag()) {
                DumpManager.getMetrics().meter(MetricRegistry.name("event", "launcherShowIcons")).mark();
                installer.showIcons();

            } else if (ap.isReinstallFlag()) {
                DumpManager.getMetrics().meter(MetricRegistry.name("event", "launcherReinstall")).mark();
                installer.reinstall();

            }/* else if (ap.isInstallFlag()) {
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
                } catch (WithDumpException e) {
                    if (!ap.isNoUiFlag() && !GraphicsEnvironment.isHeadless()) {
                        //TODO: Error Dump Dialog Control
                        ErrorDumpDialog dialog = new ErrorDumpDialog(e.getDump());
                        dialog.setModal(true);
                        dialog.setVisible(true);
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
                } catch (WithDumpException e) {
                    if (!ap.isNoUiFlag() && !GraphicsEnvironment.isHeadless()) {
                        //TODO: Error Dump Dialog Control
                        ErrorDumpDialog dialog = new ErrorDumpDialog(e.getDump());
                        dialog.setModal(true);
                        dialog.setVisible(true);
                    }

                    if (!(ap.isQuietFlag() && ap.isForceFlag())) {
                        System.out.println("Error@U$\n" + e.getDump().toString() + "Error@D$");
                    }
                }
            }*/

            DumpManager.forceMetricsReport();
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

            DumpManager.addDump(new DebugDump(null, "Configuration initialization", "Load configuration", "Set String urlStr to null", "Unable to load configuration", false, e1));
            DumpManager.forceMetricsReport();
            
            if (!GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(null, "Could not load configuration. For more details, check dump:\n" + e1, "Configuration Error",
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
        		(urlStr != null && !config.isOEEnabled()) ||
                (args != null && args.length > 0 && urlStr == null)) { //Browser if disabled OE
            runBrowser(config, args);
            
            DumpManager.getMetrics().meter(MetricRegistry.name("event", "launcherRunBrowser")).mark();
            DumpManager.forceMetricsReport();
            
            System.exit(0);
            return;
        } else {
            IDaemon d = null;
            try {
                d = (IDaemon) Naming.lookup("rmi://localhost:46726/daemon"); //Contact the daemon via RMI
            } catch (Exception e) {
            	
            }
            
            if (d == null) {
                try {
					Runtime.getRuntime().exec("\"" + OsumerNative.getProgramFiles() + "\\osumer2\\osumer-daemon.exe\"");
				} catch (IOException e) {
					e.printStackTrace();
		            DumpManager.addDump(new DebugDump(null, "Check if \"d\" is null", "Execute osumer-daemon.exe", "Initialize \"c\" as 0", "Could not start daemon. Terminating", false, e));
		            DumpManager.forceMetricsReport();
		            
                    JOptionPane.showMessageDialog(null, "Could not start daemon. For more details, check dump. Terminating:\n" + e, "osumer launcher Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                    return;
				}
                
                int c = 0;
                while (c < 20) {
                	try {
                        d = (IDaemon) Naming.lookup("rmi://localhost:46726/daemon"); //Contact the daemon via RMI
                    } catch (Exception e) {
                    	
                    }
                	try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						break;
					}
                	c++;
                }
                
                if (d == null) {
		            DumpManager.addDump(new DebugDump(null, "(While-loop) Look up daemon RMI", "Check if \\\"d\\\" is null", "Check if \"urlStr\" is null", false, "Could not connect to daemon. Terminating"));
		            DumpManager.forceMetricsReport();
		            
                    JOptionPane.showMessageDialog(null, "Could not connect to daemon. For more details, check dump. Terminating", "osumer launcher Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                    return;
                }
            }
            
            if (urlStr != null) {
                try {
                    d.addQueue(urlStr);
                    
                    System.exit(0);
                    return;
                } catch (RemoteException e) {
                    e.printStackTrace();
		            DumpManager.addDump(new DebugDump(null, "Check if \\\"urlStr\\\" is null", "Request daemon to add queue", "System Exit", "Could not connect or add queue to daemon", false, e));
		            DumpManager.forceMetricsReport();
		            
                    JOptionPane.showMessageDialog(null, "Could not connect or add queue to daemon. For more details, check dump:\n" + e, "osumer launcher Error",
                            JOptionPane.ERROR_MESSAGE);
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
            runUi(config, args, ap, d);
        }
        DumpManager.forceMetricsReport();
    }

    private static void runUi(Configuration config, String[] args, ArgParser ap, IDaemon d) {
        IUI ui = null;
        try {
            ui = (IUI) Naming.lookup("rmi://localhost:46727/ui"); //Contact the ui via RMI
        } catch (Exception e) {
        	
        }
        
    	if (ui == null) {
    		try {
    			Runtime.getRuntime().exec("\"" + OsumerNative.getProgramFiles() + "\\osumer2\\osumer-ui.exe\"");
    		} catch (IOException e) {
    			e.printStackTrace();
	            DumpManager.addDump(new DebugDump(null, "Check if \"ui\" is null", "Execute osumer-ui.exe", "Initialize \"c\" as 0", "Could not start UI. Terminating", false, e));
	            DumpManager.forceMetricsReport();
	            
                JOptionPane.showMessageDialog(null, "Could not start UI. For more details, check dump. Terminating:\n" + e, "osumer launcher Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
                return;
    		}
    		
            int c = 0;
            while (c < 20) {
            	try {
                    ui = (IUI) Naming.lookup("rmi://localhost:46727/ui"); //Contact the ui via RMI
                } catch (Exception e) {
                	
                }
            	try {
    				Thread.sleep(50);
    			} catch (InterruptedException e) {
    				break;
    			}
            	c++;
            }
    	}
        
        if (ui == null) {
            DumpManager.addDump(new DebugDump(null, "(While-loop) Look up UI RMI", "Check if \\\"ui\\\" is null", "Try to wake up UI", false, "Could not connect to UI. Terminating"));
            DumpManager.forceMetricsReport();
            
            JOptionPane.showMessageDialog(null, "Could not connect to UI. For more details, check dump. Terminating", "osumer launcher Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
            return;
        }
        
    	try {
			ui.wake();
		} catch (RemoteException e) {
            e.printStackTrace();
            DumpManager.addDump(new DebugDump(null, "Check if \\\\\\\"ui\\\\\\\" is null", "Try to wake up UI", "End of runUi()", "Could not connect or wake UI", false, e));
            DumpManager.forceMetricsReport();
            
            JOptionPane.showMessageDialog(null, "Could not connect or wake UI. For more details, check dump:\n" + e, "osumer launcher Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
            return;
		}
    }

    public static void runBrowser(Configuration config, String[] args) {
        String argstr = buildArgStr(args);
        // Run the default browser application
        if (!GraphicsEnvironment.isHeadless() && Osumer.isWindows()) {
        	
            if (config.getDefaultBrowser() == null || config.getDefaultBrowser().isEmpty()) {
                JOptionPane.showInputDialog(null,
                        "No default browser path is specified. Please maunally launch the browser the following arguments:",
                        "osumer2", JOptionPane.INFORMATION_MESSAGE, null, null, argstr);
                System.exit(-1);
                return;
            }

            String browserPath = Installer.getBrowserExePath(config.getDefaultBrowser());
            if (browserPath == null) {
                JOptionPane.showMessageDialog(null,
                        "Cannot read browser executable path in registry.\nCannot start default browser application for:\n"
                                + argstr,
                        "osumer2", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
                return;
            }

            File file = new File(browserPath.replaceAll("\"", ""));
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null,
                        "The specified browser application does not exist.\nCannot start default browser application for:\n"
                                + argstr,
                        "osumer2", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
                return;
            }

            try {
                Runtime.getRuntime().exec(browserPath + " " + argstr);
            } catch (IOException e) {
                e.printStackTrace();
                DumpManager.addDump(new DebugDump(null, "Check if the file exists", "Execute browser with args", "System Exit", "Could not execute browser with arguments", false, e));
                DumpManager.forceMetricsReport();
                
                JOptionPane.showMessageDialog(null, "Could not execute browser with arguments. For more details, check dump:\n" + e, "osumer launcher Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
                return;
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

}
