package org.spacebar.escape;

import java.io.*;

import org.spacebar.escape.util.BitInputStream;
import org.spacebar.escape.util.IntPair;
import org.spacebar.escape.util.IntTriple;
import org.spacebar.escape.util.RunLengthEncoding;

public class Level {

    // directions
    public final static int DIR_NONE = 0;

    public final static int FIRST_DIR = 1;

    public final static int DIR_UP = 1;

    public final static int DIR_DOWN = 2;

    public final static int DIR_RIGHT = 3;

    public final static int DIR_LEFT = 4;

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

    public Object clone() throws CloneNotSupportedException {
        Level newL = new Level(width, height, title, author, guyX, guyY);

        newL.replaceWith(this);

        return newL;
    }

    /**
     * @param newL
     */
    private void replaceWith(Level newL) {
        System.arraycopy(newL.tiles, 0, tiles, 0, tiles.length);
        System.arraycopy(newL.oTiles, 0, oTiles, 0, oTiles.length);
        System.arraycopy(newL.dests, 0, dests, 0, dests.length);
        System.arraycopy(newL.flags, 0, flags, 0, flags.length);
    }

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

    /**
     * @return Returns the guyX.
     */
    public int getGuyX() {
        return guyX;
    }

    /**
     * @return Returns the guyY.
     */
    public int getGuyY() {
        return guyY;
    }

    /**
     * @return Returns the height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return Returns the width.
     */
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
    private final int width;

    private final int height;

    // location of guy
    private int guyX;

    private int guyY;

    // shown
    private final int tiles[];

    // "other" (tiles swapped into bizarro world by panels)
    private final int oTiles[];

    // destinations for transporters and panels (as index into tiles)
    private final int dests[];

    // has a panel (under a pushable block)? etc.
    private final int flags[];

    // the meat
    void warp(int targX, int targY) {
        checkStepOff(guyX, guyY);
        guyX = targX;
        guyY = targY;

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

    int tileAt(int x, int y) {
        return tiles[y * width + x];
    }

    int oTileAt(int x, int y) {
        return oTiles[y * width + x];
    }

    void setTile(int x, int y, int t) {
        tiles[y * width + x] = t;
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

    boolean isWon() {
        return tileAt(guyX, guyY) == T_EXIT;
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
    IntTriple isDead() {
        // easiest way is to look for lasers from the current dude.
        for (int dd = FIRST_DIR; dd <= LAST_DIR; dd++) {
            int lx = guyX, ly = guyY;

            IntPair r;
            while ((r = travel(lx, ly, dd)) != null) {
                lx = r.getX();
                ly = r.getY();

                if (tileAt(lx, ly) == T_LASER) {
                    int tileX = r.getX();
                    int tileY = r.getY();
                    int d = dirReverse(dd);

                    return new IntTriple(tileX, tileY, d);
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
        return null;
    }

    private void swapO(int idx) {
        int tmp = tiles[idx];
        tiles[idx] = oTiles[idx];
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
        for (int i = 0; i < width * height; i++) {
            if (tiles[i] == t1)
                tiles[i] = t2;
            else if (tiles[i] == t2)
                tiles[i] = t1;
        }
    }

    void clearFlag(int fl) {
        for (int i = 0; i < width * height; i++) {
            flags[i] &= ~fl;
        }
    }

    boolean move(int d, Effects e) {
        boolean result = realMove(d, e);
        
        if (result) {
            e.doStep();
        } else {
            e.doNoStep();
        }
        
        return result;
    }
    
    private boolean realMove(int d, Effects e) {

        int target;
        IntPair newP;
        if ((newP = travel(guyX, guyY, d)) != null) {
            switch (target = tileAt(newP.getX(), newP.getY())) {

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

                checkStepOff(guyX, guyY);
                guyX = newP.getX();
                guyY = newP.getY();
                return true;

            case T_ON: {
                e.doElectricOff();
                for (int i = 0; i < width * height; i++) {
                    if (tiles[i] == T_ELECTRIC)
                        tiles[i] = T_FLOOR;
                }
                setTile(newP.getX(), newP.getY(), T_OFF);
                return true;
            }
            case T_0:
            case T_1: {
                e.doSwap();
                
                int opp = (target == T_0 ? T_1 : T_0);

                swapTiles(T_UD, T_LR);

                setTile(newP.getX(), newP.getY(), opp);

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
                while (isSphere(tileAt(newP.getX(), newP.getY()))
                        && (t = travel(newP.getX(), newP.getY(), d)) != null
                        && isSphere(tileAt(t.getX(), t.getY()))) {
                    newP = t;
                    target = tileAt(t.getX(), t.getY());
                }

                int goldX = newP.getX(), goldY = newP.getY();

                /* remove gold block */
                if ((flagAt(goldX, goldY) & TF_HASPANEL) != 0) {
                    setTile(goldX, goldY, realPanel(flagAt(goldX, goldY)));
                } else {
                    setTile(goldX, goldY, T_FLOOR);
                }

                IntPair tGold;
                while ((tGold = travel(goldX, goldY, d)) != null) {

                    int next = tileAt(tGold.getX(), tGold.getY());
                    if (next != T_ELECTRIC && next != T_PANEL
                            && next != T_BPANEL && next != T_RPANEL
                            && next != T_GPANEL && next != T_FLOOR)
                        break;

                    goldX = tGold.getX();
                    goldY = tGold.getY();

                    if (next == T_ELECTRIC)
                        break;

                }

                /* goldx is dest, newx is source */
                if (goldX != newP.getX() || goldY != newP.getY()) {

                    int landOn = tileAt(goldX, goldY);
                    boolean doSwap = false;

                    /* untrigger from source */
                    if ((flagAt(newP.getX(), newP.getY()) & TF_HASPANEL) != 0) {
                        int pan = realPanel(flagAt(newP.getX(), newP.getY()));
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
                    setTile(goldX, goldY, target);

                    if (landOn == T_ELECTRIC) {
                        /*
                         * gold zapped. however, if the electric was the target
                         * of a panel that we just left, the electric has been
                         * swapped into the o world (along with the gold). So
                         * swap there.
                         */
                        setTile(goldX, goldY, T_ELECTRIC);
                    }
                    if (doSwap)
                        swapO(destAt(newP.getX(), newP.getY()));

                    return true;

                } else {
                    /* didn't move; put it back */
                    setTile(newP.getX(), newP.getY(), target);
                    return false;
                }
            }
            case T_TRANSPORT: {
                IntPair targ;
                targ = where(dests[width * newP.getY() + newP.getX()]);

                warp(targ.getX(), targ.getY());

                return true;
            }
            case T_BUTTON: {

                for (int dd = FIRST_DIR; dd <= LAST_DIR; dd++) {
                    /* send a pulse in that direction. */
                    IntPair pulse = newP;
                    int pd = dd;

                    while (pd != DIR_NONE
                            && (pulse = travel(pulse.getX(), pulse.getY(), pd)) != null) {
                        switch (tileAt(pulse.getX(), pulse.getY())) {
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

                return true;
            }
            case T_BROKEN:
                setTile(newP.getX(), newP.getY(), T_FLOOR);
                return true;

            case T_PANEL:
                swapO(destAt(newP.getX(), newP.getY()));
                checkStepOff(guyX, guyY);
                guyX = newP.getX();
                guyY = newP.getY();
                return true;

            case T_GREEN: {
                IntPair dest;
                if ((dest = travel(newP.getX(), newP.getY(), d)) != null) {
                    if (tileAt(dest.getX(), dest.getY()) == T_FLOOR) {
                        setTile(dest.getX(), dest.getY(), T_BLUE);
                        setTile(newP.getX(), newP.getY(), T_FLOOR);

                        checkStepOff(guyX, guyY);
                        guyX = newP.getX();
                        guyY = newP.getY();
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
                if ((dest = travel(newP.getX(), newP.getY(), d)) != null) {
                    /*
                     * we're always stepping onto the panel that the block was
                     * on, so we don't need to change its state. (if it's a
                     * regular panel, then don't change because our feet are on
                     * it. if it's a colored panel, don't change because neither
                     * the man nor the block can activate it.) But we do need to
                     * put a panel there instead of floor.
                     */
                    int replacement = (flagAt(newP.getX(), newP.getY()) & TF_HASPANEL) != 0 ? realPanel(flagAt(
                            newP.getX(), newP.getY()))
                            : T_FLOOR;

                    switch (tileAt(dest.getX(), dest.getY())) {
                    case T_FLOOR:
                        /* easy */
                        setTile(dest.getX(), dest.getY(), target);
                        setTile(newP.getX(), newP.getY(), replacement);
                        break;
                    case T_BPANEL:
                    case T_RPANEL:
                    case T_GPANEL:
                        /* anything but horiz/vert sliders */
                        if (target != T_LR && target != T_UD) {
                            setTile(dest.getX(), dest.getY(), target);
                            setTile(newP.getX(), newP.getY(), replacement);
                        } else
                            return false;
                        break;
                    case T_ELECTRIC:
                        /* Zap! */
                        if (target != T_LR && target != T_UD)
                            setTile(newP.getX(), newP.getY(), replacement);
                        else
                            return false;
                        break;
                    case T_HOLE:
                        /* only grey blocks into holes */
                        if (target == T_GREY) {
                            setTile(dest.getX(), dest.getY(), T_FLOOR);
                            setTile(newP.getX(), newP.getY(), replacement);
                            break;
                        } else
                            return false;
                    case T_PANEL:
                        if (target != T_LR && target != T_UD) {
                            /* delay the swap */
                            doSwap = true;
                            setTile(dest.getX(), dest.getY(), target);
                            setTile(newP.getX(), newP.getY(), replacement);
                        } else
                            return false;
                        break;
                    default:
                        return false;
                    }
                    checkStepOff(guyX, guyY);

                    if (doSwap)
                        swapO(destAt(dest.getX(), dest.getY()));
                    guyX = newP.getX();
                    guyY = newP.getY();
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
        String magic = getStringFromStream(in, 4);
        if (!magic.equals("ESXL")) {
            throw new IOException("Bad magic");
        }

        width = getIntFromStream(in);
        height = getIntFromStream(in);

        int size;

        size = getIntFromStream(in);
        title = getStringFromStream(in, size);

        size = getIntFromStream(in);
        author = getStringFromStream(in, size);

        guyX = getIntFromStream(in);
        guyY = getIntFromStream(in);

        tiles = RunLengthEncoding.decode(in, width * height);
        oTiles = RunLengthEncoding.decode(in, width * height);
        dests = RunLengthEncoding.decode(in, width * height);
        flags = RunLengthEncoding.decode(in, width * height);
    }

    private Level(int w, int h, String title, String author, int guyX, int guyY) {
        this.width = w;
        this.height = h;

        this.title = title;
        this.author = author;

        this.guyX = guyX;
        this.guyY = guyY;

        tiles = new int[width * height];
        oTiles = new int[width * height];
        dests = new int[width * height];
        flags = new int[width * height];
    }

    public void print(PrintStream p) {
        p.println("\"" + title + "\" by " + author + " (" + width + ","
                + height + ")" + " guy: (" + guyX + "," + guyY + ")");
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

    private int getIntFromStream(InputStream in) throws IOException {
        int r = 0;
        r += in.read() << 24;
        r += in.read() << 16;
        r += in.read() << 8;
        r += in.read();

        return r;
    }

    private String getStringFromStream(InputStream in, int size)
            throws IOException {
        byte buf[] = new byte[size];

        in.read(buf);

        String result = new String(buf);
        return (result);
    }

    public static void main(String args[]) {
        File f = new File(args[0]);
        try {
            Level l = new Level(new BitInputStream(new FileInputStream(f)));
            l.print(System.out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}