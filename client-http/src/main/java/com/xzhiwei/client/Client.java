package com.xzhiwei.client;

import com.xzhiwei.client.handler.HttpSocketHandler;
import com.xzhiwei.common.ExecutorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8082);
        for (; ; ) {
            Socket socket = serverSocket.accept();
            ExecutorManager.executor.submit(new HttpSocketHandler(socket));
        }
    }

}
