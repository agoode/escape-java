package org.spacebar.escape.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

public class PlayerInfo {
    static final private String PLAYER_MAGIC = "ESXP";
    static final private String PLAYERTEXT_MAGIC = "ESPt";
    static final private String SOL_MARKER = "-- solutions";
    static final private String RAT_MARKER = "-- ratings";
    static final private String PREF_MARKER = "-- prefs";

    static final private int IGNORED_FIELDS = 8;
    
    int webID;
    int webSeqH;
    int webSeqL;
    String name;
    
    Hashtable solutions;
    
    public PlayerInfo(InputStream in) throws IOException {
        // read magic
        String magic = Misc.getStringFromStream(in, 4);
        
        // decide
        if (magic.equals(PLAYER_MAGIC)) {
            decodeBinaryFormat(in);
        } else if (magic.equals(PLAYERTEXT_MAGIC)) {
            decodeTextFormat(in);
        } else {
            throw new IOException("Bad magic");
        }
    }

    private void decodeTextFormat(InputStream in) {

    }

    private void decodeBinaryFormat(InputStream in) throws IOException {
        // get web stuff
        webID = Misc.getIntFromStream(in);
        webSeqH = Misc.getIntFromStream(in);
        webSeqL = Misc.getIntFromStream(in);
        
        // skip over
        for (int i = 0; i < IGNORED_FIELDS; i++) {
            Misc.getIntFromStream(in);
        }
        
        // name
        int len = Misc.getIntFromStream(in);
        name = Misc.getStringFromStream(in, len);
        
        // all solutions
        int numSolutions = Misc.getIntFromStream(in);
        while (numSolutions-- > 0) {
            
        }
    }
    
    private void addSolution(String md5, Level.Solution s, boolean append) {
        Vector v = (Vector) solutions.get(md5);
        if (v == null) {
            v = new Vector();
            solutions.put(md5, v);
        }
        
        // if there is already something, and we are not appending, forget it
        if (!append && v.isEmpty()) {
            return;
        }
        
        // add the item to the front
        v.add(0, s);
    }
}
