/*
 * Created on Dec 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.util;


public class IntPair {
    private final int x;

    /**
     * @return Returns the x.
     */
    public int getX() {
        return x;
    }

    /**
     * @return Returns the y.
     */
    public int getY() {
        return y;
    }

    final private int y;

    public IntPair(int x, int y) {
        this.x = x;
        this.y = y;
    }
}