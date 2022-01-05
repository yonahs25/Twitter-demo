//
// Created by yona on 02/01/2022.
//
#include "writeTask.h"
using namespace std;

writeTask::writeTask(int &currentState, ConnectionHandler &con):currentState(currentState), connectionHandler(con)
{};


void writeTask::run() {
    while (currentState == 2)
    {
        char buf[1024];
        std::cin.getline(buf, 1024);
        std::string line(buf);
        if(line=="LOGOUT")
            currentState = 1;


        if (!connectionHandler.sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        while (currentState==1){};
        if (currentState==3) break;

    }

}