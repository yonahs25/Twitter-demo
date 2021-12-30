#include <stdlib.h>
#include <connectionHandler.h>
#include <mutex>
#include <thread>
#include <iostream>



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

    bool* shouldTerminate = new bool(true);





}

class readTask 
{
private:
    int _id;
    std::mutex & _mutex;
    bool& shouldTerminate;
    ConnectionHandler& ConnectionHandler;

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