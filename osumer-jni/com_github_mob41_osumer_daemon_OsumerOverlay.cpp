#include "jni.h"
#include "com_github_mob41_osumer_daemon_OsumerOverlay.h"
#include <stdio.h>
#include <Windows.h>
#include <ShlObj.h>

JNIEXPORT void JNICALL Java_com_github_mob41_osumer_daemon_OsumerOverlay_startWithOverlay(JNIEnv *env, jclass thisClass) {
    char exe_path[32];
    char *lib_path;
    void *page;
    STARTUPINFO si = { 0 };
    PROCESS_INFORMATION pi = { 0 };
    HANDLE hThread;

    TCHAR pf[MAX_PATH];
    SHGetSpecialFolderPath(0, pf, CSIDL_PROGRAM_FILES, FALSE);

    TCHAR ad[MAX_PATH];
    SHGetFolderPath(0, CSIDL_LOCAL_APPDATA, ad, 0, ad);

    // Execute the process in suspended mode.
    strcat(exe_path, ad);
    strcat(exe_path, "\\Local\\osu!\\osu!.exe");

    si.cb = sizeof(STARTUPINFO);
    if (!CreateProcess(NULL, exe_path, NULL, NULL, FALSE, CREATE_SUSPENDED, NULL, NULL, &si, &pi)) {
        fprintf(stderr, "CreateProcess(\"%s\") failed; error code = 0x%08X\n", exe_path, GetLastError());
        return;
    }

    // Allocate a page in memory for the arguments of LoadLibrary.
    page = VirtualAllocEx(pi.hProcess, NULL, MAX_PATH, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    if (page == NULL) {
        fprintf(stderr, "VirtualAllocEx failed; error code = 0x%08X\n", GetLastError());
        return;
    }

    char jvmDllStr[32];
    strcat(jvmDllStr, pf);
    strcat(jvmDllStr, "\\jre\\bin\client\\jvm.dll");

    char osumerDllStr[32];
    strcat(osumerDllStr, pf);
    strcat(osumerDllStr, "\\osumer2\\osumer-overlay.dll");

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
        if (WriteProcessMemory(pi.hProcess, page, lib_path, strlen(lib_path) + 1, NULL) == 0) {
            fprintf(stderr, "WriteProcessMemory failed; error code = 0x%08X\n", GetLastError());
            return;
        }
        //MessageBox(0, "Written", "osumer", MB_OK);

        // Inject the shared library into the address space of the process,
        // through a call to LoadLibrary.
        hThread = CreateRemoteThread(pi.hProcess, NULL, 0, (LPTHREAD_START_ROUTINE)LoadLibraryA, page, 0, NULL);
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

    // Resume the execution of the process, once all libraries have been injected
    // into its address space.
    if (ResumeThread(pi.hThread) == -1) {
        fprintf(stderr, "ResumeThread failed; error code = 0x%08X\n", GetLastError());
        return;
    }

    //MessageBox(0, "Resume", "osumer", MB_OK);
    // Cleanup.
    CloseHandle(pi.hProcess);
    VirtualFreeEx(pi.hProcess, page, MAX_PATH, MEM_RELEASE);
    return;
};

JNIEXPORT void JNICALL Java_com_github_mob41_osumer_daemon_OsumerOverlay_injectOverlay(JNIEnv *env, jclass thisClass) {

}