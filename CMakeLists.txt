cmake_minimum_required (VERSION 3.0)

project("osumer-native")

set(JAVA_HOME "C:\\Program Files (x86)\\Java\\jdk1.8.0_212")
#find_package(JNI REQUIRED)

#include_directories(${JNI_INCLUDE_DIRS})
include_directories("${JAVA_HOME}/include")
include_directories("${JAVA_HOME}/include/win32")
file(GLOB JNI_LIBRARIES "${JAVA_HOME}/lib/*.lib")

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/minhook/minhook/include")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/imgui/imgui")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/imgui/imgui/examples")

add_subdirectory(gl3w)
add_subdirectory(minhook)
add_subdirectory(imgui)
add_subdirectory(osumer-jni)
add_subdirectory(osumer-overlay)