package com.xzhiwei;

import com.xzhiwei.common.Jiami;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8082);
        for (; ; ) {
            Socket socket = serverSocket.accept();
            new SocketHandle(socket).start();
        }
    }

    static  String shadowData = "GET / HTTP/1.1\r\n" +
            "Host: www.baidu.com\r\n" +
            "Connection: keep-alive\r\n" +
            "Pragma: no-cache\r\n" +
            "Cache-Control: no-cache\r\n" +
            "Upgrade-Insecure-Requests: 1\r\n" +
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36\r\n" +
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\n" +
            "Accept-Encoding: gzip, deflate, br\r\n" +
            "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n";

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
                //读取HTTP请求头，并拿到HOST请求头和method
                while (null != (line = lineBuffer.readLine())) {
                    headStr.append(line + "\r\n");
                    if (line.length() == 0) {
                        break;
                    } else {
                        String[] temp = line.split(" ");
                        if (temp[0].contains("Host")) {
                            host = temp[1];
                        }
                    }
                }
                System.out.println(headStr.toString() + "\r\n\r\n");
                String type = headStr.substring(0, headStr.indexOf(" "));
                //根据host头解析出目标服务器的host和port
                String[] hostTemp = host.split(":");
                host = hostTemp[0];
                int port = 80;
                if (hostTemp.length > 1) {
                    port = Integer.valueOf(hostTemp[1]);
                }
                //连接到目标服务器
                proxySocket = new Socket("a.xzhiwei.top", 80);
                proxyInput = proxySocket.getInputStream();
                proxyOutput = proxySocket.getOutputStream();
                //根据HTTP method来判断是https还是http请求
                proxyOutput.write(shadowData.getBytes());
                proxyOutput.write("request_baidu\r\n".getBytes());
                proxyOutput.write(Jiami.jiami(headStr.toString()).getBytes());
                proxyOutput.write("\r\n".getBytes());
                //新开线程转发客户端请求至目标服务器
//                while (true) {
//                    int data = clientInput.read();
//                    proxyOutput.write(data);
//                    System.out.println(data);
//                    if(data == -1){
//                        break;
//                    }
//                }
                new ProxyHandleThread(clientInput, proxyOutput).start();
                //转发目标服务器响应至客户端
                while (true) {
                    int data = proxyInput.read();
                    clientOutput.write(data);
                    if(data == -1){
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                    int data = input.read();
                    output.write(data);
                    if(data == -1){
                        break;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
