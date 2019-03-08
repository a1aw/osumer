package com.github.mob41.osumer.queue.actions;

import java.io.File;

import com.github.mob41.organdebug.DebugDump;
import com.github.mob41.organdebug.DumpManager;
import com.github.mob41.osumer.Configuration;
import com.github.mob41.osumer.queue.Queue;
import com.github.mob41.osumer.queue.QueueAction;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AfterSoundAction implements QueueAction {
    
    private final Configuration config;
    
    public AfterSoundAction(Configuration config) {
        this.config = config;
    }
    
    @Override
    public void run(Queue queue) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Media m = new Media(new File(config.getToneAfterDownloadPath()).toURI().toString());
                    MediaPlayer mp = new MediaPlayer(m);
                    mp.play();
                } catch (Exception e) {
                    e.printStackTrace();
                    DumpManager.getInstance().addDump(new DebugDump(null, "---", "Play after download sound", "---", "Error occurred when trying to play sound", false, e));
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}
