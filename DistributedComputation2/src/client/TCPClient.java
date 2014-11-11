package client;

import logicfactory.LamportMutex;
import utils.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;

/**
 * Created by prateek on 2/16/14.
 */
public class TCPClient implements Runnable {
    private static PrintWriter fileOutput = null;
    private static FileWriter fw;
    private static BufferedWriter bw;
    private final int socketTimeout = 1000;
    private final String whitespaceRegex = "\\s";
    LamportMutex lm = LamportMutex.getInstance();

    int port;
    String hostname, sentence, modifiedSentence;
    Socket clientSocket;
    private boolean getNextAddress = true;

    public TCPClient(int port, String hostname, String message) {
        this.port = port;
        this.hostname = hostname;
        this.sentence = message;
    }

    public boolean testConnection(SocketAddress testSocket) {
        Socket dummy = new Socket();

        try {
            dummy.connect(testSocket, socketTimeout);
            dummy.close();
        } catch (SocketTimeoutException to) {
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        return false;
    }

    public boolean getStatus() {
        return getNextAddress;
    }

    @Override
    public void run() {
        DataOutputStream outToServer = null;

        try {

            clientSocket = new Socket(hostname, port);

            // Send to Server
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer.writeBytes(sentence + '\n');

            // Receive from Server
            modifiedSentence = inFromServer.readLine();
            String [] message = modifiedSentence.split(whitespaceRegex);
            if(message[0].equalsIgnoreCase("server")){
                // Call lamport
                try{
                    StringTokenizer st = new StringTokenizer(modifiedSentence);
                    Message receivedMessage = Message.parseMsg(st);
                    LamportMutex.handleMsg(receivedMessage, receivedMessage.getSrcId(), receivedMessage.getTag());
                }catch (Exception e){
                }
            }

            //Close the socket when finished with the transaction
            clientSocket.close();
            getNextAddress = false;

        } catch (SocketTimeoutException to) {
            getNextAddress = true;
        } catch (IOException e) {
            //e.printStackTrace();
            getNextAddress = true;
        }
    }

    public void writeOutputFile(String result, File file) {
        String[] commandTokens = sentence.split(" ");

        String outputString = commandTokens[0] + " " + commandTokens[1];
        try {
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            if (result.equals("false")) {
                bw.write("fail " + outputString + "\n");
                bw.close();
            }
            else if (result.equals("true")) {
                bw.write(outputString + "\n");
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
