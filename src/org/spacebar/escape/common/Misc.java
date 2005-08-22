/*
 * Created on Dec 28, 2004
 */
package org.spacebar.escape.common;

import java.io.*;

/**
 * @author adam
 */
public class Misc {

    public static byte[] getByteArrayFromInputStream(InputStream in)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int data;
        while ((data = in.read()) != -1) {
            out.write(data);
        }
    
        return out.toByteArray();
    }

    public static String getStringFromData(DataInput in, int size)
            throws IOException {
        byte buf[] = new byte[size];
//        System.out.println("reading string of length " + size + "...");
        in.readFully(buf);
    
        String result = new String(buf);
//        System.out.println("\"" + result + "\"");
        return (result);
    }
}
