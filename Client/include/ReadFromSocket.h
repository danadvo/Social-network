//
// Created by danadvo@wincs.cs.bgu.ac.il on 12/31/18.
//

#ifndef CLIENT_READFROMSOCKET_H
#define CLIENT_READFROMSOCKET_H

#include "connectionHandler.h"
using namespace std;

class ReadFromSocket {
private:
    ConnectionHandler* conHelder;
    bool* logIn;

    void usersAck (string ans);
    void statsAck (string ans);

public:
    ReadFromSocket(ConnectionHandler* ch, bool* logged);
    void run();

};


#endif //CLIENT_READFROMSOCKET_H
