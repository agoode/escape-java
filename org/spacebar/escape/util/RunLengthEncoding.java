/*
 * Created on Dec 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RunLengthEncoding {
    public static int[] decode(InputStream in, int len) throws IOException {
        int result[] = new int[len];
        
        int bytes = in.read();
        if (bytes > 1) {
            throw new IOException("Bad file bytecount: " + bytes);
        }
        
        int run;
        int ri = 0;
        while (ri < len) {
            run = in.read();
            
            int ch;
            if (run == 0) {
                // anti-run
                run = in.read();
//                System.out.println();
//                System.out.print("skipping " + run + ": ");
//                System.out.flush();
                for (int i = 0; i < run; i++) {
                    ch = in.read();
//                    System.out.print(ch + " ");
//                    System.out.flush();
                    result[ri++] = ch;
                }
            } else {
                // run
                if (bytes == 1) {
                    ch = in.read();
                } else {
                    ch = 0;
                }
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
        
        return result;
    }
    
    public static void encode(OutputStream out, int[] data) {
        
    }
}
