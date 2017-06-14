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
package com.github.mob41.osumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mob41.osumer.io.beatmap.Osu;
import com.github.mob41.osumer.io.queue.QueueManager;
import com.github.mob41.osumer.updater.Updater;

public class Config {

    public static final String DEFAULT_DATA_FILE_NAME = "osumer_configuration.json";

    private static final String KEY_UPDATE_SOURCE = "updateSource";

    private static final int KEY_UPDATE_SOURCE_DEFAULT_VALUE = Osu.updateSourceStrToInt(Osu.OSUMER_BRANCH);

    private static final String KEY_DEFAULT_BROWSER = "defaultBrowser";

    private static final String KEY_DEFAULT_BROWSER_DEFAULT_VALUE = "IEXPLORE.EXE";

    private static final String KEY_OE_ENABLED = "oeenabled";

    private static final boolean KEY_OE_ENABLED_DEFAULT_VALUE = true;

    private static final String KEY_NO_UI_ARG_SWITCH_BROWSER = "switchToBrowserIfWithoutUiArg";

    private static final boolean KEY_NO_UI_ARG_SWITCH_BROWSER_DEFAULT_VALUE = false;

    private static final String KEY_AUTO_SWITCH_BROWSER = "autoSwitchBrowser";

    private static final boolean KEY_AUTO_SWITCH_BROWSER_DEFAULT_VALUE = true;

    private static final String KEY_GETTING_STARTED_STARTUP = "gettingStartedOnStartup_Version" + Osu.OSUMER_VERSION
            + "-" + Osu.OSUMER_BRANCH + "-" + Osu.OSUMER_BUILD_NUM;

    private static final boolean KEY_GETTING_STARTED_STARTUP_DEFAULT_VALUE = true;

    private static final String KEY_SERVER_PRORITY = "serverPrority";

    private static final String[] KEY_SERVER_PRORITY_DEFAULT_VALUE = { "osu! forum" }; // bloodcat
                                                                                       // not
                                                                                       // available

    private static final String KEY_ENC_ASK_ON_STARTUP = "usrPwdEncAskOnStartup";

    private static final boolean KEY_ENC_ASK_ON_STARTUP_DEFAULT_VALUE = true;

    private static final String KEY_USER = "user";

    private static final String KEY_PASS = "pass";

    private static final String KEY_ENC = "usrPwdEnc";

    private static final boolean KEY_ENC_DEFAULT_VALUE = false;

    private static final String KEY_ENC_SALT = "usrPwdEncSalt";

    private static final String KEY_ENC_IV = "usrPwdEncIv";

    private static final String KEY_RUN_DAEMON_ON_WIN_STARTUP = "runDaemonOnWinStartup";

    private static final boolean KEY_RUN_DAEMON_ON_WIN_STARTUP_DEFAULT_VALUE = true;

    private static final String KEY_DAEMON_DISABLED = "daemonDisabled";

    private static final boolean KEY_DAEMON_DISABLED_DEFAULT_VALUE = false;

    private static final String KEY_QUEUE_MAX_THREADS = "queuingMaxThreads";

    private static final int KEY_QUEUE_MAX_THREADS_DEFAULT_VALUE = 4;

    private static final String KEY_QUEUE_NEXT_CHECK_DELAY = "queueingNextCheckDelay";

    private static final int KEY_QUEUE_NEXT_CHECK_DELAY_DEFAULT_VALUE = 2000;

    private static final String KEY_QUEUE_MASSIVE_DOWNLOADING_THREADS = "queuingRemoveLimit";

    private static final boolean KEY_QUEUE_MASSIVE_DOWNLOADING_THREADS_DEFAULT_VALUE = false;

    private static final String KEY_CHECK_UPDATE_FREQ = "updateFreq";

    private static final String KEY_CHECK_UPDATE_FREQ_DEFAULT_VALUE = "everyAct";

    private static final String KEY_CHECK_UPDATE_ALGO = "updateCompareAlgo";

    private static final String KEY_CHECK_UPDATE_ALGO_DEFAULT_VALUE = "latestVerPerBranch";

    private static final String KEY_AUTO_ACCEPT_CRITICAL_UPDATES = "updateCriticalAuto";

    private static final boolean KEY_AUTO_ACCEPT_CRITICAL_UPDATES_DEFAULT_VALUE = true;

    private static final String KEY_AUTO_DOWNLOAD_APPLY_PATCHES = "updatePatchesAuto";

    private static final boolean KEY_AUTO_DOWNLOAD_APPLY_PATCHES_DEFAULT_VALUE = true;

    private static final String KEY_PLUGIN_SUMS = "plugSums";

    private static final String KEY_ENABLE_TONE_BEFORE_DOWNLOAD = "toneBeforeDownload";

    private static final boolean KEY_ENABLE_TONE_BEFORE_DOWNLOAD_DEFAULT_VALUE = true;

    private static final String KEY_ENABLE_TONE_DOWNLOAD_STARTED = "toneDownloadStarted";

    private static final boolean KEY_ENABLE_TONE_DOWNLOAD_STARTED_DEFAULT_VALUE = true;

    private static final String KEY_ENABLE_TONE_AFTER_DOWNLOAD = "toneAfterDownload";

    private static final boolean KEY_ENABLE_TONE_AFTER_DOWNLOAD_DEFAULT_VALUE = true;

    private JSONObject json;

    private final String dataFilePath;

    private final String dataFileName;

    private String unlockKey = null;

    public Config(String dataFilePath, String dataFileName) {
        this.dataFilePath = dataFilePath;
        this.dataFileName = dataFileName;
    }

    public int getUpdateSource() {
        return json.getInt(KEY_UPDATE_SOURCE);
    }

    public String getDefaultBrowser() {
        return json.getString(KEY_DEFAULT_BROWSER);
    }

    public boolean isOEEnabled() {
        return json.getBoolean(KEY_OE_ENABLED);
    }

    public boolean isSwitchToBrowserIfWithoutUiArg() {
        return json.getBoolean(KEY_NO_UI_ARG_SWITCH_BROWSER);
    }

    public boolean isAutoSwitchBrowser() {
        return json.getBoolean(KEY_AUTO_SWITCH_BROWSER);
    }

    public boolean isShowGettingStartedOnStartup() {
        return json.getBoolean(KEY_GETTING_STARTED_STARTUP);
    }

    public String[] getServerPrority() {
        JSONArray arr = json.getJSONArray(KEY_SERVER_PRORITY);
        String[] out = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            out[i] = arr.getString(i);
        }
        return out;
    }

    public boolean isEncPassAskOnStartup() {
        return json.getBoolean(KEY_ENC_ASK_ON_STARTUP);
    }

    public String getUser() {
        String raw = json.getString(KEY_USER);
        if (isUserPassEncrypted()) {
            // do enc stuff
            return null;
        } else {
            return raw;
        }
    }

    public String getPass() {
        String raw = json.getString(KEY_PASS);
        if (isUserPassEncrypted()) {
            // do enc stuff
            return null;
        } else {
            return raw;
        }
    }

    public boolean isUserPassEncrypted() {
        return json.getBoolean(KEY_ENC);
    }

    private static boolean fillJson(JSONObject json) {
        boolean modified = false;
        modified |= ifNullPutValue(json, KEY_UPDATE_SOURCE, KEY_UPDATE_SOURCE_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_DEFAULT_BROWSER, KEY_DEFAULT_BROWSER_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_OE_ENABLED, KEY_OE_ENABLED_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_NO_UI_ARG_SWITCH_BROWSER, KEY_NO_UI_ARG_SWITCH_BROWSER_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_AUTO_SWITCH_BROWSER, KEY_AUTO_SWITCH_BROWSER_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_GETTING_STARTED_STARTUP, KEY_GETTING_STARTED_STARTUP_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_SERVER_PRORITY, KEY_SERVER_PRORITY_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_ENC_ASK_ON_STARTUP, KEY_ENC_ASK_ON_STARTUP_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_USER, "");
        modified |= ifNullPutValue(json, KEY_PASS, "");
        modified |= ifNullPutValue(json, KEY_ENC, KEY_ENC_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_ENC_SALT, "");
        modified |= ifNullPutValue(json, KEY_ENC_IV, "");
        modified |= ifNullPutValue(json, KEY_RUN_DAEMON_ON_WIN_STARTUP, KEY_RUN_DAEMON_ON_WIN_STARTUP_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_DAEMON_DISABLED, KEY_DAEMON_DISABLED_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_QUEUE_MAX_THREADS, KEY_QUEUE_MAX_THREADS_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_QUEUE_NEXT_CHECK_DELAY, KEY_QUEUE_NEXT_CHECK_DELAY_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_QUEUE_MASSIVE_DOWNLOADING_THREADS,
                KEY_QUEUE_MASSIVE_DOWNLOADING_THREADS_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_CHECK_UPDATE_FREQ, KEY_CHECK_UPDATE_FREQ_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_CHECK_UPDATE_ALGO, KEY_CHECK_UPDATE_ALGO_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_AUTO_ACCEPT_CRITICAL_UPDATES,
                KEY_AUTO_ACCEPT_CRITICAL_UPDATES_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_AUTO_DOWNLOAD_APPLY_PATCHES,
                KEY_AUTO_DOWNLOAD_APPLY_PATCHES_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_PLUGIN_SUMS, new JSONArray());
        modified |= ifNullPutValue(json, KEY_ENABLE_TONE_BEFORE_DOWNLOAD,
                KEY_ENABLE_TONE_BEFORE_DOWNLOAD_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_ENABLE_TONE_DOWNLOAD_STARTED,
                KEY_ENABLE_TONE_DOWNLOAD_STARTED_DEFAULT_VALUE);
        modified |= ifNullPutValue(json, KEY_ENABLE_TONE_AFTER_DOWNLOAD, KEY_ENABLE_TONE_AFTER_DOWNLOAD_DEFAULT_VALUE);
        return modified;
    }

    private static boolean ifNullPutValue(JSONObject json, String key, Object val) {
        if (json.isNull(key)) {
            json.put(key, val);
            return true;
        } else {
            return false;
        }
    }

    public void load() throws IOException {
        File file = new File(dataFilePath + "/" + dataFileName);
        if (!file.exists()) {
            write();
            return;
        }
        FileInputStream in = new FileInputStream(file);

        String data = "";
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while ((line = reader.readLine()) != null) {
            data += line;
        }
        reader.close();

        /*
         * Preferences prefs = Preferences.userRoot();
         * 
         * String data = prefs.get("com.github.mob41.osumer.configuration",
         * null);
         * 
         * if (data == null){ write(); return; }
         */

        try {
            json = new JSONObject(data);
            if (fillJson(json)) {
                write();
            }
        } catch (JSONException e) {
            throw new IOException("Invalid configuration data structure", e);
        }
    }

    public void write() throws IOException {

        File folder = new File(dataFilePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(dataFilePath + "/" + dataFileName);
        if (!file.exists()) {
            file.createNewFile();

            if (json == null) {
                json = new JSONObject();
            }
            fillJson(json);
        }
        FileOutputStream out = new FileOutputStream(file);
        PrintWriter writer = new PrintWriter(out, true);
        writer.println(json.toString(5));
        writer.close();
        out.flush();
        out.close();

        /*
         * Preferences prefs = Preferences.userRoot();
         * 
         * if (json == null){ json = new JSONObject(); }
         * 
         * prefs.put("com.github.mob41.osumer.configuration", json.toString());
         * 
         * try { prefs.flush(); } catch (BackingStoreException e) { throw new
         * IOException("Error occurred", e); }
         */
    }

    public void setMaxThreads(int maxThreads) {
        json.put("maxThreads", maxThreads);
    }

    public int getMaxThreads() {
        if (json.isNull("maxThreads")) {
            return QueueManager.DEFAULT_MAX_THREADS;
        }

        return json.getInt("maxThreads");
    }

}
