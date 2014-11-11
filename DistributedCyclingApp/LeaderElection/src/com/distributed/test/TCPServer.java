package com.distributed.test;

import com.distributed.logic.SpanningTree;
import com.distributed.messages.MessageFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by prateek on 4/24/14.
 */
public class TCPServer implements Runnable {

    int port, cap;
    byte[] Ibuf;
    ServerSocket welcomeSocket;
    MessageFactory clientRequest;
    String clientResponse;
    SpanningTree newMsg = new SpanningTree();

    public TCPServer(int serverPort, int bufflen) {
        this.port = serverPort;
        Ibuf = new byte[bufflen];

        try {
            welcomeSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        Socket connectionSocket = null;
        while (true) {

            try {
                // Inbound
                connectionSocket = welcomeSocket.accept();
                ObjectInputStream inFromClient =
                        new ObjectInputStream(connectionSocket.getInputStream());

                clientRequest = (MessageFactory)inFromClient.readObject();

                // Business Logic
                if (clientRequest != null) {
                    newMsg.handleMsg( clientRequest,"poop" );
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
