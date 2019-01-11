package com.xzhiwei.client.handler;

import com.xzhiwei.common.AbstractSocketHandle;
import com.xzhiwei.common.Jiami;

import java.io.*;
import java.net.Socket;

public class HttpSocketHandler extends AbstractSocketHandle {

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


    public HttpSocketHandler(Socket socket) {
        super(socket);
        this.setRemoteHost("a.xzhiwei.top");
        this.setRemotePort(80);
    }

    @Override
    public String getHeader(InputStream inputStream) throws IOException {
        String line;
        BufferedReader lineBuffer = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder headStr = new StringBuilder();
        //读取HTTP请求头，并拿到HOST请求头和method
        while (null != (line = lineBuffer.readLine())) {
            headStr.append(line + "\r\n");
            if (line.length() == 0) {
                break;
            }
        }
        return headStr.toString();
    }

    @Override
    public void sendHeaderToRemote(String header, OutputStream outputStream) throws IOException {
        outputStream.write(shadowData.getBytes());
        outputStream.write("request_baidu\r\n".getBytes());
        outputStream.write(Jiami.jiami(header.toString()).getBytes());
        outputStream.write("\r\n".getBytes());
    }

}