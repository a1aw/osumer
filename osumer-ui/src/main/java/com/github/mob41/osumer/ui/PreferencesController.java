package com.github.mob41.osumer.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import org.apache.commons.codec.binary.Base64;

import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.debug.WithDumpException;
import com.github.mob41.osumer.installer.Installer;
import com.github.mob41.osumer.rmi.IDaemon;
import com.github.mob41.osums.Osums;
import com.github.mob41.osums.OsumsNewParser;
import com.github.mob41.osums.OsumsOldParser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;

public class PreferencesController implements Initializable {
	
	//
	// Constants
	//
	
	private static final String CRED_STATUS_EXIST_PREFIX = "Username: ";

	private static final String CRED_STATUS_NOT_EXIST = "No credentials entered.";

	private static final String CRED_STATUS_CURR_ENCRYPTED = "Crendentials are currently encrypted. Unlock it to manage.";
	
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
	
	@FXML
	private CheckBox autoCloseOsumerAfterOverlayCheckbox;
	
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
	
	@FXML
	private Button dwnFolderSelectBtn;
	
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
	
	//@FXML
	//private Button checkForUpdatesBtn;
	
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
	
	@FXML
	private CheckBox metricsCheckbox;
	
	private IDaemon d;

	private Configuration config;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		saveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				save();
				Stage stage = (Stage) saveBtn.getScene().getWindow();
				stage.close();
			}
		});
		
		applyBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				save();
			}
		});
		
		cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Stage stage = (Stage) cancelBtn.getScene().getWindow();
				stage.close();
			}
		});
		
		startOsuWithOverlayBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				try {
					d.startOsuWithOverlay();
					
					if (autoCloseOsumerAfterOverlayCheckbox.isSelected()) {
				        Platform.exit();
				        System.exit(0);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
            		Alert alert = new Alert(AlertType.ERROR, "Could not call daemon to start osu!:\n" + e.getMessage(), ButtonType.OK);
            		alert.showAndWait();
				}
			}
		});
		
		toneBeforeDwnSelectBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Stage stage = (Stage) toneBeforeDwnSelectBtn.getScene().getWindow();
				
				FileChooser fileChooser = new FileChooser();
				
				File sf = fileChooser.showOpenDialog(stage);
				
                if (sf != null) {
                	if (sf.exists() && sf.isFile()) {
                        toneBeforeDwnText.setText(sf.getAbsolutePath());
                	} else {
                		Alert alert = new Alert(AlertType.ERROR, "You must select a file that exists.", ButtonType.OK);
                		alert.showAndWait();
                    }
                }
			}
		});
		
		
		toneAfterDwnSelectBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Stage stage = (Stage) toneAfterDwnSelectBtn.getScene().getWindow();
				
				FileChooser fileChooser = new FileChooser();
				
				File sf = fileChooser.showOpenDialog(stage);
				
                if (sf != null) {
                	if (sf.exists() && sf.isFile()) {
                        toneAfterDwnText.setText(sf.getAbsolutePath());
                	} else {
                		Alert alert = new Alert(AlertType.ERROR, "You must select a file that exists.", ButtonType.OK);
                		alert.showAndWait();
                    }
                }
			}
		});
        
        dwnFolderSelectBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Stage stage = (Stage) dwnFolderSelectBtn.getScene().getWindow();
				
				DirectoryChooser chooser = new DirectoryChooser();
				
				File file = new File(importFolderText.getText());
                if (file.exists() && file.isDirectory()) {
                	chooser.setInitialDirectory(file);
                }
                
                File sf = chooser.showDialog(stage);

                if (sf != null) {
                	if (sf.exists() && sf.isDirectory()) {
                    	importFolderText.setText(sf.getAbsolutePath());
                	} else {
                		Alert alert = new Alert(AlertType.ERROR, "You must select a folder that exists.", ButtonType.OK);
                		alert.showAndWait();
                    }
                }
			}
		});
        
        addCredentialsBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FXMLLoader loader0 = new FXMLLoader();
		        loader0.setLocation(AppMain.class.getResource("/view/LoginDialogLayout.fxml"));

		        DialogPane loginPane = null;
		        try {
					loginPane = (DialogPane) loader0.load();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		        LoginDialogController loginController = loader0.getController();
		        
		        Alert loginDialog = new Alert(AlertType.NONE);
		        loginDialog.initStyle(StageStyle.UTILITY);
		        loginDialog.initModality(Modality.APPLICATION_MODAL);
		        loginDialog.setTitle("");
		        loginDialog.setDialogPane(loginPane);
		        loginDialog.getButtonTypes().add(ButtonType.OK);
		        loginDialog.getButtonTypes().add(ButtonType.CANCEL);

		        FXMLLoader loader1 = new FXMLLoader();
		        loader1.setLocation(AppMain.class.getResource("/view/ProgressDialogLayout.fxml"));
		        DialogPane progressPane = null;
		        try {
					progressPane = (DialogPane) loader1.load();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		        ProgressDialogController progressController = loader1.getController();
		        
		        progressController.getHeaderText().setText("Login");
		        progressController.getStatusText().setText("Status: Logging in...");
		        progressController.getProgressBar().setProgress(-1);
		        
		        Alert progressDialog = new Alert(AlertType.NONE);
		        progressDialog.initStyle(StageStyle.UTILITY);
		        progressDialog.initModality(Modality.APPLICATION_MODAL);
		        progressDialog.setTitle("");
		        progressDialog.setDialogPane(progressPane);
		        progressDialog.getButtonTypes().add(ButtonType.CANCEL);
		        
		        loginDialog.showAndWait();
                
		        if (loginDialog.getResult() == ButtonType.OK) {
	                final String usr = loginController.getUser();
	                final String pwd = loginController.getPwd();
			        
	                if (usr == null || pwd == null || usr.isEmpty() || pwd.isEmpty()) {
	                	Alert alert = new Alert(AlertType.WARNING, "Username or password must not be empty.", ButtonType.OK);
	                	alert.showAndWait();
	                	return;
	                }
	                
			        Thread thread = new Thread() {
			        	public void run() {
			        		//TODO Allow the use of new parser
			        		Osums osums = new OsumsOldParser();
	    		        	//Osums osums = config.isUseOldParser() ? new OsumsOldParser() : new OsumsNewParser();

	                        boolean err = false;
	                        try {
	                            osums.login(usr, pwd);
	                        } catch (WithDumpException e1) {
	                        	err = true;
	                        }
	                        
	                        Platform.runLater(new Runnable() {
								@Override
								public void run() {
	                                progressDialog.close();
								}
							});
	                        
	                        if (err) {
	                        	Platform.runLater(new Runnable() {
	    							@Override
	    							public void run() {
	    	                        	Alert alert = new Alert(AlertType.WARNING, "", ButtonType.YES);
	    	                        	alert.setTitle("Attempting to login");
	    	                        	alert.setHeaderText("Login Failed");
	    	                        	alert.setContentText("Cannot login into this osu! account.\nDo you still want to save it?");
	    	                        	alert.getButtonTypes().add(ButtonType.NO);
	    	                        	alert.showAndWait();
	    	                        	
	    	                        	if (alert.getResult() == ButtonType.YES) {
	    	                            	updateLoginUi(config, loginController);
	    	                        	}
	    							}
	    						});
	                        } else {
	                        	Platform.runLater(new Runnable() {
	    							@Override
	    							public void run() {
	    	                        	updateLoginUi(config, loginController);
	    							}
	    						});
	                        }
			        	}
			        };
		        	progressDialog.show();
			        thread.start();
		        }
			}
		});
        
        removeCredentialsBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.WARNING, "Are you sure to remove your credentials?", ButtonType.YES);
				alert.getButtonTypes().add(ButtonType.NO);
				alert.setHeaderText("Removing Credentials");
				alert.showAndWait();
				
				if (alert.getResult() == ButtonType.YES) {
					config.setUser("");
			        config.setPass("");
			        credentialsStatus.setText(CRED_STATUS_NOT_EXIST);
				}
			}
			
		});
        
        NumberFormat format = NumberFormat.getIntegerInstance();
        UnaryOperator<TextFormatter.Change> filter = c -> {
            if (c.isContentChange()) {
                ParsePosition parsePosition = new ParsePosition(0);
                // NumberFormat evaluates the beginning of the text
                format.parse(c.getControlNewText(), parsePosition);
                if (parsePosition.getIndex() == 0 ||
                        parsePosition.getIndex() < c.getControlNewText().length()) {
                    // reject parsing the complete text failed
                    return null;
                }
            }
            return c;
        };
        
        TextFormatter<Integer> numFormatter0 = new TextFormatter<Integer>(
                new IntegerStringConverter(), 0, filter);
        TextFormatter<Integer> numFormatter1 = new TextFormatter<Integer>(
                new IntegerStringConverter(), 0, filter);
        SpinnerValueFactory<Integer> valueFactory0 = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 16, 0);
        SpinnerValueFactory<Integer> valueFactory1 = new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 30000, 0);
        simRunningQueues.setValueFactory(valueFactory0);
        nextQueueCheckDelay.setValueFactory(valueFactory1);
        simRunningQueues.getEditor().setTextFormatter(numFormatter0);
        nextQueueCheckDelay.getEditor().setTextFormatter(numFormatter1);
	}
	
	private void updateLoginUi(Configuration config, LoginDialogController loginController) {
        config.setUser(Base64.encodeBase64String(loginController.getUser().getBytes(StandardCharsets.UTF_8)));
        config.setPass(Base64.encodeBase64String(loginController.getPwd().getBytes(StandardCharsets.UTF_8)));
        credentialsStatus.setText(CRED_STATUS_EXIST_PREFIX + loginController.getUser());
	}
	
	private void save() {
		setConfig();
		applyChanges();
	}
	
	private void applyChanges() {
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AppMain.class.getResource("/view/ProgressDialogLayout.fxml"));
        DialogPane progressPane = null;
        try {
			progressPane = (DialogPane) loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        ProgressDialogController progressController = loader.getController();
        
        progressController.getHeaderText().setText("Applying Changes");
        progressController.getStatusText().setText("Status: Writing configuration...");
        progressController.getProgressBar().setProgress(-1);
        
        Alert progressDialog = new Alert(AlertType.NONE);
        progressDialog.initStyle(StageStyle.UTILITY);
        progressDialog.initModality(Modality.APPLICATION_MODAL);
        progressDialog.setTitle("");
        progressDialog.setDialogPane(progressPane);
        progressDialog.getButtonTypes().add(ButtonType.CANCEL);
        
        Thread thread = new Thread() {
        	public void run() {
                try {
                    config.write();
                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(new Runnable() {
						@Override
						public void run() {
		                    progressDialog.close();
		            		Alert alert = new Alert(AlertType.ERROR, "Could not write configuration:\n" + e.getMessage(), ButtonType.OK);
		            		alert.showAndWait();
						}
					});
                }
                
                Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
		                progressController.getStatusText().setText("Status: Reloading daemon configuration...");
					}
				});
                
                try {
        			d.reloadConfiguration();
        		} catch (Exception e) {
        			e.printStackTrace();
                    Platform.runLater(new Runnable() {
						@Override
						public void run() {
		                    progressDialog.close();
		            		Alert alert = new Alert(AlertType.ERROR, "Could not reload daemon configuration:\n" + e.getMessage(), ButtonType.OK);
		            		alert.showAndWait();
						}
					});
        		}
                
                Platform.runLater(new Runnable() {
					@Override
					public void run() {
	                    progressDialog.close();
					}
				});
        	}
        };
        thread.start();
        
        progressDialog.showAndWait();
	}
	
	private void setConfig() {
		//
		// Main
		//
		
		config.setShowGettingStartedOnStartup(showGettingStartedStartupCheckbox.isSelected());
		config.setUiSkin(rdBtnSkinLight.isSelected() ? "light" : "dark");
		
		//
		// osumerExpress
		//
		
		config.setOEEnabled(!disabledOeCheckbox.isSelected());
		String selectedItem = browsersBox.getValue();
        if (!selectedItem.equals("--- Select ---")){
            config.setDefaultBrowser(selectedItem);
        }
        
        //
        // Overlay
        //
        
        config.setOverlayEnabled(enableOverlayCheckbox.isSelected());
        
        //
        // Parser
        //
        
        config.setUseOldParser(oldParserCheckbox.isSelected());
        
        //
        // Downloading
        //
        
        int action = 0;
        if (rdBtnImportLaunchOsu.isSelected()) {
            action = 0;
        } else if (rdBtnImportOsuSongs.isSelected()) {
            action = 1;
        } else if (rdBtnImportFolder.isSelected()) {
            action = 2;
        }
        config.setDefaultOpenBeatmapAction(action);
        config.setDefaultBeatmapSaveLocation(importFolderText.getText());
        
        config.setMaxThreads(simRunningQueues.getValue());
        config.setNextCheckDelay(nextQueueCheckDelay.getValue());
        
        //
        // Updater
        //
        
        int updateSource = -1;
        if (rdBtnUpdateStable.isSelected()){
            updateSource = 0;
        } else if (rdBtnUpdateBeta.isSelected()){
            updateSource = 1;
        } else if (rdBtnUpdateSnapshot.isSelected()){
            updateSource = 2;
        } else { //Default
            updateSource = 2;
        }
        config.setUpdateSource(updateSource);
        
        String checkFreq = null;
        if (rdBtnFreqStartup.isSelected()){
            checkFreq = Configuration.CHECK_UPDATE_FREQ_EVERY_STARTUP;
        } else if (rdBtnFreqActivation.isSelected()){
            checkFreq = Configuration.CHECK_UPDATE_FREQ_EVERY_ACT;
        } else if (rdBtnFreqNever.isSelected()){
            checkFreq = Configuration.CHECK_UPDATE_FREQ_NEVER;
        } else { //Default
            checkFreq = Configuration.CHECK_UPDATE_FREQ_EVERY_ACT;
        }
        config.setCheckUpdateFreq(checkFreq);
        
        String checkAlgo = null;
        if (rdBtnAlgoPerVersionBranch.isSelected()){
            checkAlgo = Configuration.CHECK_UPDATE_ALGO_PER_VER_PER_BRANCH;
        } else if (rdBtnAlgoLatestVerBranch.isSelected()){
            checkAlgo = Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH;
        } else if (rdBtnAlgoLatestVerOverall.isSelected()){
            checkAlgo = Configuration.CHECK_UPDATE_ALGO_LATEST_VER_OVERALL;
        } else { //Default
            checkAlgo = Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH;
        }
        config.setCheckUpdateAlgo(checkAlgo);
        
        config.setAutoAcceptCriticalUpdates(autoCriticalUpdatesCheckbox.isSelected());
        config.setAutoDownloadApplyPatches(autoDownloadApplyPatchesCheckbox.isSelected());
        
        //
        // Miscellaneous
        //

        config.setEnableToneBeforeDownload(enableToneBeforeDwnCheckbox.isSelected());
        config.setToneBeforeDownloadPath(toneBeforeDwnText.getText());
        config.setEnableToneAfterDownload(enableToneAfterDwnCheckbox.isSelected());
        config.setToneAfterDownloadPath(toneAfterDwnText.getText());
        config.setMetricsEnabled(metricsCheckbox.isSelected());
	}
	
	public void restore() {
		//
		// Main
		//
		
		showGettingStartedStartupCheckbox.setSelected(config.isShowGettingStartedOnStartup());
		rdBtnSkinLight.setSelected(config.getUiSkin().equals("light"));
		
		//
		// osumerExpress
		//
		
		String user = config.getUser();
		if (config.isUserPassEncrypted()) {
			credentialsStatus.setText(CRED_STATUS_CURR_ENCRYPTED);
		} else if (user == null || user.isEmpty()) {
			credentialsStatus.setText(CRED_STATUS_NOT_EXIST);
		} else {
			credentialsStatus.setText(CRED_STATUS_EXIST_PREFIX + user);
		}
		disabledOeCheckbox.setSelected(!config.isOEEnabled());
		
        String[] browsers = null;
        try {
            browsers = Installer.getAvailableBrowsers();
        } catch (WithDumpException e) {
            e.printStackTrace();
        }
        List<String> items = browsersBox.getItems();
        
        if (browsers == null){
            browsersBox.setDisable(true);
            browsers = new String[]{"-!- Could not relieve available browsers, check Debug Dumps. -!-"};
        } else {
            int selectedIndex = -1;
            browsersBox.setDisable(false);
            final String configBrowser = config.getDefaultBrowser();
            items.add("--- Select ---");
            for (int i = 0; i < browsers.length; i++){
                if (browsers[i].equals(configBrowser)){
                    selectedIndex = i + 1;
                }
                items.add(browsers[i]);
            }
            browsersBox.getSelectionModel().clearAndSelect(selectedIndex);
        }
        
        //
        // Overlay
        //
        
        enableOverlayCheckbox.setSelected(config.isOverlayEnabled());
        
        //
        // Parser
        //
        
        oldParserCheckbox.setSelected(config.isUseOldParser());
        
        //
        // Downloading
        //
        
        int action = config.getDefaultOpenBeatmapAction();
        if (action < 0 || action > 2) {
            action = 0;
            config.setDefaultOpenBeatmapAction(0);
            try {
                config.write();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        rdBtnImportLaunchOsu.setSelected(action == 0);
        rdBtnImportOsuSongs.setSelected(action == 1);
        rdBtnImportFolder.setSelected(action == 2);
        importFolderText.setText(config.getDefaultBeatmapSaveLocation());
        
        simRunningQueues.getValueFactory().setValue(config.getMaxThreads());
        nextQueueCheckDelay.getValueFactory().setValue(config.getNextCheckDelay());
        
        //
        // Updater
        //
        
        int configUpdateSource = config.getUpdateSource();
        if (configUpdateSource < 0 || configUpdateSource > 2){
            configUpdateSource = 2;
        }
        final int updateSource = configUpdateSource;
        
        rdBtnUpdateStable.setSelected(updateSource == 0);
        rdBtnUpdateBeta.setSelected(updateSource == 1);
        rdBtnUpdateSnapshot.setSelected(updateSource == 2);
        
        String updateFreq = config.getCheckUpdateFreq();
        
        if (!updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_EVERY_STARTUP) &&
            !updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_EVERY_ACT) &&
            !updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_NEVER)){
            updateFreq = Configuration.CHECK_UPDATE_FREQ_EVERY_ACT;
        }
        
        rdBtnFreqStartup.setSelected(updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_EVERY_STARTUP)); //TDOO Change to constant
        rdBtnFreqActivation.setSelected(updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_EVERY_ACT));
        rdBtnFreqNever.setSelected(updateFreq.equals(Configuration.CHECK_UPDATE_FREQ_NEVER));
        
        String updateAlgo = config.getCheckUpdateAlgo();
        
        if (!updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_PER_VER_PER_BRANCH) &&
                !updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH) &&
                !updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_LATEST_VER_OVERALL) &&
                !updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_STABLITY)){
            updateAlgo = Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH;
        }
        
        rdBtnAlgoPerVersionBranch.setSelected(updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_PER_VER_PER_BRANCH));
        rdBtnAlgoLatestVerBranch.setSelected(updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH));
        rdBtnAlgoLatestVerOverall.setSelected(updateAlgo.equals(Configuration.CHECK_UPDATE_ALGO_LATEST_VER_OVERALL));
        
        autoCriticalUpdatesCheckbox.setSelected(config.isAutoAcceptCriticalUpdates());
        autoDownloadApplyPatchesCheckbox.setSelected(config.isAutoDownloadApplyPatches());
        
        //
        // Miscellaneous
        //

        enableToneBeforeDwnCheckbox.setSelected(config.isEnableToneBeforeDownload());
        toneBeforeDwnText.setText(config.getToneBeforeDownloadPath());
        enableToneAfterDwnCheckbox.setSelected(config.isEnableToneAfterDownload());
        toneAfterDwnText.setText(config.getToneAfterDownloadPath());
        metricsCheckbox.setSelected(config.isMetricsEnabled());
	}

	public void setD(IDaemon d) {
		this.d = d;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

}
