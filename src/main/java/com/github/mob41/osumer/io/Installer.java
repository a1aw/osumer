package com.github.mob41.osumer.io;

import com.github.mob41.osumer.exceptions.OsuException;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;

public class Installer {

	public static final String winPath = "C:\\Program Files\\osumer";
	
	public static final String winFile = "osumer.exe";
	
	private static final String WIN_REG_CLIENTS_PATH = "SOFTWARE\\Clients\\StartMenuInternet";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress
	private static final String WIN_REG_INTERNET_CLIENT_KEY = "osumerExpress";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/@
	private static final String WIN_REG_INTERNET_CLIENT_DEFAULT_VALUE = "osumerExpress";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities
	private static final String WIN_REG_CAP_KEY = "Capabilities";
	
//Capabilities
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/@ApplicationName
	private static final String WIN_REG_CAP_APPNAME_PARA = "ApplicationName";
	private static final String WIN_REG_CAP_APPNAME_VALUE = "osumerExpress";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/@ApplicationDescription
	private static final String WIN_REG_CAP_APPDESC_PARA = "ApplicationDescription";
	private static final String WIN_REG_CAP_APPDESC_VALUE = "Automatically downloads osu! beatmaps with a click in-game.";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/@ApplicationIcon
	private static final String WIN_REG_CAP_APPICON_PARA = "ApplicationIcon";
	private static final String WIN_REG_CAP_APPICON_VALUE = winPath + "\\" + winFile + ",0";
	
//##File associations
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/FileAssociations
	private static final String WIN_REG_CAP_FILEASSOC_KEY = "FileAssociations";
	// No file associations
	
//##URL associations
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/URLAssociations
	private static final String WIN_REG_CAP_URLASSOC_KEY = "URLAssociations";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/URLAssociations/@http
	private static final String WIN_REG_CAP_URLASSOC_HTTP_PARA = "http";
	private static final String WIN_REG_CAP_URLASSOC_HTTP_VALUE = "osumer";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/URLAssociations/@https
	private static final String WIN_REG_CAP_URLASSOC_HTTPS_PARA = "https";
	private static final String WIN_REG_CAP_URLASSOC_HTTPS_VALUE = "osumer";
	
//##Legacy StartMenuInternet item
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/StartMenu
	private static final String WIN_REG_CAP_STARTMENU_KEY = "Startmenu";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/StartMenu/@StartMenuInternet
	private static final String WIN_REG_CAP_STARTMENU_STARTMENUINTERNET_PARA = "StartMenuInternet";
	private static final String WIN_REG_CAP_STARTMENU_STARTMENUINTERNET_VALUE = "osumerExpress";
	
//Default icon
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/DefaultIcon
	private static final String WIN_REG_DEFAULTICON_KEY = "DefaultIcon";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/DefaultIcon/@
	private static final String WIN_REG_DEFAULTICON_DEFAULT_VALUE = WIN_REG_CAP_APPICON_VALUE; //Refer to upstairs
	
//Install info (hide,show icons, reinstall commands)
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo
	private static final String WIN_REG_INSTALLINFO_KEY = "InstallInfo";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo/@IconsVisible
	private static final String WIN_REG_INSTALLINFO_ICONSVISIBLE_PARA = "IconsVisible";
	private static final int WIN_REG_INSTALLINFO_ICONSVISIBLE_VALUE = 1;
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo/@HideIconsCommand
	private static final String WIN_REG_INSTALLINFO_HIDEICON_PARA = "HideIconsComamnd";
	private static final String WIN_REG_INSTALLINFO_HIDEICON_VALUE = winPath + "\\" + winFile + " -hideicons";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo/@ShowIconsCommand
	private static final String WIN_REG_INSTALLINFO_SHOWICON_PARA = "ShowIconsComamnd";
	private static final String WIN_REG_INSTALLINFO_SHOWICON_VALUE = winPath + "\\" + winFile + " -showicons";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo/@ReinstallCommand
	private static final String WIN_REG_INSTALLINFO_REINSTALL_PARA = "ReinstallComamnd";
	private static final String WIN_REG_INSTALLINFO_REINSTALL_VALUE = winPath + "\\" + winFile + " -reinstall";
	
//Shell open command
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell
	private static final String WIN_REG_SHELL_KEY = "shell";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell/open
	private static final String WIN_REG_SHELL_OPEN_KEY = "open";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell/open/command
	private static final String WIN_REG_SHELL_OPEN_COMMAND_KEY = "command";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell/open/command/@
	private static final String WIN_REG_SHELL_OPEN_COMMAND_DEFAULT_VALUE = winPath + "\\" + winFile;
	
//Registered applications
	
	private static final String WIN_REG_REGISTEREDAPPS_PATH = "SOFTWARE\\Clients";
	
	// HKLM/SOFTWARE/RegisteredApplications
	private static final String WIN_REG_REGISTEREDAPPS_KEY = "RegisteredApplications";
	
	// HKLM/SOFTWARE/RegisteredApplications/@osumerExpress
	private static final String WIN_REG_REGISTEREDAPPS_OSUMEREXPRESS_PARA = "osumerExpress";
	private static final String WIN_REG_REGISTEREDAPPS_OSUMEREXPRESS_VALUE = "SOFTWARE\\Clients\\StartMenuInternet\\osumerExpress\\Capabilities";
	
//Classes
	
	private static final String WIN_REG_CLASSES_PATH = "SOFTWARE\\Classes";
	
	// HKLM/SOFTWARE/Classes/osumer
	private static final String WIN_REG_CLASSES_OSUMER_KEY = "osumer"; //Refer upstairs
	
	// HKLM/SOFTWARE/Classes/osumer/@
	private static final String WIN_REG_CLASSES_OSUMER_DEFAULT_VALUE = "osumerExpress"; //Refer upstairs
	
	public Installer() {
		
	}
	
	public boolean isInstalled(){
		return false;
	}
	
	public boolean isValidInstallation(){
		return false;
	}
	
	//TODO Complete installation
	public void install() throws OsuException{
		if (!Osu.isWindows()){
			throw new OsuException("Installer does not support non-Windows");
		}
		
		if (!Osu.isWindowsElevated()){
			throw new OsuException("osumer is not in elevated mode");
		}
		
		// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress
		boolean success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_CLIENTS_PATH, WIN_REG_INTERNET_CLIENT_KEY);
		
		String regPath = WIN_REG_CLIENTS_PATH + "\\" + WIN_REG_INTERNET_CLIENT_KEY;
		
		// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/@
		Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, regPath,
				WIN_REG_INTERNET_CLIENT_KEY, WIN_REG_INTERNET_CLIENT_DEFAULT_VALUE);
		
		// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities
		success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, regPath, WIN_REG_CAP_KEY);
		
		regPath = regPath + WIN_REG_CAP_KEY;
		
		
	}
	
	public void uninstall() throws OsuException{
		
	}

}
