package com.xzhiwei.server;

import com.xzhiwei.common.ExecutorManager;
import com.xzhiwei.server.handler.HttpSocketHander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(Config.port);
        for (; ; ) {
            Socket socket = serverSocket.accept();
            ExecutorManager.executor.submit(new HttpSocketHander(socket));
        }
    }
}
