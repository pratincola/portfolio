package logicfactory;

import utils.DirectClock;
import utils.Message;
import utils.MessageImplementation;

/**
 * Created by prateek on 3/8/14.
 */
public class LamportMutex implements utils.Lock {
    public static final int Infinity = -1;
    private static final LamportMutex singleton = new LamportMutex();
    static DirectClock v;
    static int [] q ; // request queue
    static MessageImplementation mil = new MessageImplementation();
    private static int N, myId;

    /* Static 'instance' method */
    public static LamportMutex getInstance() {
        return singleton;
    }

    public static synchronized String handleMsg(Message m, int src , String tag){
        String msg = "none";
        int timestamp = m.getClock();
        v.receiveAction(src , timestamp);
        if (tag.equals("request")) {
            q[src] = timestamp;
            msg =  mil.sendAck( m.getSrcId(), myId , "ack", String.valueOf(v.getValue(myId)));

        }
        else if ( tag.equals("release"))
            q[src] = Infinity;
//        StaticWaits.staticNotify(); // okayCS() may be true now
        return msg;
    }

    public void LamportMutex_Init(int myServerID, int numOfServers ) {
        myId = myServerID;
        N = numOfServers;
        v = new DirectClock(N, myId);
        q = new int [N + 1]; // Since all our serverIDs start with 1 and not 0, however, the loops start at 0

        for (int j = 0; j <= N; j++){
            q[j] = Infinity;
        }
    }

    @Override
    public synchronized void requestCS(){
        v.tick();
        q[myId] = v.getValue(myId);
        mil.broadcastMsg(myId, "request", String.valueOf(q[myId]));

        // Wait to enter CS
        while (! okayCS ())
            mil.myWait() ;
    }

    boolean okayCS() {
        for (int j = 1; j <= N; j++){
            if ( isGreater(q[myId], myId, q[j], j ))
                return false ;
            if (isGreater (q[myId], myId, v.getValue(j), j ))
                return false ;
        }
        return true ;
    }


    boolean isGreater (int entry1 , int pid1 , int entry2, int pid2){
        if ( entry2 == Infinity ){
            return false ;
        }
        return (( entry1 > entry2) || (( entry1 == entry2) && (pid1 > pid2 )));
    }

    @Override
    public synchronized void releaseCS () {
        q[myId] = Infinity ;
        mil.broadcastMsg(myId, "release", String.valueOf(v.getValue(myId)));
    }

}

