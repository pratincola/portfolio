package server;




import logicfactories.BusinessLogic;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
/**
 * Created with IntelliJ IDEA.
 * User: prateek
 * Date: 2/2/14mpo
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Responder implements Runnable{
    int socket ;
    DatagramPacket packet;
    DatagramSocket datasocket;


    public Responder( DatagramPacket packet) {
        this.socket = packet.getPort();
        this.packet = packet;

    }

    @Override
    public void run() {
        String msg = new String(packet.getData(), 0, packet.getLength());
        System.out.print("Server received a packet:" + msg );
    }

}
