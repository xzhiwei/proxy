package com.xzhiwei.common;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

public class Jiami {

    static String solt = "sgjageaio";

    public static String jiami(String data) {
        String randomData = RandomStringUtils.random(10, "abcdefghijklmnopqrstuvwxyz1234567890");
        String md5Hex = DigestUtils.md5Hex(data + solt);
        String base64 = Base64.getEncoder().encodeToString(data.getBytes());
        try {
            String encode = URLEncoder.encode(base64, "utf-8");
            StringBuilder sb = new StringBuilder(randomData + md5Hex + "-" + encode + "---\r\n");
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
        }
        return "";
    }


    public static String jiemi(String data){
        if(data.endsWith("---\r\n")) {
            String t = data.substring(10, data.length());
            String md5 = t.substring(0, t.indexOf("-"));
            t = t.substring(t.indexOf("-") + 1, t.length() - 5);
            try {
                t = URLDecoder.decode(t, "utf-8");
            } catch (UnsupportedEncodingException e) {

            }
            String d = new String(Base64.getDecoder().decode(t));

            if (md5.equals(DigestUtils.md5Hex(d + solt))) {
                return d;
            } else {
                return "";
            }
        } else {
            return data;
        }
    }
}
