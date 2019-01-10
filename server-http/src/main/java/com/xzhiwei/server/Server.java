package com.xzhiwei.server;

import com.xzhiwei.common.Jiami;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(80);
        for (; ; ) {
            new SocketHandle(serverSocket.accept()).start();
        }
    }

    static class SocketHandle extends Thread {

        private Socket socket;

        public SocketHandle(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            OutputStream clientOutput = null;
            InputStream clientInput = null;
            Socket proxySocket = null;
            InputStream proxyInput = null;
            OutputStream proxyOutput = null;
            try {
                clientInput = socket.getInputStream();
                clientOutput = socket.getOutputStream();
                String line;
                String host = "";
                BufferedReader lineBuffer = new BufferedReader(new InputStreamReader(clientInput));
                StringBuilder headStr = new StringBuilder();
                while (null != (line = lineBuffer.readLine())) {
                    if(line.contains("request_baidu")){
                        headStr = new StringBuilder();
                        continue;
                    }
                    if (line.length() == 0) {
                        break;
                    }
                    headStr.append(line + "\r\n");
                }
                String realData = Jiami.jiemi(headStr.toString());
                for(String i:realData.split("\r\n")){
                    String[] temp = i.split(" ");
                    if (temp[0].contains("Host")) {
                        host = temp[1];
                    }
                }

                String type = realData.substring(0, realData.indexOf(" "));
                //根据host头解析出目标服务器的host和port
                System.out.println(realData.split("\r\n")[0]);
                String[] hostTemp = host.split(":");
                host = hostTemp[0];
                int port = 80;
                if (hostTemp.length > 1) {
                    port = Integer.valueOf(hostTemp[1]);
                }
                //连接到目标服务器
                proxySocket = new Socket(host, port);
                proxyInput = proxySocket.getInputStream();
                proxyOutput = proxySocket.getOutputStream();
                //根据HTTP method来判断是https还是http请求
                if ("CONNECT".equalsIgnoreCase(type)) {//https先建立隧道
                    clientOutput.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
                    clientOutput.flush();
                } else {//http直接将请求头转发
                    proxyOutput.write(realData.getBytes());
                }
                //新开线程转发客户端请求至目标服务器
                new ProxyHandleThread(clientInput, proxyOutput).start();
                //转发目标服务器响应至客户端
                while (true) {
                    clientOutput.write(proxyInput.read());
                }
            } catch (IOException e) {
            } finally {
                if (proxyInput != null) {
                    try {
                        proxyOutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (proxyOutput != null) {
                    try {
                        proxyOutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (proxySocket != null) {
                    try {
                        proxySocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (clientInput != null) {
                    try {
                        clientInput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (clientOutput != null) {
                    try {
                        clientOutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    static class ProxyHandleThread extends Thread {

        private InputStream input;
        private OutputStream output;

        public ProxyHandleThread(InputStream input, OutputStream output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    output.write(input.read());
                }
            } catch (IOException e) {
            }
        }
    }
}
