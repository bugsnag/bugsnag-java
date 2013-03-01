package com.bugsnag.utils;

import java.io.UnsupportedEncodingException;

public class StringUtils {
    public static byte[] stringToByteArray(String str) {
        byte[] bytes = null;

        try {
            bytes = str.getBytes("UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace(System.err);
        }

        return bytes;
    }
}