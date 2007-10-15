/*
 * Created on Mar 22, 2005
 */
package org.spacebar.escape.common;

/**
 * @author adam
 */
public class Bot extends Entity {
    private final static IntPair noDirs = new IntPair(DIR_NONE, DIR_NONE);

    public Bot(int x, int y, byte d, byte type) {
        super(x, y, d);

        setToType(type);
    }

    public Bot(int x, int y, byte d, byte type, byte bombTimer) {
        super(x, y, d);

        setToType(type);
        this.bombTimer = bombTimer;
    }

    public Bot(Bot b) {
        super(b.x, b.y, b.d);

        capabilities = b.capabilities;
        type = b.type;
        
        bombTimer = b.bombTimer;
    }

    public void delete() {
        setToType(B_DELETED);
    }

    public void explode() {
        setToType(B_BOMB_X);
    }
    
    public void setToType(byte type) {
        this.type = type;
        clearCapabilities();

        switch (type) {
        case B_DELETED:
        case B_BROKEN:
        case B_DALEK_ASLEEP:
        case B_HUGBOT_ASLEEP:
        case B_BOMB_X:
            break;

        case B_DALEK:
            setToDalek();
            break;
        case B_HUGBOT:
            setToHugbot();
            break;

        default:
            // bomb?
            if (isBomb()) {
                // bomb
            } else {
                throw new UnknownBotException(type);
            }
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

    public byte getBotType() {
        return type;
    }

    public IntPair getDirChoices(Entity e) {
        switch (type) {
        case B_DALEK:
        case B_HUGBOT:
            return getMoveToDirChoices(e);

        case B_BROKEN:
        case B_DALEK_ASLEEP:
        case B_HUGBOT_ASLEEP:
        default:
            // includes bombs
            return noDirs;
        }
    }

    // some bots always move toward player,
    // preferring left/right to up/down
    protected IntPair getMoveToDirChoices(Entity e) {
        // System.out.println(e);

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

    public boolean isDeleted() {
        return type == B_DELETED || type == B_BOMB_X;
    }
}
