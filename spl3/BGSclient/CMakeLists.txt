cmake_minimum_required(VERSION 3.21)
project(BGSclient)

set(CMAKE_CXX_STANDARD 14)

include_directories(include)

add_executable(BGSclient
        include/connectionHandler.h
        src/BGSclient.cpp
        src/connectionHandler.cpp include/readTask.h src/readTask.cpp include/writeTask.h src/writeTask.cpp)
