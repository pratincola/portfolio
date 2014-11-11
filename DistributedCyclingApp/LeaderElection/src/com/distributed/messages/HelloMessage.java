package com.distributed.messages;

/**
 * Created by prateek on 4/25/14.
 */
public class HelloMessage extends MessageFactory {

//    private int leaderPriorityID ;

    // Constructor for Hello Message
    public HelloMessage( int myPID , int lid, int leaderPriorityID, long date)  {
        this.pid = myPID;
        this.lid = lid;
        this.leaderPriorityID = leaderPriorityID;
        this.timeStamp = date;
    }

    public void setLeaderPriorityID(int leaderPriorityID) {
        this.leaderPriorityID = leaderPriorityID;
    }

    @Override
    public String toString() {
        return "HelloMessage {" +
                "leaderPriorityID= " + leaderPriorityID +
                " " + super.toString() + " }";
    }
}
