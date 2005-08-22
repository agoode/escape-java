/*
 * Created on Dec 16, 2004
 */
package org.spacebar.escape.common;


public class IntPair {
    public int x;
    public int y;

    public IntPair(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public IntPair() {}
    public IntPair(IntPair t) {
        x = t.x;
        y = t.y;
    }
}