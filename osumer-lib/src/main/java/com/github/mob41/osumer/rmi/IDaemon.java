package com.github.mob41.osumer.rmi;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.github.mob41.osumer.method.MethodResult;
import com.github.mob41.osumer.queue.Queue;
import com.github.mob41.osumer.queue.QueueManager;
import com.github.mob41.osumer.queue.QueueStatus;

public interface IDaemon extends Remote{
	
	public void test() throws RemoteException;
    
    public MethodResult<Integer> addQueue(String url) throws RemoteException;
    
    public MethodResult<Integer> addQueue(String url, int downloadAction, String targetFileOrFolder) throws RemoteException;
    
    public boolean removeQueue(String name) throws RemoteException;
    
    public QueueStatus[] getQueues() throws RemoteException;
    
    public void registerUi(IUI ui) throws RemoteException;
    
    public void unregisterUi(IUI ui) throws RemoteException;
    
    public void setOverlayAgreement(boolean agreed) throws RemoteException;
    
    public boolean isOverlayAgreement() throws RemoteException;
    
    public void startOsuWithOverlay() throws RemoteException;
    
    public void trayIconDisplayMessage(String caption, String text, MessageType msgType) throws RemoteException;
    
    public void reloadConfiguration() throws RemoteException, IOException;
    
}
