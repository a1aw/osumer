package com.github.mob41.osumer.io.queue.actions;

import java.io.IOException;

import com.github.mob41.organdebug.DebugDump;
import com.github.mob41.organdebug.DumpManager;
import com.github.mob41.osumer.exceptions.ErrorDumpDialog;
import com.github.mob41.osumer.io.queue.Queue;
import com.github.mob41.osumer.io.queue.QueueAction;

public class UpdaterRunAction implements QueueAction{
    
    private final String filePath;
    
    public UpdaterRunAction(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void run(Queue queue) {
        try {
            System.out.println("Starting: \"" + filePath + "\"");
            Runtime.getRuntime().exec("cmd.exe /c \"" + filePath + "\" -install");
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
            return;
        } catch (IOException e1) {
            e1.printStackTrace();
            DebugDump dump = new DebugDump(
                    null,
                    "(If[openFile] scope) (UI) Set status to lblStatus",
                    "(Try scope) Open file loc using Desktop.getDesktop.open()",
                    "(Try scope) Sleep 2000 ms (2 sec)",
                    "Unable to open file",
                    false,
                    e1);
            DumpManager.getInstance().addDump(dump);
            ErrorDumpDialog dialog = new ErrorDumpDialog(dump);
            dialog.setModal(true);
            dialog.setVisible(true);
        }
    }

}
