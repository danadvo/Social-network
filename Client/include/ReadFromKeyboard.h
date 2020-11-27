//
// Created by danadvo@wincs.cs.bgu.ac.il on 12/31/18.
//

#ifndef CLIENT_READFROMKEYBOARD_H
#define CLIENT_READFROMKEYBOARD_H


#include <thread>
#include "connectionHandler.h"

using namespace std;
class ReadFromKeyboard {

private:
    ConnectionHandler* conHelder;
    bool* logIn;
    thread* th;

    void sendShort(short i);
    void send2params(string stream,string token);
    void sendFollow(string stream, string token);

public:
    ReadFromKeyboard(ConnectionHandler* ch, bool* logged, thread* t1);
    void run();

};


#endif //CLIENT_READFROMKEYBOARD_H
