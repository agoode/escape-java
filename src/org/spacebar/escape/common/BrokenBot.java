/*
 * Created on Mar 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.common;

/**
 * @author adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BrokenBot extends Bot {
    private final static IntPair dirs = new IntPair(DIR_NONE, DIR_NONE);
    
    public BrokenBot(int x, int y, int d) {
        super(x, y, d, B_BROKEN);
    }

    IntPair getDirChoices(Entity e) {
        return dirs;
    }
}
