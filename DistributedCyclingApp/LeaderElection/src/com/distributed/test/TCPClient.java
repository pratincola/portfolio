package com.distributed.test;

import com.distributed.messages.MessageFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by prateek on 4/24/14.
 */
public class TCPClient implements Runnable {

    int port;
    String hostname;
    MessageFactory message;
    Socket clientSocket;

    public TCPClient(int port, String hostname, MessageFactory message) {
        this.port = port;
        this.hostname = hostname;
        this.message = message;
    }


    @Override
    public void run() {
        try {
            clientSocket = new Socket();
            clientSocket.bind(null);
            clientSocket.connect(new InetSocketAddress(hostname, port), 500);

            // Send to Server
            // Write object
            ObjectOutputStream oOUT = new ObjectOutputStream(clientSocket.getOutputStream());
            oOUT.writeObject(message);

            //Close the socket when finished with the transaction
            clientSocket.close();

        } catch (SocketTimeoutException to) {
        } catch (IOException e) {
        }
    }

}
