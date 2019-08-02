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
package com.github.mob41.osumer;

import java.io.File;
import java.io.IOException;

import com.github.mob41.osumer.updater.Updater;

public class Osumer {

    // TODO: Hard-code version?

    public static final String OSUMER_VERSION = "2.0.0";

    public static final String OSUMER_BRANCH = "snapshot";

    public static final int OSUMER_BUILD_NUM = 9;
    
    public static String getVersionString() {
    	return OSUMER_VERSION + "-" + OSUMER_BRANCH + "-b" + OSUMER_BUILD_NUM;
    }

    private Osumer() {
    
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }

    public static boolean isWindowsElevated() {
        if (!isWindows()) {
            return false;
        }

        final String programfiles = System.getenv("PROGRAMFILES");

        if (programfiles == null || programfiles.length() < 1) {
            throw new IllegalStateException("OS mismatch. Program Files directory not detected");
        }

        File testPriv = new File(programfiles);
        if (!testPriv.canWrite()) {
            return false;
        }
        File fileTest = null;

        try {
            fileTest = File.createTempFile("testsu", ".dll", testPriv);
        } catch (IOException e) {
            return false;
        } finally {
            if (fileTest != null) {
                fileTest.delete();
            }
        }
        return true;
    }

    public static int updateSourceStrToInt(String branchStr) {
        if (branchStr.equals("snapshot")) {
            return 2;
        } else if (branchStr.equals("beta")) {
            return 1;
        } else if (branchStr.equals("stable")) {
            return 0;
        }
        return -1;
    }

}
