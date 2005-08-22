/*
 * Created on Dec 16, 2004
 */
package org.spacebar.escape.common;

/**
 * @author adam
 */
public class IntTriple extends IntPair {
    public int d;

    public IntTriple(int x, int y, int d) {
        super(x, y);
        this.d = d;
    }
    public IntTriple() {}
}