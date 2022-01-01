#include <connectionHandler.h>
 
using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
 
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_){}
    
ConnectionHandler::~ConnectionHandler() {
    close();
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
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, ';');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, ';');
}
 
bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.
    try {
		do{
			getBytes(&ch, 1);
            frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {




    int space = frame.find_first_of(' ');
    string op = frame.substr(0,space);
    //register
    // REGISTER ABC 12345 11-11-1996 [01STRING 0 PASSWORD 0 BIRTDAY 0]
    if(!op.compare("REGISTER")){
        int space2 = frame.find_first_of(' ', space + 1);
        string Opcode = "01";
        string userName = frame.substr(space + 1, space2 - space + 1);
        string OpAndUser = Opcode + userName;
        space = space2 + 1;
        space2 = frame.find_first_of(' ', space);
        string password = frame.substr(space, space2 - space);
        space = space2 + 1;
        string dateOfBirth = frame.substr(space , frame.size() - space);
        const char* first = OpAndUser.c_str(); // [01username 0]
        const char* second = password.c_str(); //[password 0]
        const char* third = dateOfBirth.c_str(); // [25-10-1996 0]
        int size = OpAndUser.size() + password.size() + dateOfBirth.size() + 3;
        char* final = new char[size];
        for (int i = 0; i < OpAndUser.size() + 1; i++){
            final[i] = first[i];
        }
        int j = OpAndUser.size() + 1;
        for (int i = 0; i < password.size() + 1; i++){
            final[j] = second[i];
            j++;
        } 
        j = OpAndUser.size() + 2 + password.size();
        for (int i = 0; i < dateOfBirth.size() + 1; i++){
            final[j] = third[i];
            j++;
        } 
    }














	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}