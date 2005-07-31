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
public class Bot extends Entity {
    private final static IntPair noDirs = new IntPair(DIR_NONE, DIR_NONE);

    public Bot(int x, int y, int d, int type) {
        super(x, y, d);

        setToType(type);
    }

    public void delete() {
        setToType(B_DELETED);
    }
    
    public void setToType(int type) {
        this.type = type;
        clearCapabilities();
        
        switch (type) {
        case B_DELETED:
        case B_BROKEN:
        case B_DALEK_ASLEEP:
        case B_HUGBOT_ASLEEP:
            break;

        case B_DALEK:
            setToDalek();
            break;
        case B_HUGBOT:
            setToHugbot();
            break;
                
        default:
            throw new IllegalArgumentException("Invalid bot type");
        }
    }

    private void setToHugbot() {
        iPushPlayer();
        iPushBots();
    }

    private void setToDalek() {
        iCanTeleport();
        iCrushPlayer();
        iWalkIntoBots();
    }

    public int getBotType() {
        return type;
    }

    public IntPair getDirChoices(Entity e) {
        switch (type) {
        case B_BROKEN:
        case B_DALEK_ASLEEP:
        case B_HUGBOT_ASLEEP:
            return noDirs;
        case B_DALEK:
        case B_HUGBOT:
        default:
            return getMoveToDirChoices(e);
        }
    }

    // some bots always move toward player,
    // preferring left/right to up/down
    protected IntPair getMoveToDirChoices(Entity e) {
//        System.out.println(e);
        
        int bd = DIR_NONE;
        int bd2 = DIR_NONE;

        if (getX() == e.getX()) {
            // same column
            if (getY() < e.getY()) {
                bd = DIR_DOWN;
            } else if (getY() > e.getY()) {
                bd = DIR_UP;
            }
        } else {
            if (getX() > e.getX()) {
                bd = DIR_LEFT;
            } else {
                bd = DIR_RIGHT;
            }

            // second choice if first doesn't move
            if (getY() < e.getY()) {
                bd2 = DIR_DOWN;
            } else if (getY() > e.getY()) {
                bd2 = DIR_UP;
            }
        }
        return new IntPair(bd, bd2);
    }
}