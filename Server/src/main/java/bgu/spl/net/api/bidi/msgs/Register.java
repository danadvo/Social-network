package bgu.spl.net.api.bidi.msgs;

/**
 * class represents REGISTER message , opcode 1
 */
public class Register implements Message {
    private String username;
    private String password;

    public Register(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public short getOpcode() {
        return 1;
    }
}
