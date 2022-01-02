//
// Created by yona on 02/01/2022.
//
#include "writeTask.h"

writeTask::writeTask(bool &shouldTerminate, ConnectionHandler &con):shouldTerminate(shouldTerminate), connectionHandler(con)
{};


void writeTask::run() {
    while (1)
    {
        char buf[1024];
        std::cin.getline(buf, 1024);
        std::string line(buf);
        if (!shouldTerminate) break;
        if (!connectionHandler.sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }

    }

}