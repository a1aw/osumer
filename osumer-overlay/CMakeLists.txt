add_library(osumer-overlay SHARED dllmain.cpp)

add_dependencies(osumer-overlay gl3w)
add_dependencies(osumer-overlay minhook)
add_dependencies(osumer-overlay imgui)

target_link_libraries(osumer-overlay gl3w)
target_link_libraries(osumer-overlay imgui)
target_link_libraries(osumer-overlay minhook)
target_link_libraries(osumer-overlay ${JNI_LIBRARIES})