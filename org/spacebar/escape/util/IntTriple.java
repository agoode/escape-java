/*
 * Created on Dec 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.util;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class IntTriple extends IntPair {
    private int d;

    public IntTriple() {
        super();
    }

    public IntTriple(int x, int y, int d) {
        super(x, y);
        this.d = d;
    }

    /**
     * @return Returns the D.
     */
    public int getD() {
        return d;
    }

    /**
     * @param d
     *            The D to set.
     */
    public void setD(int d) {
        this.d = d;
    }
}