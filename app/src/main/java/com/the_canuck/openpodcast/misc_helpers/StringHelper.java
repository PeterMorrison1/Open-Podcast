package com.the_canuck.openpodcast.misc_helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class StringHelper {

    /**
     * Encodes the filename in UTF-8 but keeps spaces as whitespace, not as "+".
     *
     * @param term the filename to be encoded
     * @return the encoded filename
     */
    public static String encodeFileName(String term) {
//        try {
//            term = URLEncoder.encode(term, "UTF-8");
//            term = term.replaceAll("\\+", " ");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String encoded = term.replaceAll("[^a-zA-Z0-9\\.\\- ]", "_");

        return encoded;
    }
}
