package com.github.mob41.osumer.ui;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.github.mob41.osumer.rmi.IUI;

public class UI extends UnicastRemoteObject implements IUI {

    private final UIFrame frame;
    
    public UI(UIFrame frame) throws RemoteException {
        this.frame = frame;
    }

    @Override
    public void wake() throws RemoteException {
        //TODO Use configuration
        frame.checkUpdate();
        
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.requestFocus();
        frame.setAlwaysOnTop(false);
    }

}
