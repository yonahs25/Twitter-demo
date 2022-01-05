#include <stdlib.h>
#include <connectionHandler.h>
#include <mutex>
#include <thread>
#include <iostream>
#include <string>
#include <boost/asio.hpp>

#ifndef BGSCLIENT_READTASK_H
#define BGSCLIENT_READTASK_H

class readTask{
private:
    ConnectionHandler &connectionHandler;
    int& currentState;



public:
    readTask(int &currentState, ConnectionHandler &con);
    void run();
};


#endif //BGSCLIENT_READTASK_H
