project("osumer-jni")

add_library(osumer-jni SHARED dllmain.cpp com_github_mob41_osumer_daemon_OsumerNative.cpp)