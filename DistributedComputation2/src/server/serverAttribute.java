package server;

//import com.sun.tools.javac.util.Pair;

import utils.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by prateek on 3/4/14.
 */
public class serverAttribute {

    private static final serverAttribute singleton = new serverAttribute();
    private final String colonRegex = ":";
    int serverID, numOfServersInstances;
    Map<Integer, String> serverAddresses = new LinkedHashMap<Integer, String>();
    List<String> serverInstruction = new ArrayList<String>();
    private  int sleepCounter = -1;
    private  long Time2Sleep = 0L;

    private serverAttribute() {
    }

    /* Static 'instance' method */
    public static serverAttribute getInstance() {
        return singleton;
    }

    public  int getSleepCounter() {
        return sleepCounter;
    }

    public void setSleepCounter(int sC) { sleepCounter = sC; }

    public void decSleepCounter() {
        sleepCounter--;
    }

    public long getTime2Sleep() {
        return Time2Sleep;
    }

    public void setTime2Sleep(long time2Sleep) {
        Time2Sleep = time2Sleep;
    }

    public int getServerID() {
        return serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public String getCanonicalServerID() {
        return "s" + String.valueOf(serverID);
    }

    public int getNumOfServersInstances() {
        return numOfServersInstances;
    }

    public void setNumOfServersInstances(int numOfServersInstances) {
        this.numOfServersInstances = numOfServersInstances;
    }

    public Map<Integer, String> getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(Map<Integer, String> serverAddresses) {
        this.serverAddresses = serverAddresses;
    }

    public List<String> getServerInstruction() {
        return serverInstruction;
    }

    public void setServerInstruction(List<String> serverInstruction) {
        this.serverInstruction = serverInstruction;
    }

    public Pair<String, Integer> getAddressForServer(int i){
        String val = serverAddresses.get(i);
        String[] tokens = val.split(colonRegex);
        return Pair.create(tokens[0], Integer.valueOf(tokens[1]));

    }

}
