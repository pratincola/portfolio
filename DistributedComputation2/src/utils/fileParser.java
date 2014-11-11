package utils;

import client.clientAttribute;
import logicfactory.library;
import server.serverAttribute;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by prateek on 3/4/14.
 */
public class fileParser {
    private final String whitespaceRegex = "\\s";
    private final String periodRegex = "\\.";
    serverAttribute server = serverAttribute.getInstance();
    clientAttribute client = clientAttribute.getInstance();
    library lib = new library();
    private String line = null;

    /**
     * Parses the file server1.in to get the serverID, #ofServers, ServerAddresses & ServerInstructions
     */
    public library serverFileParser(String filename) throws IOException {

        //Get server ID for the process
        server.setServerID(Integer.valueOf(filename.split(periodRegex, 0)[0].replaceAll("server", "")));

        //Get how many servers are in the cluster
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String lineONE = reader.readLine();
        server.setNumOfServersInstances(Integer.valueOf(lineONE.split(whitespaceRegex)[0]));

        //Get how many books in the library
        lib.setNumOfBooks(Integer.valueOf(lineONE.split(whitespaceRegex)[1]));

        //Load all Server addresses for communication
        for (int i = 1; i <= server.getNumOfServersInstances(); i++) {
            server.getServerAddresses().put(i, reader.readLine());
        }

        //Load server instructions for later use
        while ((line = reader.readLine()) != null) {
            server.getServerInstruction().add(line);
        }
        return lib;
    }

    /**
     * Parses the file client1.in to get the clientID, #ofServers, ServerAddresses & ClientInstructions
     */
    public void clientFileParser(String filename) throws IOException {

        //Get server ID for the process
        client.setClientID(Integer.valueOf(filename.split(periodRegex, 0)[0].replaceAll("client", "")));

        //Get how many servers are in the cluster
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String lineONE = reader.readLine();
        client.setNumOfClientsInstances(Integer.valueOf(lineONE.split(whitespaceRegex)[0]));

        for (int i = 1; i <= client.getNumOfClientsInstances(); i++) {
            client.getHostAddresses().add(reader.readLine());
        }

        //Load all Server addresses for communication
        while ((line = reader.readLine()) != null) {
            client.getClientInstruction().add(line);
        }
    }

}
