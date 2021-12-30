#include <stdlib.h>
#include <connectionHandler.h>
#include <mutex>
#include <thread>
#include <iostream>


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
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





}

class readTask 
{
private:
    int _id;
    std::mutex & _mutex;
    bool& shouldTerminate;
    ConnectionHandler& ConnectionHandler;
    const short bufsize = 1024;

public:

    void run()
    {

    }
    









    void shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}
};

class writeTask
{
private:
    bool& shouldTerminate;
    ConnectionHandler& ConnectionHandler;



public:    








    short bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
};