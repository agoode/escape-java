package org.spacebar.escape.common;

import java.io.*;

public class LevelPack {
    /* Defines a format for packing levels into a single seekable file.
     * 
     * Integer format: little endian
     * 
     * Format of file:
     * 
     * Bytes 0--3: Unsigned int representing number of files packed in
     *            and number of shorts in header
     * Bytes 4--(2*n): Header (described below)
     * After header: Standard .esx files, represented directly. There are n
     *               levels here.
     *               
     * Header size: 4*n
     *   Header contains n unsigned ints. Each int represents the number of
     *   bytes in each level.
     */

    
    private final String resource;
    private BitInputStream in;
    private int levelsRead;
    private final int levelCount;
    
    public LevelPack(String resource) throws IOException {
        this.resource = resource;
        initStream();
        levelCount = in.readInt();
    }

    private void initStream() throws IOException {
        if (in != null) {
            in.close();
        }
        levelsRead = 0;
        in = new BitInputStream(this.getClass().getResourceAsStream(resource));
    }
    
    public int getLevelCount() {
        return levelCount;
    }
    
    static public void pack(InputStream levels[], OutputStream pack) throws IOException {
        DataOutputStream out = new DataOutputStream(pack);
        
        byte data[][] = new byte[levels.length][];
        
        // write number of files
        out.writeInt(levels.length);
        
        // read in all levels
        for (int i = 0; i < levels.length; i++) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            int d;
            while ((d = levels[i].read()) != -1) {
                b.write(d);
            }
            // got a level
            data[i] = b.toByteArray();
            
            // write the size
            out.writeInt(data[i].length);
        }
        
        // have all levels, now write the rest
        for (int i = 0; i < data.length; i++) {
            out.write(data[i]);
        }
        
        // done!
        out.flush();
    }
}
