//
// Created by yona on 02/01/2022.
//
#include "readTask.h"
#include "connectionHandler.h"
#include <string>
using namespace std;

readTask::readTask(int &currentState, ConnectionHandler &con):currentState(currentState), connectionHandler(con)
        {};

void readTask::run() {
        while(currentState == 2){
            string answer;

            if (!connectionHandler.getLine(answer)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }

            cout<< answer << endl;
            if(answer == "ACK 3")
//                terminate();
                currentState = 3;
            else if(answer == "ERROR 3")
                currentState = 2;

        }



}

