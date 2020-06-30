package com.github.mob41.osumer.ui;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import com.github.mob41.osumer.io.Downloader;
import com.github.mob41.osumer.queue.QueueStatus;
import com.github.mob41.osumer.rmi.IDaemon;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class QueueController implements Initializable {
	
	@FXML
	private Label title;
	
	@FXML
	private Label status;
	
	@FXML
	private Label remain;
	
	@FXML
	private Label elapsed;
	
	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private Hyperlink startLink;
	
	@FXML
	private Hyperlink pauseLink;
	
	@FXML
	private Hyperlink stopLink;
	
	@FXML
	private Hyperlink cancelLink;

	@FXML
	private ImageView image;
	
	private IDaemon d;
	
	private String lastThumbUrl = null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cancelLink.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				cancelLink.setDisable(true);
				try {
					d.removeQueue(title.getText());
				} catch (RemoteException e) {
					cancelLink.setDisable(false);
					e.printStackTrace();
					Alert alert = new Alert(AlertType.ERROR, "Error requesting daemon to remove queue:\n" + e.getMessage(), ButtonType.OK);
					alert.showAndWait();
				}
				
			}
		});
	}

    private String nanoSecToString(long ns) {
    	if (ns < 0) {
    		return "...";
    	}
    	
        long sec = TimeUnit.NANOSECONDS.toSeconds(ns);
        long min = 0;
        if (sec >= 60) {
            min = (long) (sec / 60.0);
            sec -= min * 60;
        }

        long hr = 0;
        if (min >= 60) {
            hr = (long) (min / 60.0);
            min -= hr * 60;
        }

        return (hr != 0 ? (hr + " hr(s) ") : "") + (min != 0 ? (min + " min(s) ") : "") + sec + " sec(s)";
    }
	
	public void update(QueueStatus qs) {
		String thumbUrl = qs.getThumbUrl();
		//TODO To do with null thumb url
		if (thumbUrl != null && (lastThumbUrl == null || !lastThumbUrl.equals(thumbUrl))) {
			this.lastThumbUrl = thumbUrl;
			image.setImage(new Image(thumbUrl));
		}
		title.setText(qs.getTitle());
		
		int p = qs.getProgress();
		switch (qs.getStatus()) {
        case Downloader.DOWNLOADING:
            elapsed.setText("Elapsed Time: " + nanoSecToString(qs.getElapsed()));
            remain.setText("Remaining Time: " + nanoSecToString(qs.getEta()));
            status.setText("Status: Downloading... (" + p + "%)");
    		progressBar.setProgress(p / 100.0);
    		startLink.setDisable(true);
    		pauseLink.setDisable(false);
    		stopLink.setDisable(false);
    		cancelLink.setDisable(false);
            break;
        case Downloader.COMPLETED:
            elapsed.setText("");
            remain.setText("");
            status.setText("Status: Completed.");
    		startLink.setDisable(true);
    		pauseLink.setDisable(true);
    		stopLink.setDisable(true);
    		cancelLink.setDisable(false);
    		progressBar.setProgress(1);
            break;
        case Downloader.ERROR:
            elapsed.setText("");
            remain.setText("");
            status.setText("Status: Error occurred while downloading.");
    		startLink.setDisable(true);
    		pauseLink.setDisable(true);
    		stopLink.setDisable(true);
    		cancelLink.setDisable(false);
    		progressBar.setProgress(0);
            break;
        case Downloader.PAUSED:
            elapsed.setText("");
            remain.setText("");
            status.setText("Status: Paused.");
    		startLink.setDisable(true);
    		pauseLink.setDisable(true);
    		stopLink.setDisable(false);
    		cancelLink.setDisable(false);
            break;
        case Downloader.CANCELLED:
            elapsed.setText("");
            remain.setText("");
            status.setText("Status: Cancelled.");
    		startLink.setDisable(true);
    		pauseLink.setDisable(true);
    		stopLink.setDisable(true);
    		cancelLink.setDisable(false);
    		progressBar.setProgress(0);
            break;
        case -1:
            elapsed.setText("");
            remain.setText("");
            status.setText("Status: Waiting for queuing...");
    		startLink.setDisable(true);
    		pauseLink.setDisable(true);
    		stopLink.setDisable(true);
    		cancelLink.setDisable(false);
            break;
        default:
            elapsed.setText("");
            remain.setText("");
            status.setText("Status: Unknown status.");
    		startLink.setDisable(true);
    		pauseLink.setDisable(true);
    		stopLink.setDisable(true);
    		cancelLink.setDisable(false);
        }
	}
	
	public void setD(IDaemon d) {
		this.d = d;
	}

	public String getId() {
		return title.getText();
	}

	public Label getTitle() {
		return title;
	}

	public Label getStatus() {
		return status;
	}

	public Label getRemain() {
		return remain;
	}

	public Label getElapsed() {
		return elapsed;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public Hyperlink getStartLink() {
		return startLink;
	}

	public Hyperlink getPauseLink() {
		return pauseLink;
	}

	public Hyperlink getStopLink() {
		return stopLink;
	}

	public Hyperlink getCancelLink() {
		return cancelLink;
	}

	public ImageView getImage() {
		return image;
	}

}
