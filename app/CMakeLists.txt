cmake_minimum_required(VERSION 3.4.1)

set(distribution_DIR ${CMAKE_CURRENT_SOURCE_DIR}/distribution)

add_library(libencrypt
            SHARED
            src/main/cpp/rsa_encrypt.cpp)

find_library(log-lib
              log )

# openssl env need
find_library(zlib
               z)

include_directories(distribution/openssl-1.1.0f/include/)

add_library(openssl-crypto STATIC IMPORTED)
set_target_properties(openssl-crypto
  PROPERTIES IMPORTED_LOCATION ${distribution_DIR}/openssl-1.1.0f/lib/${ANDROID_ABI}/libcrypto.a)

add_library(openssl-ssl STATIC IMPORTED)
set_target_properties(openssl-ssl
  PROPERTIES IMPORTED_LOCATION ${distribution_DIR}/openssl-1.1.0f/lib/${ANDROID_ABI}/libssl.a)

target_link_libraries(libencrypt
                        openssl-crypto
                        openssl-ssl
                        ${log-lib}
                        ${zlib})