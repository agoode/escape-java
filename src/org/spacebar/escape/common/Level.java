package org.spacebar.escape.common;

import java.io.*;

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

    // directions
    public final static int DIR_NONE = 0;

    public final static int FIRST_DIR = 1;

    public final static int DIR_UP = 1;

    public final static int DIR_DOWN = 2;

    public final static int DIR_LEFT = 3;

    public final static int DIR_RIGHT = 4;

    public final static int LAST_DIR = 4;

    // panel colors
    public final static int PANEL_REGULAR = 0;

    public final static int PANEL_BLUE = 1;

    public final static int PANEL_GREEN = 2;

    public final static int PANEL_RED = 3;

    // panels under tiles
    public final static int TF_NONE = 0;

    /* panel under tile (ie, pushable block) */
    /*
     * if HASPANEL is set, then TF_RPANELH * 2 + TF_RPANELL says what kind (see
     * panel colors above)
     */
    public final static int TF_HASPANEL = 1;

    public final static int TF_RPANELL = 4;

    public final static int TF_RPANELH = 8;

    /* panel under tile in bizarro world */
    /* same refinement */
    public final static int TF_OPANEL = 2;

    public final static int TF_ROPANELL = 16;

    public final static int TF_ROPANELH = 32;

    // panels
    public final static int T_FLOOR = 0;

    public final static int T_RED = 1;

    public final static int T_BLUE = 2;

    public final static int T_GREY = 3;

    public final static int T_GREEN = 4;

    public final static int T_EXIT = 5;

    public final static int T_HOLE = 6;

    public final static int T_GOLD = 7;

    public final static int T_LASER = 8;

    public final static int T_PANEL = 9;

    public final static int T_STOP = 10;

    public final static int T_RIGHT = 11;

    public final static int T_LEFT = 12;

    public final static int T_UP = 13;

    public final static int T_DOWN = 14;

    public final static int T_ROUGH = 15;

    public final static int T_ELECTRIC = 16;

    public final static int T_ON = 17;

    public final static int T_OFF = 18;

    public final static int T_TRANSPORT = 19;

    public final static int T_BROKEN = 20;

    public final static int T_LR = 21;

    public final static int T_UD = 22;

    public final static int T_0 = 23;

    public final static int T_1 = 24;

    public final static int T_NS = 25;

    public final static int T_NE = 26;

    public final static int T_NW = 27;

    public final static int T_SE = 28;

    public final static int T_SW = 29;

    public final static int T_WE = 30;

    public final static int T_BUTTON = 31;

    public final static int T_BLIGHT = 32;

    public final static int T_RLIGHT = 33;

    public final static int T_GLIGHT = 34;

    public final static int T_BLACK = 35;

    public final static int T_BUP = 36;

    public final static int T_BDOWN = 37;

    public final static int T_RUP = 38;

    public final static int T_RDOWN = 39;

    public final static int T_GUP = 40;

    public final static int T_GDOWN = 41;

    public final static int T_BSPHERE = 42;

    public final static int T_RSPHERE = 43;

    public final static int T_GSPHERE = 44;

    public final static int T_SPHERE = 45;

    public final static int T_TRAP2 = 46;

    public final static int T_TRAP1 = 47;

    public final static int T_BPANEL = 48;

    public final static int T_RPANEL = 49;

    public final static int T_GPANEL = 50;

    public final static int LAST_T = 50;

    /**
     * @return Returns the author.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author
     *            The author to set.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
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

    // member variables

    // metadata
    private String title;

    private String author;

    // width, height
    final int width;

    final int height;

    // location of player
    private int playerX;

    private int playerY;

    // shown
    private final int tiles[];

    // "other" (tiles swapped into bizarro world by panels)
    private final int oTiles[];

    // destinations for transporters and panels (as index into tiles)
    private final int dests[];

    // has a panel (under a pushable block)? etc.
    private final int flags[];

    // dirty
    public final DirtyList dirty;
    
    // cached laser
    private IntTriple laser;


    // the meat
    void warp(int targX, int targY) {
        checkStepOff(playerX, playerY);
        playerX = targX;
        playerY = targY;

        switch (tileAt(targX, targY)) {
        case T_PANEL:
            swapO(destAt(targX, targY));
            break;
        default:
            ;
        }
    }

    IntPair where(int idx) {
        int x = idx % width;
        int y = idx / width;

        return new IntPair(x, y);
    }

    int index(int x, int y) {
        return (y * width) + x;
    }

    public int tileAt(int i) {
        return tiles[i];
    }
    
    public int tileAt(int x, int y) {
        return tiles[y * width + x];
    }

    public int oTileAt(int x, int y) {
        return oTiles[y * width + x];
    }

    void setTile(int i, int t) {
        tiles[i] = t;
        dirty.setDirty(i);
    }

    void setTile(int x, int y, int t) {
        setTile(y * width + x, t);
    }

    void oSetTile(int x, int y, int t) {
        oTiles[y * width + x] = t;
    }

    void setDest(int x, int y, int xd, int yd) {
        dests[y * width + x] = yd * width + xd;
    }

    int destAt(int x, int y) {
        return dests[y * width + x];
    }

    IntPair getDest(int x, int y) {
        int xd = dests[y * width + x] % width;
        int yd = dests[y * width + x] / width;

        return new IntPair(xd, yd);
    }

    int flagAt(int x, int y) {
        return flags[y * width + x];
    }

    public boolean isWon() {
        return tileAt(playerX, playerY) == T_EXIT;
    }

    private IntPair travel(int x, int y, int d) {
        switch (d) {
        case DIR_UP:
            if (y == 0) {
                return null;
            } else {
                return new IntPair(x, y - 1);
            }
        case DIR_DOWN:
            if (y == (height - 1)) {
                return null;
            } else {
                return new IntPair(x, y + 1);
            }
        case DIR_LEFT:
            if (x == 0) {
                return null;
            } else {
                return new IntPair(x - 1, y);
            }
        case DIR_RIGHT:
            if (x == (width - 1)) {
                return null;
            } else {
                return new IntPair(x + 1, y);
            }
        default:
            return null; /* ?? */
        }
    }

    // Return true if a laser can 'see' the player.
    public IntTriple isDead() {
        if (laser != null) {
            return laser;
        }

        // easiest way is to look for lasers from the current dude.
        for (int dd = FIRST_DIR; dd <= LAST_DIR; dd++) {
            int lx = playerX, ly = playerY;

            IntPair r;
            while ((r = travel(lx, ly, dd)) != null) {
                lx = r.x;
                ly = r.y;

                if (tileAt(lx, ly) == T_LASER) {
                    int tileX = r.x;
                    int tileY = r.y;
                    int d = dirReverse(dd);

                    laser = new IntTriple(tileX, tileY, d);
                    return laser;
                }
                int tt = tileAt(lx, ly);
                if (tt != T_FLOOR && tt != T_ELECTRIC && tt != T_ROUGH
                        && tt != T_RDOWN && tt != T_GDOWN && tt != T_BDOWN
                        && tt != T_TRAP2 && tt != T_TRAP1 && tt != T_PANEL
                        && tt != T_BPANEL && tt != T_GPANEL && tt != T_RPANEL
                        && tt != T_BLACK && tt != T_HOLE)
                    break;
            }
        }
        laser = null;
        return null;
    }

    private void swapO(int idx) {
        int tmp = tiles[idx];
        setTile(idx, oTiles[idx]);
        oTiles[idx] = tmp;

        /* swap haspanel/opanel and their refinements as well */
        flags[idx] =

        /* panel bits */
        ((flags[idx] & TF_HASPANEL) != 0 ? TF_OPANEL : TF_NONE)
                | ((flags[idx] & TF_OPANEL) != 0 ? TF_HASPANEL : TF_NONE)
                |

                /* refinement */
                ((flags[idx] & TF_RPANELL) != 0 ? TF_ROPANELL : TF_NONE)
                | ((flags[idx] & TF_RPANELH) != 0 ? TF_ROPANELH : TF_NONE)
                |

                /* orefinement */
                ((flags[idx] & TF_ROPANELL) != 0 ? TF_RPANELL : TF_NONE)
                | ((flags[idx] & TF_ROPANELH) != 0 ? TF_RPANELH : TF_NONE)
                |

                /* erase old */
                (flags[idx] & ~(TF_HASPANEL | TF_OPANEL | TF_RPANELL
                        | TF_RPANELH | TF_ROPANELL | TF_ROPANELH));
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
        checkLeavePanel(x, y);
        if (tileAt(x, y) == T_TRAP1) {
            setTile(x, y, T_HOLE);
        } else if (tileAt(x, y) == T_TRAP2) {
            setTile(x, y, T_TRAP1);
        }
    }

    private static int realPanel(int f) {
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

    private boolean isSphere(int t) {
        return (t == T_SPHERE || t == T_RSPHERE || t == T_GSPHERE || t == T_BSPHERE);
    }

    private void swapTiles(int t1, int t2) {
        for (int i = (width * height) - 1; i >= 0; i--) {
            if (tiles[i] == t1)
                setTile(i, t2);
            else if (tiles[i] == t2)
                setTile(i, t1);
        }
    }

    void clearFlag(int fl) {
        for (int i = (width * height) - 1; i >= 0; i--) {
            flags[i] &= ~fl;
        }
    }

    public boolean move(int d) {
        return move(d, null);
    }

    public boolean move(int d, Effects e) {
        boolean result = realMove(d, e);

        if (result) {
            if (e != null) {
                e.doStep();
            }
        } else {
            if (e != null) {
                e.doNoStep();
            }
        }

        isDead(); // update laser cache
        return result;
    }

    private boolean realMove(int d, Effects e) {

        int target;
        IntPair newP;
        if ((newP = travel(playerX, playerY, d)) != null) {
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
            case T_EXIT: /* now we allow player to walk onto exit */

                checkStepOff(playerX, playerY);
                playerX = newP.x;
                playerY = newP.y;
                return true;

            case T_ON: {
                if (e != null) {
                    e.doElectricOff();
                }
                for (int i = (width * height) - 1; i >= 0; i--) {
                    if (tiles[i] == T_ELECTRIC)
                        setTile(i, T_FLOOR);
                }
                setTile(newP.x, newP.y, T_OFF);
                return true;
            }
            case T_0:
            case T_1: {
                int opp = (target == T_0 ? T_1 : T_0);

                swapTiles(T_UD, T_LR);

                if (e != null) {
                    e.doSwap();
                }
                setTile(newP.x, newP.y, opp);

                return true;
            }

            case T_BSPHERE:
            case T_RSPHERE:
            case T_GSPHERE:
            case T_SPHERE:
            case T_GOLD: {

                /*
                 * spheres allow pushing in a line: ->OOOO becomes OOO ---->O
                 * 
                 * so keep travelling while the tile in the destination
                 * direction is a sphere of any sort.
                 */
                IntPair t;
                while (isSphere(tileAt(newP.x, newP.y))
                        && (t = travel(newP.x, newP.y, d)) != null
                        && isSphere(tileAt(t.x, t.y))) {
                    newP = t;
                    target = tileAt(t.x, t.y);
                }

                int goldX = newP.x, goldY = newP.y;

                /* remove gold block */
                if ((flagAt(goldX, goldY) & TF_HASPANEL) != 0) {
                    setTile(goldX, goldY, realPanel(flagAt(goldX, goldY)));
                } else {
                    setTile(goldX, goldY, T_FLOOR);
                }

                IntPair tGold;
                while ((tGold = travel(goldX, goldY, d)) != null) {

                    int next = tileAt(tGold.x, tGold.y);
                    if (next != T_ELECTRIC && next != T_PANEL
                            && next != T_BPANEL && next != T_RPANEL
                            && next != T_GPANEL && next != T_FLOOR)
                        break;

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

                    switch (landOn) {
                    /*
                     * only the correct color sphere can trigger the colored
                     * panels
                     */
                    case T_GPANEL:
                        if (target == T_GSPHERE) {
                            swapO(destAt(goldX, goldY));
                        }
                        break;
                    case T_BPANEL:
                        if (target == T_BSPHERE) {
                            swapO(destAt(goldX, goldY));
                        }
                        break;
                    case T_RPANEL:
                        if (target == T_RSPHERE) {
                            swapO(destAt(goldX, goldY));
                        }
                        break;
                    case T_PANEL:
                        swapO(destAt(goldX, goldY));
                        break;
                    default:
                        ;
                    }
                    if (e != null) {
                        e.doSlide();
                    }
                    setTile(goldX, goldY, target);

                    if (landOn == T_ELECTRIC) {
                        /*
                         * gold zapped. however, if the electric was the target
                         * of a panel that we just left, the electric has been
                         * swapped into the o world (along with the gold). So
                         * swap there.
                         */
                        if (e != null) {
                            e.doZap();
                        }
                        setTile(goldX, goldY, T_ELECTRIC);
                    }
                    if (doSwap)
                        swapO(destAt(newP.x, newP.y));

                    return true;

                } else {
                    /* didn't move; put it back */
                    setTile(newP.x, newP.y, target);
                    return false;
                }
            }
            case T_TRANSPORT: {
                IntPair targ;
                targ = where(dests[width * newP.y + newP.x]);

                if (e != null) {
                    e.doTransport();
                }
                warp(targ.x, targ.y);

                return true;
            }
            case T_BUTTON: {

                for (int dd = FIRST_DIR; dd <= LAST_DIR; dd++) {
                    /* send a pulse in that direction. */
                    IntPair pulse = newP;
                    int pd = dd;

                    while (pd != DIR_NONE
                            && (pulse = travel(pulse.x, pulse.y, pd)) != null) {
                        switch (tileAt(pulse.x, pulse.y)) {
                        case T_BLIGHT:
                            swapTiles(T_BUP, T_BDOWN);
                            pd = DIR_NONE;
                            break;
                        case T_RLIGHT:
                            swapTiles(T_RUP, T_RDOWN);
                            pd = DIR_NONE;
                            break;
                        case T_GLIGHT:
                            swapTiles(T_GUP, T_GDOWN);
                            pd = DIR_NONE;
                            break;

                        case T_NS:
                            if (pd == DIR_UP || pd == DIR_DOWN)
                                continue;
                            else
                                pd = DIR_NONE;
                            break;

                        case T_WE:
                            if (pd == DIR_LEFT || pd == DIR_RIGHT)
                                continue;
                            else
                                pd = DIR_NONE;
                            break;

                        case T_NW:
                            if (pd == DIR_DOWN)
                                pd = DIR_LEFT;
                            else if (pd == DIR_RIGHT)
                                pd = DIR_UP;
                            else
                                pd = DIR_NONE;
                            break;

                        case T_SW:
                            if (pd == DIR_UP)
                                pd = DIR_LEFT;
                            else if (pd == DIR_RIGHT)
                                pd = DIR_DOWN;
                            else
                                pd = DIR_NONE;
                            break;

                        case T_NE:
                            if (pd == DIR_DOWN)
                                pd = DIR_RIGHT;
                            else if (pd == DIR_LEFT)
                                pd = DIR_UP;
                            else
                                pd = DIR_NONE;
                            break;

                        case T_SE:
                            if (pd == DIR_UP)
                                pd = DIR_RIGHT;
                            else if (pd == DIR_LEFT)
                                pd = DIR_DOWN;
                            else
                                pd = DIR_NONE;
                            break;

                        default:
                            pd = DIR_NONE;
                        }
                    }
                }

                if (e != null) {
                    e.doPulse();
                }
                return true;
            }
            case T_BROKEN:
                setTile(newP.x, newP.y, T_FLOOR);
                if (e != null) {
                    e.doBroken();
                }
                return true;

            case T_PANEL:
                swapO(destAt(newP.x, newP.y));
                checkStepOff(playerX, playerY);
                playerX = newP.x;
                playerY = newP.y;
                return true;

            case T_GREEN: {
                IntPair dest;
                if ((dest = travel(newP.x, newP.y, d)) != null) {
                    if (tileAt(dest.x, dest.y) == T_FLOOR) {
                        setTile(dest.x, dest.y, T_BLUE);
                        setTile(newP.x, newP.y, T_FLOOR);

                        checkStepOff(playerX, playerY);
                        playerX = newP.x;
                        playerY = newP.y;
                        return true;
                    } else
                        return false;
                } else
                    return false;
            }

            /* most pushable blocks use this case */
            case T_RED:
            case T_NS:
            case T_NE:
            case T_NW:
            case T_SE:
            case T_SW:
            case T_WE:

            case T_LR:
            case T_UD:

            case T_GREY: {

                if (target == T_LR && (d == DIR_UP || d == DIR_DOWN))
                    return false;
                if (target == T_UD && (d == DIR_LEFT || d == DIR_RIGHT))
                    return false;

                boolean doSwap = false;
                IntPair dest;
                if ((dest = travel(newP.x, newP.y, d)) != null) {
                    /*
                     * we're always stepping onto the panel that the block was
                     * on, so we don't need to change its state. (if it's a
                     * regular panel, then don't change because our feet are on
                     * it. if it's a colored panel, don't change because neither
                     * the man nor the block can activate it.) But we do need to
                     * put a panel there instead of floor.
                     */
                    int replacement = (flagAt(newP.x, newP.y) & TF_HASPANEL) != 0 ? realPanel(flagAt(
                            newP.x, newP.y))
                            : T_FLOOR;

                    switch (tileAt(dest.x, dest.y)) {
                    case T_FLOOR:
                        /* easy */
                        setTile(dest.x, dest.y, target);
                        setTile(newP.x, newP.y, replacement);
                        break;
                    case T_BPANEL:
                    case T_RPANEL:
                    case T_GPANEL:
                        /* anything but horiz/vert sliders */
                        if (target != T_LR && target != T_UD) {
                            setTile(dest.x, dest.y, target);
                            setTile(newP.x, newP.y, replacement);
                        } else
                            return false;
                        break;
                    case T_ELECTRIC:
                        /* Zap! */
                        if (target != T_LR && target != T_UD) {
                            if (e != null) {
                                e.doZap();
                            }
                            setTile(newP.x, newP.y, replacement);
                        } else
                            return false;
                        break;
                    case T_HOLE:
                        /* only grey blocks into holes */
                        if (target == T_GREY) {
                            if (e != null) {
                                e.doHole();
                            }
                            setTile(dest.x, dest.y, T_FLOOR);
                            setTile(newP.x, newP.y, replacement);
                            break;
                        } else
                            return false;
                    case T_PANEL:
                        if (target != T_LR && target != T_UD) {
                            /* delay the swap */
                            doSwap = true;
                            setTile(dest.x, dest.y, target);
                            setTile(newP.x, newP.y, replacement);
                        } else
                            return false;
                        break;
                    default:
                        return false;
                    }
                    checkStepOff(playerX, playerY);

                    if (doSwap)
                        swapO(destAt(dest.x, dest.y));
                    playerX = newP.x;
                    playerY = newP.y;
                    return true;
                } else
                    return false;
            }

            case T_BLUE:
            case T_HOLE:
            case T_LASER: /* should be dead */
            case T_STOP:
            case T_RIGHT:
            case T_LEFT:
            case T_UP:
            case T_DOWN:
            case T_ELECTRIC:
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
        } else
            return false;
    }

    public Level(BitInputStream in) throws IOException {
        MetaData m = getMetaData(in);

        width = m.width;
        height = m.height;
        
        author = m.author;
        title = m.title;

        playerX = getIntFromStream(in);
        playerY = getIntFromStream(in);

        tiles = RunLengthEncoding.decode(in, width * height);
        oTiles = RunLengthEncoding.decode(in, width * height);
        dests = RunLengthEncoding.decode(in, width * height);
        flags = RunLengthEncoding.decode(in, width * height);

        dirty = new DirtyList();
        dirty.setAllDirty();

        isDead(); // calculate laser cache
    }

    
    public static MetaData getMetaData(BitInputStream in) throws IOException {
        String magic = getStringFromStream(in, 4);
        if (!magic.equals("ESXL")) {
            throw new IOException("Bad magic");
        }

        int width = getIntFromStream(in);
        int height = getIntFromStream(in);

        //        System.out.println("width: " + width + ", height: " + height);

        int size;

        size = getIntFromStream(in);
        String title = getStringFromStream(in, size);

        size = getIntFromStream(in);
        String author = getStringFromStream(in, size);
        
        return new MetaData(width, height, title, author);
    }


    public class DirtyList {
        boolean allDirty;

        private final boolean dirty[];
        private final int dirtyList[];
        private int numDirty;

        DirtyList() {
            int n = width * height;
            dirty = new boolean[n];
            dirtyList = new int[n];
        }

        public void clearDirty() {
            for (int i = dirty.length - 1; i >= 0; i--) {
                dirty[i] = false;
            }
            numDirty = 0;
            allDirty = false;
        }

        public void setDirty(int x, int y) {
            setDirty(index(x, y));
        }
        public void setDirty(int i) {
            if (dirty[i]) {
                return;
            }
            
            dirty[i] = true;
            dirtyList[numDirty] = i;
            numDirty++;
        }
        
        public void setAllDirty() {
            allDirty = true;
        }

        public boolean isDirty(int i) {
            return allDirty || dirty[i];
        }
        
        public boolean isDirty(int x, int y) {
            return allDirty || dirty[index(x, y)];
        }
        
        public boolean isAnyDirty() {
            return allDirty || numDirty > 0;
        }
    }
    
    public void print(PrintStream p) {
        p.println("\"" + title + "\" by " + author + " (" + width + ","
                + height + ")" + " player: (" + playerX + "," + playerY + ")");
        p.println();
        p.println("tiles");
        printM(p, tiles, width);

        p.println();
        p.println("oTiles");
        printM(p, oTiles, width);

        p.println();
        p.println("dests");
        printM(p, dests, width);

        p.println();
        p.println("flags");
        printM(p, flags, width);
    }

    private void printM(PrintStream p, int[] m, int w) {
        int l = 0;
        for (int i = 0; i < m.length; i++) {
            p.print((char) (m[i] + 32));
            l++;
            if (l == w) {
                p.println();
                l = 0;
            }
        }
    }

    private static int getIntFromStream(InputStream in) throws IOException {
        int r = 0;
        r += in.read() << 24;
        r += in.read() << 16;
        r += in.read() << 8;
        r += in.read();

        return r;
    }

    private static String getStringFromStream(InputStream in, int size)
            throws IOException {
        byte buf[] = new byte[size];

        in.read(buf);

        String result = new String(buf);
        return (result);
    }
}