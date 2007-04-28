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
        try {
//            System.out.println("reading string of length " + size + "...");
            byte buf[] = new byte[size];
            in.readFully(buf);

            String result = new String(buf);
            // System.out.println("\"" + result + "\"");
            return (result);
        } catch (OutOfMemoryError e) {
            throw new IOException("String too long to read into memory: " + size);
        } catch (NegativeArraySizeException e) {
            throw new IOException("String length cannot be negative: " + size);
        }
    }
}
