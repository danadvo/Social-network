//
// Created by danadvo@wincs.cs.bgu.ac.il on 12/31/18.
//

#include <string>
#include <iostream>
#include <sstream>
#include "../include/ReadFromKeyboard.h"
#include "../include/connectionHandler.h"

using namespace std;

ReadFromKeyboard :: ReadFromKeyboard(ConnectionHandler* ch, bool* logged, thread* t1) : conHelder(ch), logIn(logged), th(t1) {

}

void ReadFromKeyboard :: run(){
    while(true){
        short bufsize = 1024;
        char buf[1024];
        cin.getline(buf, bufsize);
        string line(buf);
        if (line == "LOGOUT") {
            sendShort(short(3));
            if(*logIn){
                th->join();
                break;
            }
        } else if (line == "USERLIST")
            sendShort(7);
        else {
            stringstream stream(line);
            string token;
            if (getline(stream,token, ' ')) {
                if (token == "REGISTER") {
                    sendShort(1);
                    send2params(line, token);
                }
                else if (token == "LOGIN") {
                    sendShort(2);
                    send2params(line, token);
                }
                else if (token == "FOLLOW"){
                    sendShort(4);
                    sendFollow(line,token);
                }
                else if (token == "POST"){
                    sendShort(5);
                    getline(stream,token);
                    conHelder->sendLine(token);

                }
                else if (token == "PM"){
                    sendShort(6);
                    send2params(line,token);
                }
                else if (token == "STAT"){
                    sendShort(8);
                    getline(stream,token);
                    conHelder->sendLine(token);
                }

            }
        }
    }

}

void ReadFromKeyboard::sendShort(short i) {
    char bytesArr[2];
     conHelder->shortToBytes(i,bytesArr);
    conHelder->sendBytes(bytesArr, sizeof(bytesArr));
}

void ReadFromKeyboard::send2params(string line , string token) {
    stringstream stream(line);
    getline(stream,token, ' ');
    getline(stream,token, ' ');
    conHelder->sendLine(token);
    getline(stream,token);
    conHelder->sendLine(token);
}

void ReadFromKeyboard::sendFollow(string line, string token) {
    stringstream stream(line);
    getline(stream ,token,' ');
    getline(stream,token,' ');
    char arr[1];
    if (token == "0")
        arr[0]=0;
    else if (token == "1")
        arr[0]=1;
    conHelder->sendBytes(arr,1);
    getline(stream,token,' ');
    sendShort((short)stoi(token));
    while (getline(stream,token,' '))
        conHelder->sendLine(token);
}
