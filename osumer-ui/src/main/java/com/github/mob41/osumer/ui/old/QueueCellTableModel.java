/*******************************************************************************
 * Any modification, copies of sections of this file must be attached with this
 * license and shown clearly in the developer's project. The code can be used
 * as long as you state clearly you do not own it. Any violation might result in
 *  a take-down.
 *
 * MIT License
 *
 * Copyright (c) 2016, 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.github.mob41.osumer.ui.old;

import java.rmi.RemoteException;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import com.github.mob41.osumer.queue.QueueStatus;
import com.github.mob41.osumer.rmi.IDaemon;

public class QueueCellTableModel extends AbstractTableModel {

    private IDaemon d;
    
    private boolean enabled = true;

    public QueueCellTableModel(IDaemon d) {
        this.d = d;
    }

    public Class getColumnClass(int columnIndex) {
        return QueueStatus.class;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return "Running queue(s)";
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getRowCount() {
        if (!enabled) {
            return 0;
        }
        QueueStatus[] queues = null;
        try {
            queues = d.getQueues();
        } catch (RemoteException e) {
            e.printStackTrace();
            enabled = false;
            JOptionPane.showMessageDialog(null, "Queue Synchronization will be disabled from now on. Please restart the application to re-enable synchronization.\nCould not connect to daemon:\n" + e.getMessage(), "Daemon Queue Synchronization Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        if (queues == null) {
            return 0;
        } else {
            return queues.length;
        }
        /*
        if (mgr == null || mgr.getList() == null) {
            return 0;
        }
        return mgr.getList().size();
        */
    }

    @Override
    public Object getValueAt(int arg0, int arg1) {
        if (!enabled) {
            return null;
        }
        QueueStatus[] queues = null;
        try {
            queues = d.getQueues();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        
        if (queues == null) {
            return null;
        } else {
            return queues[arg0];
        }
        /*
        if (mgr == null || mgr.getList() == null) {
            return null;
        }
        return mgr.getList().get(arg0);
        */
    }

}
