package com.github.mob41.osumer.io;

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
}
