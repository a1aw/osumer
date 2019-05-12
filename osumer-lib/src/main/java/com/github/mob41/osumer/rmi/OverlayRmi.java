package com.github.mob41.osumer.rmi;

import java.awt.TrayIcon.MessageType;
import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

import com.github.mob41.osumer.queue.QueueStatus;

public class OverlayRmi {
	
    private static final int RMI_DAEMON_PORT = 46726;

    private static final String RMI_DAEMON_PATH = "daemon";

	private IDaemon d = null;
	
	public OverlayRmi() {
        String daemonSuffix = RMI_DAEMON_PORT + "/" + RMI_DAEMON_PATH;
        try {
            d = (IDaemon) Naming.lookup("rmi://localhost:" + daemonSuffix); //Contact the daemon via RMI
        } catch (Exception e) {
            e.printStackTrace();
            
            String msg = 
                    "Could not connect to daemon! Please ensure osumer-daemon is running properly.\n" +
                    "osumer2 Overlay requires daemon to be running. osu! will terminate right now.";
            System.err.println(msg);

            JOptionPane.showMessageDialog(null, msg, 
                    "osumer-overlay RMI Connection Error",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(-1);
            return;
        }
	}
	
	public QueueStatus[] getQueues() throws RemoteException{
		return d.getQueues();
	}
	
	public void test() throws RemoteException{
		d.trayIconDisplayMessage("Overlay has been successfully connected.", "osumer2", MessageType.INFO);
		d.test();
	}
	
}
