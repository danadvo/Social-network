#include <mutex>
#include <condition_variable>
#include <thread>
#include "../include/connectionHandler.h"
#include "../include/ReadFromKeyboard.h"
#include "../include/ReadFromSocket.h"

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
using namespace std;
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    
    ConnectionHandler con(host, port);
    if (!con.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    bool* logIn = new bool(false);
    ReadFromSocket rfs(&con, logIn);
    thread t1(&ReadFromSocket::run,&rfs);
    ReadFromKeyboard rfk(&con, logIn, &t1);
    thread t2(&ReadFromKeyboard::run,&rfk);

    t2.join();

    delete(logIn);
    return 0;
}
