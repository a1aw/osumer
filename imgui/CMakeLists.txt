file(GLOB ROOT_SOURCES imgui/*.cpp)

add_library(imgui STATIC ${ROOT_SOURCES} imgui/examples/imgui_impl_opengl3.cpp imgui/examples/imgui_impl_win32.cpp)

target_link_libraries(imgui gl3w)

add_dependencies(imgui gl3w)