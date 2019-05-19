#include "jni.h"
#include "com_github_mob41_osumer_OsumerNative.h"
#include <stdio.h>
#include <string>
#include <Windows.h>
#include <ShlObj.h>
#include <tlhelp32.h>
#include <psapi.h>

void inject(HANDLE hProcess) {
    HANDLE hThread;
    char *lib_path;
    void *page;
    DWORD cbNeeded;
    HMODULE hMods[1024];
    unsigned int i;

    TCHAR pf[MAX_PATH];
    SHGetSpecialFolderPath(0, pf, CSIDL_PROGRAM_FILES, FALSE);

    char jvmDllStr[MAX_PATH];
    snprintf(jvmDllStr, MAX_PATH, "%s\\%s", pf, "osumer2\\jre\\bin\\client\\jvm.dll");

    char osumerDllStr[MAX_PATH];
    snprintf(osumerDllStr, MAX_PATH, "%s\\%s", pf, "osumer2\\osumer-overlay.dll");

    if (EnumProcessModules(hProcess, hMods, sizeof(hMods), &cbNeeded))
    {
        for (i = 0; i < (cbNeeded / sizeof(HMODULE)); i++)
        {
            TCHAR szModName[MAX_PATH];

            if (GetModuleFileNameEx(hProcess, hMods[i], szModName,
                sizeof(szModName) / sizeof(TCHAR)))
            {
                if (strcmp(szModName, osumerDllStr) == 0) {
                    return;
                }
            }
        }
    }

    // Allocate a page in memory for the arguments of LoadLibrary.
    page = VirtualAllocEx(hProcess, NULL, MAX_PATH, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    if (page == NULL) {
        fprintf(stderr, "VirtualAllocEx failed; error code = 0x%08X\n", GetLastError());
        return;
    }

    char *libs[2] = {
        jvmDllStr,
        osumerDllStr
    };

    for (int i = 0; i < 2; i++) {
        lib_path = libs[i];

        if (GetFileAttributes(lib_path) == INVALID_FILE_ATTRIBUTES) {
            fprintf(stderr, "unable to locate library (%s).\n", lib_path);
            return;
        }
        //MessageBox(0, "Located", "osumer", MB_OK);

        // Write library path to the page used for LoadLibrary arguments.
        if (WriteProcessMemory(hProcess, page, lib_path, strlen(lib_path) + 1, NULL) == 0) {
            fprintf(stderr, "WriteProcessMemory failed; error code = 0x%08X\n", GetLastError());
            return;
        }
        //MessageBox(0, "Written", "osumer", MB_OK);

        // Inject the shared library into the address space of the process,
        // through a call to LoadLibrary.
        hThread = CreateRemoteThread(hProcess, NULL, 0, (LPTHREAD_START_ROUTINE)LoadLibraryA, page, 0, NULL);
        if (hThread == NULL) {
            fprintf(stderr, "CreateRemoteThread failed; error code = 0x%08X\n", GetLastError());
            return;
        }
        //MessageBox(0, "Thread", "osumer", MB_OK);

        // Wait for DllMain to return.
        if (WaitForSingleObject(hThread, INFINITE) == WAIT_FAILED) {
            fprintf(stderr, "WaitForSingleObject failed; error code = 0x%08X\n", GetLastError());
            return;
        }
    }
    //MessageBox(0, "Returned", "osumer", MB_OK);

    // Cleanup.
    CloseHandle(hThread);
    VirtualFreeEx(hProcess, page, MAX_PATH, MEM_RELEASE);
}

JNIEXPORT jstring JNICALL Java_com_github_mob41_osumer_OsumerNative_getProgramFiles(JNIEnv *env, jclass thisClass) {
    TCHAR pf[MAX_PATH];
    SHGetSpecialFolderPath(0, pf, CSIDL_PROGRAM_FILES, FALSE);
    return env->NewStringUTF(pf);
}

JNIEXPORT void JNICALL Java_com_github_mob41_osumer_OsumerNative_startWithOverlay(JNIEnv *env, jclass thisClass) {
    char exe_path[MAX_PATH];
    STARTUPINFO si = { 0 };
    PROCESS_INFORMATION pi = { 0 };

    TCHAR ad[MAX_PATH];
    SHGetFolderPath(NULL, CSIDL_LOCAL_APPDATA, NULL, 0, ad);
    fprintf(stderr, "ap:%s\n", ad);

    // Execute the process in suspended mode.
    snprintf(exe_path, MAX_PATH, "%s\\%s", ad, "osu!\\osu!.exe");
    fprintf(stderr, "ep:%s\n", exe_path);

    si.cb = sizeof(STARTUPINFO);
    if (!CreateProcess(NULL, exe_path, NULL, NULL, FALSE, CREATE_SUSPENDED, NULL, NULL, &si, &pi)) {
        fprintf(stderr, "CreateProcess(\"%s\") failed; error code = 0x%08X\n", exe_path, GetLastError());
        return;
    }

    inject(pi.hProcess);

    // Resume the execution of the process, once all libraries have been injected
    // into its address space.
    if (ResumeThread(pi.hThread) == -1) {
        fprintf(stderr, "ResumeThread failed; error code = 0x%08X\n", GetLastError());
        return;
    }

    //MessageBox(0, "Resume", "osumer", MB_OK);
    // Cleanup.
    CloseHandle(pi.hProcess);
    return;
};

JNIEXPORT void JNICALL Java_com_github_mob41_osumer_OsumerNative_injectOverlay(JNIEnv *env, jclass thisClass) {
    PROCESSENTRY32 entry;
    entry.dwSize = sizeof(PROCESSENTRY32);

    HANDLE snapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, NULL);

    if (Process32First(snapshot, &entry) == TRUE)
    {
        while (Process32Next(snapshot, &entry) == TRUE)
        {
            if (stricmp(entry.szExeFile, "osu!.exe") == 0)
            {
                HANDLE hProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, entry.th32ProcessID);

                inject(hProcess);

                CloseHandle(hProcess);
            }
        }
    }

    CloseHandle(snapshot);
}