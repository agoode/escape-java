package org.spacebar.escape.j2se;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.spacebar.escape.common.BitInputStream;
import org.spacebar.escape.common.Level;
import org.spacebar.escape.common.Misc;
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

    private void decodeTextFormat(InputStream in) throws IOException {
        String s;

        // get line after magic
        Misc.getLine(in);

        // get web stuff
        webID = Integer.parseInt(Misc.getLine(in));
        webSeqH = Integer.parseInt(Misc.getLine(in));
        webSeqL = Integer.parseInt(Misc.getLine(in));

        // skip over
        for (int i = 0; i < IGNORED_FIELDS; i++) {
            Misc.getLine(in);
        }

        // name
        name = Misc.getLine(in);

        // all solutions
        if (!Misc.getLine(in).equals(SOL_MARKER)) {
            throw new IOException("Solution marker not found");
        }

        for (;;) {
            s = Misc.getLine(in);
            if (s.equals(RAT_MARKER)) {
                break;
            }
            
            // XXX XXX
            StringTokenizer st = new StringTokenizer(s);
            // md5
            MD5 md5 = new MD5(in);


            // get solution
        }

        // read optional ratings
        // XXX

        // read optional chunks
        // XXX
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
