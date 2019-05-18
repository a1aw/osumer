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
package com.github.mob41.osumer.launcher;

public class ArgParser {

    private final String[] args;

    private boolean showIconsFlag = false;

    private boolean hideIconsFlag = false;

    private boolean reinstallFlag = false;

    private boolean installFlag = false;

    private boolean uninstallFlag = false;

    private boolean forceFlag = false;

    private boolean quietFlag = false;

    private boolean uiFlag = false;

    private boolean noUiFlag = false;

    private boolean versionFlag = false;

    private boolean daemonFlag = false;

    public ArgParser(String[] args) {
        this.args = args;

        if (args == null) {
            return;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-hideicons")) {
                setHideIconsFlag(true);
            } else if (args[i].equals("-showicons")) {
                setShowIconsFlag(true);
            } else if (args[i].equals("-reinstall")) {
                setReinstallFlag(true);
            } else if (args[i].equals("-install")) {
                setInstallFlag(true);
            } else if (args[i].equals("-uninstall")) {
                setUninstallFlag(true);
            } else if (args[i].equals("-force")) {
                setForceFlag(true);
            } else if (args[i].equals("-quiet")) {
                setQuietFlag(true);
            } else if (args[i].equals("-ui")) {
                setUiFlag(true);
            } else if (args[i].equals("-noui")) {
                setNoUiFlag(true);
            } else if (args[i].equals("-version")) {
                setVersionFlag(true);
            } else if (args[i].equals("-daemon")) {
                setDaemonFlag(true);
            }
        }
    }

    public boolean isShowIconsFlag() {
        return showIconsFlag;
    }

    public void setShowIconsFlag(boolean showIconsFlag) {
        this.showIconsFlag = showIconsFlag;
    }

    public boolean isHideIconsFlag() {
        return hideIconsFlag;
    }

    public void setHideIconsFlag(boolean hideIconsFlag) {
        this.hideIconsFlag = hideIconsFlag;
    }

    public boolean isReinstallFlag() {
        return reinstallFlag;
    }

    public void setReinstallFlag(boolean reinstallFlag) {
        this.reinstallFlag = reinstallFlag;
    }

    public boolean isInstallFlag() {
        return installFlag;
    }

    public void setInstallFlag(boolean installFlag) {
        this.installFlag = installFlag;
    }

    public boolean isUninstallFlag() {
        return uninstallFlag;
    }

    public void setUninstallFlag(boolean uninstallFlag) {
        this.uninstallFlag = uninstallFlag;
    }

    public boolean isForceFlag() {
        return forceFlag;
    }

    public void setForceFlag(boolean forceFlag) {
        this.forceFlag = forceFlag;
    }

    public boolean isQuietFlag() {
        return quietFlag;
    }

    public void setQuietFlag(boolean quietFlag) {
        this.quietFlag = quietFlag;
    }

    public boolean isUiFlag() {
        return uiFlag;
    }

    public void setUiFlag(boolean uiFlag) {
        this.uiFlag = uiFlag;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean isVersionFlag() {
        return versionFlag;
    }

    public void setVersionFlag(boolean versionFlag) {
        this.versionFlag = versionFlag;
    }

    public boolean isNoUiFlag() {
        return noUiFlag;
    }

    public void setNoUiFlag(boolean noUiFlag) {
        this.noUiFlag = noUiFlag;
    }

    public boolean isDaemonFlag() {
        return daemonFlag;
    }

    public void setDaemonFlag(boolean daemonFlag) {
        this.daemonFlag = daemonFlag;
    }

}
