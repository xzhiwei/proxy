package com.xzhiwei.common;

import com.xzhiwei.common.entity.HostAndPort;

public class Utils {

    public static String getRequestType(String header){
        return header.substring(0, header.indexOf(" "));
    }


    public static HostAndPort getHostAndPort(String header){
        String host = "";
        for(String i:header.split("\r\n")){
            String[] temp = i.split(" ");
            if (temp[0].contains("Host")) {
                host = temp[1];
            }
        }
        String[] hostTemp = host.split(":");
        host = hostTemp[0];
        int port = 80;
        if (hostTemp.length > 1) {
            port = Integer.valueOf(hostTemp[1]);
        }
        return new HostAndPort(host,port);
    }

}
