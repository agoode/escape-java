/*
 * Created on Dec 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.common;

import java.io.EOFException;
import java.io.IOException;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class RunLengthEncoding {
    public static int[] decode(BitInputStream in, int len) throws IOException {
        int result[] = new int[len];

        // number of bytes to represent one integer
        final int bytecount = in.read();
        if (bytecount == -1) {
            throw new EOFException();
        }

        int bits;
        int framebits = 8;
        if ((bytecount & 128) == 128) {
//            System.out.println("bytecount has bit 8 set");
            if ((bytecount & 64) == 64) {
//                System.out.println("bytecount has bit 7 set");
                framebits = in.readBits(5);
            }
            bits = bytecount & 63;
        } else {
            if (bytecount > 4) {
                throw new IOException("Bad file bytecount: " + bytecount);
            }
            bits = bytecount * 8;
        }

//        System.out.println("bits:" + bits + ", bytecount: " + bytecount
//                + ", framebits: " + framebits);

        int run;
        int ri = 0;
        while (ri < len) {
            run = in.readBits(framebits);

            int ch;
            if (run == 0) {
                // anti-run
                run = in.readBits(framebits);
//                System.out.println();
//                System.out.print("skipping " + run + ": ");
//                System.out.flush();
                for (int i = 0; i < run; i++) {
                    ch = in.readBits(bits);
//                    System.out.print(ch + " ");
//                    System.out.flush();
                    result[ri++] = ch;
                }
            } else {
                // run
                ch = in.readBits(bits);
//                System.out.println();
//                System.out.print(run + " " + ch + "\'s: ");
//                System.out.flush();
                for (int i = 0; i < run; i++) {
//                    System.out.print(ch + " ");
//                    System.out.flush();
                    result[ri++] = ch;
                }
            }
        }

        in.readRestOfByte();
        return result;
    }
}