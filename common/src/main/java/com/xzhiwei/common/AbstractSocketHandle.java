package com.xzhiwei.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class AbstractSocketHandle extends Thread  {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSocketHandle.class);

    private Socket socket;

    private String remoteHost;

    private int remotePort;


    public AbstractSocketHandle(Socket socket) {
        this.socket = socket;
    }

    public abstract String getHeader(InputStream inputStream) throws IOException ;

    public abstract void sendHeaderToRemote(String header, OutputStream outputStream) throws IOException ;

    @Override
    public void run() {
        OutputStream clientOutput = null;
        InputStream clientInput = null;
        Socket proxySocket = null;
        InputStream proxyInput = null;
        OutputStream proxyOutput = null;
        try {
            socket.setSoTimeout(1000*60*5);
            clientInput = socket.getInputStream();
            clientOutput = socket.getOutputStream();
            String header = getHeader(clientInput);
            logger.info(header.split("\r\n")[0]);
            //连接到目标服务器
            proxySocket = new Socket(this.remoteHost, this.remotePort);
            proxySocket.setSoTimeout(1000*60*5);
            proxyInput = proxySocket.getInputStream();
            proxyOutput = proxySocket.getOutputStream();
            //根据HTTP method来判断是https还是http请求
            this.sendHeaderToRemote(header,proxyOutput);
            //新开线程转发客户端请求至目标服务器
            ExecutorManager.handler.submit(new ProxyHandleThread(clientInput, proxyOutput));
            //转发目标服务器响应至客户端
            while (true) {
                int data = proxyInput.read();
                clientOutput.write(data);
                if(data == -1){
                    break;
                }
            }
        } catch (IOException e) {
           logger.error(e.getMessage());
        } finally {
            if (proxyInput != null) {
                try {
                    proxyInput.close();
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



    static class ProxyHandleThread extends Thread {

        private InputStream input;
        private OutputStream output;

        public ProxyHandleThread(InputStream input, OutputStream output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public void run() {
            int data = 0;
            try {
                do {
                    data = input.read();
                    output.write(data);
                    if(data == -1){
                        break;
                    }
                } while (true);
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
}
