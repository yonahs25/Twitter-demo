//
// Created by yona on 02/01/2022.
//
#include "readTask.h"
#include "connectionHandler.h"
#include <string>
using namespace std;

readTask::readTask(bool &shouldTerminate, ConnectionHandler &con):shouldTerminate(shouldTerminate), connectionHandler(con)
        {};

void readTask::run() {
        while(true){
            string answer;
            if (!connectionHandler.getLine(answer)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            //int len=answer.length();
//            answer.resize(len-1);
            cout<< answer << endl;
            if(answer == "ACK 3")
                break;
        }



}

