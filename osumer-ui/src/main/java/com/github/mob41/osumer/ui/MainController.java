package com.github.mob41.osumer.ui;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.github.mob41.organdebug.exceptions.DebuggableException;
import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.queue.QueueStatus;
import com.github.mob41.osumer.rmi.IDaemon;
import com.github.mob41.osums.io.beatmap.OsuBeatmap;
import com.github.mob41.osums.io.beatmap.Osums;

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
	
	private Configuration config;
	
	private IDaemon d;
	
	private Osums osums;
	
	private QueueStatus[] queues;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
        beatmapDwnBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				boolean showPreview = showPreviewCheckbox.isSelected();
				String idUrl = beatmapUrlText.getText();
				if (idUrl == null || idUrl.isEmpty()) {
	        		Alert alert = new Alert(AlertType.WARNING, "Please enter a valid osu! beatmap link.", ButtonType.OK);
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
	            loader.setLocation(AppMain.class.getResource("view/PreferencesLayout.fxml"));
	            BorderPane borderPane = null;
		        try {
		        	borderPane = (BorderPane) loader.load();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        
		        Scene scene = new Scene(borderPane);
		        
		        new JMetro(JMetro.Style.DARK).applyTheme(scene);

		        Stage stage = new Stage();
		        stage.setScene(scene);
		        //stage.setTitle("Preferences");
		        stage.initStyle(StageStyle.UTILITY);
		        stage.initModality(Modality.APPLICATION_MODAL);
		        stage.showAndWait();
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
	            loader.setLocation(AppMain.class.getResource("view/QueueLayout.fxml"));
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
        loader.setLocation(AppMain.class.getResource("view/ProgressDialogLayout.fxml"));
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
            	//TODO Do JavaFX version login
            	/*
                pbd.getLabel().setText("Status: Prompting username and password...");
                LoginPanel loginPanel = new LoginPanel();
                int option = JOptionPane.showOptionDialog(UIFrame_old.this, loginPanel, "Login to osu!",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
                        JOptionPane.CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    if (loginPanel.getUsername().isEmpty() || loginPanel.getPassword().isEmpty()) {
                        JOptionPane.showMessageDialog(UIFrame_old.this, "Username or password cannot be empty.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        pbd.dispose();
                        return;
                    }

                    user = loginPanel.getUsername();
                    pass = loginPanel.getPassword();
                } else {
                    pbd.dispose();
                    return;
                }
                */
        		Alert alert = new Alert(AlertType.INFORMATION, "Not implmented: JavaFX Login", ButtonType.OK);
        		alert.showAndWait();
            	return;
            }
            
            //TODO Remove once new parser implemented
            final String modUrl = config.isLegacyEnableOldSiteBeatmapRedirecting() ? url.replace("osu.ppy.sh", "old.ppy.sh") : url;
        	final String _url = url;
        	
            OsuBeatmap map = null;
            
            Thread thread = new Thread() {
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
                    } catch (DebuggableException e) {
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
                            loader.setLocation(AppMain.class.getResource("view/BeatmapPreviewDialogLayout.fxml"));
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


		boolean success = false;
        try {
            success = d.addQueue(url, downloadAction, targetFileOrFolder);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return success;
	}

}
