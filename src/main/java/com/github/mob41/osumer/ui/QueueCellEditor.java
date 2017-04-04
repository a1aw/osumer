package com.github.mob41.osumer.ui;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.github.mob41.osumer.io.Queue;

public class QueueCellEditor extends AbstractCellEditor implements TableCellEditor {

	private QueueCell comp;
	
	public QueueCellEditor(QueueCellTableModel model){
		comp = new QueueCell(model);
	}
	
	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		comp.updateData((Queue) value);
		if (isSelected){
			comp.setBackground(table.getSelectionBackground());
		} else {
			comp.setBackground(table.getBackground());
		}
		return comp;
	}

}
