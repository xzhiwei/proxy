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
                proxySocket = new Socket("localhost", 5601);
                proxyInput = proxySocket.getInputStream();
                proxyOutput = proxySocket.getOutputStream();
                //根据HTTP method来判断是https还是http请求
                proxyOutput.write(Jiami.jiami(headStr.toString()).getBytes());
                proxyOutput.write("\r\n".getBytes());
                //新开线程转发客户端请求至目标服务器
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
