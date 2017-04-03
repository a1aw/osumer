package com.github.mob41.osumer.ui;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class QueueCellRenderer implements TableCellRenderer {
	
	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4,
			int arg5) {
		return (JPanel) arg1;
	}

}
