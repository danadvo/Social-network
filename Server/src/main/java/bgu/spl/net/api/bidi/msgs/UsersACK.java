package bgu.spl.net.api.bidi.msgs;

import java.util.List;
/**
 * class represents STATS ACK message which is an ACK with specific optional
 */
public class UsersACK extends ACK {
    private short numOfUsers;
    private List<String> usernamesList;

    public UsersACK(short opcode, short numOfUsers, List<String> usernamesList){
        super(opcode);
        this.numOfUsers = numOfUsers;
        this.usernamesList = usernamesList;
    }

    public short getNumOfUsers() {
        return numOfUsers;
    }

    public List<String> getUsernamesList() {
        return usernamesList;
    }
}
