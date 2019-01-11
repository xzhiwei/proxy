package com.xzhiwei.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static Properties properties = new Properties();

    static {
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = Config.class.getClassLoader().getResourceAsStream("client.properties");
        // 使用properties对象加载输入流
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("load config file error,",e);
        }
    }

    public static int PORT = Integer.parseInt(properties.getProperty("client.port"));

    public static String REMOTE_SERVER_HOST = properties.getProperty("remote.server.host");

    public static int REMOTE_SERVER_PORT = Integer.parseInt(properties.getProperty("remote.server.port"));

    public static String HTTP_SOCKET_FLAG = properties.getProperty("http.socket.flag");
}
