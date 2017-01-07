package com.github.mob41.osumer.updater;

public class VersionInfo {
	
	private final String version;
	
	private final int branch;
	
	private final int buildNum;
	
	private final boolean isThisVersion;
	
	private final boolean upgradedVersion;

	public VersionInfo(String version, int branch, int buildNum, boolean isThisVersion, boolean upgradedVersion) {
		this.version = version;
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
	
	public boolean isThisVersion(){
		return isThisVersion;
	}
	
	public boolean isUpgradedVersion(){
		return upgradedVersion;
	}

}
