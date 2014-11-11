package utils;

import client.TCPClient;
import logicfactory.LamportMutex;
import logicfactory.businessLogic;
import logicfactory.library;
import server.serverAttribute;

import java.util.Map;
import java.util.StringTokenizer;

//import com.sun.tools.javac.util.Pair;

/**
 * Created by prateek on 3/9/14.
 */
public class MessageImplementation {

    private final String whitespaceRegex = "\\s";
    private final String whitespace = " ";

    serverAttribute server = serverAttribute.getInstance();
    businessLogic bl = new businessLogic();
    LamportMutex lm = LamportMutex.getInstance();

    public void broadcastMsg(int src, String MsgType , String Msg){
        for(Map.Entry<Integer,String> entry: server.getServerAddresses().entrySet()){
            // Send message to everyone but me
            if(entry.getKey() != src){
                Pair<String,Integer> idPair  = server.getAddressForServer(entry.getKey());
                sendMsg(idPair.second,idPair.first, entry.getKey() ,src, MsgType, Msg);
            }
        }
    }

    public void sendMsg( int destPort , String destIP, int destServerID, int srcServerID, String tag, String msg){
        // Compose message
        Message m = new Message(srcServerID, destServerID, tag, msg );
        TCPClient tcpClient = new TCPClient(destPort,destIP, m.toString());
        Thread tC = new Thread(tcpClient);
        tC.start();
        try {
            tC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(tcpClient.getStatus() == true && !m.tag.equals("replicate")){
            //Faking message from the faulted server.
            Message ms = new Message(destServerID, srcServerID, "ack", String.valueOf( Integer.valueOf(msg) + 1));
            LamportMutex.handleMsg(ms, destServerID, "ack");
        }

    }

    public String sendAck( int destServerID, int srcServerID, String tag, String msg){
        // Compose message
        Message m = new Message(srcServerID, destServerID, tag, msg );
        Pair<String, Integer> idPair =  server.getAddressForServer(destServerID);
        return m.toString();
    }

    // Have to distinguish the message from client vs. from another server
    public byte [] receiveMsg (String tcpMessage, library lib) throws InterruptedException {
        byte[] res = "false".getBytes();

        try{
            String [] message = tcpMessage.split(whitespaceRegex);
            if(message[0].equalsIgnoreCase("server")){
                // call mutex
                String ackMsg;
                StringTokenizer st = new StringTokenizer(tcpMessage);
                Message receivedMessage = Message.parseMsg(st);
                if(receivedMessage.getTag().equalsIgnoreCase("replicate")){
                    //replicate
                    bl.updateLib(receivedMessage.getMessage(), lib);
                    res = "Successful".getBytes();
                }else{
                    ackMsg = lm.handleMsg(receivedMessage, receivedMessage.getSrcId(), receivedMessage.getTag() );
                    res = ackMsg.getBytes();
                }
            }

            else{

                res =  bl.makeResponse(tcpMessage, lib);

            }
        }catch (NullPointerException e){

        }
        return res;
    }

    // useless, need to remove; but okay for now.
    public synchronized void myWait() {
        try {
            wait(1000);
        } catch (InterruptedException e) {System.err.println(e);
        }
    }

}