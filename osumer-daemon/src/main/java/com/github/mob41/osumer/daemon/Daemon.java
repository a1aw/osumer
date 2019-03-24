package com.github.mob41.osumer.daemon;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.github.mob41.organdebug.exceptions.DebuggableException;
import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.io.OsuDownloader;
import com.github.mob41.osumer.queue.Queue;
import com.github.mob41.osumer.queue.QueueAction;
import com.github.mob41.osumer.queue.QueueManager;
import com.github.mob41.osumer.queue.actions.AfterSoundAction;
import com.github.mob41.osumer.queue.actions.BeatmapImportAction;
import com.github.mob41.osumer.queue.actions.BeforeSoundAction;
import com.github.mob41.osumer.queue.actions.CustomImportAction;
import com.github.mob41.osums.io.beatmap.OsuBeatmap;
import com.github.mob41.osums.io.beatmap.Osums;

public class Daemon extends UnicastRemoteObject implements IDaemon {

    /**
     * 
     */
    private static final long serialVersionUID = -7873474580122978651L;
    
    private final QueueManager queueManager;
    
    private final Configuration config;
    
    private final Osums osums;
    
    private final TrayIcon trayIcon;

    protected Daemon(Configuration config) throws RemoteException {
        this.config = config;
        queueManager = new QueueManager(config);
        osums = new Osums();
        
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit()
                .getImage(Daemon.class.getResource("/com/github/mob41/osumer/daemon/trayIcon.png")));
        
        trayIcon.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                /*
                frame.setVisible(!frame.isVisible());

                if (!frame.isVisible()) {
                    icon.displayMessage("osumer2", "osumer2 is now running in background.",
                            TrayIcon.MessageType.INFO);
                }
                */
                //TODO Launch UI remotely
            }

        });
        trayIcon.setToolTip("osumer2");
        
        SystemTray tray = SystemTray.getSystemTray();

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage("osumer2", "osumer2 is now running in background.", TrayIcon.MessageType.INFO);
        } catch (AWTException e) {
            e.printStackTrace();
            //JOptionPane.showMessageDialog(null,
            //        "Error when adding tray icon: " + e
            //                + "\nAs a result, you are not able to start osumer from the tray.",
            //        "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    @Override
    public boolean addQueue(String url) throws RemoteException {
        return addQueue(url, -1, null);
    }
    
    @Override
    public boolean addQueue(String url, int downloadAction, String targetFileOrFolder) throws RemoteException {
        if (config.getCheckUpdateFreq() == Configuration.CHECK_UPDATE_FREQ_EVERY_ACT) {
            //TODO do check update
            //checkUpdate();
        }
        
        String user = config.getUser();
        String pass = config.getPass();

        if (user == null || user.isEmpty() || pass == null || pass.isEmpty()) {
            /*
            LoginPanel loginPanel = new LoginPanel();
            int option = JOptionPane.showOptionDialog(UIFrame.this, loginPanel, "Login to osu!",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
                    JOptionPane.CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                if (loginPanel.getUsername().isEmpty() || loginPanel.getPassword().isEmpty()) {
                    JOptionPane.showMessageDialog(UIFrame.this, "Username or password cannot be empty.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                user = loginPanel.getUsername();
                pass = loginPanel.getPassword();
            } else {
                return false;
            }
            */
            //TODO Do login
            return false;
        }
        
        try {
            osums.login(user, pass);
        } catch (DebuggableException e) {
            e.printStackTrace();
            //JOptionPane.showMessageDialog(UIFrame.this, "Error logging in:\n" + e.getDump().getMessage(),
            //        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String modBUrl = config.isLegacyEnableOldSiteBeatmapRedirecting() ? url.replace("osu.ppy.sh", "old.ppy.sh") : url;
        OsuBeatmap map;
        
        try {
            map = osums.getBeatmapInfo(modBUrl);
        } catch (DebuggableException e) {
            e.printStackTrace();
            //JOptionPane.showMessageDialog(UIFrame.this,
            //        "Error getting beatmap info:\n" + e.getDump().getMessage(), "Error",
            //        JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String modUrl = map.getThumbUrl();
        URL thumbUrl = null;
        try {
            thumbUrl = new URL("http:" + modUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }

        BufferedImage thumb = null;
        
        /*
        URLConnection conn = null;
        try {
            conn = thumbUrl.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        

        try {
            thumb = ImageIO.read(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            thumb = null;
        }
        */
        
        URL downloadUrl = null;
        try {
            downloadUrl = new URL("http://osu.ppy.sh" + map.getDwnUrl());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            //JOptionPane.showMessageDialog(UIFrame.this, "Error validating download URL:\n" + e1, "Error",
            //        JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String tmpdir = System.getProperty("java.io.tmpdir");

        final String mapName = map.getName();
        OsuDownloader dwn = new OsuDownloader(tmpdir,
                map.getDwnUrl().substring(3, map.getDwnUrl().length()) + " " + map.getName(), osums, downloadUrl);
        
        QueueAction importAction;
        if (downloadAction == -1) {
            importAction = new BeatmapImportAction(config);
        } else {
            importAction = new CustomImportAction(downloadAction, targetFileOrFolder);
        }

        QueueAction[] beforeActions = new QueueAction[] {
                new BeforeSoundAction(config)
        };
        
        QueueAction[] afterActions = new QueueAction[] { 
                new AfterSoundAction(config),
                new QueueAction(){

                    @Override
                    public void run(Queue queue) {
                        trayIcon.displayMessage("Download completed for \"" + mapName + "\"", "This osumer queue has completed downloading.", TrayIcon.MessageType.INFO);
                    }
                    
                },
                importAction
        };
        
        boolean added = queueManager.addQueue(new Queue(map.getName(), dwn, thumb, beforeActions, afterActions));
        
        if (added){
            trayIcon.displayMessage("Downloading \"" + mapName + "\"", "osumerExpress is downloading the requested beatmap!", TrayIcon.MessageType.INFO);
        } else {
            trayIcon.displayMessage("Could not add \"" + mapName + "\" to queue", "It has already in queue/downloading or completed.", TrayIcon.MessageType.INFO);
        }
        
        //tableModel.fireTableDataChanged();
        
        return true;
    }

    @Override
    public void reloadConfiguration() throws RemoteException, IOException{
        try {
            config.load();
        } catch (IOException e) {
            throw e;
        }
    }

}
