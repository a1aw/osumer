package com.github.mob41.osumer.ui;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.Osumer;
import com.github.mob41.osumer.rmi.IDaemon;
import com.github.mob41.osumer.rmi.IUI;
import com.github.mob41.osumer.ui.old.UIFrame_old;
import com.github.mob41.osums.io.beatmap.Osums;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro8.JMetro;

public class AppMain extends Application {

    public static final String INTRO = "osumer2 (osuMapDownloadEr) by mob41\n" + "Licensed under MIT License\n" + "\n"
            + "https://github.com/mob41/osumer\n" + "\n" + "This is a unoffical software to download beatmaps.\n" + "\n"
            + "Disclaimer:\n" + "This software does not contain malicious code to send\n"
            + "username and password to another server other than\n"
            + "osu!'s login server. This is a Open Source software.\n"
            + "You can feel free to look through the code. If you still\n"
            + "feel uncomfortable with this software, you can simply\n" + "stop using it. Thank you!\n";
    
    private static final int RMI_DAEMON_PORT = 46726;
    
    private static final int RMI_UI_PORT = 46727;

    private static final String RMI_DAEMON_PATH = "daemon";
    
    private static final String RMI_UI_PATH = "ui";
	
    private Stage primaryStage;
    
    private BorderPane rootLayout;
    
    private Configuration config;
    
    private IDaemon d;
    
    private Osums osums;

	private MainController controller;

	private IUI ui;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
		//Arg is handled by osumer-launcher

        String configPath = Osumer.isWindows() ? System.getenv("localappdata") + "\\osumerExpress" : "";

        config = new Configuration(configPath, Configuration.DEFAULT_DATA_FILE_NAME);

        try {
            config.load();
        } catch (IOException e1) {
            System.err.println("Unable to load configuration");
            e1.printStackTrace();

            if (!GraphicsEnvironment.isHeadless()) {
        		Alert alert = new Alert(AlertType.ERROR, e1.getMessage(), ButtonType.OK);
        		alert.setHeaderText("Could not load configuration");
        		alert.showAndWait();
            }

            System.exit(-1);
            return;
        }
        
        String uiSuffix = RMI_UI_PORT + "/" + RMI_UI_PATH;
        String daemonSuffix = RMI_DAEMON_PORT + "/" + RMI_DAEMON_PATH;
        ui = null;
        try {
            ui = (IUI) Naming.lookup("rmi://localhost:" + uiSuffix); //Find any running UI
        } catch (Exception e) {
            //e.printStackTrace();
        }
        
        if (ui != null) {
            try {
                ui.wake();
            } catch (RemoteException e) {
                e.printStackTrace();
                String msg = 
                        "Unable to wake up the UI:\n" +
                        e.getMessage();
                System.err.println(msg);

                if (!GraphicsEnvironment.isHeadless()) {
            		Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
            		alert.setHeaderText("osumer RMI Connection Error");
            		alert.showAndWait();
                }

                System.exit(-1);
                return;
            }
            try {
				stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
            return;
        }
        
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("osumer-ui requires a graphics environment to launch its user interface.");
            System.exit(-1);
            return;
        }
        
        d = null;
        try {
            d = (IDaemon) Naming.lookup("rmi://localhost:" + daemonSuffix); //Contact the daemon via RMI
        } catch (Exception e) {
            e.printStackTrace();
            
            String msg = 
                    "Could not connect to daemon! Please ensure osumer-daemon is running properly.\n" +
                    "Instead of starting directly with \"osumer-ui.exe\", please use \"osumer.exe\" to launch osumer.";
            System.err.println(msg);

    		Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
    		alert.setHeaderText("osumer RMI Connection Error");
    		alert.showAndWait();

            System.exit(-1);
            return;
        }
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            LocateRegistry.createRegistry(RMI_UI_PORT);

            //AppUI.launch(args);
            
            //UIFrame_old frame = new UIFrame_old(config, d);
            
            ui = new UI(this);
            
            Naming.bind("rmi://localhost:" + uiSuffix, ui);
            //frame.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            String msg = 
                    "Could not register UI RMI registry on port " + RMI_UI_PORT + ":\n" +
                    e.getMessage();
            System.err.println(msg);

    		Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
    		alert.setHeaderText("osumer RMI Initialization Error");
    		alert.showAndWait();
            
            System.exit(-1);
            return;
        }
        
        try {
			d.registerUi(ui);
		} catch (RemoteException e) {
            e.printStackTrace();
            
            String msg = 
                    "Could not register UI to daemon\n" +
                    e.getMessage();
            System.err.println(msg);

    		Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
    		alert.setHeaderText("osumer RMI Initialization Error");
    		alert.showAndWait();
            
            System.exit(-1);
            return;
		}
		
        primaryStage.setTitle("osumer2");
        primaryStage.getIcons().add(new Image(AppMain.class.getResourceAsStream("image/osumerIcon_64px.png")));
        
        osums = new Osums();

        initRootLayout();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		if (d != null && ui != null) {
			d.unregisterUi(ui);
		}
		System.exit(0);
		return;
	}
    
    /**
     * Initializes the root layout.
     */
    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppMain.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            controller = loader.getController();
            controller.setDaemon(d);
            controller.setConfiguration(config);
            controller.setOsums(osums);
            controller.fetchQueues();
            //rootLayout.setStyle("-fx-background-color: #111;");
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            new JMetro(JMetro.Style.DARK).applyTheme(scene);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AppMain.launch(args);
    }
    
	public void onQueueStatusUpdate() throws RemoteException {
		controller.fetchQueues();
	}

	public void wake() throws RemoteException {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				primaryStage.setAlwaysOnTop(true);
				primaryStage.requestFocus();
				primaryStage.setAlwaysOnTop(false);
			}
		});
	}
	
}
