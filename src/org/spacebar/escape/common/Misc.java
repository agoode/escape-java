/*
 * Created on Dec 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.common;

import java.io.*;

/**
 * @author adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
        in.readFully(buf);
    
        String result = new String(buf);
        return (result);
    }
}
