package com.distributed.test;

import com.distributed.logic.SpanningTree;

import java.io.IOException;


/**
 * Created by prateek on 4/26/14.
 */
public class TestRun {
    static NodeTest server = NodeTest.getInstance();
    static SpanningTree st = new SpanningTree();

    public static void main(String[] args) throws IOException {

        server.setMyPID(Integer.valueOf(args[0]));
        server.nodeInit(Integer.valueOf(args[0]));
        // Get all the known servers...
        server.serverFileParser(args[1]);
        server.startMyServerInstance();
        st.beacon();
    }

}
