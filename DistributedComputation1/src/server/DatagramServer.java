package server; /**
 * Created with IntelliJ IDEA.
 * User: prateek
 * Date: 1/30/14
 * Time: 10:43 PM
 * To change this template use File | Settings | File Templates.
 */
import logicfactories.BusinessLogic;

import java .io .*;
import java.net.*;
import java.util.concurrent.BlockingQueue;


public class DatagramServer implements Runnable{
    int port, cap;
    byte [] Ibuf;
    DatagramPacket rdataPacket, sdataPacket;
    DatagramSocket dataSocket;



    public DatagramServer(int serverPort, int bufflen){

        try {
            this.port = serverPort;
            Ibuf = new byte [bufflen] ;

            dataSocket = new DatagramSocket(port);

            } catch (IOException e ) {
                    System.err. println (e);
            }
    }

    @Override
    public void run(){
        rdataPacket = new DatagramPacket(Ibuf ,Ibuf.length);
        while (true) {
            try {
                // Receive data
                dataSocket.receive(rdataPacket);
                String msg = new String(rdataPacket.getData(), 0, rdataPacket.getLength());
                // Do business logic here

                byte [] data = BusinessLogic.getInstance().makeResponse(msg); // singleton call

                // Send data
                sdataPacket = new DatagramPacket(data, data.length, rdataPacket.getAddress(), rdataPacket.getPort());
                dataSocket.send(sdataPacket);

            } catch (IOException e ) {
                System.err. println (e);
            }
        }
    }

}