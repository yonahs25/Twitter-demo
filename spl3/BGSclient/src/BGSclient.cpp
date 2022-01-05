#include <stdlib.h>
#include <connectionHandler.h>
#include <mutex>
#include <thread>
#include <iostream>
#include <string>
#include <readTask.h>
#include <writeTask.h>

using namespace std;

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    int currentState = 2;
    // 1 - waiting for response on LOGOUT
    // 2 - running
    // 3 - done
    writeTask write(currentState, connectionHandler);
    readTask read(currentState, connectionHandler);
    thread t1(&writeTask::run, write);
    thread t2(&readTask::run, read);
    t1.join();
    t2.join();


    return 0;






}

