package org.spacebar.escape.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.spacebar.escape.common.Level.Solution;
import org.spacebar.escape.common.hash.MD5;

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
    
    Hashtable solutions = new Hashtable();
    
    public PlayerInfo(BitInputStream in) throws IOException {
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

    private void decodeBinaryFormat(BitInputStream in) throws IOException {
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
            // md5
            MD5 md5 = new MD5(in);
            
            // discard length of rle encoded bytes
            Misc.getIntFromStream(in);
            
            // get solution
            Level.Solution s = new Level.Solution(in);
            
            addSolution(md5, s, false);
        }
        
        // read optional ratings
        // XXX
        
        // read optional chunks
        // XXX
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        Enumeration e = solutions.keys();
        while (e.hasMoreElements()) {
            MD5 md5 = (MD5) e.nextElement();

            sb.append(md5 + " -> ");
            
            Vector v = (Vector) solutions.get(md5);
            for (int i = 0; i < v.size(); i++) {
                Level.Solution s = (Solution) v.elementAt(i);
                sb.append(s.toString() + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    private void addSolution(MD5 md5, Level.Solution s, boolean append) {
        Vector v = (Vector) solutions.get(md5);
        if (v == null) {
            v = new Vector();
            solutions.put(md5, v);
        }
        
        // if there is already something, and we are not appending, forget it
        if (!append && !v.isEmpty()) {
            return;
        }
        
        // add the item to the front
        v.insertElementAt(s, 0);
    }
}
