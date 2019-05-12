package com.github.mob41.osumer.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class PreferencesController implements Initializable {
	
	//
	// Toolbar
	//
	
	@FXML
	private Button saveBtn;
	
	@FXML
	private Button applyBtn;
	
	@FXML
	private Button cancelBtn;
	
	//
	// Main
	//
	
	@FXML
	private CheckBox showGettingStartedStartupCheckbox;
	
	@FXML
	private RadioButton rdBtnSkinLight;
	
	@FXML
	private RadioButton rdBtnSkinDark;
	
	//
	// osumerExpress
	//
	
	@FXML
	private Label credentialsStatus;
	
	@FXML
	private Button addCredentialsBtn;
	
	@FXML
	private Button removeCredentialsBtn;
	
	@FXML
	private CheckBox disabledOeCheckbox;
	
	@FXML
	private ComboBox<String> browsersBox;
	
	//
	// Overlay
	//
	
	@FXML
	private CheckBox enableOverlayCheckbox;
	
	@FXML
	private Button startOsuWithOverlayBtn;
	
	//
	// Parser
	//
	
	@FXML
	private RadioButton oldParserCheckbox;
	
	@FXML
	private RadioButton newParserCheckbox;
	
	//
	// Downloading
	//
	
	@FXML
	private RadioButton rdBtnImportLaunchOsu;
	
	@FXML
	private RadioButton rdBtnImportOsuSongs;
	
	@FXML
	private RadioButton rdBtnImportFolder;
	
	@FXML
	private TextField importFolderText;
	
	@FXML
	private Spinner<Integer> simRunningQueues;
	
	@FXML
	private Spinner<Integer> nextQueueCheckDelay;
	
	//
	// Updater
	//
	
	@FXML
	private RadioButton rdBtnUpdateStable;
	
	@FXML
	private RadioButton rdBtnUpdateBeta;
	
	@FXML
	private RadioButton rdBtnUpdateSnapshot;
	
	@FXML
	private RadioButton rdBtnFreqStartup;
	
	@FXML
	private RadioButton rdBtnFreqActivation;
	
	@FXML
	private RadioButton rdBtnFreqNever;
	
	@FXML
	private RadioButton rdBtnAlgoPerVersionBranch;
	
	@FXML
	private RadioButton rdBtnAlgoLatestVerBranch;
	
	@FXML
	private RadioButton rdBtnAlgoLatestVerOverall;
	
	@FXML
	private CheckBox autoCriticalUpdatesCheckbox;
	
	@FXML
	private CheckBox autoDownloadApplyPatchesCheckbox;
	
	@FXML
	private Button checkForUpdatesBtn;
	
	//
	// Miscellaneous
	//
	
	@FXML
	private CheckBox enableToneBeforeDwnCheckbox;
	
	@FXML
	private Label toneBeforeDwnFileText;
	
	@FXML
	private TextField toneBeforeDwnText;
	
	@FXML
	private Button toneBeforeDwnSelectBtn;
	
	@FXML
	private CheckBox enableToneAfterDwnCheckbox;
	
	@FXML
	private Label toneAfterDwnFileText;
	
	@FXML
	private TextField toneAfterDwnText;
	
	@FXML
	private Button toneAfterDwnSelectBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

}
