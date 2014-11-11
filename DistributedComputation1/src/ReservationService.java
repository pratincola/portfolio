/**
 * Created with IntelliJ IDEA.
 * User: prateek
 * Date: 1/30/14
 * Time: 11:38 PM
 * To change this template use File | Settings | File Templates.
 */
import client.DatagramClient;
import client.TCPClient;
import logicfactories.BusinessLogic;
import server.DatagramServer;
import server.TCPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

public class ReservationService {

    public static void main(String [] args) throws IOException {
        int theater_seat, server_port;
        int client_port;
        String server_ip;


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        switch (args[0].charAt(1)) {
            case 's':{
                if (args.length < 4){
                    throw new IllegalArgumentException("Not a valid length: "+args);
                }
                theater_seat = Integer.parseInt(args[1]);


                if (! "-p".equalsIgnoreCase(args[2])){
                    throw  new IllegalArgumentException("Not a valid argument: "+args[2]);
                }
                server_port = Integer.parseInt(args[3]);

                // Call server
                BusinessLogic.getInstance( ).initBusinessLogic(theater_seat);

                DatagramServer udpServer = new DatagramServer(server_port, 1024);
                TCPServer tcpServer = new TCPServer(server_port, 1024);
                Thread pt = new Thread(udpServer);
                Thread qt = new Thread(tcpServer);
                pt.start();
                qt.start();

                break;
            }
            case 'c':{
                if (args.length < 6){
                    throw new IllegalArgumentException("Not a valid length: "+args);
                }
                if ("-u".equalsIgnoreCase(args[1])) {
                    // Call the UDP client
                    server_ip = args[3];
                    client_port = Integer.parseInt(args[5]);
                    String in = br.readLine();
                    DatagramClient udpClient = new DatagramClient(in, 1024, client_port, server_ip);

                    Thread uC = new Thread(udpClient);
                    uC.start();
                    break;

                } else if ("-t".equalsIgnoreCase(args[1])) {
                    // Call the TCP client
                    server_ip = args[3];
                    client_port = Integer.parseInt(args[5]);
                    TCPClient tcpClient = new TCPClient(client_port,server_ip );

                    Thread tC = new Thread(tcpClient);
                    tC.start();
                    break;
                }
            }
            default:
                throw  new IllegalArgumentException("Not a valid argument, Shutting Down: "+args);

        }
    }
}
