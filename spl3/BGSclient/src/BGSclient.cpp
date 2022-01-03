#include <stdlib.h>
#include <connectionHandler.h>
#include <mutex>
#include <thread>
#include <iostream>
#include <string>
#include <readTask.h>
#include <writeTask.h>

using namespace std;

/*
class readTask
{
private:
    bool& shouldTerminate;
    ConnectionHandler& ConnectionHandler;

public:
    readTask(bool &shouldTerminate, class ConnectionHandler &connectionHandler) : shouldTerminate(shouldTerminate),
                                                                                  ConnectionHandler(connectionHandler) {}


    void run()
    {
        while(true){
            string answer;
            if (!ConnectionHandler.getLine(answer)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            int len=answer.length();
            answer.resize(len-1);
            cout<< answer << endl;
            if(answer == "ACK 3")
                break;
        }
    }
};

class writeTask
{
private:
    bool& shouldTerminate;
    ConnectionHandler& ConnectionHandler;
    const short bufsize = 1024;




public:
    void run()
    {
        while (1)
        {
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            if (!shouldTerminate) break;
            if (!ConnectionHandler.sendLine(line)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }

        }


    }







    short bytesToShort(char* bytesArr)
    {
        short result = (short)((bytesArr[0] & 0xff) << 8);
        result += (short)(bytesArr[1] & 0xff);
        return result;
    }
};
 */

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

    bool shouldTerminate = true;
    writeTask write(shouldTerminate, connectionHandler);
    readTask read(shouldTerminate, connectionHandler);
    thread t1(&writeTask::run, write);
    thread t2(&readTask::run, read);
    t1.join();






}

