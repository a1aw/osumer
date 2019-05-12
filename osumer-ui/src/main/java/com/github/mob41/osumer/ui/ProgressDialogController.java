package com.github.mob41.osumer.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProgressDialogController implements Initializable {
	
	@FXML
	private Label headerText;

	@FXML
	private Label statusText;
	
	@FXML
	private ProgressBar progressBar;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public Label getHeaderText() {
		return headerText;
	}
	
	public Label getStatusText() {
		return statusText;
	}
	
	public ProgressBar getProgressBar() {
		return progressBar;
	}

}
