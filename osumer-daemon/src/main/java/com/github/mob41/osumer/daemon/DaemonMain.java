package com.github.mob41.osumer.daemon;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class DaemonMain {

    public static final int DEFAULT_PORT = 46725;

    public static void main(String[] args) {
        try {
            Daemon daemon = new Daemon();
            int port = DEFAULT_PORT;
            LocateRegistry.createRegistry(port);
            Naming.bind("rmi://localhost:" + port + "/daemon", daemon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
