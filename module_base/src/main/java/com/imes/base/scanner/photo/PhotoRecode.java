package com.imes.base.scanner.photo;

import java.nio.charset.Charset;

public class PhotoRecode {

    public static String recode(String str) {
        String formart = str;
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
            }
        } catch (Exception e) {
        }
        return formart;
    }
}
