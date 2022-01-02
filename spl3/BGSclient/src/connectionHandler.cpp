#include <connectionHandler.h>
#include <sstream>
#include <iostream>
#include <string>
#include <stdlib.h>


using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
using namespace std;

ConnectionHandler::ConnectionHandler(string host, short port) : host_(host), port_(port), io_service_(),
                                                                socket_(io_service_) {}

ConnectionHandler::~ConnectionHandler() {
    close();
}
short bytesToShort(char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}

bool ConnectionHandler::connect() {
    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception &e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp) {
            tmp += socket_.read_some(boost::asio::buffer(bytes + tmp, bytesToRead - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }

    return true;

}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string &line) {
    return getFrameAscii(line, ';');
}

bool ConnectionHandler::sendLine(std::string &line) {
    return sendFrameAscii(line, ';');
}
void doSome() {
    cout << "hi" <<endl;
}

bool ConnectionHandler::getFrameAscii(std::string &frame, char delimiter) {
    char ch[2];

    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.

    //getting op code
    try {
        getBytes(ch, 2);
        frame.append(ch, 2);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }

    //notification
    if (!frame.compare("09")) {
        char c;
        try {
            do {
                getBytes(&c, 1);
                frame.append(1, c);
            } while (delimiter != c);
        } catch (std::exception &e) {
            std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
            return false;
        }
        string op = "NOTIFICATION";
        string type;
        if (frame.at(2) == '0') type = "PM";
        else type == "Public";
        c = '\0';
        int space = frame.find_first_of(c);
        // [091string0]
        string name = frame.substr(3, space - 3);
        string content = frame.substr(space + 1);
        string newFrame = op + " " + type + " " + name + " " + content; // check if need to shrink later
        frame = newFrame;
    }
        //error
    else if (!frame.compare("11")) { //TODO mistake, need to ignore 0
        char c;
        try {
            do {
                getBytes(&c, 1);
                frame.append(1, c);
            } while (delimiter != c);
        } catch (std::exception &e) {
            std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
            return false;
        }
        string newframe = "ERROR ";
        string opcode = frame.substr(2);
        if (opcode.at(0) == '0')
            newframe.append(1, opcode.at(1));
        else
            newframe = newframe + opcode;


        frame = newframe;
    }
        //ack
    else {
        try {
            getBytes(ch, 2);
        } catch (std::exception &e) {
            std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
            return false;
        }
        if (!frame.substr(2, 2).compare("01")) {
            string new_frame = "ACK 1";
            frame = new_frame;
        } else if (!frame.substr(2, 2).compare("02")) {
            string new_frame = "ACK 2";
            frame = new_frame;
        } else if (!frame.substr(2, 2).compare("03")) {
            string new_frame = "ACK 3";
            frame = new_frame;
        } else if (!frame.substr(2, 2).compare("04")) {
            string new_frame = "ACK 4";
            frame = new_frame;
        } else if (!frame.substr(2, 2).compare("07")) {
            string alwaysAdd = "ACK 07 ";
            string ans;
            char c;
            try {
                getBytes(&c, 1);
            } catch (std::exception &e) {
                std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
                return false;
            }
            while (delimiter != c) {
                ch[0] = c;
                try {
                    getBytes(&c, 1);
                } catch (std::exception &e) {
                    std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
                    return false;
                }
                ch[1] = c;
                short one = bytesToShort(ch);
                try {
                    getBytes(ch, 2);
                } catch (std::exception &e) {
                    std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
                    return false;
                }
                short two = bytesToShort(ch);

                try {
                    getBytes(ch, 2);
                } catch (std::exception &e) {
                    std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
                    return false;
                }
                short three = bytesToShort(ch);
                try {
                    getBytes(ch, 2);
                } catch (std::exception &e) {
                    std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
                    return false;
                }
                short four = bytesToShort(ch);
                ans = ans + alwaysAdd + to_string(one) + " " + to_string(two) +
                        " " + to_string(three) + " " + to_string(four) + '\n';
            }



        }


        char c;
        try {
            do {
                getBytes(&c, 1);
                frame.append(1, c);
            } while (delimiter != ch[0]);
        } catch (std::exception &e) {
            std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
            return false;
        }


        // if string is for log or stat call new function
        //else continue this
        //ACK message

        if (!frame.substr(0, 2).compare("10")) {
            if (!frame.substr(2, 2).compare("01")) {
                string new_frame = "ACK 1";
                frame = new_frame;
            }
            if (!frame.substr(2, 2).compare("02")) {
                string new_frame = "ACK 2";
                frame = new_frame;
            }
            if (!frame.substr(2, 2).compare("03")) {
                string new_frame = "ACK 3";
                frame = new_frame;
            }
            if (!frame.substr(2, 2).compare("04")) {
                string new_frame = "ACK 4";
                frame = new_frame;
            }
            if (!frame.substr(2, 2).compare("07")) {//logstat





            }
            if (!frame.substr(2, 2).compare("08")) {
                //stat
            }
        }
            //ERROR
        else if (!frame.substr(0, 2).compare("11")) {
            string newframe = "ERROR " + frame.substr(2);
            frame = newframe;
        }
            //NOTIFICATION
        else if (!frame.substr(0, 2).compare("09")) {
            string op = "NOTIFICATION";
            string type;
            if (frame.at(2) == '0') type = "PM";
            else type == "Public";
            char c = NULL;
            int space = frame.find_first_of(c);
            // [091string0]
            string name = frame.substr(3, space - 3);
            string content = frame.substr(space + 1);
            string newFrame = op + " " + type + " " + name + " " + content; // check if need to shrink later




        }
    }


    return true;
}


bool ConnectionHandler::sendFrameAscii(const std::string &frame, char delimiter) {

    bool sent;
    // if for op with no spaces
    if (!frame.compare("LOGSTAT")) {
        string final = "07";
        sent = sendBytes(final.c_str(), 2);
    } else if (!frame.compare("LOGOUT")) {
        string final = "03";
        sent = sendBytes(final.c_str(), 2);

    } else {
        int space = frame.find_first_of(' ');
        string op = frame.substr(0, space);
        //register
        // REGISTER ABC 12345 11-11-1996 [01STRING 0 PASSWORD 0 BIRTDAY 0]
        if (!op.compare("REGISTER")) {
            int space2 = frame.find_first_of(' ', space + 1);
            string Opcode = "01";
            string userName = frame.substr(space + 1, space2 - space - 1);
            string OpAndUser = Opcode + userName;
            space = space2 + 1;
            space2 = frame.find_first_of(' ', space);
            string password = frame.substr(space, space2 - space);
            space = space2 + 1;
            string dateOfBirth = frame.substr(space, frame.size() - space);
            const char *first = OpAndUser.c_str(); // [01username 0]
            const char *second = password.c_str(); //[password 0]
            const char *third = dateOfBirth.c_str(); // [25-10-1996 0]
            int size = OpAndUser.size() + password.size() + dateOfBirth.size() + 3;
            char *final = new char[size];
            for (int i = 0; i < OpAndUser.size() + 1; i++) {
                final[i] = first[i];
            }
            int j = OpAndUser.size() + 1;
            for (int i = 0; i < password.size() + 1; i++) {
                final[j] = second[i];
                j++;
            }
            j = OpAndUser.size() + 2 + password.size();
            for (int i = 0; i < dateOfBirth.size() + 1; i++) {
                final[j] = third[i];
                j++;
            }

        } else if (!op.compare("LOGIN")) {
            int space2 = frame.find_first_of(' ', space + 1);
            string Opcode = "02";
            string userName = frame.substr(space + 1, space2 - space - 1);
            string OpAndUser = Opcode + userName;
            space = space2 + 1;
            space2 = frame.find_first_of(' ', space);
            string password = frame.substr(space, space2 - space);
            char captcha = frame.at(frame.size() - 1);
            const char *first = OpAndUser.c_str(); // [01username 0]
            const char *second = password.c_str(); //[password 0]
            int size = OpAndUser.size() + password.size() + 2;
            char *final = new char[size];
            for (int i = 0; i < OpAndUser.size() + 1; i++) {
                final[i] = first[i];
            }
            int j = OpAndUser.size() + 1;
            for (int i = 0; i < password.size() + 1; i++) {
                final[j] = second[i];
                j++;
            } //[02username 0 password 1]
            final[size - 1] = captcha;

        } else if (!op.compare("FOLLOW")) {
            string Opcode = "04";
            string followOrUnfollow = frame.substr(7, 1);
            string username = frame.substr(9);
            string final = Opcode + followOrUnfollow + username;
            // aaaaa 5
            // [a a a a a 0] 6
            sendBytes(final.c_str(), final.size());

            //need to send
        } else if (!op.compare("PM")) {
            // PM username content
            string Opcode = "06";
            int space2 = frame.find_first_of(' ', space + 1);
            string userName = frame.substr(space + 1, space2 - space - 1);
            string OpAndUsername = Opcode + userName;


        } else if (!op.compare("STAT")) {
            string Opcode = "08";
            string usernames = frame.substr(5);
            string final = Opcode + usernames;
            sent = sendBytes(final.c_str(), final.size() + 1);
            // STAT aa|BB|CC
        } else if (!op.compare("BLOCK")) {
            string Opcode = "12";
            string username = frame.substr(6);
            string final = Opcode + username;
            sent = sendBytes(final.c_str(), final.size() + 1);
        } else {
            string opCode = "05";
            string content = frame.substr(5);
            string final = opCode + content;
            sent = sendBytes(final.c_str(), final.size() + 1);
        }


    }
    //bool result=sendBytes(frame.c_str(),frame.length());
    if (!sent) return false;
    return sendBytes(&delimiter, 1);
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try {
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}


