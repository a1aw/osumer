package com.github.mob41.osumer.ui;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.github.mob41.osumer.rmi.IUI;
import com.github.mob41.osumer.ui.old.UIFrame_old;

public class UI extends UnicastRemoteObject implements IUI {

    private final AppMain appMain;
    
    public UI(AppMain appMain) throws RemoteException {
    	this.appMain = appMain;
    }

    @Override
    public void wake() throws RemoteException {
    	appMain.wake();
    }

    @Override
    public void onQueueStatusUpdate() throws RemoteException {
        appMain.onQueueStatusUpdate();
    }

}
