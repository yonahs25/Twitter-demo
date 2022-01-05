#include <connectionHandler.h>
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
short bytesToShort(const char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}
string addZero(int x){
    if(x<10)
        return "0"+ to_string(x);

    return to_string(x);

}
string DateAndTime(){
    time_t theTime = time(nullptr);
    struct tm *aTime = localtime((&theTime));
    int day = aTime->tm_mday;
    int month = aTime->tm_mon+1;
    int year = aTime->tm_year+1900;
    int hour = aTime->tm_hour;
    int minutes = aTime->tm_min;
    string ans = addZero(day)+"-"+ addZero(month)+"-"+ to_string(year) + " "
                 + addZero(hour)+":"+ addZero(minutes);
    return ans;
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

bool ConnectionHandler::getFrameAscii(std::string &frame, char delimiter) {
    char ch[2];
    char c;

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
    if (ch[0] == '0' && ch[1] == '9') {

        try {
            do {
                getBytes(&c, 1);
                frame.append(1, c);
            } while (delimiter != c);
        } catch (std::exception &e) {
            std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
            return false;
        }
        frame.resize(frame.size()-1);
        string op = "NOTIFICATION";
        string type;
        if (frame.at(2) == '0') type = "PM";
        else type = "Public";
        c = '\0';
        int space = frame.find_first_of(c);
        // [091string0]
        string name = frame.substr(3, space - 3);
        string content = frame.substr(space + 1);
        string newFrame = op + " " + type + " " + name + " " + content; // check if need to shrink later
        frame = newFrame;
    }
        //error
    else if (ch[0] == '1' && ch[1] == '1') {
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
        else{
            opcode.resize(opcode.size()-1);
            newframe = newframe + opcode;
        }

        frame = newframe;
    }
        //ack
    else {
        string ans;
        try {
            getBytes(ch, 2);
        } catch (std::exception &e) {
            std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
            return false;
        }
        if ((ch[1] != '7') && (ch[1] != '8') && ch[1] !='4'){
            try {
                do {
                    getBytes(&c, 1);
                }while (c!=delimiter);
            } catch (std::exception &e) {
                std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
                return false;
            }
        }
        if (ch[0] == '0' && ch[1] == '1'){
            string new_frame = "ACK 1";
            frame = new_frame;
        } else if (ch[0] == '0' && ch[1] == '2') {
            string new_frame = "ACK 2";
            frame = new_frame;
        } else if (ch[0] == '0' && ch[1] == '3') {
            string new_frame = "ACK 3";
            frame = new_frame;
        } else if (ch[0] == '0' && ch[1] == '4') {
            string name;
            try {
                do {
                    getBytes(&c, 1);
                    name.append(1,c);
                }while (c!=delimiter);
            } catch (std::exception &e) {
                std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
                return false;
            }
            name.resize(name.size()-1);
            string followOrUn = name.substr(0,1);
            name.erase(0,1);
            string new_frame = "ACK 4 " + followOrUn + " " +name;
            frame = new_frame;
        }else if (ch[0] == '0' && ch[1] == '5') {
            string new_frame = "ACK 5";
            frame = new_frame;
        }else if (ch[0] == '0' && ch[1] == '6') {
            string new_frame = "ACK 6";
            frame = new_frame;
        }else if(ch[0] == '1' && ch[1] == '2'){
            frame = "ACK 12";
        } else if (ch[0] == '0' && ch[1] == '7') {
            string alwaysAdd = "ACK 07 ";
            string ans;
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
                try {
                    getBytes(&c, 1);
                } catch (std::exception &e) {
                    std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
                    return false;
                }
            }
            ans.resize(ans.size()-1);
            frame = ans;
        } else if (ch[0] == '0' && ch[1] == '8') {
            string alwaysAdd = "ACK 08 ";
            string ans;
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
                try {
                    getBytes(&c, 1);
                } catch (std::exception &e) {
                    std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
                    return false;
                }
            }
            ans.resize(ans.size()-1);
            frame = ans;
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
            char final[size];
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
            sent = sendBytes(final, size);

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
            int size = OpAndUser.size() + password.size() + 3;
            char *final = new char[size];
            for (int i = 0; i < OpAndUser.size() + 1; i++) {
                final[i] = first[i];
            }
            int j = OpAndUser.size() + 1;
            for (int i = 0; i < password.size() + 1; i++) {
                final[j] = second[i];
                j++;
            }
            final[size - 1] = captcha;
            sent = sendBytes(final, size);


        } else if (!op.compare("FOLLOW")) {
            string Opcode = "04";
            string followOrUnfollow = frame.substr(7, 1);
            string username = frame.substr(9);
            string final = Opcode + followOrUnfollow + username;
            sent = sendBytes(final.c_str(), final.size());

            //need to send
        } else if (!op.compare("PM")) {
            string Opcode = "06";
            int space2 = frame.find_first_of(' ', space + 1);
            string userName = frame.substr(space + 1, space2 - space - 1);
            string OpAndUsername = Opcode + userName;
            string OpAndUser = Opcode + userName;
            string content = frame.substr(space2 + 1);
            string dateAndTime = DateAndTime();
            const char *first = OpAndUser.c_str();
            const char *second = content.c_str();
            const char *third = dateAndTime.c_str();
            int size = OpAndUser.size() + content.size() + dateAndTime.size() +3;
            char final[size];
            for (int i = 0; i < OpAndUser.size() + 1; i++) {
                final[i] = first[i];
            }
            int j = OpAndUser.size() + 1;
            for (int i = 0; i < content.size() + 1; i++) {
                final[j] = second[i];
                j++;
            } //[02username 0 password 1]
            j = OpAndUser.size() + 2 + content.size();
            for (int i = 0; i < dateAndTime.size() + 1; i++) {
                final[j] = third[i];
                j++;
            }

            sent = sendBytes(final, size);

        } else if (!op.compare("STAT")) {
            string Opcode = "08";
            string usernames = frame.substr(5);
            string final = Opcode + usernames;
            sent = sendBytes(final.c_str(), final.size() + 1);
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


