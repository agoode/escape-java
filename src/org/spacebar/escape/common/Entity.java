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
    protected final void iCanTeleport() {
        this.canTeleportB = true;
    }
    protected final void iCrushPlayer() {
        this.crushPlayerB = true;
    }
    protected final void iAmPlayer() {
        this.isPlayerB = true;
    }
    protected final void iPushBots() {
        this.pushBotsB = true;
        iPushPlayer();
    }
    protected final void iPushPlayer() {
        this.pushPlayerB = true;
    }
    protected final void iWalkIntoBots() {
        this.walkIntoBotsB = true;
        iCrushPlayer();
    }
    protected final void iZapSelf() {
        this.zapSelfB = true;
        iWalkIntoBots();
    }
    private boolean isPlayerB;
    private boolean canTeleportB;
    private boolean crushPlayerB;
    private boolean walkIntoBotsB;
    private boolean pushPlayerB;
    private boolean zapSelfB;
    private boolean pushBotsB;
    
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
}