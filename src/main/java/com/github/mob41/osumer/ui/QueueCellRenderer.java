package com.github.mob41.osumer.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.github.mob41.osumer.io.queue.Queue;

public class QueueCellRenderer implements TableCellRenderer {

    private QueueCell comp;

    public QueueCellRenderer(QueueCellTableModel model) {
        comp = new QueueCell(model);
    }

    @Override
    public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4,
            int arg5) {
        comp.updateData((Queue) arg1);
        if (arg2) {
            comp.setBackground(arg0.getSelectionBackground());
        } else {
            comp.setBackground(arg0.getBackground());
        }
        return comp;

    }

}
