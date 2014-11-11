package logicfactory;

import server.serverAttribute;
import utils.MessageImplementation;
import utils.bookValues;

/**
 * Created by prateek on 3/4/14.
 */
public class businessLogic {

    static MessageImplementation mi_bl = new MessageImplementation();
    private final String whitespaceRegex = "\\s";
    serverAttribute server = serverAttribute.getInstance();

    public String[] parseCommands (String command, String regex) {
        String[] tokens = command.split(regex);
        return tokens;
    }

    /**
     * Parse client's request & call appropriate methods
     *
     * @return byte value for true/false depending on if the execution succeeds or not
     * c1 b2 reserve
     */
    public byte[] makeResponse(String msgIN, library l) throws InterruptedException {

        // Sleep on the Kth command.
        Boolean actionResult = false;
        try {
            String[] terms = msgIN.split(" ");
            if (3 == terms.length) {
                String clientID = terms[0];
                String bookID = terms[1];
                String commandID = terms[2];

                if (commandID.equalsIgnoreCase("reserve")) {
                    LamportMutex.getInstance().requestCS();
                    actionResult = reserveBook(clientID, bookID, l);
                    replicateLib(l);
                    server.decSleepCounter();
                    LamportMutex.getInstance().releaseCS();
                } else if (commandID.equalsIgnoreCase("return")) {
                    LamportMutex.getInstance().requestCS();
                    actionResult = returnBook(clientID, bookID, l);
                    replicateLib(l);
                    server.decSleepCounter();
                    LamportMutex.getInstance().releaseCS();
                } else {
                    // No-op
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            return String.valueOf(actionResult).getBytes();
        }


    }

    /**
     * @param client
     * @param book
     * @return True if successfully reserved the book; else false
     */
    public Boolean reserveBook(String client, String book, library myLibrary) {
        // Check if book exists & already taken
        if (myLibrary.getBooks().containsKey(book)) {
            bookValues b = (bookValues) myLibrary.getBooks().get(book);

            // Reserve the book if not checked-out
            if (!b.getCheckedOut()) {
                b.setClientName(client);
                b.setCheckedOut(true);
                return b.getCheckedOut();
            }
        }
        return false;
    }

    public boolean replicateLib(library lib){
        String libraryString = lib.toString();
        mi_bl.broadcastMsg(server.getServerID(), "replicate",libraryString );
        return true;
    }

    /**
     * @param client
     * @param book
     * @return True if successfully returned the book; else false
     */
    public Boolean returnBook(String client, String book, library myLibrary) {
        // Check if we know about the book & already taken
        if (myLibrary.getBooks().containsKey(book)) {
            bookValues b = (bookValues) myLibrary.getBooks().get(book);
            // Return the book if checked-out

            if (b.getCheckedOut() && client.equalsIgnoreCase(b.getClientName())) {
                b.setClientName("none");
                b.setCheckedOut(false);

                return true;
            }
        }
        return false;
    }

    public void updateLib(String s, library lib){
        lib.libraryUpdate(s);
    }

    public library getlibrary(library l) {
        return l;
    }

}
