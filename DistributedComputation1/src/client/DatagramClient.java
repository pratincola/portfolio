package client;

import java.io.IOException;
import java.net.*;

/**
 * Created with IntelliJ IDEA.
 * User: prateek
 * Date: 2/2/14
 * Time: 11:36 PM
 * To change this template use File | Settings | File Templates.
 */

public class DatagramClient implements Runnable {
    int len = 1024;
    int port;
    String hostname;
    DatagramPacket sPacket , rPacket ;
    DatagramSocket udpClientSocket;
    byte[] sendData, readData = new byte[len];


    public DatagramClient(String message, int len, int port, String hostname) throws SocketException {
        this.len = len;
        this.port = port;
        this.hostname = hostname;
        this.sendData = message.getBytes();
        this.udpClientSocket = new DatagramSocket();
    }

    @Override
    public void run(){
        try {
            // Send Client connection data
            InetAddress ia = InetAddress.getByName(hostname);
            sPacket = new DatagramPacket(sendData, sendData.length, ia, port);
            udpClientSocket.send(sPacket);

            // Receive data from the server & display it to the user
            rPacket = new DatagramPacket(readData, readData.length);
            udpClientSocket.receive(rPacket);
            String retstring = new String (rPacket.getData (), 0, rPacket.getLength());
            System.out.print(retstring);

        } catch (UnknownHostException e){
            System.err.print(e);
        } catch (IOException e){
            System.err.print(e);
        }
    }
}