package com.xzhiwei.common.entity;

public class HostAndPort {

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private String host;

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}


