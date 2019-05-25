#include "stdafx.h"

#pragma comment(lib,"OpenGL32.lib")
#pragma comment(lib,"GLu32.lib")

#include <GL\gl3w.h>
#include <gl\gl.h>
#include <gl\glu.h>

#include "MinHook.h"

#include "imgui.h"
#include "imgui_impl_win32.h"
#include "imgui_impl_opengl3.h"
#include "imgui_internal.h"

#include <windows.h>
#include <ShlObj.h>
#include <vector>

#include "jni.h"

typedef BOOL(__stdcall * wglSwapBuffers_t)(HDC);
typedef LRESULT(WINAPI * CallWindowProcA_t)(WNDPROC, HWND, UINT, WPARAM, LPARAM);

wglSwapBuffers_t oSwapBuffers = NULL;
CallWindowProcA_t oCallWindowProcA = NULL;

HGLRC   g_GLRenderContext;
HDC     g_HDCDeviceContext;
HWND    g_hwnd;

int     g_display_w = 800;
int     g_display_h = 600;

typedef struct {
    const char* title;
    int progress;
} queueStatus_t;

std::vector<queueStatus_t> queues;

bool agreed = false;

bool isShowErrorMsg = false;
char *errorHeader;
char *errorMsg;

const char* excToStr(JNIEnv* env) {
    jthrowable e = env->ExceptionOccurred();
    env->ExceptionClear(); // clears the exception; e seems to remain valid

    jclass clazz = env->GetObjectClass(e);
    jmethodID getMessage = env->GetMethodID(clazz,
        "getMessage",
        "()Ljava/lang/String;");
    jstring message = (jstring)env->CallObjectMethod(e, getMessage);
    const char *mstr = env->GetStringUTFChars(message, NULL);
    // do whatever with mstr
    env->ReleaseStringUTFChars(message, mstr);
    env->DeleteLocalRef(message);
    env->DeleteLocalRef(clazz);
    env->DeleteLocalRef(e);
    return mstr;
}

void showErrorMsg(char* msg, char* header) {
    isShowErrorMsg = true;
    errorHeader = header;
    errorMsg = msg;
}

int fetchThread() {
    TCHAR pf[MAX_PATH];
    SHGetSpecialFolderPath(0, pf, CSIDL_PROGRAM_FILES, FALSE);

    char libStr[MAX_PATH];
    snprintf(libStr, MAX_PATH, "-Djava.class.path=%s\\%s", pf, "osumer2\\lib\\osumer-lib-2.0.0-SNAPSHOT.jar");

    JavaVMOption jvmopt[1];
    jvmopt[0].optionString = libStr;

    JavaVMInitArgs vmArgs;
    vmArgs.version = JNI_VERSION_1_2;
    vmArgs.nOptions = 1;
    vmArgs.options = jvmopt;
    vmArgs.ignoreUnrecognized = JNI_TRUE;

    JavaVM *javaVM;
    JNIEnv *jniEnv;

    // Create the JVM
    long flag = JNI_CreateJavaVM(&javaVM, (void**)
        &jniEnv, &vmArgs);
    if (flag == JNI_ERR) {
        //MessageBox(g_hwnd, "Could not create Java VM!", "osumer2 Overlay Error", MB_ICONERROR | MB_OK);
        showErrorMsg("Could not create Java VM!", "osumer2 Overlay Error");
        return 1;
    }

    jclass jRmiClass = jniEnv->FindClass("com/github/mob41/osumer/rmi/OverlayRmi");

    if (jRmiClass == NULL) {
        //MessageBox(g_hwnd, "Could not find RMI class for operation.", "osumer2 Overlay Error", MB_ICONERROR | MB_OK);
        showErrorMsg("Could not find RMI class for operation.", "osumer2 Overlay Error");
        jniEnv->ExceptionDescribe();
        javaVM->DestroyJavaVM();
        return 1;
    }

    jclass jQueueClass = jniEnv->FindClass("com/github/mob41/osumer/queue/QueueStatus");

    if (jQueueClass == NULL) {
        //MessageBox(g_hwnd, "Could not find Queue class", "osumer2 Overlay Error", MB_ICONERROR | MB_OK);
        showErrorMsg("Could not find Queue class", "osumer2 Overlay Error");
        jniEnv->ExceptionDescribe();
        javaVM->DestroyJavaVM();
        return 1;
    }

    jfieldID jQueueTitleFieldId = jniEnv->GetFieldID(jQueueClass, "title", "Ljava/lang/String;");

    if (jQueueTitleFieldId == NULL) {
        //MessageBox(g_hwnd, "Could not find title field in class", "osumer2 Overlay Error", MB_ICONERROR | MB_OK);
        showErrorMsg("Could not find title field in class", "osumer2 Overlay Error");
        jniEnv->ExceptionDescribe();
        javaVM->DestroyJavaVM();
        return 1;
    }

    jfieldID jQueueProgressFieldId = jniEnv->GetFieldID(jQueueClass, "progress", "I");

    if (jQueueProgressFieldId == NULL) {
        //MessageBox(g_hwnd, "Could not find progress field in class", "osumer2 Overlay Error", MB_ICONERROR | MB_OK);
        showErrorMsg("Could not find progress field in class", "osumer2 Overlay Error");
        jniEnv->ExceptionDescribe();
        javaVM->DestroyJavaVM();
        return 1;
    }

    jmethodID jRmiInitMethodId = jniEnv->GetMethodID(jRmiClass, "<init>", "()V");

    if (jRmiInitMethodId == NULL) {
        //MessageBox(g_hwnd, "Could not find initialization method in RMI class", "osumer2 Overlay Error", MB_ICONERROR | MB_OK);
        showErrorMsg("Could not find initialization method in RMI class", "osumer2 Overlay Error");
        jniEnv->ExceptionDescribe();
        javaVM->DestroyJavaVM();
        return 1;
    }

    jobject jRmiObj = jniEnv->AllocObject(jRmiClass);

    jniEnv->CallObjectMethod(jRmiObj, jRmiInitMethodId);

    if (jniEnv->ExceptionCheck()) {
        //MessageBox(g_hwnd, excToStr(jniEnv), "Error initializing RMI", MB_ICONERROR | MB_OK);
        showErrorMsg((char*) excToStr(jniEnv), "Error initializing RMI");
        jniEnv->ExceptionDescribe();
        javaVM->DestroyJavaVM();
        return 1;
    }

    jmethodID jRmiTestMethodId = jniEnv->GetMethodID(jRmiClass, "test", "()V");

    if (jRmiTestMethodId == NULL) {
        //MessageBox(g_hwnd, "Could not find test method", "osumer2 Overlay Error", MB_ICONERROR | MB_OK);
        showErrorMsg("Could not find test method", "osumer2 Overlay Error");
        jniEnv->ExceptionDescribe();
        javaVM->DestroyJavaVM();
        return 1;
    }

    jmethodID jRmiQueuesMethodId = jniEnv->GetMethodID(jRmiClass, "getQueues", "()[Lcom/github/mob41/osumer/queue/QueueStatus;");

    if (jRmiQueuesMethodId == NULL) {
        //MessageBox(g_hwnd, "Could not find get queues method.", "osumer2 Overlay Error", MB_ICONERROR | MB_OK);
        showErrorMsg("Could not find get queues method.", "osumer2 Overlay Error");
        jniEnv->ExceptionDescribe();
        javaVM->DestroyJavaVM();
        return 1;
    }

    jniEnv->CallVoidMethod(jRmiObj, jRmiTestMethodId);

    if (jniEnv->ExceptionCheck()) {
        //MessageBox(g_hwnd, excToStr(jniEnv), "Error executing test method", MB_ICONERROR | MB_OK);
        showErrorMsg((char*)excToStr(jniEnv), "Error executing test method");
        jniEnv->ExceptionDescribe();
        javaVM->DestroyJavaVM();
        return 1;
    }

    while (true) {
        jobjectArray jQueues = (jobjectArray)jniEnv->CallObjectMethod(jRmiObj, jRmiQueuesMethodId); //TODO: Event oriented

        if (jniEnv->ExceptionCheck()) {
            //MessageBox(g_hwnd, excToStr(jniEnv), "Error executing queues method", MB_ICONERROR | MB_OK);
            showErrorMsg((char*)excToStr(jniEnv), "Error executing queues method");
            jniEnv->ExceptionDescribe();
            javaVM->DestroyJavaVM();
            return 1;
        }

        jsize len = jniEnv->GetArrayLength(jQueues);
        int i;
        jobject queueObj;
        jstring title;
        jint progress;
        const char* titleStr;

        queues.clear();

        for (i = 0; i < len; i++) {
            queueStatus_t status;

            queueObj = jniEnv->GetObjectArrayElement(jQueues, i);
            title = (jstring)jniEnv->GetObjectField(queueObj, jQueueTitleFieldId);
            progress = jniEnv->GetIntField(queueObj, jQueueProgressFieldId);
            titleStr = jniEnv->GetStringUTFChars(title, NULL);

            if (progress >= 100) {
                continue;
            }

            status.title = titleStr;
            status.progress = progress;

            queues.push_back(status);
        }

        Sleep(500);
    }
    javaVM->DestroyJavaVM();
    return 0;
}

extern LRESULT ImGui_ImplWin32_WndProcHandler(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam);
LRESULT WINAPI hkCallWindowProcA(WNDPROC lpPrevWndFunc, HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam) {
    if (ImGui_ImplWin32_WndProcHandler(hWnd, msg, wParam, lParam))
        return true;

    switch (msg)
    {
    case WM_SIZE:
        if (wParam != SIZE_MINIMIZED)
        {
            g_display_w = (UINT)LOWORD(lParam);
            g_display_h = (UINT)HIWORD(lParam);
        }
        return 0;
    case WM_SYSCOMMAND:
        if ((wParam & 0xfff0) == SC_KEYMENU) // Disable ALT application menu
            return 0;
        break;
    case WM_DESTROY:
        PostQuitMessage(0);
        return 0;
    }
    return oCallWindowProcA(lpPrevWndFunc, hWnd, msg, wParam, lParam);
}

char* concat(const char *s1, const char *s2)
{
    char *result = reinterpret_cast<char*>(malloc(strlen(s1) + strlen(s2) + 1)); // +1 for the null-terminator
    // in real code you would check for errors in malloc here
    strcpy(result, s1);
    strcat(result, s2);
    return result;
}

BOOL _stdcall hkSwapBuffers(HDC hdc) {
    // Start the Dear ImGui frame
    ImGui_ImplOpenGL3_NewFrame();
    ImGui_ImplWin32_NewFrame();
    ImGui::NewFrame();
    ImGuiIO& io = ImGui::GetIO(); (void)io;

    if (isShowErrorMsg) {
        isShowErrorMsg = false;
        ImGui::OpenPopup("Error occurred");
    }

    if (!agreed) {
        ImGui::OpenPopup("osumer2 Overlay License Agreement");
    }
    else {
        {
            static float f = 0.0f;
            static int counter = 0;

            ImGui::Begin("osumer2", NULL, ImGuiWindowFlags_AlwaysAutoResize);

            if (queues.size() == 0) {
                ImGui::SetWindowCollapsed(true);
                ImGui::SetWindowPos(ImVec2(0.0f, 0.0f), ImGuiCond_Always);

                ImGui::Text("No pending downloads.");
            }
            else {
                ImGui::SetWindowCollapsed(false);
                ImGui::SetWindowPos(ImVec2(0.0f, g_display_h / 3.0f), ImGuiCond_Always);

                int i;
                queueStatus_t status;
                char progressStr[50];
                for (i = queues.size() - 1; i >= 0; i--) {
                    status = queues[i];
                    sprintf(progressStr, "%d%%", status.progress);
                    ImGui::Text(status.title);
                    ImGui::ProgressBar(status.progress / 100.0f, ImVec2(-1.0f, 0.0f));
                }
            }

            ImGui::End();
        }
    }

    if (ImGui::BeginPopupModal("Error occurred", NULL, NULL)) {
        io.WantCaptureMouse = true;
        io.MouseDrawCursor = true;
        ImGui::Text(errorHeader);
        ImGui::Separator();
        ImGui::Text(errorMsg);

        if (ImGui::Button("Dismiss")) {
            io.WantCaptureMouse = false;
            io.MouseDrawCursor = false;
            ImGui::CloseCurrentPopup();
        }
        ImGui::SameLine();
        if (ImGui::Button("Terminate osu!")) {
            DestroyWindow(g_hwnd);
        }

        ImGui::EndPopup();
    }
    else {
        io.MouseDrawCursor = false;
    }

    if (ImGui::BeginPopupModal("osumer2 Overlay License Agreement", NULL, NULL)) {
        io.MouseDrawCursor = true;
        ImGui::Text("By using the osumer2 overlay program, \nyou agree with the terms in osumer2's License, \nand you are using this in your risk.\n\nPressing \"Disagree\" will terminate the application.");

        if (ImGui::Button("Agree")) {
            agreed = true;
            io.WantCaptureMouse = false;
            io.MouseDrawCursor = false;
            ImGui::CloseCurrentPopup();
        }
        ImGui::SameLine();
        if (ImGui::Button("Disagree")) {
            DestroyWindow(g_hwnd);
        }

        ImGui::EndPopup();
    }

    // Rendering
    ImGui::Render();
    wglMakeCurrent(g_HDCDeviceContext, g_GLRenderContext);
    //glViewport(0, 0, g_display_w, g_display_h);                 //Display Size got from Resize Command
    ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
    wglMakeCurrent(g_HDCDeviceContext, g_GLRenderContext);

    return oSwapBuffers(hdc);
}

void CreateGlContext() {

    PIXELFORMATDESCRIPTOR pfd =
    {
        sizeof(PIXELFORMATDESCRIPTOR),
        1,
        PFD_DRAW_TO_WINDOW | PFD_SUPPORT_OPENGL | PFD_DOUBLEBUFFER,    //Flags
        PFD_TYPE_RGBA,        // The kind of framebuffer. RGBA or palette.
        32,                   // Colordepth of the framebuffer.
        0, 0, 0, 0, 0, 0,
        0,
        0,
        0,
        0, 0, 0, 0,
        24,                   // Number of bits for the depthbuffer
        8,                    // Number of bits for the stencilbuffer
        0,                    // Number of Aux buffers in the framebuffer.
        PFD_MAIN_PLANE,
        0,
        0, 0, 0
    };

    g_HDCDeviceContext = GetDC(g_hwnd);

    int pixelFormal = ChoosePixelFormat(g_HDCDeviceContext, &pfd);
    SetPixelFormat(g_HDCDeviceContext, pixelFormal, &pfd);
    g_GLRenderContext = wglCreateContext(g_HDCDeviceContext);
    wglMakeCurrent(g_HDCDeviceContext, g_GLRenderContext);
}

int hookThread()
{
    //MessageBox(0, "hook", "o", MB_OK);
    CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)fetchThread, NULL, 0, NULL);

    Sleep(5000);
    g_hwnd = FindWindow(NULL, "osu!");

    if (g_hwnd == NULL) {
        return 1;
    }

    CreateGlContext();
    //SetSwapInterval(1);
    gl3wInit();

    IMGUI_CHECKVERSION();
    ImGui::CreateContext();
    ImGuiIO& io = ImGui::GetIO(); (void)io;
    io.IniFilename = NULL;

    //Init Win32
    ImGui_ImplWin32_Init(g_hwnd);

    //Init OpenGL Imgui Implementation
    // GL 3.0 + GLSL 130
    const char* glsl_version = "#version 130";
    ImGui_ImplOpenGL3_Init(glsl_version);

    // Setup style
    ImGui::StyleColorsDark();

    MH_Initialize();

    MH_STATUS status;

    status = MH_CreateHookApiEx(L"opengl32", "wglSwapBuffers", &hkSwapBuffers, reinterpret_cast<LPVOID*>(&oSwapBuffers), NULL);
    status = MH_CreateHookApiEx(L"user32", "CallWindowProcA", &hkCallWindowProcA, reinterpret_cast<LPVOID*>(&oCallWindowProcA), NULL);

    MH_EnableHook(MH_ALL_HOOKS);
    return 0;
}

BOOL WINAPI DllMain(HINSTANCE hInstance, DWORD fdwReason, LPVOID)
{
    DisableThreadLibraryCalls(hInstance);

    switch (fdwReason)
    {
    case DLL_PROCESS_ATTACH:
        CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)hookThread, NULL, 0, NULL);
        break;
    }

    return TRUE;
}