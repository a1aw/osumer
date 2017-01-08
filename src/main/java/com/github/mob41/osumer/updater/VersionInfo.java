package com.github.mob41.osumer.updater;

public class VersionInfo {
	
	private final String version;
	
	private final String webLink;
	
	private final String exeLink;
	
	private final String jarLink;
	
	private final int branch;
	
	private final int buildNum;
	
	private final boolean isThisVersion;
	
	private final boolean upgradedVersion;

	public VersionInfo(String version, int branch, int buildNum, String webLink, String exeLink, String jarLink, boolean isThisVersion, boolean upgradedVersion) {
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
	
	public boolean isThisVersion(){
		return isThisVersion;
	}
	
	public boolean isUpgradedVersion(){
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
