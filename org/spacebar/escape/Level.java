/*
 * Created on Dec 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Level {
    public final static int DIR_NONE = 0;

    public final static int DIR_UP = 1;

    public final static int DIR_DOWN = 2;

    public final static int DIR_RIGHT = 3;

    public final static int DIR_LEFT = 4;

    public static int turnLeft(int d) {
        switch (d) {
        case DIR_UP:
            return DIR_LEFT;
        case DIR_DOWN:
            return DIR_RIGHT;
        case DIR_RIGHT:
            return DIR_UP;
        case DIR_LEFT:
            return DIR_DOWN;
        default:
        case DIR_NONE:
            return DIR_NONE; /* ? */
        }
    }

    public static int turnRight(int d) {
        switch (d) {
        case DIR_UP:
            return DIR_RIGHT;
        case DIR_DOWN:
            return DIR_LEFT;
        case DIR_RIGHT:
            return DIR_DOWN;
        case DIR_LEFT:
            return DIR_UP;
        default:
        case DIR_NONE:
            return DIR_NONE; /* ? */
        }
    }

    public static int[] dirchange(int d) {
        int dx, dy;
        switch (d) {
        case DIR_UP:
            dx = 0;
            dy = -1;
            break;
        case DIR_LEFT:
            dx = -1;
            dy = 0;
            break;
        case DIR_RIGHT:
            dx = 1;
            dy = 0;
            break;
        case DIR_DOWN:
            dx = 0;
            dy = 1;
            break;
        default:
            dx = 0;
            dy = 0;
        }
        return new int[] { dx, dy };
    }
    
    
}