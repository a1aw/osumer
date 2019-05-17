package com.github.mob41.osumer.daemon;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import javax.swing.JOptionPane;

import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.Osumer;
import com.github.mob41.osumer.debug.DumpManager;

import javafx.embed.swing.JFXPanel;

public class Main {

    public static final int DEFAULT_PORT = 46726; //Old version uses 46725, increased by 1 to avoid problems

    public static void main(String[] args) {
		try {
			DumpManager.init(Osumer.getVersionString(), Osumer.getVersionString());
		} catch (IOException e2) {
			e2.printStackTrace();
			System.err.println("DumpManager: Error initializing dump manager");
		}
		
        String configPath = Osumer.isWindows() ? System.getenv("localappdata") + "\\osumerExpress" : "";

        Configuration config = new Configuration(configPath, Configuration.DEFAULT_DATA_FILE_NAME);

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
        
        //Initialize JXToolkit
        new JFXPanel();

        int port = DEFAULT_PORT; //TODO: Port to config
        
        try {
            LocateRegistry.createRegistry(port);
            
            Daemon daemon = new Daemon(config);
            Naming.bind("rmi://localhost:" + port + "/daemon", daemon);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return;
        }
    }

}
