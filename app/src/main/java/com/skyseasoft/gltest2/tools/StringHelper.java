package com.skyseasoft.gltest2.tools;

/**
 * Created by junodeveloper on 15. 7. 13..
 */
public class StringHelper {
    public static String[] splitByLength(String str, int len) {
        int k = 0;
        String tmp = "";
        do {
            tmp += str.substring(k, (k + len) < str.length() ? (k + len) : str.length()) + "|";
            k += len;
        } while(k<str.length());
        return tmp.split("\\|");
    }
}
