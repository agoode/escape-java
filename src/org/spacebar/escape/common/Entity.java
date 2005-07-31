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
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
abstract public class Entity {
    static private final int CAP_IS_PLAYER = 0;

    static private final int CAP_CAN_TELEPORT = 1;

    static private final int CAP_CRUSH_PLAYER = 2;

    static private final int CAP_WALK_INTO_BOTS = 3;

    static private final int CAP_PUSH_PLAYER = 4;

    static private final int CAP_ZAP_SELF = 5;

    static private final int CAP_PUSH_BOTS = 6;
    
    static private final int CAP_HEARTFRAMERS = 7;

    static private final int NUM_CAPS = 8;

    protected final void iCanTeleport() {
        capabilities[CAP_CAN_TELEPORT] = true;
    }

    protected final void iCrushPlayer() {
        iWalkIntoBots();
    }

    protected final void iAmPlayer() {
        capabilities[CAP_IS_PLAYER] = true;
    }

    protected final void iPushBots() {
        iPushPlayer();
    }

    protected final void iPushPlayer() {
        capabilities[CAP_PUSH_BOTS] = true;
        capabilities[CAP_PUSH_PLAYER] = true;
    }

    protected final void iWalkIntoBots() {
        capabilities[CAP_CRUSH_PLAYER] = true;
        capabilities[CAP_WALK_INTO_BOTS] = true;
        capabilities[CAP_ZAP_SELF] = true;
    }

    protected final void iZapSelf() {
        iWalkIntoBots();
    }
    
    protected final void iGetHeartFramers() {
    	capabilities[CAP_HEARTFRAMERS] = true;
    }

    protected final void clearCapabilities() {
        for (int i = 0; i < capabilities.length; i++) {
            capabilities[i] = false;
        }
    }

    final private boolean capabilities[] = new boolean[NUM_CAPS];

    private int d;

    private int x;

    private int y;

    // directions
    public final static int DIR_NONE = 0;

    public final static int FIRST_DIR = 1;

    public final static int DIR_UP = 1;

    public final static int DIR_DOWN = 2;

    public final static int DIR_LEFT = 3;

    public final static int DIR_RIGHT = 4;

    public final static int LAST_DIR = 4;

    static public String directionToString(int dir) {
        String s;
        
        switch (dir) {
        case DIR_UP:
            //                            s = "up";
            s = "↑";
            break;
        case DIR_DOWN:
            //                            s = "down";
            s = "↓";
            break;
        case DIR_LEFT:
            //                            s = "left";
            s = "←";
            break;
        case DIR_RIGHT:
            //                            s = "right";
            s = "→";
            break;
        default:
            s = "?";
        }
        return s;
    }

    public Entity(int x, int y, int d) {
        this.x = x;
        this.y = y;
        this.d = d;
    }

    final public boolean canTeleport() {
        return capabilities[CAP_CAN_TELEPORT];
    }

    final public boolean crushesPlayer() {
        return capabilities[CAP_CRUSH_PLAYER];
    }

    /**
     * @return Returns the d.
     */
    final public int getDir() {
        return d;
    }

    /**
     * @return Returns the x.
     */
    final public int getX() {
        return x;
    }

    /**
     * @return Returns the y.
     */
    final public int getY() {
        return y;
    }

    final public boolean isPlayer() {
        return capabilities[CAP_IS_PLAYER];
    }

    final public boolean canPushBots() {
        return capabilities[CAP_PUSH_BOTS];
    }

    final public boolean canPushPlayer() {
        return capabilities[CAP_PUSH_PLAYER];
    }

    /**
     * @param d
     *           The d to set.
     */
    final public void setDir(int d) {
        this.d = d;
    }

    /**
     * @param x
     *           The x to set.
     */
    final public void setX(int x) {
        this.x = x;
    }

    /**
     * @param y
     *           The y to set.
     */
    final public void setY(int y) {
        this.y = y;
    }

    final public boolean isAt(int x, int y) {
        return this.x == x && this.y == y;
    }

    final public boolean walksIntoBots() {
        return capabilities[CAP_WALK_INTO_BOTS];
    }

    final public boolean zapsSelf() {
        return capabilities[CAP_ZAP_SELF];
    }

    public String toString() {
        String s = getClass().getName() + ": (" + x + "," + y + "," + d + ") [";
        if (isPlayer())
            s += " isPlayer";
        if (canTeleport())
            s += " canTeleport";
        if (crushesPlayer())
            s += " crushesPlayer";
        if (walksIntoBots())
            s += " walksIntoBots";
        if (canPushPlayer())
            s += " pushesPlayer";
        if (zapsSelf())
            s += " zapsSelf";
        if (canPushBots())
            s += " pushesBots";
        if (canGetHeartframers()) {
        	s += " heartframers";
        }
        s += " ]";
        return s;
    }

    public boolean canGetHeartframers() {
    	return capabilities[CAP_HEARTFRAMERS];
	}

	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Entity) {
            Entity e = (Entity) obj;

            return x == e.x && y == e.y && type == e.type;
        }
        return false;
    }

    static IntPair dirChange(int d) {
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
        return new IntPair(dx, dy);
    }

    static String dirString(int d) {
        switch (d) {
        case DIR_UP:
            return "up";
        case DIR_LEFT:
            return "left";
        case DIR_RIGHT:
            return "right";
        case DIR_DOWN:
            return "down";
        case DIR_NONE:
            return "none";
        default:
            return "??";
        }
    }

    static int dirReverse(int d) {
        switch (d) {
        case DIR_UP:
            return DIR_DOWN;
        case DIR_LEFT:
            return DIR_RIGHT;
        case DIR_DOWN:
            return DIR_UP;
        case DIR_RIGHT:
            return DIR_LEFT;
        default:
        case DIR_NONE:
            return DIR_NONE;
        }
    }

    // static functions
    static int turnLeft(int d) {
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

    static int turnRight(int d) {
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

    public static final int B_DELETED = -2;

    public static final int B_PLAYER = -1;

    public static final int B_BROKEN = 0;

    public static final int B_DALEK = 1;

    public static final int B_HUGBOT = 2;
    
    public static final int B_DALEK_ASLEEP = 3;
    
    public static final int B_HUGBOT_ASLEEP = 4;

    protected int type;
}