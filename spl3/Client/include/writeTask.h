//
// Created by yona on 02/01/2022.
//
#include <stdlib.h>
#include <connectionHandler.h>
#include <mutex>
#include <thread>
#include <iostream>
#include <string>
#include <boost/asio.hpp>

#ifndef BGSCLIENT_WRITETASK_H
#define BGSCLIENT_WRITETASK_H

class writeTask{
private:
    ConnectionHandler &connectionHandler;
    int& currentState;


public:
    writeTask(int& currentState, ConnectionHandler &con);
    void run();

};


#endif //BGSCLIENT_WRITETASK_H


