package server;

import logicfactory.library;
import utils.MessageImplementation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by prateek on 3/6/14.
 */
public class TCPServer implements Runnable {
    int port, cap;
    byte[] Ibuf;
    ServerSocket welcomeSocket;
    String clientRequest;
    String clientResponse;
    library myLib;

    MessageImplementation ml = new MessageImplementation();
    serverAttribute server = serverAttribute.getInstance();

    public TCPServer(int serverPort, int bufflen, library lib) {
        this.port = serverPort;
        this.myLib = lib;
        Ibuf = new byte[bufflen];

        try {
            welcomeSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Socket connectionSocket = null;
        while (true) {

            try {
                // Inbound
                connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                clientRequest = inFromClient.readLine();

                // Business Logic
                if(clientRequest!= null){
                    byte[] b = ml.receiveMsg(clientRequest,myLib);
                    clientResponse = new String(b, "UTF-8");

                    // Outbound

                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.writeBytes(clientResponse + '\n');
                    outToClient.flush();

                    if(0 == server.getSleepCounter()){
                        Thread.sleep(server.getTime2Sleep());
                    }
                }
                else{
                    clientRequest = "false";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
