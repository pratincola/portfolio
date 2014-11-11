package utils;

import java.util.StringTokenizer;

/**
 * Created by prateek on 3/5/14.
 */
public class Message {
    private final String whitespaceRegex = "\\s";
    private final String whitespace = " ";
    int srcId, destId;
    String tag;
    String msgBuf;

    public Message(int s, int t, String msgType, String buf) {
        this.srcId = s;
        destId = t;
        tag = msgType;
        msgBuf = buf;
    }

    public static Message parseMsg(StringTokenizer st){
        String s = st.nextToken() ;
        String tag = st.nextToken();
        int srcId = Integer.parseInt(st.nextToken());
        int destId = Integer.parseInt(st.nextToken());
        String buf = st.nextToken("#");
        return new Message(srcId, destId, tag, buf);
    }

    public int getSrcId() {
        return srcId;
    }

    public int getDestId() {
        return destId;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        String [] token = msgBuf.split(whitespace);
        return token[1];
    }

    public Integer getClock() {
        if(msgBuf.contains(" ")){
            String [] token = msgBuf.split(whitespace);
            return Integer.valueOf(token[1]);
        }
        return Integer.valueOf(msgBuf);
    }

    public String toString(){
        String s = "server" + whitespace +
                tag + whitespace +
                String.valueOf(srcId) + whitespace +
                String.valueOf(destId) + whitespace +
                msgBuf + "#";
        return s;
    }

}
