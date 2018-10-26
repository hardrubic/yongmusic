cmake_minimum_required(VERSION 3.4.1)

set(lameHome ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jni/mp3lame-3.99.5)

include_directories(${lameHome})
include_directories(${lameHome}/include)
aux_source_directory(${lameHome} lameSource)

add_library(mp3lame
        SHARED
        ${lameSource}
        ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jni/lame_util.c)

find_library(log-lib
        log)

target_link_libraries(mp3lame
        ${log-lib})