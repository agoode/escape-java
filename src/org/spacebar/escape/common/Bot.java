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
abstract public class Bot extends Entity {
    public final static int B_BROKEN = 0;

    public final static int B_DALEK = 1;

    public final static int B_HUGBOT = 2;

    protected Bot(int x, int y, int d, int type) {
        super(x, y, d);

        if (type != B_BROKEN && type != B_DALEK && type != B_HUGBOT) {
            throw new IllegalArgumentException("Invalid bot type");
        }
        botType = type;
    }

    private final int botType;

    public int getBotType() {
        return botType;
    }

    static public Bot createBotFromType(int x, int y, int d, int type) {
        switch (type) {
        case B_BROKEN:
            return new BrokenBot(x, y, d);
        case B_DALEK:
            return new Dalek(x, y, d);
        case B_HUGBOT:
            return new Hugbot(x, y, d);
        default:
            throw new IllegalArgumentException("Invalid bot type");
        }
    }

    abstract IntPair getDirChoices(Entity e);

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