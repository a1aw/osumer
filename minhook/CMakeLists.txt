file(GLOB ROOT_SOURCES minhook/src/*.c)
file(GLOB HDE_SOURCES minhook/src/hde/*.c)

add_library(minhook STATIC ${ROOT_SOURCES} ${HDE_SOURCES})