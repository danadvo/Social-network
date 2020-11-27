package bgu.spl.net.api.bidi.msgs;

import java.util.LinkedList;
import java.util.List;

/**
 * class represents FOLLOW message , opcode 4
 */
public class Follow implements Message {
    private char follow;
    private short numOfUsers;
    private List<String> userNameList;

    public Follow(char follow, short numOfUsers, String[] users){
        this.follow = follow;
        this.numOfUsers = numOfUsers;
        userNameList= new LinkedList<>();
        for (String s : users)
            userNameList.add(s);
        }

    @Override
    public short getOpcode() {
        return 4;
    }

    public char getFollow() {
        return follow;
    }

    public short getNumOfUsers() { return numOfUsers; }

    public List<String> getUserNameList(){
        return userNameList;
    }
}
