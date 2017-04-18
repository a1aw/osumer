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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.io.officialosu.Osu;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;

public class Installer {

	public static final String winPath = "C:\\Program Files\\osumer";
	
	public static final String winFile = "osumer.exe";
	
	public static final String verInfoFile = "version.osumer";
	
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
	private static final String WIN_REG_INSTALLINFO_HIDEICON_PARA = "HideIconsCommand";
	private static final String WIN_REG_INSTALLINFO_HIDEICON_VALUE = "\"" + winPath + "\\" + winFile + "\" -hideicons";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo/@ShowIconsCommand
	private static final String WIN_REG_INSTALLINFO_SHOWICON_PARA = "ShowIconsCommand";
	private static final String WIN_REG_INSTALLINFO_SHOWICON_VALUE = "\"" + winPath + "\\" + winFile + "\" -showicons";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo/@ReinstallCommand
	private static final String WIN_REG_INSTALLINFO_REINSTALL_PARA = "ReinstallCommand";
	private static final String WIN_REG_INSTALLINFO_REINSTALL_VALUE = "\"" + winPath + "\\" + winFile + "\" -reinstall";
	
//Shell open command
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell
	private static final String WIN_REG_SHELL_KEY = "shell";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell/open
	private static final String WIN_REG_SHELL_OPEN_KEY = "open";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell/open/command
	private static final String WIN_REG_SHELL_OPEN_COMMAND_KEY = "command";
	
	// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell/open/command/@
	private static final String WIN_REG_SHELL_OPEN_COMMAND_DEFAULT_VALUE = "\"" + winPath + "\\" + winFile + "\"";
	
//Registered applications
	
	private static final String WIN_REG_REGISTEREDAPPS_PATH = "SOFTWARE\\RegisteredApplications";
	
	// HKLM/SOFTWARE/RegisteredApplications/@osumerExpress
	private static final String WIN_REG_REGISTEREDAPPS_OSUMEREXPRESS_PARA = "osumerExpress";
	private static final String WIN_REG_REGISTEREDAPPS_OSUMEREXPRESS_VALUE = "SOFTWARE\\Clients\\StartMenuInternet\\osumerExpress\\Capabilities";
	
//Classes
	
	private static final String WIN_REG_CLASSES_PATH = "SOFTWARE\\Classes";
	
	// HKLM/SOFTWARE/Classes/osumer
	private static final String WIN_REG_CLASSES_OSUMER_KEY = "osumer"; //Refer upstairs
	
	// HKLM/SOFTWARE/Classes/osumer/@FriendlyTypeName
	private static final String WIN_REG_CLASSES_OSUMER_FRIENDLYTYPENAME_PARA = "FriendlyTypeName";
	private static final String WIN_REG_CLASSES_OSUMER_FRIENDLYTYPENAME_VALUE = "osumerExpress"; //Refer upstairs
	
	// HKLM/SOFTWARE/Classes/osumer/@
	private static final String WIN_REG_CLASSES_OSUMER_DEFAULT_VALUE = "osumerExpress"; //Refer upstairs
	
	public Installer() {
		
	}
	
	public void showIcons(){
		//TODO Show icons command https://msdn.microsoft.com/en-us/library/windows/desktop/cc144109(v=vs.85).aspx#show_icons_command
	}
	
	public void hideIcons(){
		//TODO Hide icons command https://msdn.microsoft.com/en-us/library/windows/desktop/cc144109(v=vs.85).aspx#hide_icons_command
	}
	
	public void reinstall(){
		//TODO Reinstall command https://msdn.microsoft.com/en-us/library/windows/desktop/cc144109(v=vs.85).aspx#reinstall_command
	}
	
	public static String[] getAvailableBrowsers() throws DebuggableException{
		try {
			String[] keys = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_CLIENTS_PATH);
			
			List<String> filteredList = new ArrayList<String>(50);
			for (int i = 0; i < keys.length; i++){
				if (!keys[i].equals(WIN_REG_INTERNET_CLIENT_KEY)){
					filteredList.add(keys[i]);
				}
			}
			
			String[] out = new String[filteredList.size()];
			for (int i = 0; i < out.length; i++){
				out[i] = filteredList.get(i);
			}
			
			return out;
		} catch (Win32Exception e){
			throw new DebuggableException(
					null,
					"(Try&catch try)",
					"Throw debuggable exception",
					"(End of function)",
					"Error reading registry",
					false, e);
		}
	}
	
	public static String getBrowserExePath(String browserName){
		String value = null;
		try {
			value = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
					WIN_REG_CLIENTS_PATH + "\\" + browserName + "\\shell\\open\\command", "");
		} catch (Win32Exception e){
			return null;
		}
		return value;
	}
	
	public boolean isSameVersionInstalled(){
		VersionInfo verInfo = getInstalledVersion();
		
		if (verInfo == null){
			return false;
		}
		
		return verInfo.getVersion().equals(Osu.OSUMER_VERSION) &&
				verInfo.getBranch().equals(Osu.OSUMER_BRANCH) &&
				verInfo.getBuildNum() == Osu.OSUMER_BUILD_NUM;
	}
	
	public VersionInfo getInstalledVersion(){
		if (!Osu.isWindows()){
			return null;
		}
		
		if (!isInstalled()){
			return null;
		}
		
		File file = new File(winPath + "\\" + verInfoFile);
		try {
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int read;
			
			byte[] data = new byte[1024];
			while ((read = in.read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, read);
			}

			buffer.flush();

			in.close();
			
			String str = new String(Base64.decodeBase64(data), StandardCharsets.UTF_8);
			
			JSONObject json = null;
			try {
				json = new JSONObject(str);
			} catch (JSONException e){
				return null;
			}
			
			String ver = json.isNull("version") ? null : json.getString("version");
			String branch = json.isNull("branch") ? null : json.getString("branch");
			int build = json.isNull("build") ? -1 : json.getInt("build");
			
			if (ver == null || branch == null || build == -1){
				return null;
			}
			
			return new VersionInfo(ver, branch, build);
		} catch (IOException e){
			return null;
		}
	}
	
	public boolean isInstalled(){
		File file = new File(winPath + "\\" + winFile);
		return file.exists();
	}
	
	public boolean isInstallValid(){
		//TODO Do verification
		return false;
	}
	
	public void install() throws DebuggableException{
		if (!Osu.isWindows()){
			throw new DebuggableException(
					null,
					"Validate OS is Windows",
					"Throw debuggable exception",
					"Validate is osumer elevated",
					"Installer does not support non-Windows environment",
					false);
		}
		
		if (!Osu.isWindowsElevated()){
			throw new DebuggableException(
					null,
					"Validate OS is Windows",
					"Validate is osumer elevated",
					"Create File instance of \"osumer.exe\"",
					"osumer is not elevated. Restart osumer with administrative privileges.",
					false);
		}
		
		String runningFilePath = null;
		try {
			runningFilePath = URLDecoder.decode(Installer.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			throw new DebuggableException(
					null,
					"Validate is osumer elevated",
					"Get current running file path",
					"Validate is running file name is ending with .exe",
					"Unsupported encoding UTF-8",
					false, e1);
		}
		
		if (!runningFilePath.endsWith(".exe")){
			throw new DebuggableException(
					runningFilePath,
					"Get current running file path",
					"Validate is running file name is ending with .exe",
					"Create File instance of \"osumer.exe\"",
					"A Windows executable (.exe) version of osumer is required for installation. It can be downloaded from the releases. If you have it, rename it to \"osumer.exe\" to continue.",
					false);
		}
		
		File file = new File(runningFilePath);
		if (!file.exists()){
			throw new DebuggableException(
					runningFilePath,
					"Validate is running file name is ending with .exe",
					"Create File instance of \"osumer.exe\"",
					"Create File instance of constant field winPath",
					"Unexpected? The running file-name does not exist!",
					false);
		}
		
		File destFolder = new File(winPath);
		if (!destFolder.exists()){
			destFolder.mkdirs();
		}
		
		File verInfo = new File(winPath + "\\" + verInfoFile);
		
		try {
			if (verInfo.exists()){
				verInfo.delete();
			}
			
			JSONObject verInfoJson = new JSONObject();
			verInfoJson.put("version", Osu.OSUMER_VERSION);
			verInfoJson.put("branch", Osu.OSUMER_BRANCH);
			verInfoJson.put("build", Osu.OSUMER_BUILD_NUM);
			
			verInfo.createNewFile();
			
			FileOutputStream out = new FileOutputStream(verInfo);
			out.write(Base64.encodeBase64(verInfoJson.toString().getBytes(StandardCharsets.UTF_8)));
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new DebuggableException(
					null,
					"Create new File instance with \"" + winPath + "\\" + verInfoFile + "\"",
					"Create application version info",
					"Create new File instance with \"" + winPath + "\\" + winFile + "\"",
					"Error creating version info",
					false, e);
		}
		
		File dest = new File(winPath + "\\" + winFile);
		
		try {
			if (dest.exists()){
				dest.delete();
			}
			
			dest.createNewFile();
			
			FileOutputStream out = new FileOutputStream(dest);
			Files.copy(file.toPath(), out);
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new DebuggableException(
					null,
					"Create new File instance with \"" + winPath + "\\" + winFile + "\"",
					"(Try scope) Copying file",
					"(Next Try scope) Writing to registry",
					"Error copying file",
					false, e);
		}
		
		try {
			//Create root key
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress
			boolean success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_CLIENTS_PATH, WIN_REG_INTERNET_CLIENT_KEY);
			
			final String clientRegPath = WIN_REG_CLIENTS_PATH + "\\" + WIN_REG_INTERNET_CLIENT_KEY;
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/@
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, clientRegPath, "", WIN_REG_INTERNET_CLIENT_DEFAULT_VALUE);
			
	//Capabilities
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities
			success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, clientRegPath, WIN_REG_CAP_KEY);
			
			final String capRegPath = clientRegPath + "\\" + WIN_REG_CAP_KEY;
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/@ApplicationName,@ApplicationDescription,@ApplicationIcon
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, capRegPath, WIN_REG_CAP_APPNAME_PARA, WIN_REG_CAP_APPNAME_VALUE);
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, capRegPath, WIN_REG_CAP_APPDESC_PARA, WIN_REG_CAP_APPDESC_VALUE);
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, capRegPath, WIN_REG_CAP_APPICON_PARA, WIN_REG_CAP_APPICON_VALUE);

	//Capabilities: Startmenu
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/Startmenu
			success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, capRegPath, WIN_REG_CAP_STARTMENU_KEY);
			// Legacy use
			
			final String capStartMenuRegPath = capRegPath + "\\" + WIN_REG_CAP_STARTMENU_KEY;
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/Startmenu/@StartMenuInternet
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, capStartMenuRegPath, WIN_REG_CAP_STARTMENU_STARTMENUINTERNET_PARA, WIN_REG_CAP_STARTMENU_STARTMENUINTERNET_VALUE);
			
	//Capabilities: File associations
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/FileAssociations
			success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, capRegPath, WIN_REG_CAP_FILEASSOC_KEY);
			// No file associations currently
			
	//Capabilities: URL Associations
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/URLAssociations
			success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, capRegPath, WIN_REG_CAP_URLASSOC_KEY);
			
			final String urlAssocRegPath = capRegPath + "\\" + WIN_REG_CAP_URLASSOC_KEY;
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/Capabilities/URLAssociations/@http,@https
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, urlAssocRegPath, WIN_REG_CAP_URLASSOC_HTTP_PARA, WIN_REG_CAP_URLASSOC_HTTP_VALUE);
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, urlAssocRegPath, WIN_REG_CAP_URLASSOC_HTTPS_PARA, WIN_REG_CAP_URLASSOC_HTTPS_VALUE);
			
	//Default icon
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/DefaultIcon
			success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, clientRegPath, WIN_REG_DEFAULTICON_KEY);
			
			final String defaultIconRegPath = clientRegPath + "\\" + WIN_REG_DEFAULTICON_KEY;
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/DefaultIcon/@
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, defaultIconRegPath, "", WIN_REG_DEFAULTICON_DEFAULT_VALUE);
			
	//Install info
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo
			success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, clientRegPath, WIN_REG_INSTALLINFO_KEY);
			
			final String installInfoRegPath = clientRegPath + "\\" + WIN_REG_INSTALLINFO_KEY;
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo/@HideIconsCommand
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, installInfoRegPath, WIN_REG_INSTALLINFO_HIDEICON_PARA, WIN_REG_INSTALLINFO_HIDEICON_VALUE);
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo/@ShowIconsCommand
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, installInfoRegPath, WIN_REG_INSTALLINFO_SHOWICON_PARA, WIN_REG_INSTALLINFO_SHOWICON_VALUE);
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo/@ReinstallCommand
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, installInfoRegPath, WIN_REG_INSTALLINFO_REINSTALL_PARA, WIN_REG_INSTALLINFO_REINSTALL_VALUE);
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/InstallInfo/@IconsVisible
			Advapi32Util.registrySetIntValue(WinReg.HKEY_LOCAL_MACHINE, installInfoRegPath, WIN_REG_INSTALLINFO_ICONSVISIBLE_PARA, WIN_REG_INSTALLINFO_ICONSVISIBLE_VALUE);

	//Shell: Open: Command
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell
			success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, clientRegPath, WIN_REG_SHELL_KEY);
			
			final String shellRegPath = clientRegPath + "\\" + WIN_REG_SHELL_KEY;
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell/open
			success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, shellRegPath, WIN_REG_SHELL_OPEN_KEY);
			
			final String shellOpenRegPath = shellRegPath + "\\" + WIN_REG_SHELL_OPEN_KEY;
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell/open/command
			success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, shellOpenRegPath, WIN_REG_SHELL_OPEN_COMMAND_KEY);
			
			final String shellOpenCmdRegPath = shellOpenRegPath + "\\" + WIN_REG_SHELL_OPEN_COMMAND_KEY;
			
			// HKLM/SOFTWARE/Clients/StartMenuInternet/osumerExpress/shell/open/command/@
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, shellOpenCmdRegPath, "", WIN_REG_SHELL_OPEN_COMMAND_DEFAULT_VALUE);
			
	//Registered Applications
			
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_REGISTEREDAPPS_PATH, WIN_REG_REGISTEREDAPPS_OSUMEREXPRESS_PARA, WIN_REG_REGISTEREDAPPS_OSUMEREXPRESS_VALUE);
		
	//Classes
			
			// HKLM/SOFTWARE/Classes/osumer
			success = Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_CLASSES_PATH, WIN_REG_CLASSES_OSUMER_KEY);
			
			final String osumerClassRegPath = WIN_REG_CLASSES_PATH + "\\" + WIN_REG_CLASSES_OSUMER_KEY;
			
			// HKLM/SOFTWARE/Classes/osumer/@
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, osumerClassRegPath, "", WIN_REG_CLASSES_OSUMER_DEFAULT_VALUE);
			
			// HKLM/SOFTWARE/Classes/osumer/@FriendlyTypeName
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, osumerClassRegPath, WIN_REG_CLASSES_OSUMER_FRIENDLYTYPENAME_PARA, WIN_REG_CLASSES_OSUMER_FRIENDLYTYPENAME_VALUE);
		
			Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, osumerClassRegPath, "shell");
			Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, osumerClassRegPath + "\\shell", "open");
			Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, osumerClassRegPath + "\\shell\\open", "command");
			Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, osumerClassRegPath + "\\shell\\open\\command", "", WIN_REG_SHELL_OPEN_COMMAND_DEFAULT_VALUE + " \"%1\"");
		} catch (Win32Exception e){
			throw new DebuggableException(
					null,
					"(Try&catch try) Writing to registry",
					"Throw debuggable exception",
					"(End of function)",
					"Error writing registry",
					false, e);
		}
	}
	
	public void uninstall() throws DebuggableException{
		File file = new File(winPath + "\\" + winFile);
		if (file.exists()){
			file.delete();
		}
		file = new File(winPath + "\\" + verInfoFile);
		if (file.exists()){
			file.delete();
		}
		
		try {
			final String clientRegPath = WIN_REG_CLIENTS_PATH + "\\" + WIN_REG_INTERNET_CLIENT_KEY;
			final String capRegPath = clientRegPath + "\\" + WIN_REG_CAP_KEY;
			final String shellRegPath = clientRegPath + "\\" + WIN_REG_SHELL_KEY;
			final String shellOpenRegPath = shellRegPath + "\\" + WIN_REG_SHELL_OPEN_KEY;
			
			//Capabilities
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, capRegPath, WIN_REG_CAP_FILEASSOC_KEY);
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, capRegPath, WIN_REG_CAP_URLASSOC_KEY);
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, capRegPath, WIN_REG_CAP_STARTMENU_KEY);
			
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, clientRegPath, WIN_REG_CAP_KEY);
			
			//Shell open command

			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, shellOpenRegPath, WIN_REG_SHELL_OPEN_COMMAND_KEY);
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, shellRegPath, WIN_REG_SHELL_OPEN_KEY);
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, clientRegPath, WIN_REG_SHELL_KEY);
			
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, clientRegPath, WIN_REG_DEFAULTICON_KEY);
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, clientRegPath, WIN_REG_INSTALLINFO_KEY);
			
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_CLIENTS_PATH, WIN_REG_INTERNET_CLIENT_KEY);
			
			//Classes and Registered applications
			Advapi32Util.registryDeleteValue(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_REGISTEREDAPPS_PATH, WIN_REG_REGISTEREDAPPS_OSUMEREXPRESS_PARA);
			
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_CLASSES_PATH + "\\" + WIN_REG_CLASSES_OSUMER_KEY + "\\shell\\open", "command");
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_CLASSES_PATH + "\\" + WIN_REG_CLASSES_OSUMER_KEY + "\\shell", "open");
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_CLASSES_PATH + "\\" + WIN_REG_CLASSES_OSUMER_KEY, "shell");
			Advapi32Util.registryDeleteKey(WinReg.HKEY_LOCAL_MACHINE, WIN_REG_CLASSES_PATH, WIN_REG_CLASSES_OSUMER_KEY);
		} catch (Win32Exception e){
			e.printStackTrace();
			throw new DebuggableException(
					null,
					"(Try&catch try) Writing to registry",
					"Throw debuggable exception",
					"(End of function)",
					"Error writing registry",
					false, e);
		}
	}

}
