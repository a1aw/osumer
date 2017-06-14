/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Anthony Law
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
package com.github.mob41.osumer.updater;

public class UpdateInfo {

    private final String version;

    private final String webLink;

    private final String exeLink;

    private final String jarLink;

    private final String description;

    private final int branch;

    private final int buildNum;

    private final boolean isThisVersion;

    private final boolean upgradedVersion;

    public UpdateInfo(String description, String version, int branch, int buildNum, String webLink, String exeLink,
            String jarLink, boolean isThisVersion, boolean upgradedVersion) {
        this.description = description;
        this.version = version;
        this.webLink = webLink;
        this.exeLink = exeLink;
        this.jarLink = jarLink;
        this.branch = branch;
        this.buildNum = buildNum;
        this.isThisVersion = isThisVersion;
        this.upgradedVersion = upgradedVersion;
    }

    public String getVersion() {
        return version;
    }

    public int getBranch() {
        return branch;
    }

    public int getBuildNum() {
        return buildNum;
    }

    public String getDescription() {
        return description;
    }

    public boolean isThisVersion() {
        return isThisVersion;
    }

    public boolean isUpgradedVersion() {
        return upgradedVersion;
    }

    public String getExeLink() {
        return exeLink;
    }

    public String getJarLink() {
        return jarLink;
    }

    public String getWebLink() {
        return webLink;
    }

}
