package com.xzhiwei.server.handler;

import com.xzhiwei.common.AbstractSocketHandle;
import com.xzhiwei.common.Jiami;
import com.xzhiwei.common.Utils;
import com.xzhiwei.common.entity.HostAndPort;
import com.xzhiwei.server.Config;

import java.io.*;
import java.net.Socket;

public class HttpSocketHander extends AbstractSocketHandle {


    public HttpSocketHander(Socket socket) {
        super(socket);
    }

    @Override
    public String getHeader(InputStream inputStream) throws IOException {
        String line;
        BufferedReader lineBuffer = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder headStr = new StringBuilder();
        while (null != (line = lineBuffer.readLine())) {
            if(line.contains(Config.HTTP_SOCKET_FLAG)){
                headStr = new StringBuilder();
                continue;
            }
            if (line.length() == 0) {
                break;
            }
            headStr.append(line).append("\r\n");
        }
        String realData = Jiami.jiemi(headStr.toString());
        HostAndPort hostAndPort = Utils.getHostAndPort(realData);
        this.setRemoteHost(hostAndPort.getHost());
        this.setRemotePort(hostAndPort.getPort());
        return realData;
    }

    @Override
    public void sendHeaderToRemote(String header, OutputStream outputStream) throws IOException {
        String type = Utils.getRequestType(header);
        if ("CONNECT".equalsIgnoreCase(type)) {//https先建立隧道
            outputStream.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
            outputStream.flush();
        } else {//http直接将请求头转发
            outputStream.write(header.getBytes());
        }
    }
}