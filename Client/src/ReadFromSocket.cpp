//
// Created by danadvo@wincs.cs.bgu.ac.il on 12/31/18.
//

#include "../include/ReadFromSocket.h"
#include "../include/connectionHandler.h"
using namespace std;

ReadFromSocket::ReadFromSocket(ConnectionHandler* ch, bool* logged) :conHelder(ch), logIn(logged){}

void ReadFromSocket::run() {
    while (true) {
        string ans;
        char twoBts[2];
        short opcode;
        conHelder->getBytes(&twoBts[0], 2);
        opcode = conHelder->bytesToShort(twoBts);

        if(opcode==9) {
            ans = "NOTIFICATION";
            char type;
            conHelder->getBytes(&type, 1);
            if (type == 0)
                ans = ans + " PM ";
            else if (type == 1)
                ans = ans + " Public ";
            conHelder->getLine(ans);
            ans = ans + " ";
            conHelder->getLine(ans);
            cout << ans << endl;

        } else if(opcode== 10) {
                ans = "ACK ";
                char twobts2[2];
                conHelder->getBytes(&twobts2[0], 2);
                short msgOPcode = conHelder->bytesToShort(twobts2);
                ans = ans + to_string(msgOPcode);
                if (msgOPcode == 2) {
                    *logIn = true;
                    cout << ans << endl;
                } else if (msgOPcode == 4 || msgOPcode == 7) {
                    usersAck(ans);
                } else if (msgOPcode == 8) {
                    statsAck(ans);
                } else if (msgOPcode == 3) {
                    cout << ans << endl;
                    break;
                } else
                    cout << ans << endl;

            } else if (opcode==11){
                ans = "ERROR ";
                char twobts3[2];
                conHelder->getBytes(twobts3, 2);
                short msgOpcode = conHelder->bytesToShort(twobts3);
                ans = ans + to_string(msgOpcode);
                cout << ans << endl;
            }
        }
    }

void ReadFromSocket::usersAck(string ans) {
        char twobtsNum[2];
        conHelder->getBytes(&twobtsNum[0], 2);
        short numUsers = conHelder->bytesToShort(&twobtsNum[0]);
        ans = ans + " " + to_string(numUsers);
        while (numUsers > 0) {
            string user;
            conHelder->getLine(user);
            ans = ans + " " + user;
            numUsers--;
        }
        cout << ans << endl;
}

void ReadFromSocket::statsAck(string ans) {
    char twobtsPosts[2];
    conHelder->getBytes(twobtsPosts,2);
    short numPosts = conHelder->bytesToShort(twobtsPosts);
    char twobtsFlwrs[2];
    conHelder->getBytes(twobtsFlwrs,2);
    short numFollowers = conHelder->bytesToShort(twobtsFlwrs);
    char twobtsFlwng[2];
    conHelder->getBytes(twobtsFlwng,2);
    short numFollowings = conHelder->bytesToShort(twobtsFlwng);
    cout<< ans + " " + to_string(numPosts) + " " + to_string(numFollowers) + " " + to_string(numFollowings) << endl;
}



