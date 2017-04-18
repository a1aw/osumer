package com.github.mob41.osumer.io.queue;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.github.mob41.osumer.io.Downloader;

public class BeatmapImportAction implements QueueAction {
	
	@Override
	public void run(Queue queue) {
		Downloader dwn = queue.getDownloader();
		String path = dwn.getDownloadFolder() + dwn.getFileName();
		
		File file = new File(path + ".osz");
		
		if (!file.exists()){
			System.out.println("File not exists: " + path + ".osz");
			return;
		}
		
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
