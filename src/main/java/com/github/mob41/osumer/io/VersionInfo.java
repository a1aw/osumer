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
package com.github.mob41.osumer.io;

import com.github.mob41.osumer.io.officialosu.Osu;
import com.github.mob41.osumer.updater.Updater;

public class VersionInfo {
	
	private final String version;
	
	private final String branch;
	
	private final int buildNum;

	public VersionInfo(String version, String branch, int buildNum) {
		this.version = version;
		this.branch = branch;
		this.buildNum = buildNum;
	}

	public String getVersion() {
		return version;
	}

	public String getBranch() {
		return branch;
	}

	public int getBuildNum() {
		return buildNum;
	}
	
	public boolean isEqualToRunning(){
		return Updater.compareVersion(Osu.OSUMER_VERSION, version) == 0 &&
				Osu.OSUMER_BRANCH.equals(branch) &&
				Osu.OSUMER_BUILD_NUM == buildNum;
	}
	
	public boolean isNewerThanRunning(){
		int result = Updater.compareVersion(Osu.OSUMER_VERSION, version);
		return result == -1 ||
				(result == 0 && Osu.OSUMER_BUILD_NUM < buildNum);
	}
}
