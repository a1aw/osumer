package com.github.mob41.osumer.io;

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
		return Updater.compareVersion(Osu.OSUMER_VERSION, version) == -1 &&
				Osu.OSUMER_BUILD_NUM < buildNum;
	}
}
