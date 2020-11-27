package bgu.spl.net.api.bidi.msgs;

/**
 * class represents LOGIN message , opcode 2
 */
public class Login implements Message {
    private String username;
    private String password;

    public Login(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public short getOpcode() {
        return 2;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
