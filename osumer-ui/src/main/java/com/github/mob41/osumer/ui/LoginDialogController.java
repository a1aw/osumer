package com.github.mob41.osumer.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginDialogController implements Initializable {

	@FXML
	private TextField usr;
	
	@FXML
	private PasswordField pwd;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public String getUser() {
		return usr.getText();
	}
	
	public String getPwd() {
		return pwd.getText();
	}

}
