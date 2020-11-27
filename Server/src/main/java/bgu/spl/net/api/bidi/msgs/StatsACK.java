package bgu.spl.net.api.bidi.msgs;

/**
 * class represents STATS ACK message which is an ACK with specific optional
 */
public class StatsACK extends ACK {
    private short numPosts;
    private short numOfFollowers;
    private short numOfFollowings;

    public StatsACK(short opcode, short numPosts, short numOfFollowers, short numOfFollowings){
        super(opcode);
        this.numPosts = numPosts;
        this.numOfFollowers = numOfFollowers;
        this.numOfFollowings = numOfFollowings;
    }

    public short getNumPosts() {
        return numPosts;
    }


    public short getNumOfFollowers() {
        return numOfFollowers;
    }


    public short getNumOfFollowings() {
        return numOfFollowings;
    }

}
