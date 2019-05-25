package com.github.mob41.osumer.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.Osumer;
import com.github.mob41.osumer.debug.DebugDump;
import com.github.mob41.osumer.debug.DumpManager;
import com.github.mob41.osumer.debug.WithDumpException;
import com.github.mob41.osumer.exceptions.NoBuildsForVersionException;
import com.github.mob41.osumer.exceptions.NoSuchBuildNumberException;
import com.github.mob41.osumer.exceptions.NoSuchVersionException;
import com.github.mob41.osumer.io.OsuDownloader;
import com.github.mob41.osumer.io.URLDownloader;
import com.github.mob41.osumer.method.ErrorCode;
import com.github.mob41.osumer.method.MethodResult;
import com.github.mob41.osumer.queue.QueueStatus;
import com.github.mob41.osumer.rmi.IDaemon;
import com.github.mob41.osumer.updater.UpdateInfo;
import com.github.mob41.osumer.updater.Updater;
import com.github.mob41.osums.Osums;
import com.github.mob41.osums.beatmap.OsuBeatmap;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro8.JMetro;

public class MainController implements Initializable {
    
	@FXML
    private Label updateText;

	@FXML
    private TextField beatmapUrlText;

	@FXML
    private Button beatmapDwnBtn;

	@FXML
    private ToggleGroup beatmapImportSettings;

	@FXML
    private CheckBox showPreviewCheckbox;

	@FXML
    private VBox queuesBox;
	
	@FXML
	private RadioButton rdBtnUseDefault;
	
	@FXML
	private RadioButton rdBtnDwnImport;
	
	@FXML
	private RadioButton rdBtnDwnOsuSong;
	
	@FXML
	private RadioButton rdBtnDwnFolder;
	
	@FXML
	private RadioButton rdBtnDwnFile;
	
	@FXML
	private TabPane tabPane;
	
	@FXML
	private MenuItem preferencesMenuItem;
	
	@FXML
	private MenuItem updatesMenuItem;
	
	@FXML
	private MenuItem dumpsMenuItem;
	
	@FXML
	private MenuItem locateConfigMenuItem;
	
	@FXML
	private MenuItem closeMenuItem;
	
	@FXML
	private MenuItem docsMenuItem;
	
	@FXML
	private MenuItem issueMenuItem;
	
	@FXML
	private MenuItem aboutMenuItem;
	
	private Configuration config;
	
	private IDaemon d;
	
	private Osums osums;
	
	private QueueStatus[] queues;

	private Updater updater;

	private boolean checkingUpdate;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
        beatmapDwnBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				boolean showPreview = showPreviewCheckbox.isSelected();
				String idUrl = beatmapUrlText.getText();
				if (idUrl == null || idUrl.isEmpty()) {
	        		Alert alert = new Alert(AlertType.WARNING, "Please enter a valid osu! beatmap link/ID.", ButtonType.OK);
	        		alert.showAndWait();
					return;
				}
				addQueue(idUrl, showPreview, true);
			}
		});
        
        preferencesMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(AppMain.class.getResource("/view/PreferencesLayout.fxml"));
	            BorderPane borderPane = null;
		        try {
		        	borderPane = (BorderPane) loader.load();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        PreferencesController controller = loader.getController();
		        controller.setD(d);
		        controller.setConfig(config);
		        controller.restore();
		        
		        Scene scene = new Scene(borderPane);

	            String skin = config.getUiSkin();
	            new JMetro(skin.equals("light") ? JMetro.Style.LIGHT : JMetro.Style.DARK).applyTheme(scene);

		        Stage stage = new Stage();
		        stage.setScene(scene);
		        //stage.setTitle("Preferences");
		        stage.initStyle(StageStyle.UTILITY);
		        stage.initModality(Modality.APPLICATION_MODAL);
		        stage.showAndWait();
			}
		});
        
        updatesMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				checkUpdate();
			}
		});
        
        locateConfigMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
		        String configPath = Osumer.isWindows() ? System.getenv("localappdata") + "\\osumerExpress" : "";
				try {
					Desktop.getDesktop().open(new File(configPath));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        dumpsMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
		        String dumpPath = Osumer.isWindows() ? System.getenv("localappdata") + "\\osumerExpress\\dumps" : "";
		        File folder = new File(dumpPath);
		        if (!folder.exists()) {
                	Alert alert = new Alert(AlertType.ERROR, "There are no dumps currently.", ButtonType.OK);
                	alert.showAndWait();
                	return;
		        }
				try {
					Desktop.getDesktop().open(folder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        closeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
        
        docsMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/mob41/osumer/wiki"));;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        issueMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/mob41/osumer/issues/new"));;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
        aboutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.NONE, "", ButtonType.OK);
				alert.setHeaderText("About");
				alert.setContentText(
						"osumer2 (" + Osumer.getVersionString() +  ")\n" +
						"Copyright (c) mob41. 2016-2019\n\n" +
						"osumer is an application that provides osu! players a\n" +
						"more comfortable and faster way to download beatmaps."
						);
				alert.showAndWait();
			}
		});
	}

	private void updateQueuesUi() {
		if (queues == null) {
			return;
		}
		
		List<Node> queueNodes = queuesBox.getChildren();
		
		if (queues.length == 0) {
			queueNodes.clear();
			return;
		}
		
		List<Node> unmodNodes = new ArrayList<Node>(queueNodes);
		
		QueueController controller;
		boolean exist;
		int fix = 0;
		for (int i = 0; i < queues.length; i++) {
			exist = false;
			for (Node node : unmodNodes) {
				controller = (QueueController) node.getUserData();
				if (queues[i].getTitle().equals(controller.getId())) {
					controller.update(queues[i]);
					exist = true;
					break;
				}
			}
			
			if (!exist) {
				//Add queue layout
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(AppMain.class.getResource("/view/QueueLayout.fxml"));
	            FlowPane queuePane = null;
		        try {
		            queuePane = (FlowPane) loader.load();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        controller = loader.getController();
		        queuePane.setUserData(controller);
		        
		        controller.setD(d);
		        controller.update(queues[i]);
		        
		        if (queueNodes.size() == 0) {
		        	queueNodes.add(queuePane);
		        } else {
			        queueNodes.add(i + fix, queuePane);
			        fix++;
		        }
			}
		}
		
		for (Node node : unmodNodes) {
			exist = false;
			controller = (QueueController) node.getUserData();
			for (int i = 0; i < queues.length; i++) {
				if (queues[i].getTitle().equals(controller.getId())) {
					exist = true;
					break;
				}
			}
			
			if (!exist) {
				queueNodes.remove(node);
			}
		}
	}
	
	protected void setOsums(Osums osums) {
		this.osums = osums;
	}
	
	protected void setConfiguration(Configuration config) {
		this.config = config;
        
        updater = new Updater(config);
        checkingUpdate = false;
        
        //TODO do freq check
        checkUpdate();
	}
	
	protected void setDaemon(IDaemon d) {
		this.d = d;
	}
	
	protected void fetchQueues() {
		try {
			queues = d.getQueues();
		} catch (RemoteException e) {
			e.printStackTrace();
    		Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
	        		Alert alert = new Alert(AlertType.ERROR, "Could not obtain queue status from daemon! Terminating", ButtonType.OK);
	        		alert.showAndWait();
	        		System.exit(-1);
				}
				
			});
			return;
		}
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				updateQueuesUi();
			}
			
		});
	}
	
	private void addQueue(String beatmapUrlId, boolean showPreview, boolean changeTab) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AppMain.class.getResource("/view/ProgressDialogLayout.fxml"));
        DialogPane progressPane = null;
        try {
			progressPane = (DialogPane) loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        ProgressDialogController progressController = loader.getController();
        
        progressController.getHeaderText().setText("Beatmap Download");
        progressController.getStatusText().setText("Status: Initializing");
        progressController.getProgressBar().setProgress(-1);
        
        Alert progressDialog = new Alert(AlertType.NONE);
        progressDialog.initStyle(StageStyle.UTILITY);
        progressDialog.initModality(Modality.APPLICATION_MODAL);
        progressDialog.setTitle("Beatmap Download");
        progressDialog.setDialogPane(progressPane);
        progressDialog.getButtonTypes().add(ButtonType.CANCEL);
        progressDialog.show();
        
        int id = -1;
        try {
        	id = Integer.parseInt(beatmapUrlId);
        } catch (NumberFormatException ignore) {
        	
        }
        
        String url = null;
        
        if (id > 0) {
        	if (id > 999999) { //probably beatmap? //TODO Modify to use new parser
        		url = "https://osu.ppy.sh/b/" + id;
        	} else {
        		url = "https://osu.ppy.sh/s/" + id;
        	}
        } else {
        	if (Osums.isVaildBeatMapUrl(beatmapUrlId)) {
        		url = beatmapUrlId;
        	} else {
        		Alert alert = new Alert(AlertType.WARNING, "Please enter a valid osu! beatmap link.", ButtonType.OK);
        		alert.showAndWait();
        		return;
        	}
        }
        
        if (showPreview) {
            progressController.getStatusText().setText("Status: Getting Configuration...");

            String user = config.getUser();
            String pass = config.getPass();
            
            if (user == null || user.isEmpty() || pass == null || pass.isEmpty()) {
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
		        loginDialog.showAndWait();
		        
		        String usr = loginController.getUser();
                String pwd = loginController.getPwd();
		        
                if (usr == null || pwd == null || usr.isEmpty() || pwd.isEmpty()) {
                	Alert alert = new Alert(AlertType.WARNING, "Username or password must not be empty.", ButtonType.OK);
                	alert.showAndWait();
                	return;
                }
                
                user = usr;
                pass = pwd;
            }
            
            //TODO Remove once new parser implemented
            final String modUrl = config.isUseOldParser() ? url.replace("osu.ppy.sh", "old.ppy.sh") : url;
        	final String _url = url;
        	
            //OsuBeatmap map = null;
            
            final String _user = user;
            final String _pass = pass;
            
            Thread thread = new Thread() {
            	public void run() {
            		Platform.runLater(new Runnable() {
						@Override
						public void run() {
		            		progressController.getStatusText().setText("Status: Logging in...");
						}
					});
            		
                    try {
                        osums.login(_user, _pass);
                    } catch (WithDumpException e) {
                        e.printStackTrace();
                        Platform.runLater(new Runnable() {
							@Override
							public void run() {
		                		Alert alert = new Alert(AlertType.INFORMATION, "Error logging in:\n" + e.getDump().getStacktrace(), ButtonType.OK);
		                		alert.showAndWait();
							}
						});
                        return;
                    }
                    
                    OsuBeatmap map = null;
            		Platform.runLater(new Runnable() {
						@Override
						public void run() {
		                    progressController.getStatusText().setText("Status: Obtaining beatmap information...");
						}
					});
                    try {
                        map = osums.getBeatmapInfo(modUrl);
                    } catch (WithDumpException e) {
                        e.printStackTrace();
                        Platform.runLater(new Runnable() {
							@Override
							public void run() {
		                		Alert alert = new Alert(AlertType.INFORMATION, "Error getting beatmap info:\n" + e.getDump().getStacktrace(), ButtonType.OK);
		                		alert.showAndWait();
							}
						});
                        return;
                    }
                    
                    final OsuBeatmap _map = map;
                    Platform.runLater(new Runnable() {
            			@Override
            			public void run() {
            				progressDialog.hide();
            				
            				FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(AppMain.class.getResource("/view/BeatmapPreviewDialogLayout.fxml"));
                            DialogPane dialogPane = null;;
                            try {
                    			dialogPane = (DialogPane) loader.load();
                    		} catch (IOException e) {
                    			e.printStackTrace();
                    		}
                            
                            BeatmapPreviewDialogController controller = loader.getController();
                            controller.preview(_map);
                            
            				Alert previewDialog = new Alert(AlertType.NONE);
                            previewDialog.initStyle(StageStyle.UTILITY);
                            previewDialog.initModality(Modality.APPLICATION_MODAL);
                            previewDialog.setTitle("Beatmap Preview");
                            previewDialog.setDialogPane(dialogPane);
                            
                            List<ButtonType> btnTypes = previewDialog.getButtonTypes();
                            btnTypes.add(ButtonType.YES);
                            btnTypes.add(ButtonType.NO);
                            previewDialog.showAndWait();
                            
                            ButtonType result = previewDialog.getResult();
                            
                            if (result != ButtonType.YES) {
                            	progressDialog.close();
                            	return;
                            }
                            
                            progressDialog.show();
                        	uiRequestQueue(progressDialog, progressController, _url, changeTab);
            			}
            		});
            	}
            };
            thread.start();
        } else {
        	uiRequestQueue(progressDialog, progressController, url, changeTab);
        }        
	}
	
	private void uiRequestQueue(Alert progressDialog, ProgressDialogController progressController, final String url, boolean changeTab) {
		progressController.getStatusText().setText("Status: Requesting daemon...");
        new Thread() {
        	public void run() {
        		requestQueue(url);
        		Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
	                    progressDialog.close();
	                    
	                    if (changeTab) {
	                    	SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
	                        selectionModel.clearAndSelect(1);
	                    }
					}
				});
        	}
        }.start();
	}
	
	private boolean requestQueue(String url) {
		int downloadAction = -1;
        String targetFileOrFolder = null;
        
        if (rdBtnUseDefault.isSelected()) {
            downloadAction = -1;
        } else if (rdBtnDwnImport.isSelected()) {
            downloadAction = 0;
        } else if (rdBtnDwnOsuSong.isSelected()) {
            downloadAction = 1;
        } else if (rdBtnDwnFile.isSelected()) {
            downloadAction = 2;
            targetFileOrFolder = "";
        } else if (rdBtnDwnFolder.isSelected()) {
            downloadAction = 3;
            targetFileOrFolder = "";
        }


		MethodResult<Integer> result = null;
        try {
            result = d.addQueue(url, downloadAction, targetFileOrFolder);
        } catch (RemoteException e) {
            e.printStackTrace();
            
            DumpManager.addDump(new DebugDump(null, "Process download action", "Request daemon to add queue", "(Method End)", "Unable to request daemon to add queue", false, e));
            
    		Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.OK);
    		alert.setHeaderText("Unable to request daemon to add queue");
    		alert.showAndWait();
    		
            DumpManager.forceMetricsReport();
            return false;
        }
        
        if (result == null) {
        	Alert alert = new Alert(AlertType.ERROR, "No result was returned from daemon.", ButtonType.OK);
    		alert.setHeaderText("Unable to request daemon to add queue");
    		alert.showAndWait();
        	return false;
        } else if (result.getResult() != ErrorCode.RESULT_OK) {
        	String header = null;
        	String msg = null;
        	AlertType type = null;
        	switch (result.getResult()) {
        	case ErrorCode.RESULT_NO_CREDENTIALS:
        		header = "No credentials available";
        		msg = "Please enter your credentials in the Preferences in order to download beatmaps.";
        		type = AlertType.WARNING;
        		break;
        	case ErrorCode.RESULT_LOGIN_FAILED:
        		header = "Login Failed";
        		msg = "Please validate your credentials entered in the Preferences.";
        		type = AlertType.ERROR;
        		break;
        	case ErrorCode.RESULT_GET_BEATMAP_INFO_FAILED:
        		header = "Could not obtain beatmap info";
        		msg = "Is your beatmap link/ID correct?";
        		type = AlertType.ERROR;
        		break;
        	case ErrorCode.RESULT_VALIDATE_DOWNLOAD_URL_FAILED:
        		header = "Invalid download link received";
        		msg = "Is your beatmap link/ID correct?";
        		type = AlertType.ERROR;
        		break;
            default:
            	header = "Unable to request daemon to add queue";
            	msg = "Unknown result code.";
        		type = AlertType.ERROR;
        	}
        	Alert alert = new Alert(type, msg, ButtonType.OK);
    		alert.setHeaderText(header);
    		alert.showAndWait();
        	return false;
        } else {
        	return true;
        }
	}
	
	private UpdateInfo getUpdateInfoByConfig() throws WithDumpException {
        String algo = config.getCheckUpdateAlgo();
        if (algo.equals(Configuration.CHECK_UPDATE_ALGO_PER_VER_PER_BRANCH)) {
            return updater.getPerVersionPerBranchLatestVersion();
        } else if (algo.equals(Configuration.CHECK_UPDATE_ALGO_LATEST_VER_PER_BRANCH)) {
            return updater.getLatestVersion();
        } else { //TODO: Implement other algo
            return updater.getLatestVersion();
        }
    }
    
	public void checkUpdate() {
        if (checkingUpdate) {
            return;
        }
        
        checkingUpdate = true;
        Thread thread = new Thread() {
            public void run() {
            	Platform.runLater(new Runnable() {
					@Override
					public void run() {
		            	updateText.setText("Checking for updates...");
					}
				});
                
                UpdateInfo verInfo = null;
                try {
                    verInfo = getUpdateInfoByConfig();
                } catch (NoBuildsForVersionException e){
                	Platform.runLater(new Runnable() {
    					@Override
    					public void run() {
    						updateText.setText("No builds available for the new version. See dump.");
    					}
    				});
                	checkingUpdate = false;
                    return;
                } catch (NoSuchVersionException e){
                	Platform.runLater(new Runnable() {
    					@Override
    					public void run() {
    						updateText.setText("No current version in the selected branch. See dump.");
    			    		Alert alert = new Alert(AlertType.INFORMATION, 
    			    				"We don't have version " + Osumer.OSUMER_VERSION + " in the current update branch\n\n" +
    			    				"Please try another update branch (snapshot, beta, stable).",
    			    						ButtonType.OK);
    			    		alert.setHeaderText("osumer - Version not available");
    			    		alert.showAndWait();
    					}
    				});
                    checkingUpdate = false;
                    return;
                } catch (NoSuchBuildNumberException e){
                	Platform.runLater(new Runnable() {
    					@Override
    					public void run() {
    						updateText.setText("This version has a invalid build number. See dump");
    						Alert alert = new Alert(AlertType.WARNING, 
    								"We don't have build number greater or equal to " + Osumer.OSUMER_BUILD_NUM + " in version " + Osumer.OSUMER_VERSION + ".\n" +
    			                    "If you are using a modified/development osumer,\n" +
    			                    " you can just ignore this message.\n" +
    			                    "If not, this might be the versions.json in GitHub goes wrong,\n" +
    			                    " post a new issue about this.",
    			    				ButtonType.OK);
    			    		alert.setHeaderText("osumer - Build not available");
    			    		alert.showAndWait();
    					}
    				});
                	checkingUpdate = false;
                    return;
                } catch (WithDumpException e){
                    e.printStackTrace();
                	Platform.runLater(new Runnable() {
    					@Override
    					public void run() {
    						updateText.setText("Could not connect to update server.");
    						Alert alert = new Alert(AlertType.ERROR, 
    								"Could not connect to update server.",
    			    				ButtonType.OK);
    			    		alert.setHeaderText("osumer - Error Checking Update");
    			    		alert.showAndWait();
    					}
    				});
                	checkingUpdate = false;
                    return;
                }
                
                final UpdateInfo _verInfo = verInfo;

            	Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (_verInfo == null) {
    						updateText.setText("Could not obtain update info.");
    						Alert alert = new Alert(AlertType.ERROR, 
    								"Could not obtain update info.",
    			    				ButtonType.OK);
    			    		alert.setHeaderText("osumer - Error Checking Update");
    			    		alert.showAndWait();
		                	checkingUpdate = false;
		                    return;
		                }
						
		                if (_verInfo.isThisVersion()) {
    						updateText.setText("Running the latest: "
    	                            + " " + _verInfo.getVersion() + "-" + Updater.getBranchStr(_verInfo.getBranch()) + "-b" + _verInfo.getBuildNum());
		                	checkingUpdate = false;
		                    return;
		                }
		                
		                updateText.setText(
		                        (_verInfo.isUpgradedVersion() ? "Upgrade" : "Update") +
		                        " now to " + _verInfo.getVersion() +
		                        "-" + Updater.getBranchStr(_verInfo.getBranch()) +
		                        "-b" + _verInfo.getBuildNum());
		                
		                Alert alert = new Alert(AlertType.INFORMATION, "", ButtonType.YES);
						alert.getButtonTypes().add(ButtonType.NO);
			    		alert.setHeaderText("Update available");
			    		
		                String desc = _verInfo.getDescription();
		                
		                alert.setContentText(
		                		"New " +
		                	    (_verInfo.isUpgradedVersion() ? "upgrade" : "update") +
		                        " available! New version:\n" + _verInfo.getVersion() +
		                        "-" + Updater.getBranchStr(_verInfo.getBranch()) +
		                        "-b" + _verInfo.getBuildNum() + "\n\n" +
		                        "Do you want to update it now?"
		                );
		                
		            	ButtonType detailsBtn = new ButtonType("Details");
		            	
		                if (desc != null){
		    				alert.getButtonTypes().add(detailsBtn);
		                }
		                
		                Alert detailsAlert = new Alert(AlertType.NONE, 
								"",
			    				ButtonType.OK);
						detailsAlert.setHeaderText("Change-log");
						detailsAlert.setContentText(desc);
						
		                ButtonType result;
		                do {
		    	    		alert.showAndWait();
		    	    		
		                	result = alert.getResult();
		    	    		
		                    if (result == detailsBtn){
	    						detailsAlert.showAndWait();
		                    }
		                } while (result == detailsBtn);
		                
		                if (result == ButtonType.YES){
		                	/*
		                    try {
		                        Desktop.getDesktop().browse(new URI(_verInfo.getWebLink()));
		                    } catch (IOException | URISyntaxException e) {
		                        DebugDump dump = new DebugDump(
		                                _verInfo.getWebLink(),
		                                "Show option dialog of updating osumer or not",
		                                "Set checkingUpdate to false",
		                                "(End of function / thread)",
		                                "Error when opening the web page",
		                                false,
		                                e);
		                        DumpManager.addDump(dump);
		                        //DebugDump.showDebugDialog(dump);
		                    }
		                    */
		                    
		                    try {
		                        String updaterLink = Updater.getUpdaterLink();
		                        
		                        if (updaterLink == null){
		                            System.out.println("No latest updater .exe defined! Falling back to legacy updater!");
		                            updaterLink = Updater.LEGACY_UPDATER_JAR;
		                        }
		                        
		                        URL url;
		                        try {
		                            url = new URL(updaterLink);
		                        } catch (MalformedURLException e) {
		                            e.printStackTrace();
		                        	Alert alert0 = new Alert(AlertType.ERROR, "Error:\n" + e, ButtonType.OK);
		                        	alert0.showAndWait();
		                        	return;
		                        }
		                        
		                        final String folder = System.getProperty("java.io.tmpdir");
		                        final String fileName = "osumer_updater_" + Calendar.getInstance().getTimeInMillis() + ".exe";
		                        
		                        FXMLLoader loader = new FXMLLoader();
		                        loader.setLocation(AppMain.class.getResource("/view/ProgressDialogLayout.fxml"));
		                        DialogPane progressPane = null;
		                        try {
		                			progressPane = (DialogPane) loader.load();
		                		} catch (IOException e1) {
		                			e1.printStackTrace();
		                		}
		                        ProgressDialogController progressController = loader.getController();
		                        
		                        progressController.getHeaderText().setText("Update");
		                        progressController.getStatusText().setText("Status: Initializing...");
		                        progressController.getProgressBar().setProgress(-1);
		                        
		                        boolean noClose = false;
		                        Alert progressDialog = new Alert(AlertType.NONE);
		                        progressDialog.initStyle(StageStyle.UTILITY);
		                        progressDialog.initModality(Modality.APPLICATION_MODAL);
		                        progressDialog.setTitle("");
		                        progressDialog.setDialogPane(progressPane);
		                        progressDialog.getButtonTypes().add(ButtonType.CANCEL);
		                        
		                        Thread thread = new Thread() {
		                        	public void run() {
			        					Platform.runLater(new Runnable() {
											@Override
											public void run() {
						                        progressController.getStatusText().setText("Status: Downloading updater...");
											}
										});
			        					
				                        URLDownloader dwn = new URLDownloader(folder, fileName, url);
				                        
				                        dwn.download();
				                        
				                        while (dwn.getStatus() == OsuDownloader.DOWNLOADING){
				        					if (this.isInterrupted()){
				        						return;
				        					}
				        					
				        					int progress = (int) dwn.getProgress();
				        					
				        					Platform.runLater(new Runnable() {
												@Override
												public void run() {
													progressController.getProgressBar().setProgress(progress / 100.0);
												}
											});
				        					
				        					try {
												Thread.sleep(50);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
				        				}
			        					
			        					Platform.runLater(new Runnable() {
											@Override
											public void run() {
												progressController.getProgressBar().setProgress(-1);
											}
										});
				        				
				        				if (dwn.getStatus() == OsuDownloader.ERROR){
				        					Platform.runLater(new Runnable() {
												@Override
												public void run() {
							                        progressController.getStatusText().setText("Status: Error when downloading updater. Please restart osumer.");
												}
											});
				        					
				        					System.out.println("Download failed.");
				        				} else if (dwn.getStatus() == OsuDownloader.COMPLETED){
				        					String loc = folder + "\\" + fileName;

				        					Platform.runLater(new Runnable() {
												@Override
												public void run() {
							                        progressController.getStatusText().setText("Status: Download completed. Starting...");
												}
											});
				        					System.out.println("Download completed...");
				        					
				        					try {
												Thread.sleep(2000);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
				        					
				        					try {
				        						Runtime.getRuntime().exec("cmd.exe /c " + loc + " -install");
				        					} catch (IOException e1) {
				        						e1.printStackTrace();
				        						DumpManager.addDump(new DebugDump(
				        								null,
				        								"(If[openFile] scope) (UI) Set status to lblStatus",
				        								"(Try scope) Open file loc using Desktop.getDesktop.open()",
				        								"(Try scope) Sleep 2000 ms (2 sec)",
				        								"Unable to open file",
				        								false,
				        								e1));
				        					}
				        					
				        					try {
												Thread.sleep(2000);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
				        					
				        					Platform.exit();
				        					System.exit(0);
				        					return;
				        				}
		                        	}
		                        };
		                        thread.start();
		                        
		                        progressDialog.showAndWait();
		                    } catch (WithDumpException e){
		                        e.printStackTrace();
		                        checkingUpdate = false;
	                        	Alert alert0 = new Alert(AlertType.ERROR, "Error:\n" + e, ButtonType.OK);
	                        	alert0.showAndWait();
	                        	return;
		                    }
		                }
		                checkingUpdate = false;
	                }
				});
            }
        };
        thread.start();
    }

}
