package com.github.mob41.osumer.daemon;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.Osumer;
import com.github.mob41.osumer.OsumerNative;
import com.github.mob41.osumer.debug.DebugDump;
import com.github.mob41.osumer.debug.DumpManager;
import com.github.mob41.osumer.debug.WithDumpException;
import com.github.mob41.osumer.io.Downloader;
import com.github.mob41.osumer.io.OsuDownloader;
import com.github.mob41.osumer.method.ErrorCode;
import com.github.mob41.osumer.method.MethodResult;
import com.github.mob41.osumer.queue.Queue;
import com.github.mob41.osumer.queue.QueueAction;
import com.github.mob41.osumer.queue.QueueManager;
import com.github.mob41.osumer.queue.QueueStatus;
import com.github.mob41.osumer.queue.actions.AfterSoundAction;
import com.github.mob41.osumer.queue.actions.BeatmapImportAction;
import com.github.mob41.osumer.queue.actions.BeforeSoundAction;
import com.github.mob41.osumer.queue.actions.CustomImportAction;
import com.github.mob41.osumer.rmi.IDaemon;
import com.github.mob41.osumer.rmi.IUI;
import com.github.mob41.osums.Osums;
import com.github.mob41.osums.OsumsNewParser;
import com.github.mob41.osums.OsumsOldParser;
import com.github.mob41.osums.beatmap.OsuBeatmap;
import com.github.mob41.osums.beatmap.OsuSong;

public class Daemon extends UnicastRemoteObject implements IDaemon {

    /**
     * 
     */
    private static final long serialVersionUID = -7873474580122978651L;
    
    private final QueueManager queueManager;
    
    private final Configuration config;
    
    private final Osums osums;
    
    private final TrayIcon trayIcon;
    
    private final List<IUI> uis;
    
    private OverlayThread thread;
    
    private boolean startingUi = false;

    protected Daemon(Configuration config) throws RemoteException {
        this.config = config;
        queueManager = new QueueManager(config);

        //TODO allow the use of new parser
    	osums = new OsumsOldParser();
    	/*
        if (config.isUseOldParser()) {
        	osums = new OsumsOldParser();
        } else {
        	osums = new OsumsNewParser();
        }
        */
        
        uis = new ArrayList<IUI>();
        
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit()
                .getImage(Daemon.class.getResource("/trayIcon.png")));
        
        trayIcon.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (startingUi) {
					return;
				}
				startingUi = true;
				
				try {
					Runtime.getRuntime().exec("\"" + OsumerNative.getProgramFiles() + "\\osumer2\\osumer-ui.exe\"");
				} catch (IOException e1) {
					e1.printStackTrace();
		            JOptionPane.showMessageDialog(null,
		                    "Could not execute UI! Check dumps for more details.",
		                    "osumer-daemon Error", JOptionPane.ERROR_MESSAGE);
					DumpManager.addDump(new DebugDump(null, "Set \"startingUi\" as true", "Execute osumer-ui.exe", "Sleep 5000 ms", "Could not execute UI", false, e1));
				}
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ignore) {}
				
				startingUi = false;
			}
			
		});
        
        String key = Osumer.OSUMER_VERSION + "-" + Osumer.OSUMER_BRANCH + "-b" + Osumer.OSUMER_BUILD_NUM;
        trayIcon.setToolTip("osumer2 (" + key + ")");
        
        SystemTray tray = SystemTray.getSystemTray();

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage("osumer2", "osumer2 is now running in background.", TrayIcon.MessageType.INFO);
        } catch (AWTException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error when adding tray icon: " + e
                            + "\nAs a result, you are not able to start osumer from the tray.",
                    "osumer-daemon Warning", JOptionPane.WARNING_MESSAGE);
			DumpManager.addDump(new DebugDump(null, "Get System Tray", "Add tray icon into system tray", "Start Overlay Thread", "Could not add icon into system tray", false, e));
        }
        
        thread = new OverlayThread(config);
        thread.start();
    }
    
    @Override
    public MethodResult<Integer> addQueue(String url) throws RemoteException {
        return addQueue(url, -1, null);
    }
    
    @Override
    public MethodResult<Integer> addQueue(String url, int downloadAction, String targetFileOrFolder) throws RemoteException {
		DumpManager.reportEvent("event", "queueAdd");
        
        String user = config.getUser();
        String pass = config.getPass();

        if (user == null || user.isEmpty() || pass == null || pass.isEmpty()) {
            return new MethodResult<Integer>(ErrorCode.RESULT_NO_CREDENTIALS);
        }
        
        try {
        	osums.login(user, pass);
        } catch (WithDumpException e) {
            e.printStackTrace();
            return new MethodResult<Integer>(ErrorCode.RESULT_LOGIN_FAILED, e.getDump());
        }
        
        OsuSong map;
        
        try {
        	if (url.contains("b/")) {
                map = osums.getBeatmapInfo(url);
        	} else {
        		map = osums.getSongInfo(url);
        	}
        } catch (WithDumpException e) {
            e.printStackTrace();
            return new MethodResult<Integer>(ErrorCode.RESULT_GET_BEATMAP_INFO_FAILED, e.getDump());
        }
        
        String thumbUrl = "http:" + map.getThumbUrl();
        
        String dwnUrlStr = map.getDwnUrl();
        
        if (dwnUrlStr.length() <= 3) {
            return new MethodResult<Integer>(ErrorCode.RESULT_DOWNLOAD_URL_TOO_SHORT);
        }
        
        URL downloadUrl = null;
        try {
            downloadUrl = new URL("https://osu.ppy.sh" + dwnUrlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new MethodResult<Integer>(ErrorCode.RESULT_VALIDATE_DOWNLOAD_URL_FAILED);
        }
        String tmpdir = System.getProperty("java.io.tmpdir");

        final String mapName = map.getName();
        String fileName = dwnUrlStr.substring(3, map.getDwnUrl().length()) + " " + mapName;
        
        fileName = fileName.replaceAll("[\\/:*?\"<>|]", " ");
        
        OsuDownloader dwn = new OsuDownloader(tmpdir, fileName, osums, downloadUrl);
        
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
        
        boolean added = queueManager.addQueue(new Queue(map.getName(), dwn, thumbUrl, beforeActions, afterActions));
        
        if (added){
            trayIcon.displayMessage("Downloading \"" + mapName + "\"", "osumerExpress is downloading the requested beatmap!", TrayIcon.MessageType.INFO);
            dwn.addObserver(new Observer() {
    			
    			@Override
    			public void update(Observable o, Object arg) {
    				requestAllUiUpdateQueues();
    			}
    		});
    		DumpManager.reportEvent("event", "queueAdded");
    		
            if (!url.endsWith("/")) {
                String beatmapNum = url.replaceAll("\\D+","");;
        		DumpManager.reportEvent("beatmap", "queueFrequency",  beatmapNum);
            }
        } else {
            trayIcon.displayMessage("Could not add \"" + mapName + "\" to queue", "It has already in queue/downloading or completed.", TrayIcon.MessageType.INFO);
        }

        requestAllUiUpdateQueues();
        
        return new MethodResult<Integer>(ErrorCode.RESULT_OK);
    }

    private void requestAllUiUpdateQueues() {
		List<IUI> copy = new ArrayList<IUI>(uis);
		for (IUI ui : copy) {
			try {
				ui.onQueueStatusUpdate();
			} catch (RemoteException e) {
				if (uis.contains(ui)) {
					uis.remove(ui);
				}
			}
		}
	}

	@Override
    public void reloadConfiguration() throws RemoteException, IOException{
        try {
            config.load();
    		DumpManager.reportEvent("event", "configReload");
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public void trayIconDisplayMessage(String caption, String text, MessageType msgType) throws RemoteException {
        trayIcon.displayMessage(caption, text, msgType);
    }

    @Override
    public QueueStatus[] getQueues() throws RemoteException {
        List<Queue> queues = queueManager.getList();
        QueueStatus[] status = new QueueStatus[queues.size()];
        
        Downloader dwn;
        Queue q;
        QueueStatus s;
        
        for (int i = 0; i < status.length; i++) {
            q = queues.get(i);
            dwn = q.getDownloader();
            
            long elapsedTime = System.nanoTime() - q.getStartTime();
            long allTimeForDownloading = dwn.getDownloaded() != 0 ? (elapsedTime * dwn.getSize() / dwn.getDownloaded())
                    : -1;
            long eta = allTimeForDownloading - elapsedTime;
            
            s = new QueueStatus(q.getName(), dwn.getFileName(), q.getThumbUrl(), (int) dwn.getProgress(), eta, elapsedTime, dwn.getStatus());
            status[i] = s;
        }
        
        return status;
    }

    @Override
    public boolean removeQueue(String name) throws RemoteException {
		DumpManager.reportEvent("event", "queueRemove");
        Queue queue = queueManager.getQueue(name);
        if (queue == null) {
            return false;
        }
        boolean result = queueManager.removeQueue(queue);
        if (result) {
    		DumpManager.reportEvent("event", "queueRemoved");
            requestAllUiUpdateQueues();
        }
        return result;
    }

	@Override
	public void test() throws RemoteException {
		DumpManager.reportEvent("active", "overlay");
	}

	@Override
	public void registerUi(IUI ui) throws RemoteException {
		DumpManager.reportEvent("event", "uiDaemonRegister");
		uis.add(ui);
	}

	@Override
	public void unregisterUi(IUI ui) throws RemoteException {
		DumpManager.reportEvent("event", "uiDaemonUnregister");
		if (uis.contains(ui)) {
			uis.remove(ui);
		}
	}

	@Override
	public void startOsuWithOverlay() throws RemoteException {
		DumpManager.reportEvent("event", "startOsuWithOverlay");
		OsumerNative.startWithOverlay();
	}

	@Override
	public void setOverlayAgreement(boolean agreed) throws RemoteException {
		config.setOverlayAgreement(agreed);
		try {
			config.write();
		} catch (IOException e) {
			e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Could not write configuration! Check dumps for more details.",
                    "osumer-daemon Error", JOptionPane.ERROR_MESSAGE);
			DumpManager.addDump(new DebugDump(null, "Set overlay agreement", "Write config", "(End of function)", "Could not write configuration", false, e));
		}
	}

	@Override
	public boolean isOverlayAgreement() throws RemoteException {
		return config.isOverlayAgreement();
	}

}
