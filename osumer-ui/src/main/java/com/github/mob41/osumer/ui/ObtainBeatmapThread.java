package com.github.mob41.osumer.ui;

import com.github.mob41.organdebug.exceptions.DebuggableException;
import com.github.mob41.osums.io.beatmap.OsuBeatmap;
import com.github.mob41.osums.io.beatmap.Osums;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class ObtainBeatmapThread extends Thread {
	
	private final String user;
	
	private final String pass;
	
	private final Osums osums;
	
	private final String url;
	
	private final ProgressDialogController progressController;
	
	private OsuBeatmap map;

	public ObtainBeatmapThread(String user, String pass, Osums osums, String url,
			ProgressDialogController progressController) {
		super();
		this.user = user;
		this.pass = pass;
		this.osums = osums;
		this.url = url;
		this.progressController = progressController;
	}


	@Override
	public void run() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
        		progressController.getStatusText().setText("Status: Logging in...");
			}
		});
		
        try {
            osums.login(user, pass);
        } catch (DebuggableException e) {
            e.printStackTrace();
    		Alert alert = new Alert(AlertType.INFORMATION, "Error logging in:\n" + e.getDump().getStacktrace(), ButtonType.OK);
    		alert.showAndWait();
            return;
        }
        
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
                progressController.getStatusText().setText("Status: Obtaining beatmap information...");
			}
		});
        try {
            map = osums.getBeatmapInfo(url);
        } catch (DebuggableException e) {
            e.printStackTrace();
    		Alert alert = new Alert(AlertType.INFORMATION, "Error getting beatmap info:\n" + e.getDump().getStacktrace(), ButtonType.OK);
    		alert.showAndWait();
            return;
        }
	}

	public OsuBeatmap getMap() {
		return map;
	}
}
