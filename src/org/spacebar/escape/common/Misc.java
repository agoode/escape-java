/*
 * Created on Dec 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.common;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

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

    static int getIntFromStream(InputStream in) throws IOException {
        int r = 0;
    
        r += eofRead(in) << 24;
        r += eofRead(in) << 16;
        r += eofRead(in) << 8;
        r += eofRead(in);
    
        return r;
    }

    static int eofRead(InputStream in) throws IOException {
        int i = in.read();
        if (i == -1) {
            throw new EOFException();
        }
        return i;
    }

    static String getStringFromStream(InputStream in, int size)
            throws IOException {
        byte buf[] = getBytesFromStream(in, size);
    
        String result = new String(buf);
        return (result);
    }

    static byte[] getBytesFromStream(InputStream in, int size) throws IOException {
        byte b[] = new byte[size];
    
        int c = b.length;
        int o = 0;
        while (c > 0) {
            int amount = in.read(b, o, c);
            if (amount == -1) {
                throw new EOFException();
            }
            
            c -= amount;
            o += amount;
        }
        
        return b;
    }
}
