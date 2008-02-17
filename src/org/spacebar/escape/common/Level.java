package org.spacebar.escape.common;

import java.io.*;
import java.util.ArrayList;

public class Level {

    public static class MetaData {
        final public int width;

        final public int height;

        final public String title;

        final public String author;

        public MetaData(int width, int height, String title, String author) {
            this.width = width;
            this.height = height;
            this.title = title;
            this.author = author;
        }
    }

    // panel colors
    public final static byte PANEL_REGULAR = 0;

    public final static byte PANEL_BLUE = 1;

    public final static byte PANEL_GREEN = 2;

    public final static byte PANEL_RED = 3;

    /* panel under tile (ie, pushable block) */
    /*
     * if HASPANEL is set, then TF_RPANELH * 2 + TF_RPANELL says what kind (see
     * panel colors above)
     */
    public final static byte TF_HASPANEL = 1;

    public final static byte TF_RPANELL = 4;

    public final static byte TF_RPANELH = 8;

    /* panel under tile in bizarro world */
    /* same refinement */
    public final static byte TF_OPANEL = 2;

    public final static byte TF_ROPANELL = 16;

    public final static byte TF_ROPANELH = 32;

    public final static byte TF_TEMP = 64; // used for swapping during play

    // panels
    public final static byte T_FLOOR = 0;

    public final static byte T_RED = 1;

    public final static byte T_BLUE = 2;

    public final static byte T_GREY = 3;

    public final static byte T_GREEN = 4;

    public final static byte T_EXIT = 5;

    public final static byte T_HOLE = 6;

    public final static byte T_GOLD = 7;

    public final static byte T_LASER = 8;

    public final static byte T_PANEL = 9;

    public final static byte T_STOP = 10;

    public final static byte T_RIGHT = 11;

    public final static byte T_LEFT = 12;

    public final static byte T_UP = 13;

    public final static byte T_DOWN = 14;

    public final static byte T_ROUGH = 15;

    public final static byte T_ELECTRIC = 16;

    public final static byte T_ON = 17;

    public final static byte T_OFF = 18;

    public final static byte T_TRANSPORT = 19;

    public final static byte T_BROKEN = 20;

    public final static byte T_LR = 21;

    public final static byte T_UD = 22;

    public final static byte T_0 = 23;

    public final static byte T_1 = 24;

    public final static byte T_NS = 25;

    public final static byte T_NE = 26;

    public final static byte T_NW = 27;

    public final static byte T_SE = 28;

    public final static byte T_SW = 29;

    public final static byte T_WE = 30;

    public final static byte T_BUTTON = 31;

    public final static byte T_BLIGHT = 32;

    public final static byte T_RLIGHT = 33;

    public final static byte T_GLIGHT = 34;

    public final static byte T_BLACK = 35;

    public final static byte T_BUP = 36;

    public final static byte T_BDOWN = 37;

    public final static byte T_RUP = 38;

    public final static byte T_RDOWN = 39;

    public final static byte T_GUP = 40;

    public final static byte T_GDOWN = 41;

    public final static byte T_BSPHERE = 42;

    public final static byte T_RSPHERE = 43;

    public final static byte T_GSPHERE = 44;

    public final static byte T_SPHERE = 45;

    public final static byte T_TRAP2 = 46;

    public final static byte T_TRAP1 = 47;

    public final static byte T_BPANEL = 48;

    public final static byte T_RPANEL = 49;

    public final static byte T_GPANEL = 50;

    public final static byte T_STEEL = 51;

    public final static byte T_BSTEEL = 52;

    public final static byte T_RSTEEL = 53;

    public final static byte T_GSTEEL = 54;

    public final static byte T_HEARTFRAMER = 55;

    public final static byte T_SLEEPINGDOOR = 56;

    public final static byte T_TRANSPONDER = 57;

    public final static byte T_NSWE = 58;

    public final static byte T_REMOTE = 59;

    // shifts and masks
    private static final int TILES_SHIFT = 0;

    private static final int O_TILES_SHIFT = 6;

    private static final int DESTS_SHIFT = 12;

    private static final int FLAGS_SHIFT = 25;

    private static final int TILES_MASK = 0x3F << TILES_SHIFT;

    private static final int O_TILES_MASK = 0x3F << O_TILES_SHIFT;

    private static final int DESTS_MASK = 0x1FFF << DESTS_SHIFT;

    private static final int FLAGS_MASK = 0x7F << FLAGS_SHIFT;

    /**
     * @return Returns the author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    public int getPlayerX() {
        return player.x;
    }

    public int getPlayerY() {
        return player.y;
    }

    public int getHeight() {
        return playboard.length / width;
    }

    public int getWidth() {
        return width;
    }

    public Bot[] getBots() {
        // XXX doesn't allow for correct numbering
        Bot bb[] = new Bot[goodBots.length + brokenBots.length];

        System.arraycopy(goodBots, 0, bb, 0, goodBots.length);
        System.arraycopy(brokenBots, 0, bb, goodBots.length, brokenBots.length);

        return bb;
    }

    // metadata
    protected final String title;

    protected final String author;

    // width, height
    protected final int width;

    protected final Player player;

    // the level
    protected int playboard[];

    protected Bot goodBots[];

    protected Bot brokenBots[];

    // cached laser
    private IntTriple laser;

    private boolean playboardCOW;

    final private boolean hasLasers;

    // the meat
    private void warp(Entity ent, int targX, int targY) {
        checkStepOff(ent.x, ent.y);
        ent.x = targX;
        ent.y = targY;

        switch (tileAt(targX, targY)) {
        case T_PANEL:
            swapO(destAt(targX, targY));
            break;
        default:
            ;
        }
    }

    private IntPair where(int idx) {
        int x = idx % width;
        int y = idx / width;

        return new IntPair(x, y);
    }

    public byte tileAt(int i) {
        return (byte) ((playboard[i] & TILES_MASK) >> TILES_SHIFT);
    }

    public byte tileAt(int x, int y) {
        return tileAt(index(x, y));
    }

    private int index(int x, int y) {
        return y * width + x;
    }

    public byte oTileAt(int i) {
        return (byte) ((playboard[i] & O_TILES_MASK) >> O_TILES_SHIFT);
    }

    public byte oTileAt(int x, int y) {
        return oTileAt(index(x, y));
    }

    private void setPlayboardEntry(int i, int mask, int shift, int value) {
        checkPlayboardCOW();
        int tmp = playboard[i] & ~mask;
        playboard[i] = tmp | (value << shift);
    }

    private void setTile(int i, byte t) {
        setPlayboardEntry(i, TILES_MASK, TILES_SHIFT, t);
    }

    private void checkPlayboardCOW() {
        if (playboardCOW) {
            playboardCOW = false;

            // copy
            int tt[] = new int[playboard.length];
            System.arraycopy(playboard, 0, tt, 0, tt.length);
            playboard = tt;
        }
    }

    private void setTile(int x, int y, byte t) {
        setTile(index(x, y), t);
    }

    public int destAt(int i) {
        return (playboard[i] & DESTS_MASK) >> DESTS_SHIFT;
    }

    public int destAt(int x, int y) {
        return destAt(index(x, y));
    }

    public byte flagAt(int x, int y) {
        return flagAt(index(x, y));
    }

    public byte flagAt(int i) {
        return (byte) ((playboard[i] & FLAGS_MASK) >> FLAGS_SHIFT);
    }

    public boolean isWon() {
        return tileAt(player.x, player.y) == T_EXIT;
    }

    private boolean travel(int x, int y, int d, IntPair result) {
        int height = getHeight();
        switch (d) {
        case Entity.DIR_UP:
            if (y == 0) {
                return false;
            } else {
                result.x = x;
                result.y = y - 1;
                return true;
            }
        case Entity.DIR_DOWN:
            if (y == (height - 1)) {
                return false;
            } else {
                result.x = x;
                result.y = y + 1;
                return true;
            }
        case Entity.DIR_LEFT:
            if (x == 0) {
                return false;
            } else {
                result.x = x - 1;
                result.y = y;
                return true;
            }
        case Entity.DIR_RIGHT:
            if (x == (width - 1)) {
                return false;
            } else {
                result.x = x + 1;
                result.y = y;
                return true;
            }
        default:
            return false; /* ?? */
        }
    }

    // Return true if a laser can 'see' the player, or other things kill him or
    // her.
    public boolean isDead() {
        int px = player.x;
        int py = player.y;

        // bots kill, without laser
        for (int i = 0; i < goodBots.length; i++) {
            if (goodBots[i].isAt(px, py)) {
                laser = null;
                return true;
            }
        }

        // is there an exploded bomb adjacent to us, or on us?
        // then die.
        IntPair xy = new IntPair();
        for (byte db = Entity.FIRST_DIR; db <= Entity.LAST_DIR; db++) {
            if (travel(px, py, db, xy)) {
                int xx = xy.x;
                int yy = xy.y;

                /*
                 * nb, botat might not be correct, since it returns the lowest
                 * bot; so just loop manually:
                 */
                for (int m = 0; m < goodBots.length; m++) {
                    Bot b = goodBots[m];
                    if (b.isAt(xx, yy) && b.getBotType() == Entity.B_BOMB_X) {
                        return true;
                    }
                }
            }
        }

        if (!hasLasers) {
            return false;
        }

        // otherwise, look for lasers from the current dude
        for (byte dd = Entity.FIRST_DIR; dd <= Entity.LAST_DIR; dd++) {
            int lx = player.x, ly = player.y;

            IntPair r = new IntPair();
            while (travel(lx, ly, dd, r)) {
                lx = r.x;
                ly = r.y;

                if (tileAt(lx, ly) == T_LASER) {
                    int tileX = r.x;
                    int tileY = r.y;
                    byte d = Entity.dirReverse(dd);

                    laser = new IntTriple(tileX, tileY, d);
                    return true;
                }
                int tt = tileAt(lx, ly);
                if (tt != T_FLOOR && tt != T_ELECTRIC && tt != T_ROUGH
                        && tt != T_RDOWN && tt != T_GDOWN && tt != T_BDOWN
                        && tt != T_TRAP2 && tt != T_TRAP1 && tt != T_PANEL
                        && tt != T_BPANEL && tt != T_GPANEL && tt != T_RPANEL
                        && tt != T_BLACK && tt != T_HOLE)
                    break;
                // all robots also block lasers
                if (isBotAt(lx, ly))
                    break;
            }
        }
        laser = null;
        return false;
    }

    private void swapO(int idx) {
        byte tmp = tileAt(idx);
        setTile(idx, oTileAt(idx));
        setOTile(idx, tmp);

        /* swap haspanel/opanel and their refinements as well */
        byte oldFlag = flagAt(idx);
        tmp =

        (byte) (/* panel bits */
        ((oldFlag & TF_HASPANEL) != 0 ? TF_OPANEL : 0)
                | ((oldFlag & TF_OPANEL) != 0 ? TF_HASPANEL : 0) |

                /* refinement */
                ((oldFlag & TF_RPANELL) != 0 ? TF_ROPANELL : 0)
                | ((oldFlag & TF_RPANELH) != 0 ? TF_ROPANELH : 0) |

                /* orefinement */
                ((oldFlag & TF_ROPANELL) != 0 ? TF_RPANELL : 0)
                | ((oldFlag & TF_ROPANELH) != 0 ? TF_RPANELH : 0) |

        /* erase old */
        (oldFlag & ~(TF_HASPANEL | TF_OPANEL | TF_RPANELL | TF_RPANELH
                | TF_ROPANELL | TF_ROPANELH)));

        setFlag(idx, tmp);
    }

    private void setFlag(int idx, byte flag) {
        setPlayboardEntry(idx, FLAGS_MASK, FLAGS_SHIFT, flag);
    }

    private void setOTile(int idx, byte t) {
        setPlayboardEntry(idx, O_TILES_MASK, O_TILES_SHIFT, t);
    }

    /*
     * after stepping off a tile, deactivate a panel if there was one there.
     */
    private void checkLeavePanel(int x, int y) {
        /* nb: only for regular panels */
        if (tileAt(x, y) == T_PANEL) {
            swapO(destAt(x, y));
        }
    }

    /* actions on the player stepping off of a tile */
    private void checkStepOff(int x, int y) {
        /* nb: only for regular panels */
        checkTrap(x, y);
        checkLeavePanel(x, y);
    }

    private void checkTrap(int x, int y) {
        if (tileAt(x, y) == T_TRAP1) {
            setTile(x, y, T_HOLE);
        } else if (tileAt(x, y) == T_TRAP2) {
            setTile(x, y, T_TRAP1);
        }
    }

    public static byte realPanel(int f) {
        if ((f & TF_RPANELH) != 0) {
            if ((f & TF_RPANELL) != 0)
                return T_RPANEL;
            else
                return T_GPANEL;
        } else {
            if ((f & TF_RPANELL) != 0)
                return T_BPANEL;
            else
                return T_PANEL;
        }
    }

    static public boolean isPanel(int t) {
        return (t == T_PANEL || t == T_RPANEL || t == T_GPANEL || t == T_BPANEL);
    }

    static private boolean isSphere(int t) {
        return (t == T_SPHERE || t == T_RSPHERE || t == T_GSPHERE || t == T_BSPHERE);
    }

    static private boolean isSteel(int t) {
        return (t == T_STEEL || t == T_RSTEEL || t == T_GSTEEL || t == T_BSTEEL);
    }

    private void swapTiles(byte t1, byte t2) {
        for (int i = (playboard.length) - 1; i >= 0; i--) {
            if (tileAt(i) == t1)
                setTile(i, t2);
            else if (tileAt(i) == t2)
                setTile(i, t1);
        }
    }

    public boolean move(byte d) {
        player.d = d; // always set dir
        boolean result = realMove(player, d);

        if (result) {
            // move bots
            for (int i = 0; i < goodBots.length; i++) {
                Bot b = goodBots[i];
                if (b.getBotType() == Entity.B_BOMB_X) {
                    b.delete();
                }
                if (b.getBotType() == Entity.B_DELETED) {
                    continue;
                }
                IntPair dirs = b.getDirChoices(player);

                if (dirs.x != Entity.DIR_NONE) {
                    boolean bm = realMove(b, (byte) dirs.x);

                    // no good? try 2nd move
                    if (!bm && dirs.y != Entity.DIR_NONE) {
                        realMove(b, (byte) dirs.y);
                    }
                }
                if (b.getBombTimer() == 0) {
                    // time's up: explodes
                    bombsplode(i, b);
                } else {
                    b.burnLitFuse(); // for bombs only
                }
            }
        }

        cleanBotsArray();
        isDead(); // update laser cache
        return result;
    }

    static private void sortBotsArray(Bot bots[]) {
        for (int i = 1; i < bots.length; i++) {
            Bot b = bots[i];
            int x = b.x;
            int y = b.y;

            int j = i - 1;
            while (j >= 0) {
                Bot b2 = bots[j];
                int x2 = b2.x;
                int y2 = b2.y;
                if (y2 < y || (y2 == y && x2 <= x)) {
                    break;
                }

                bots[j + 1] = bots[j];
                j--;
            }
            bots[j + 1] = b;
        }
    }

    private void cleanBotsArray() {
        int gBots = 0;
        int bBots = 0;
        boolean newBroken = false;

        // count both kinds and also see if something is newly broken
        for (int i = 0; i < goodBots.length; i++) {
            Bot b = goodBots[i];
            if (!b.isDeleted()) {
                byte type = b.getBotType();
                if (type == Entity.B_BROKEN) {
                    bBots++;
                    newBroken = true;
                } else {
                    gBots++;
                }
            }
        }
        for (int i = 0; i < brokenBots.length; i++) {
            Bot b = brokenBots[i];
            if (!b.isDeleted()) {
                bBots++;
            }
        }

        // maybe do some copying
        Bot newGBots[] = goodBots;
        Bot newBBots[] = brokenBots;

        // the goodBots array can only shrink
        if (gBots < goodBots.length) {
            newGBots = new Bot[gBots];

            int j = 0;
            for (int i = 0; i < goodBots.length; i++) {
                Bot b = goodBots[i];
                if (!b.isDeleted() && b.getBotType() != Entity.B_BROKEN) {
                    newGBots[j++] = b;
                }
            }
        }

        // now check broken bots array
        if (newBroken || bBots < brokenBots.length) {
            newBBots = new Bot[bBots];

            int j = 0;
            for (int i = 0; i < goodBots.length; i++) {
                Bot b = goodBots[i];
                if (b.getBotType() == Entity.B_BROKEN) {
                    newBBots[j++] = b;
                }
            }
            for (int i = 0; i < brokenBots.length; i++) {
                Bot b = brokenBots[i];
                if (b.getBotType() == Entity.B_BROKEN) {
                    newBBots[j++] = b;
                }
            }
        }

        // commit back
        goodBots = newGBots;
        brokenBots = newBBots;

        sortBotsArray(brokenBots);
    }

    protected boolean realMove(Entity ent, byte d) {
        final IntPair newP = new IntPair();
        if (travel(ent.x, ent.y, d, newP)) {
            return maybeDoMove(ent, d, newP);
        } else
            return false; // no move for sure
    }

    private boolean maybeDoMove(Entity ent, byte d, final IntPair newP) {
        final byte target;
        switch (target = tileAt(newP.x, newP.y)) {

        /* these aren't pressed by the player so act like floor */
        case T_BPANEL:
        case T_GPANEL:
        case T_RPANEL:

            /* these are only affected when we step *off* */
        case T_TRAP2:
        case T_TRAP1:

        case T_FLOOR:
        case T_ROUGH:
        case T_BDOWN:
        case T_RDOWN:
        case T_GDOWN:
        case T_PANEL:
            return doFloorMove(ent, d, target, newP);

        case T_EXIT:
            return doExitMove(ent, newP);

        case T_ON:
            return doElectricOffMove(newP);

        case T_0:
        case T_1:
            return doToggleMove(target, newP);

        case T_BSPHERE:
        case T_RSPHERE:
        case T_GSPHERE:
        case T_SPHERE:
        case T_GOLD:
            return doSphereGoldMove(d, target, newP);

        case T_TRANSPORT:
            return doTransportMove(ent, newP);

        case T_BUTTON:
            return doButtonMove(newP);

        case T_BROKEN:
            return doBrokenMove(newP);

        case T_GREEN:
            return doGreenBlockMove(ent, d, newP);

            // steel
        case T_STEEL:
        case T_RSTEEL:
        case T_GSTEEL:
        case T_BSTEEL:
            return doSteelMove(ent, d, newP);

            // simple pushable blocks use this case
        case T_RED:
        case T_NS:
        case T_NE:
        case T_NW:
        case T_SE:
        case T_SW:
        case T_WE:
        case T_NSWE:

        case T_LR:
        case T_UD:

        case T_TRANSPONDER:

        case T_GREY:
            return doSimpleBlockMove(ent, d, target, newP);

        case T_HEARTFRAMER:
            return doHeartframer(ent, newP);

        case T_ELECTRIC:
            return doZapMove(ent, newP);

        case T_BLUE:
        case T_HOLE:
        case T_LASER:
        case T_STOP:
        case T_RIGHT:
        case T_LEFT:
        case T_UP:
        case T_DOWN:
        case T_BLIGHT:
        case T_RLIGHT:
        case T_GLIGHT:
        case T_RUP:
        case T_BUP:
        case T_GUP:
        case T_OFF:
        case T_BLACK:
        default:
            return false;
        }
    }

    private boolean doHeartframer(Entity ent, IntPair newP) {
        /*
         * only the player can pick up heart framers
         */
        if (isBotAt(newP.x, newP.y) || player.isAt(newP.x, newP.y))
            return false;

        if (ent.canGetHeartframers()) {
            /* snag heart framer */
            setTile(newP.x, newP.y, T_FLOOR);

            /* any heart framers left? */

            if (!hasFramers()) {
                int height = getHeight();
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int t = tileAt(x, y);
                        if (t == T_SLEEPINGDOOR) {
                            setTile(x, y, T_EXIT);
                        }
                    }
                }

                /* also bots */
                for (int i = 0; i < goodBots.length; i++) {
                    Bot b = goodBots[i];
                    switch (b.getBotType()) {
                    case Entity.B_DALEK_ASLEEP:
                        b.setToType(Entity.B_DALEK);
                        break;

                    case Entity.B_HUGBOT_ASLEEP:
                        b.setToType(Entity.B_HUGBOT);

                        break;
                    }
                }
            }

            /* panel actions are last */
            checkStepOff(ent.x, ent.y);
            ent.x = newP.x;
            ent.y = newP.y;

            return true;
        } else {
            return false;
        }
    }

    private boolean hasFramers() {
        for (int i = 0; i < playboard.length; i++) {
            if (tileAt(i) == T_HEARTFRAMER) {
                return true;
            }
        }
        return false;
    }

    static boolean isBombable(int t) {
        switch (t) {
        /* some level of danger */
        case T_EXIT:
        case T_SLEEPINGDOOR:
            /* useful */
        case T_LASER:

            /* obvious */
        case T_BROKEN:
        case T_GREY:
        case T_RED:
        case T_GREEN:

            /* a soft metal. ;) */
        case T_GOLD:

        case T_NS:
        case T_WE:
        case T_NW:
        case T_NE:
        case T_SW:
        case T_SE:
        case T_NSWE:
        case T_TRANSPONDER:
        case T_BUTTON:
        case T_BLIGHT:
        case T_GLIGHT:
        case T_RLIGHT:
        case T_REMOTE:

            /* ?? sure? */
        case T_BLUE:
            /* don't want walls made of this ugly thing */
        case T_STOP:

            /* but doesn't count as picking it up */
        case T_HEARTFRAMER:

            /* ?? easier */
        case T_PANEL:
        case T_RPANEL:
        case T_GPANEL:
        case T_BPANEL:

            return true;

        case T_FLOOR:

            /* obvious */
        case T_HOLE:
        case T_ELECTRIC:
        case T_BLACK:
        case T_ROUGH:

            /*
             * for symmetry with holes. maybe could become holes, but that is
             * just more complicated
             */
        case T_TRAP1:
        case T_TRAP2:

            /* useful for level designers */
        case T_LEFT:
        case T_RIGHT:
        case T_UP:
        case T_DOWN:

            /* Seems sturdy */
        case T_TRANSPORT:
        case T_ON:
        case T_OFF:
        case T_1:
        case T_0:

            /* made of metal */
        case T_STEEL:
        case T_BSTEEL:
        case T_RSTEEL:
        case T_GSTEEL:

        case T_LR:
        case T_UD:

        case T_SPHERE:
        case T_BSPHERE:
        case T_RSPHERE:
        case T_GSPHERE:

            /*
             * shouldn't bomb the floorlike things, so also their 'up'
             * counterparts
             */
        case T_BUP:
        case T_BDOWN:
        case T_GUP:
        case T_GDOWN:
        case T_RUP:
        case T_RDOWN:
            return false;
        }

        /* illegal tile */
        return false;
    }

    private boolean doSphereGoldMove(int d, byte target, IntPair newP) {
        /*
         * spheres allow pushing in a line: ->OOOO becomes OOO ---->O
         * 
         * so keep travelling while the tile in the destination direction is a
         * sphere of any sort.
         */
        IntPair t = new IntPair();
        while (isSphere(tileAt(newP.x, newP.y))
                && !(player.isAt(newP.x, newP.y) || isBotAt(newP.x, newP.y))
                && travel(newP.x, newP.y, d, t) && isSphere(tileAt(t.x, t.y))) {
            newP = new IntPair(t);
            target = tileAt(newP.x, newP.y);
        }

        // can't push if entity there
        if (player.isAt(newP.x, newP.y) || isBotAt(newP.x, newP.y)) {
            return false;
        }

        int goldX = newP.x, goldY = newP.y;

        /* remove gold block */
        byte replacement = ((flagAt(goldX, goldY) & TF_HASPANEL) != 0) ? realPanel(flagAt(
                goldX, goldY))
                : T_FLOOR;

        setTile(goldX, goldY, replacement);

        IntPair tGold = new IntPair();
        while (travel(goldX, goldY, d, tGold)) {
            int next = tileAt(tGold.x, tGold.y);
            if (!(next == T_ELECTRIC || next == T_PANEL || next == T_BPANEL
                    || next == T_RPANEL || next == T_GPANEL || next == T_FLOOR)
                    || isBotAt(tGold.x, tGold.y)
                    || player.isAt(tGold.x, tGold.y)) {
                break;
            }

            goldX = tGold.x;
            goldY = tGold.y;

            if (next == T_ELECTRIC)
                break;
        }

        /* goldx is dest, newx is source */
        if (goldX != newP.x || goldY != newP.y) {

            int landOn = tileAt(goldX, goldY);
            boolean doSwap = false;

            /* untrigger from source */
            if ((flagAt(newP.x, newP.y) & TF_HASPANEL) != 0) {
                int pan = realPanel(flagAt(newP.x, newP.y));
                /* any */
                if (pan == T_PANEL ||
                /* colors */
                (target == T_GSPHERE && pan == T_GPANEL)
                        || (target == T_RSPHERE && pan == T_RPANEL)
                        || (target == T_BSPHERE && pan == T_BPANEL))
                    doSwap = true;
            }

            /*
             * only the correct color sphere can trigger the colored panels
             */
            boolean doSwapT = triggers(target, landOn);

            setTile(goldX, goldY, target);

            // boolean zapped = false;
            if (landOn == T_ELECTRIC) {
                /*
                 * gold zapped. however, if the electric was the newTarget of a
                 * panel that we just left, the electric has been swapped into
                 * the o world (along with the gold). So swap there.
                 */
                setTile(goldX, goldY, T_ELECTRIC);

                // zapped = true;
            }

            if (doSwapT) {
                swapO(destAt(goldX, goldY));
            }

            if (doSwap) {
                swapO(destAt(newP.x, newP.y));
            }

            return true;
        } else {
            // didn't move, put it back
            setTile(newP.x, newP.y, target);

            return false;
        }
    }

    private boolean doZapMove(Entity ent, IntPair newP) {
        // some bots are stupid enough to zap themselves
        if (isBotAt(newP.x, newP.y) || player.isAt(newP.x, newP.y)) {
            return false;
        }

        if (ent != player && ent.zapsSelf()) {
            int oldX = ent.x;
            int oldY = ent.y;

            // move
            ent.x = newP.x;
            ent.y = newP.y;

            // kill
            ((Bot) ent).delete();

            // might have stepped off
            checkStepOff(oldX, oldY);
            return true;
        } else
            return false;
    }

    private void bombsplode(int currentTimeslot, Bot b) {
        b.explode();

        int x = b.x;
        int y = b.y;

        IntPair bxy = new IntPair();
        for (byte d = Entity.FIRST_DIR; d <= Entity.LAST_DIR; d++) {
            if (travel(x, y, d, bxy)) {
                int bx = bxy.x;
                int by = bxy.y;
                if (isBombable(tileAt(bx, by))) {
                    setTile(bx, by, T_FLOOR);
                    setFlag(bx, by, (byte) (flagAt(bx, by) & ~(TF_HASPANEL
                            | TF_RPANELL | TF_RPANELH)));
                }

                for (int bDie = 0; bDie < goodBots.length; bDie++) {
                    Bot bd = goodBots[bDie];
                    if (bd.isAt(bx, by)) {
                        byte type = bd.getBotType();
                        if (type == Entity.B_DELETED || type == Entity.B_BOMB_X) {
                            continue;
                        }

                        if (bd.isBomb()) {
                            // chain reaction
                            if (bDie < currentTimeslot) {
                                bombsplode(currentTimeslot, bd);
                                break;
                            } else {
                                // will explode this turn (unless a bot pushes
                                // it??)
                                bd.expireTimer();
                                break;
                            }
                        } else {
                            // non-bomb, so just kill it
                            bd.delete();
                            break;
                        }
                    }
                }
                for (int bDie = 0; bDie < brokenBots.length; bDie++) {
                    Bot bd = brokenBots[bDie];
                    if (bd.isAt(bx, by)) {
                        bd.delete();
                        break;
                    }
                }
            }
        }
    }

    private boolean doSimpleBlockMove(Entity ent, int d, byte target,
            IntPair newP) {
        if (player.isAt(newP.x, newP.y) || isBotAt(newP.x, newP.y)) {
            return false;
        }

        /*
         * we're always stepping onto the panel that the block was on, so we
         * don't need to change its state. (if it's a regular panel, then don't
         * change because our feet are on it. if it's a colored panel, don't
         * change because neither the man nor the block can activate it.) But we
         * do need to put a panel there instead of floor.
         */

        byte replacement = ((flagAt(newP.x, newP.y) & TF_HASPANEL) == TF_HASPANEL) ? realPanel(flagAt(
                newP.x, newP.y))
                : T_FLOOR;

        boolean doSwap = false;
        // boolean zap = false;
        // boolean hole = false;
        IntPair dest = new IntPair();

        if (target == T_LR && (d == Entity.DIR_UP || d == Entity.DIR_DOWN))
            return false;
        if (target == T_UD && (d == Entity.DIR_LEFT || d == Entity.DIR_RIGHT))
            return false;

        if (travel(newP.x, newP.y, d, dest)) {
            int destT = tileAt(dest.x, dest.y);
            if (player.isAt(dest.x, dest.y) || isBotAt(dest.x, dest.y)) {
                return false;
            }
            switch (destT) {
            case T_FLOOR:
                /* easy */
                setTile(dest.x, dest.y, target);
                setTile(newP.x, newP.y, replacement);
                break;
            case T_ELECTRIC:
                /* Zap! */
                if (target != T_LR && target != T_UD) {
                    setTile(newP.x, newP.y, replacement);
                } else
                    return false;
                // zap = true;
                break;
            case T_HOLE:
                /* only grey blocks into holes */
                if (target == T_GREY) {
                    setTile(dest.x, dest.y, T_FLOOR);
                    setTile(newP.x, newP.y, replacement);
                    // hole = true;
                    break;
                } else
                    return false;
            case T_BPANEL:
            case T_RPANEL:
            case T_GPANEL:
            case T_PANEL:
                if (target != T_LR && target != T_UD) {
                    /* delay the swap */
                    doSwap = (destT == T_PANEL); // grey down holes
                    setTile(dest.x, dest.y, target);
                    setTile(newP.x, newP.y, replacement);
                } else
                    return false;
                break;
            default:
                return false;
            }
            checkStepOff(ent.x, ent.y);

            if (doSwap)
                swapO(destAt(dest.x, dest.y));

            ent.x = newP.x;
            ent.y = newP.y;
            return true;
        } else
            return false;
    }

    private boolean doSteelMove(Entity ent, byte d, IntPair newP) {
        /*
         * three phases. first, see if we can push this whole column one space.
         * 
         * if so, generate animations.
         * 
         * then, update panel states. this is tricky.
         */

        IntPair dest = new IntPair(newP);
        {
            int curx = newP.x, cury = newP.y;
            /*
             * go until not steel, or if we hit a robot anywhere along this, end
             */
            while (!isBotAt(curx, cury) && !player.isAt(curx, cury)
                    && travel(curx, cury, d, dest)
                    && isSteel(tileAt(dest.x, dest.y))) {
                curx = dest.x;
                cury = dest.y;
            }
        }

        /* entity in our column or at the end? sorry */
        if (isBotAt(dest.x, dest.y) || player.isAt(dest.x, dest.y))
            return false;

        /* what did we hit? */
        int hitTile = tileAt(dest.x, dest.y);
        boolean zap = false;
        switch (hitTile) {
        /*
         * nb if we "hit" steel, then it's steel to the edge of the level, so no
         * push.
         */
        case T_PANEL:
        case T_GPANEL:
        case T_BPANEL:
        case T_RPANEL:
        case T_FLOOR:
            break;
        case T_ELECTRIC:
            zap = true;
            break;
        default:
            return (false);
        }

        /*
         * guy destx,desty v v [ ][S][S][S][S][ ] ^ steels starting at newx,newy
         * 
         * d ---->
         */
        byte revD = Entity.dirReverse(d);

        /* move the steel blocks first. */
        {
            int moveX = dest.x, moveY = dest.y;
            IntPair next = new IntPair();
            while (!(moveX == newP.x && moveY == newP.y)) {
                travel(moveX, moveY, revD, next);
                setTile(moveX, moveY, tileAt(next.x, next.y));
                moveX = next.x;
                moveY = next.y;
            }
        }

        /* and one more, for the tile that we're stepping onto */
        {
            byte replacement = ((flagAt(newP.x, newP.y) & TF_HASPANEL) == TF_HASPANEL) ? realPanel(flagAt(
                    newP.x, newP.y))
                    : T_FLOOR;
            setTile(newP.x, newP.y, replacement);
        }

        /*
         * reconcile panels.
         * 
         * imagine pushing a row of blocks one space to the right.
         * 
         * we loop over the NEW positions for the steel blocks. If a steel block
         * is on a panel (that it can trigger), then we trigger that panel as
         * long as the thing to its right (which used to be there) couldn't
         * trigger it. this handles new panels that are turned ON.
         * 
         * if we can't trigger the panel, then we check to see if the panel to
         * our right (which used to be there) also can't trigger it. If so, we
         * don't do anything. Otherwise, we "untrigger" the panel.
         * 
         * To simplify, if triggerstatus_now != triggerstatus_old, we trigger.
         * (Trigger has the same effect as untriggering.)
         * 
         * Because these swaps are supposed to be delayed, we set the TF_TEMP
         * flag if the tile should do a swap afterwards.
         */

        boolean swapNew = false;
        {
            int lookX = dest.x, lookY = dest.y;
            int prevT = T_FLOOR; /* anything that doesn't trigger */
            IntPair next = new IntPair();
            while (!(lookX == newP.x && lookY == newP.y)) {

                int hereT = tileAt(lookX, lookY);

                /* triggerstatus for this location (lookx, looky) */
                boolean triggerStatusNow = ((flagAt(lookX, lookY) & TF_HASPANEL) == TF_HASPANEL)
                        && triggers(hereT, realPanel(flagAt(lookX, lookY)));

                boolean triggerStatusOld = ((flagAt(lookX, lookY) & TF_HASPANEL) == TF_HASPANEL)
                        && isSteel(prevT)
                        && triggers(prevT, realPanel(flagAt(lookX, lookY)));

                if (triggerStatusNow != triggerStatusOld) {
                    setFlag(lookX, lookY,
                            (byte) (flagAt(lookX, lookY) | TF_TEMP));
                    // printf("Yes swap at %d/%d\n", lookx, looky);
                } else
                    setFlag(lookX, lookY,
                            (byte) (flagAt(lookX, lookY) & ~TF_TEMP));

                prevT = hereT;

                travel(lookX, lookY, revD, next);

                lookX = next.x;
                lookY = next.y;
            }

            /* first panel is slightly different */
            {
                int first = tileAt(newP.x, newP.y);
                boolean trigNow = (first == T_PANEL);
                boolean trigOld = isPanel(first)
                        && triggers(prevT, realPanel(flagAt(newP.x, newP.y)));

                if (trigOld != trigNow) {
                    swapNew = true;
                }
            }
        } /* zap, if necessary, before swapping */
        if (zap) {
            setTile(dest.x, dest.y, T_ELECTRIC);
        }

        /* now we can start swapping. */
        checkStepOff(ent.x, ent.y);

        /*
         * this part is now invariant to order, because there is only one
         * destination per location
         */

        if (swapNew) {
            swapO(destAt(newP.x, newP.y));
        }

        {
            int lookx = dest.x, looky = dest.y;
            IntPair next = new IntPair();
            while (!(lookx == newP.x && looky == newP.y)) {

                if ((flagAt(lookx, looky) & TF_TEMP) == TF_TEMP) {
                    swapO(destAt(lookx, looky));
                    setFlag(lookx, looky,
                            (byte) (flagAt(lookx, looky) & ~TF_TEMP));
                }

                /* next */
                travel(lookx, looky, revD, next);
                lookx = next.x;
                looky = next.y;
            }
        }

        /* XXX also boundary conditions? (XXX what does that mean?) */
        ent.x = newP.x;
        ent.y = newP.y;

        return true;
    }

    private boolean doGreenBlockMove(Entity ent, int d, IntPair newP) {
        if (player.isAt(newP.x, newP.y) || isBotAt(newP.x, newP.y)) {
            return false;
        }

        IntPair dest = new IntPair();
        if (travel(newP.x, newP.y, d, dest)) {
            if (tileAt(dest.x, dest.y) == T_FLOOR && !isBotAt(dest.x, dest.y)
                    && !player.isAt(dest.x, dest.y)) {
                setTile(dest.x, dest.y, T_BLUE);
                setTile(newP.x, newP.y, T_FLOOR);

                checkStepOff(ent.x, ent.y);

                ent.x = newP.x;
                ent.y = newP.y;
                return true;
            } else
                return false;
        } else
            return false;
    }

    private boolean doBrokenMove(IntPair newP) {
        if (player.isAt(newP.x, newP.y) || isBotAt(newP.x, newP.y)) {
            return false;
        }
        setTile(newP.x, newP.y, T_FLOOR);
        return true;
    }

    private boolean doButtonMove(IntPair newP) {
        if (player.isAt(newP.x, newP.y) || isBotAt(newP.x, newP.y)) {
            return false;
        }

        // swaps delayed until the end
        int bSwaps = 0;
        int rSwaps = 0;
        int gSwaps = 0;

        // panel swaps (from T_REMOTE) delayed until after regular swaps
        ArrayList<Integer> panelSwaps = new ArrayList<Integer>();

        for (int dd = Entity.FIRST_DIR; dd <= Entity.LAST_DIR; dd++) {
            /* send a pulse in that direction. */
            IntPair pulse = new IntPair(newP);
            int pd = dd;

            while (pd != Entity.DIR_NONE && travel(pulse.x, pulse.y, pd, pulse)) {
                switch (tileAt(pulse.x, pulse.y)) {
                case T_REMOTE:
                    panelSwaps.add(Integer.valueOf(destAt(pulse.x, pulse.y)));
                    break;
                case T_BLIGHT:
                    pd = Entity.DIR_NONE;
                    bSwaps++;
                    break;
                case T_RLIGHT:
                    pd = Entity.DIR_NONE;
                    rSwaps++;
                    break;
                case T_GLIGHT:
                    pd = Entity.DIR_NONE;
                    gSwaps++;
                    break;

                case T_NS:
                    if (pd == Entity.DIR_UP || pd == Entity.DIR_DOWN)
                        continue;
                    else
                        pd = Entity.DIR_NONE;
                    break;

                case T_WE:
                    if (pd == Entity.DIR_LEFT || pd == Entity.DIR_RIGHT)
                        continue;
                    else
                        pd = Entity.DIR_NONE;
                    break;

                case T_NW:
                    if (pd == Entity.DIR_DOWN)
                        pd = Entity.DIR_LEFT;
                    else if (pd == Entity.DIR_RIGHT)
                        pd = Entity.DIR_UP;
                    else
                        pd = Entity.DIR_NONE;
                    break;

                case T_SW:
                    if (pd == Entity.DIR_UP)
                        pd = Entity.DIR_LEFT;
                    else if (pd == Entity.DIR_RIGHT)
                        pd = Entity.DIR_DOWN;
                    else
                        pd = Entity.DIR_NONE;
                    break;

                case T_NE:
                    if (pd == Entity.DIR_DOWN)
                        pd = Entity.DIR_RIGHT;
                    else if (pd == Entity.DIR_LEFT)
                        pd = Entity.DIR_UP;
                    else
                        pd = Entity.DIR_NONE;
                    break;

                case T_SE:
                    if (pd == Entity.DIR_UP)
                        pd = Entity.DIR_RIGHT;
                    else if (pd == Entity.DIR_LEFT)
                        pd = Entity.DIR_DOWN;
                    else
                        pd = Entity.DIR_NONE;
                    break;

                case T_NSWE:
                    continue;

                case T_TRANSPONDER: {
                    // System.out.println("transponder at " + pulse.x + "/" +
                    // pulse.y);
                    if (!travel(pulse.x, pulse.y, pd, pulse))
                        pd = Entity.DIR_NONE;
                    else {
                        /* keep going until we hit another transponder. */
                        do {
                            int ta = tileAt(pulse.x, pulse.y);
                            // System.out.println(" ... at " + pulse.x + "/" +
                            // pulse.y + ": " + ta);
                            if (!allowBeam(ta) || isBotAt(pulse.x, pulse.y)
                                    || player.isAt(pulse.x, pulse.y)) {
                                /* hit something. is it a transponder? */
                                if (ta == T_TRANSPONDER) {
                                    // animation stuff
                                } else {
                                    /* stop. */
                                    pd = Entity.DIR_NONE;
                                }
                                break;
                            }
                            /* otherwise keep going... */
                        } while (travel(pulse.x, pulse.y, pd, pulse));
                    }
                    break;
                }

                default:
                    pd = Entity.DIR_NONE;
                }
            }
        }

        // do the swaps
        if ((bSwaps & 1) == 1) {
            swapTiles(T_BUP, T_BDOWN);
        }
        if ((rSwaps & 1) == 1) {
            swapTiles(T_RUP, T_RDOWN);
        }
        if ((gSwaps & 1) == 1) {
            swapTiles(T_GUP, T_GDOWN);
        }

        for (int i = 0; i < panelSwaps.size(); i++) {
            swapO(panelSwaps.get(i));
        }

        return true;
    }

    private static boolean allowBeam(int tt) {
        return (tt == T_FLOOR || tt == T_ELECTRIC || tt == T_ROUGH
                || tt == T_RDOWN || tt == T_GDOWN || tt == T_BDOWN
                || tt == T_TRAP2 || tt == T_TRAP1 || tt == T_PANEL
                || tt == T_BPANEL || tt == T_GPANEL || tt == T_RPANEL
                || tt == T_BLACK || tt == T_HOLE);
    }

    private boolean doTransportMove(Entity ent, IntPair newP) {
        // not if there's an entity there
        if (player.isAt(newP.x, newP.y) || isBotAt(newP.x, newP.y)) {
            return false;
        }

        if (ent.canTeleport() || ent.isPlayer()) {
            IntPair targ;
            targ = where(destAt(newP.x, newP.y));

            warp(ent, targ.x, targ.y);

            checkBotDeath(targ.x, targ.y, ent);

            return true;
        } else {
            return false;
        }
    }

    private boolean doToggleMove(int target, IntPair newP) {
        if (player.isAt(newP.x, newP.y) || isBotAt(newP.x, newP.y)) {
            return false;
        }

        byte opp = (target == T_0 ? T_1 : T_0);

        swapTiles(T_UD, T_LR);

        setTile(newP.x, newP.y, opp);

        return true;
    }

    private boolean doElectricOffMove(IntPair newP) {
        if (player.isAt(newP.x, newP.y) || isBotAt(newP.x, newP.y)) {
            return false;
        }

        for (int i = (playboard.length) - 1; i >= 0; i--) {
            if (tileAt(i) == T_ELECTRIC)
                setTile(i, T_FLOOR);
        }
        setTile(newP.x, newP.y, T_OFF);
        return true;
    }

    private boolean doExitMove(Entity ent, IntPair newP) {
        // bots don't exit
        if (player.isAt(newP.x, newP.y) || isBotAt(newP.x, newP.y)) {
            return false;
        }

        checkStepOff(ent.x, ent.y);
        ent.x = newP.x;
        ent.y = newP.y;
        return true;
    }

    private boolean doFloorMove(Entity ent, int d, int target, IntPair newP) {
        // sometimes we will push
        Entity pushee = null;
        Bot b;
        if (player.isAt(newP.x, newP.y)) { // ent is not player!
            // if player is on bot, no pushing either of them
            if (getBotAt(newP.x, newP.y) != null) {
                return false;
            }

            if (ent.canPushPlayer()) {
                pushee = player;
            } else if (ent.crushesPlayer()) {
                pushee = null; // CRUSH! not push
            } else {
                return false;
            }
        } else if ((b = getBotAt(newP.x, newP.y)) != null) {
            if (ent.canPushBots()) {
                pushee = b;
            } else if (ent.walksIntoBots()) {
                pushee = null; // WALK! not push
            } else {
                return false;
            }
        }

        if (pushee != null) {
            // we are pushing, do some sort of recursive push
            IntPair far = new IntPair();
            if (travel(newP.x, newP.y, d, far)) {
                int fTarget = tileAt(far.x, far.y);
                switch (fTarget) {
                case T_ELECTRIC:
                    // only bots can go into electric
                    if (pushee == player) {
                        return false;
                    }
                    break;
                case T_TRAP2:
                case T_TRAP1:
                case T_FLOOR:
                case T_ROUGH:
                case T_RDOWN:
                case T_GDOWN:
                case T_BDOWN:
                case T_PANEL:
                case T_RPANEL:
                case T_GPANEL:
                case T_BPANEL:
                    break;
                default:
                    return false;
                }

                // can't push 2 entities
                if (isBotAt(far.x, far.y)) {
                    return false;
                }
                if (player.isAt(far.x, far.y)) {
                    return false;
                }

                // if bomb, reset fuse
                pushee.armFuseIfBomb();

                // push
                pushee.x = far.x;
                pushee.y = far.y;

                // handle leaving current (pusher) position
                checkTrap(ent.x, ent.y);

                // still need to check panels later
                int srcX = ent.x;
                int srcY = ent.y;
                boolean swapSrc = tileAt(ent.x, ent.y) == T_PANEL;

                // move pusher
                ent.x = newP.x;
                ent.y = newP.y;

                // zapping
                if (fTarget == T_ELECTRIC && pushee != player) {
                    ((Bot) pushee).delete();
                }

                // the tile in the middle is being stepped off
                // and stepped on; if it's a panel, don't do anything
                // (to avoid a double swap)
                if (target == T_PANEL) {
                    // nothing
                } else {
                    checkTrap(newP.x, newP.y);
                }

                // -- panel phase --

                // first, if pusher stepped off a panel, it swaps
                if (swapSrc) {
                    swapO(destAt(srcX, srcY));
                }

                // pushed entity is stepping onto new panel, perhaps
                if (fTarget == T_PANEL) {
                    swapO(destAt(far.x, far.y));
                }

                // done?
                return true;
            } else {
                return false;
            }
        } else {
            // might have stepped onto bot
            checkBotDeath(newP.x, newP.y, ent);

            // panels again
            checkStepOff(ent.x, ent.y);

            ent.x = newP.x;
            ent.y = newP.y;

            if (target == T_PANEL) {
                swapO(destAt(newP.x, newP.y));
            }
            return true;
        }
    }

    private void setFlag(int x, int y, byte f) {
        setFlag(index(x, y), f);
    }

    static private boolean triggers(int tile, int panel) {
        /* "anything" triggers grey panels */
        if (panel == T_PANEL)
            return true;
        if (panel == T_RPANEL) {
            return tile == T_RSPHERE || tile == T_RSTEEL;
        }
        if (panel == T_GPANEL) {
            return tile == T_GSPHERE || tile == T_GSTEEL;
        }
        if (panel == T_BPANEL) {
            return tile == T_BSPHERE || tile == T_BSTEEL;
        }
        /* ? */
        return false;
    }

    private void checkBotDeath(int x, int y, Entity ent) {
        if (ent != player) {
            for (int b = 0; b < goodBots.length; b++) {
                Bot bb = goodBots[b];
                if (ent != bb && !bb.isDeleted() && x == bb.x
                        && y == bb.y) {
                    goodBots[b].delete();
                    ((Bot) ent).setToType(Entity.B_BROKEN);
                }
            }
            for (int b = 0; b < brokenBots.length; b++) {
                Bot bb = brokenBots[b];
                if (ent != bb && !bb.isDeleted() && x == bb.x
                        && y == bb.y) {
                    brokenBots[b].delete();
                    ((Bot) ent).setToType(Entity.B_BROKEN);
                }
            }
        }
    }

    public boolean isBotAt(int x, int y) {
        return getBotAt(x, y) != null;
    }

    public boolean isPlayerAt(int x, int y) {
        return x == getPlayerX() && y == getPlayerY();
    }

    public boolean isEntityAt(int x, int y) {
        return isBotAt(x, y) || isPlayerAt(x, y);
    }

    private Bot getBotAt(int x, int y) {
        for (int i = 0; i < goodBots.length; i++) {
            Bot b = goodBots[i];
            if (!b.isDeleted() && b.isAt(x, y)) {
                return b;
            }
        }
        for (int i = 0; i < brokenBots.length; i++) {
            Bot b = brokenBots[i];
            if (!b.isDeleted() && b.isAt(x, y)) {
                return b;
            }
        }
        return null;
    }

    public Level(Level l) {
        width = l.width;

        author = l.author;
        title = l.title;

        hasLasers = l.hasLasers;

        player = new Player(l.player.x, l.player.y, l.player.d);

        // COW!
        playboard = l.playboard;

        playboardCOW = true;

        goodBots = new Bot[l.goodBots.length];
        for (int i = 0; i < l.goodBots.length; i++) {
            Bot b = l.goodBots[i];
            goodBots[i] = new Bot(b);
        }

        brokenBots = new Bot[l.brokenBots.length];
        for (int i = 0; i < l.brokenBots.length; i++) {
            Bot b = l.brokenBots[i];
            brokenBots[i] = new Bot(b);
        }

        // dirty = new DirtyList();

        isDead();
    }

    public Level(BitInputStream in) throws IOException {
        MetaData m = getMetaData(in);

        width = m.width;
        int height = m.height;

        checkMaxSize(width, height);

        author = m.author;
        title = m.title;

        int playerX = in.readInt();
        int playerY = in.readInt();

        player = new Player(playerX, playerY, Entity.DIR_DOWN);

        if (playerX >= width || playerY >= height || playerX < 0 || playerY < 0) {
            throw new IOException("Player outside level: (" + playerX + ","
                    + playerY + ")");
        }

        int len = width * height;

        // RunLengthEncoding.decode will check sanity of len
        int tmp1[] = RunLengthEncoding.decode(in, len);
        int tmp2[] = RunLengthEncoding.decode(in, len);
        int tmp3[] = RunLengthEncoding.decode(in, len);
        int tmp4[] = RunLengthEncoding.decode(in, len);

        playboard = new int[len];

        hasLasers = setTilesFromIntArrays(len, tmp1, tmp2, tmp3, tmp4);

        // load bots if in file
        int bots;
        int botI[] = null;
        byte botT[] = null;
        try {
            bots = in.readInt(); // may be insane
            botI = RunLengthEncoding.decode(in, bots); // bots is sane now
            botT = new byte[len];
            int tmp[] = RunLengthEncoding.decode(in, bots);
            for (int i = 0; i < bots; i++) {
                botT[i] = (byte) tmp[i];
            }
        } catch (EOFException e) {
            bots = 0;
        }

        goodBots = new Bot[bots];
        brokenBots = new Bot[0];

        try {
            for (byte i = 0; i < this.goodBots.length; i++) {
                int pos = botI[i];
                if (pos < 0 || pos >= width * height) {
                    throw new IOException("Bot " + i + " outside of level: "
                            + pos);
                }

                int x = pos % width;
                int y = pos / width;
                this.goodBots[i] = new Bot(x, y, Entity.DIR_DOWN, botT[i]);
            }
        } catch (UnknownBotException e) {
            throw new IOException("Unknown bot type: " + e.getType());
        }

        // dirty = new DirtyList();

        cleanBotsArray();
        isDead(); // calculate laser cache
    }

    private int composePlayboardEntry(int tile, int oTile, int dest, int flag) {
        return (tile << TILES_SHIFT) | (oTile << O_TILES_SHIFT)
                | (dest << DESTS_SHIFT) | (flag << FLAGS_SHIFT);
    }

    private boolean setTilesFromIntArrays(int len, int[] tiles, int[] oTiles,
            int[] dests, int[] flags) {
        boolean hasLasers = false;
        for (int i = 0; i < len; i++) {
            byte tile = (byte) tiles[i];
            byte oTile = (byte) oTiles[i];
            if (tile == T_LASER || oTile == T_LASER) {
                hasLasers = true;
            }

            this.playboard[i] = composePlayboardEntry(tile, oTile, dests[i],
                    flags[i]);
        }
        return hasLasers;
    }

    public Level(LevelManip m) {
        width = m.w;
        int height = m.h;

        checkMaxSize(width, height);

        author = m.author;
        title = m.title;

        int len = width * height;

        playboard = new int[len];

        int i = 0;
        boolean hasLasers = false;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                byte tile = (byte) m.tiles[x][y];
                byte oTile = (byte) m.oTiles[x][y];
                if (tile == T_LASER || oTile == T_LASER) {
                    hasLasers = true;
                }

                this.playboard[i++] = composePlayboardEntry(tile, oTile,
                        m.dests[x][y], m.flags[x][y]);
            }
        }
        this.hasLasers = hasLasers;

        player = new Player(m.player.x, m.player.y, m.player.d);
        goodBots = new Bot[m.bots.length];
        for (int b = 0; b < m.bots.length; b++) {
            Bot bb = m.bots[b];
            goodBots[b] = new Bot(bb.x, bb.y, bb.d, bb
                    .getBotType());
            goodBots[b].bombTimer = bb.getBombTimer();
        }
        brokenBots = new Bot[0];

        cleanBotsArray();
    }

    static private void checkMaxSize(int width, int height) {
        int size = width * height;
        int maxSize = (DESTS_MASK >> DESTS_SHIFT) + 1;
        if (size > maxSize) {
            throw new IllegalArgumentException("Level too large: " + width
                    + " × " + height + " = " + size + " > " + maxSize);
        }
    }

    public Level(InputStream in, int width, int height) throws IOException {
        this.width = width;
        title = null;
        author = null;

        checkMaxSize(width, height);

        DataInputStream dd = new DataInputStream(in);

        // read player
        player = new Player(dd.readInt(), dd.readInt(), Entity.DIR_DOWN);

        // read bots
        goodBots = new Bot[dd.readInt()];
        for (int i = 0; i < goodBots.length; i++) {
            goodBots[i] = new Bot(dd.readInt(), dd.readInt(), Entity.DIR_DOWN,
                    dd.readByte(), dd.readByte());
        }

        brokenBots = new Bot[0];

        cleanBotsArray();

        // read level
        hasLasers = dd.readBoolean();

        playboard = new int[width * height];
        for (int i = 0; i < playboard.length; i++) {
            playboard[i] = composePlayboardEntry(dd.readByte(), dd.readByte(),
                    dd.readShort(), dd.readByte());
        }
    }

    public static MetaData getMetaData(BitInputStream in) throws IOException {
        String magic = Misc.getStringFromData(in, 4);
        if (!magic.equals("ESXL")) {
            throw new IOException("Bad magic: '" + magic + "'");
        }

        // System.out.println("going to read width...");
        int width = in.readInt();
        if (width <= 0) {
            throw new IOException("Invalid width: " + width);
        }

        // System.out.println("going to read height...");
        int height = in.readInt();
        if (height <= 0) {
            throw new IOException("Invalid height: " + height);
        }

        // System.out.println("width: " + width + ", height: " + height);

        int size;

        size = in.readInt();
        String title = Misc.getStringFromData(in, size);

        size = in.readInt();
        String author = Misc.getStringFromData(in, size);

        return new MetaData(width, height, title, author);
    }

    public void print(PrintStream p) {
        int height = getHeight();
        p.println(toString());
        p.println("\"" + title + "\" by " + author + " (" + width + "x"
                + height + ")" + " player: (" + this.player.x + ","
                + this.player.y + ")");
        p.println();
        p.println("tiles");
        printM(p, playboard, width, TILES_MASK, TILES_SHIFT);

        // p.println();
        // p.println("oTiles");
        // printM(p, oTiles, width);
        //
        // p.println();
        // p.println("dests");
        // printM(p, dests, width);
        //
        // p.println();
        // p.println("flags");
        // printM(p, flags, width);
    }

    static private void printM(PrintStream p, int[] m, int w, int mask,
            int shift) {
        int l = 0;
        for (int i = 0; i < m.length; i++) {
            p.print((char) (((m[i] & mask) >> shift) + 32));
            l++;
            if (l == w) {
                p.println();
                l = 0;
            }
        }
    }

    public boolean hasTile(byte t) {
        for (int i = 0; i < playboard.length; i++) {
            if (tileAt(i) == t) {
                return true;
            }
        }
        return false;
    }

    public int getPlayerDir() {
        return player.d;
    }

    public IntTriple getLaser() {
        return laser;
    }

    public void serializeLossily(OutputStream out) throws IOException {
        DataOutputStream dd = new DataOutputStream(out);

        // write player
        dd.writeInt(getPlayerX());
        dd.writeInt(getPlayerY());

        // write bots
        dd.writeInt(goodBots.length + brokenBots.length);
        for (int i = 0; i < goodBots.length; i++) {
            Bot b = goodBots[i];
            dd.writeInt(b.x);
            dd.writeInt(b.y);

            dd.writeByte(b.getBotType());
            dd.writeByte(b.getBombTimer());
        }
        for (int i = 0; i < brokenBots.length; i++) {
            Bot b = brokenBots[i];
            dd.writeInt(b.x);
            dd.writeInt(b.y);

            dd.writeByte(b.getBotType());
            dd.writeByte(b.getBombTimer());
        }

        // write level
        dd.writeBoolean(hasLasers);
        for (int i = 0; i < playboard.length; i++) {
            dd.writeByte(tileAt(i));
            dd.writeByte(oTileAt(i));
            dd.writeShort(destAt(i));
            dd.writeByte(flagAt(i));
        }
        /*
         * for (int i = 0; i < tiles.length; i++) { } for (int i = 0; i <
         * tiles.length; i++) { } for (int i = 0; i < tiles.length; i++) { }
         */
    }

    public Player getPlayer() {
        return player;
    }
}
