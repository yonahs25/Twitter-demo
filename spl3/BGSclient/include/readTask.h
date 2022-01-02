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
    bool& shouldTerminate;
    ConnectionHandler &connectionHandler;



public:
    readTask(bool &shouldTerminate, ConnectionHandler &con);
    void run();
};


#endif //BGSCLIENT_READTASK_H
