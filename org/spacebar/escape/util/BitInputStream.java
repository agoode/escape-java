/*
 * Created on Dec 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BitInputStream extends InputStream {
    private byte nextByte;
    private byte bitsLeftInByte;
    final InputStream baseStream;
    
    public BitInputStream(InputStream in) {
        super();
        baseStream = in;
    }

    public int read() throws IOException {
        return readBits(8);
    }

    public int readBits(int bits) throws IOException {
        int result = 0;
//        System.out.print("readBits: bits " + bits);
        
        while(bits > 0) {
            result <<= 1;
            try {
                if (readBit()) {
                    result++;
                }
            } catch (EOFException e) {
                return -1;
            }
            bits--;
        }
//        System.out.println(": " + Integer.toHexString(result));
        return result;
    }

    private boolean readBit() throws IOException, EOFException {
        if (bitsLeftInByte == 0) {
            int result = baseStream.read();
            if (result == -1) {
                throw new EOFException();
            }
            nextByte = (byte) result;
            bitsLeftInByte = 8;
        }
        
        boolean result = ((nextByte & 128) == 128);
        nextByte <<= 1;
        bitsLeftInByte--;
        
        return result;
    }
    
    public int readRestOfByte() throws IOException {
        return readBits(bitsLeftInByte);
    }
}
