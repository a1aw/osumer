package com.github.mob41.osumer.ui;

import javax.swing.table.AbstractTableModel;

import com.github.mob41.osumer.io.queue.Queue;
import com.github.mob41.osumer.io.queue.QueueManager;

public class QueueCellTableModel extends AbstractTableModel {
	
	private QueueManager mgr;

	public QueueCellTableModel(QueueManager mgr) {
		this.mgr = mgr;
	}
	
	public Class getColumnClass(int columnIndex){
		return Queue.class;
	}
	
	@Override
	public String getColumnName(int columnIndex){
		return "Running queue(s)";
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		if (mgr == null || mgr.getList() == null){
			return 0;
		}
		return mgr.getList().size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		if (mgr == null || mgr.getList() == null){
			return null;
		}
		return mgr.getList().get(arg0);
	}

}
