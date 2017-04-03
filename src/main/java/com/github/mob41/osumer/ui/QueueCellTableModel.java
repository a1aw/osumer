package com.github.mob41.osumer.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class QueueCellTableModel extends AbstractTableModel {
	
	private List<QueueCell> cells;

	public QueueCellTableModel() {
		cells = new ArrayList<QueueCell>(50);
	}
	
	public void addCell(QueueCell cell){
		cells.add(cell);
	}
	
	public void removeCell(QueueCell cell){
		cells.remove(cell);
	}
	
	public void removeCell(int i){
		cells.remove(i);
	}
	
	public void removeAll(){
		cells.clear();
	}
	
	public Class getColumnClass(int columnIndex){
		return QueueCell.class;
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
		return cells.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		return cells.get(arg0);
	}

}
