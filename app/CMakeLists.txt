cmake_minimum_required(VERSION 3.4.1)

set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../distribution)
message(STATUS "distribution_DIR:${distribution_DIR}")

set(lib_build_DIR $ENV{HOME}/tmp)
file(MAKE_DIRECTORY ${lib_build_DIR})
message(STATUS "lib_build_DIR:${lib_build_DIR}")

set(opensslVersion openssl-1.1.0f)

add_library(encrypt
        SHARED
        src/main/cpp/rsa_encrypt.cpp)

find_library(log-lib
        log)

# openssl env need
find_library(zlib
        z)

include_directories(${distribution_DIR}/${opensslVersion}/include/)

add_library(openssl-crypto STATIC IMPORTED)
set_target_properties(openssl-crypto
        PROPERTIES IMPORTED_LOCATION ${distribution_DIR}/${opensslVersion}/lib/${ANDROID_ABI}/libcrypto.a)

add_library(openssl-ssl STATIC IMPORTED)
set_target_properties(openssl-ssl
        PROPERTIES IMPORTED_LOCATION ${distribution_DIR}/${opensslVersion}/lib/${ANDROID_ABI}/libssl.a)

target_link_libraries(encrypt
        openssl-crypto
        openssl-ssl
        ${log-lib}
        ${zlib})