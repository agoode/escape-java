package org.spacebar.escape.common;

import java.io.*;

public class BitInputStream extends DataInputStream {
    private byte nextByte;
    private byte bitsLeftInByte;
    
    public BitInputStream(InputStream in) {
        super(in);
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

    private boolean readBit() throws IOException {
        if (bitsLeftInByte == 0) {
            int result = readUnsignedByte();

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
