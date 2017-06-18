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
package com.github.mob41.osumer.sock;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DumpManager;
import com.github.mob41.osumer.ui.UIFrame;

public class SockThread extends Thread {

    public static final int PORT = 46725;

    private boolean running = false;

    private ServerSocket sock = null;

    private final UIFrame frame;

    public SockThread(UIFrame frame) {
        this.frame = frame;
    }

    public static boolean testPortFree(int port) {
        try {
            new ServerSocket(port).close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void run() {
        if (!running) {
            running = true;

            try {
                sock = new ServerSocket(PORT, 0, InetAddress.getLoopbackAddress());
            } catch (IOException e1) {
                e1.printStackTrace();
                DebugDump dump = new DebugDump(null, null, "Opening osumer socket", null,
                        "Could not open socket at 46725 for BG call. Another osumer application running?", false, e1);
                DumpManager.getInstance().addDump(dump);
                DebugDump.showDebugDialog(dump);
                System.exit(-1);
                return;
            }

            try {
                while (running) {
                    Socket cs = sock.accept();
                    new ConnThread(this, cs).start();
                }
                sock.close();
            } catch (IOException e) {
                DebugDump dump = new DebugDump(null, null, "ServerSocket breaks at exception", null,
                        "Unexpected ServerSocket break", false, e);
                DumpManager.getInstance().addDump(dump);
                DebugDump.showDebugDialog(dump);
                System.exit(-1);
                return;
            }

            running = false;
        }
    }

    public UIFrame getFrame() {
        return frame;
    }

}
