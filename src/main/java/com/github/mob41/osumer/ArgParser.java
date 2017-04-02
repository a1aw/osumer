package com.github.mob41.osumer;

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

	public ArgParser(String[] args) {
		this.args = args;

		if (args == null){
			return;
		}
		
		for (int i = 0 ; i < args.length; i++){
			if (args[i].equals("-hideicons")){
				setHideIconsFlag(true);
			} else if (args[i].equals("-showicons")){
				setShowIconsFlag(true);
			} else if (args[i].equals("-reinstall")){
				setReinstallFlag(true);
			} else if (args[i].equals("-install")){
				setInstallFlag(true);
			} else if (args[i].equals("-uninstall")){
				setUninstallFlag(true);
			} else if (args[i].equals("-force")){
				setForceFlag(true);
			} else if (args[i].equals("-quiet")){
				setQuietFlag(true);
			} else if (args[i].equals("-ui")){
				setUiFlag(true);
			} else if (args[i].equals("-noui")){
				setNoUiFlag(true);
			} else if (args[i].equals("-version")){
				setVersionFlag(true);
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

}
