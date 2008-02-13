package org.spacebar.escape.common;

import java.io.*;

public class BitInputStream extends DataInputStream {
    private int bitsLeftInByte;

    private byte nextByte;

    public BitInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        discardRestOfByte();
        return super.read();
    }

    public int readBits(int bits) throws IOException {
        int result = 0;
        // System.out.print("readBits: bits " + bits);

        while (bits > 0) {
            result <<= 1;
            if (readBit()) {
                result++;
            }
            bits--;
        }
        // System.out.println(": " + Integer.toHexString(result));
        return result;
    }

    private boolean readBit() throws IOException {
        if (bitsLeftInByte == 0) {
            nextByte = readByte();
            bitsLeftInByte = 8;
        }

        boolean result = ((nextByte & 128) == 128);
        nextByte <<= 1;
        bitsLeftInByte--;

        return result;
    }

    public void discardRestOfByte() {
        bitsLeftInByte = 0;
    }
}
