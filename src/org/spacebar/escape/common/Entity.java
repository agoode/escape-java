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
    static private final int NUM_CAPS = 7;
    
    protected final void iCanTeleport() {
        capabilities[CAP_CAN_TELEPORT] = true;
    }
    protected final void iCrushPlayer() {
        capabilities[CAP_CRUSH_PLAYER] = true;
    }
    protected final void iAmPlayer() {
        capabilities[CAP_IS_PLAYER] = true;
    }
    protected final void iPushBots() {
        capabilities[CAP_PUSH_BOTS] = true;
        iPushPlayer();
    }
    protected final void iPushPlayer() {
        capabilities[CAP_PUSH_PLAYER] = true;
    }
    protected final void iWalkIntoBots() {
        capabilities[CAP_WALK_INTO_BOTS] = true;
        iCrushPlayer();
    }
    protected final void iZapSelf() {
        capabilities[CAP_ZAP_SELF] = true;
        iWalkIntoBots();
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

    
    public Entity(int x, int y, int d) {
        this.x = x;
        this.y = y;
        this.d = d;
    }
    
    final public boolean canTeleport() {
        return canTeleportB;
    }

    final public boolean crushesPlayer() {
        return crushPlayerB;
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
        return isPlayerB;
    }

    final public boolean canPushBots() {
        return pushBotsB;
    }
    
    final public boolean canPushPlayer() {
        return pushPlayerB;
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
        return walkIntoBotsB;
    }

    final public boolean zapsSelf() {
        return zapSelfB;
    }
    
    public String toString() {
        String s = getClass().getName() + ": (" + x + "," + y + "," + d + ") [";
        if (isPlayer()) s += " isPlayer";
        if (canTeleport()) s += " canTeleport";
        if (crushesPlayer()) s += " crushesPlayer";
        if (walksIntoBots()) s += " walksIntoBots";
        if (canPushPlayer()) s += " pushesPlayer";
        if (zapsSelf()) s += " zapsSelf";
        if (canPushBots()) s += " pushesBots";
        s += " ]";
        return s;
    }
    public boolean equals(Object obj) {
        if (obj instanceof Entity) {
            Entity e = (Entity) obj;
            
            
        }
        return false;
    }
}