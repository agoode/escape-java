package org.spacebar.escape;

import org.spacebar.escape.util.IntPair;
import org.spacebar.escape.util.IntTriple;

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
    String title;

    String author;

    // width, height
    int w;

    int h;

    // location of guy
    int guyX;

    int guyY;

    // shown
    int tiles[];

    // "other" (tiles swapped into bizarro world by panels)
    int otiles[];

    // destinations for transporters and panels (as index into tiles)
    int dests[];

    // has a panel (under a pushable block)? etc.
    int flags[];

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
        int x = idx % w;
        int y = idx / w;

        return new IntPair(x, y);
    }

    int index(int x, int y) {
        return (y * w) + x;
    }

    int tileAt(int x, int y) {
        return tiles[y * w + x];
    }

    int oTileAt(int x, int y) {
        return otiles[y * w + x];
    }

    void setTile(int x, int y, int t) {
        tiles[y * w + x] = t;
    }

    void oSetTile(int x, int y, int t) {
        otiles[y * w + x] = t;
    }

    void setDest(int x, int y, int xd, int yd) {
        dests[y * w + x] = yd * w + xd;
    }

    int destAt(int x, int y) {
        return dests[y * w + x];
    }

    IntPair getDest(int x, int y) {
        int xd = dests[y * w + x] % w;
        int yd = dests[y * w + x] / w;

        return new IntPair(xd, yd);
    }

    int flagAt(int x, int y) {
        return flags[y * w + x];
    }

    boolean isWon() {
        return tileAt(guyX, guyY) == T_EXIT;
    }

    IntPair travel(int x, int y, int d) {
        switch (d) {
        case DIR_UP:
            if (y == 0) {
                return null;
            } else {
                return new IntPair(x, y - 1);
            }
        case DIR_DOWN:
            if (y == (h - 1)) {
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
            if (x == (w - 1)) {
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

 
/*
 * void level::swapo(int idx) { int tmp = tiles[idx]; tiles[idx] = otiles[idx];
 * otiles[idx] = tmp;
 *  /* swap haspanel/opanel and their refinements as well flags[idx] =
 *  /* panel bits ((flags[idx]&TF_HASPANEL) ? TF_OPANEL : TF_NONE) |
 * ((flags[idx]&TF_OPANEL) ? TF_HASPANEL : TF_NONE) |
 *  /* refinement ((flags[idx]&TF_RPANELL) ? TF_ROPANELL : TF_NONE) |
 * ((flags[idx]&TF_RPANELH) ? TF_ROPANELH : TF_NONE) |
 *  /* orefinement ((flags[idx]&TF_ROPANELL) ? TF_RPANELL : TF_NONE) |
 * ((flags[idx]&TF_ROPANELH) ? TF_RPANELH : TF_NONE) |
 *  /* erase old (flags[idx] & ~(TF_HASPANEL | TF_OPANEL | TF_RPANELL |
 * TF_RPANELH | TF_ROPANELL | TF_ROPANELH)); }
 *  /* after stepping off a tile, deactivate a panel if there was one there.
 * void level::checkleavepanel(int x, int y) { /* nb: only for regular panels if
 * (tileat(x, y) == T_PANEL) { swapo(destat(x, y)); } }
 *  /* actions on the player stepping off of a tile void level::checkstepoff(int
 * x, int y) { /* nb: only for regular panels checkleavepanel(x, y); if
 * (tileat(x, y) == T_TRAP1) { settile(x, y, T_HOLE); } else if (tileat(x, y) ==
 * T_TRAP2) { settile(x, y, T_TRAP1); } }
 * 
 * static tile realpanel(int f) { if (f & TF_RPANELH) { if (f & TF_RPANELL)
 * return T_RPANEL; else return T_GPANEL; } else { if (f & TF_RPANELL) return
 * T_BPANEL; else return T_PANEL; } }
 * 
 * bool level::issphere(int t) { return (t == T_SPHERE || t == T_RSPHERE || t ==
 * T_GSPHERE || t == T_BSPHERE); }
 * 
 * bool level::move(dir d) {
 * 
 * int newx = 0, newy = 0; int target; if (travel(guyx, guyy, d, newx, newy)) {
 * switch (target = tileat(newx, newy)) {
 *  /* these aren't pressed by the player so act like floor case T_BPANEL: case
 * T_GPANEL: case T_RPANEL:
 *  /* these are only affected when we step *off* case T_TRAP2: case T_TRAP1:
 * 
 * case T_FLOOR: case T_ROUGH: case T_BDOWN: case T_RDOWN: case T_GDOWN: case
 * T_EXIT: /* now we allow player to walk onto exit
 * 
 * checkstepoff(guyx, guyy); guyx = newx; guyy = newy; return true;
 * 
 * case T_ON: { for(int i = 0; i < w * h; i++) { if (tiles[i] == T_ELECTRIC)
 * tiles[i] = T_FLOOR; } settile(newx, newy, T_OFF); return true; } case T_0:
 * case T_1: {
 * 
 * int opp = (target == T_0 ? T_1 : T_0);
 * 
 * swaptiles(T_UD, T_LR);
 * 
 * settile(newx, newy, opp);
 * 
 * return true; }
 * 
 * case T_BSPHERE: case T_RSPHERE: case T_GSPHERE: case T_SPHERE: case T_GOLD: {
 *  /* spheres allow pushing in a line: ->OOOO becomes OOO ---->O
 * 
 * so keep travelling while the tile in the destination direction is a sphere of
 * any sort.
 * 
 * int tnx, tny; while (issphere(tileat(newx, newy)) && travel(newx, newy, d,
 * tnx, tny) && issphere(tileat(tnx, tny))) { newx = tnx; newy = tny; target =
 * tileat(tnx, tny); }
 * 
 * int goldx = newx, goldy = newy;
 *  /* remove gold block if (flagat(goldx, goldy) & TF_HASPANEL) {
 * settile(goldx, goldy, realpanel(flagat(goldx, goldy))); } else {
 * settile(goldx, goldy, T_FLOOR); }
 * 
 * int tgoldx, tgoldy;
 * 
 * while(travel(goldx, goldy, d, tgoldx, tgoldy)) {
 * 
 * int next = tileat(tgoldx, tgoldy); if (next != T_ELECTRIC && next != T_PANEL &&
 * next != T_BPANEL && next != T_RPANEL && next != T_GPANEL && next != T_FLOOR)
 * break;
 * 
 * goldx = tgoldx; goldy = tgoldy;
 * 
 * if (next == T_ELECTRIC) break;
 *  }
 *  /* goldx is dest, newx is source if (goldx != newx || goldy != newy) {
 * 
 * int landon = tileat(goldx, goldy); int doswap = 0;
 *  /* untrigger from source if (flagat(newx, newy) & TF_HASPANEL) { int pan =
 * realpanel(flagat(newx,newy)); /* any if (pan == T_PANEL || /* colors (target ==
 * T_GSPHERE && pan == T_GPANEL) || (target == T_RSPHERE && pan == T_RPANEL) ||
 * (target == T_BSPHERE && pan == T_BPANEL)) doswap = 1; }
 * 
 * switch(landon) { /* only the correct color sphere can trigger the colored
 * panels case T_GPANEL: if (target == T_GSPHERE) { swapo(destat(goldx, goldy)); }
 * break; case T_BPANEL: if (target == T_BSPHERE) { swapo(destat(goldx, goldy)); }
 * break; case T_RPANEL: if (target == T_RSPHERE) { swapo(destat(goldx, goldy)); }
 * break; case T_PANEL: swapo(destat(goldx, goldy)); break; default:; }
 * settile(goldx, goldy, target);
 * 
 * if (landon == T_ELECTRIC) { /* gold zapped. however, if the electric was the
 * target of a panel that we just left, the electric has been swapped into the o
 * world (along with the gold). So swap there. settile(goldx, goldy,
 * T_ELECTRIC); } if (doswap) swapo(destat(newx, newy));
 * 
 * return true;
 *  } else { /* didn't move; put it back settile(newx, newy, target); return
 * false; } } case T_TRANSPORT: { int targx, targy; where(dests[w * newy +
 * newx], targx, targy);
 * 
 * warp(targx, targy);
 * 
 * return true; } case T_BUTTON: {
 * 
 * for(dir dd = FIRST_DIR; dd <= LAST_DIR; dd ++) { /* send a pulse in that
 * direction. int pulsex = newx, pulsey = newy; dir pd = dd;
 * 
 * while(pd != DIR_NONE && travel(pulsex, pulsey, pd, pulsex, pulsey)) { int
 * targ; switch(targ = tileat(pulsex, pulsey)) { case T_BLIGHT: swaptiles(T_BUP,
 * T_BDOWN); pd = DIR_NONE; break; case T_RLIGHT: swaptiles(T_RUP, T_RDOWN); pd =
 * DIR_NONE; break; case T_GLIGHT: swaptiles(T_GUP, T_GDOWN); pd = DIR_NONE;
 * break;
 * 
 * case T_NS: if (pd == DIR_UP || pd == DIR_DOWN) continue; else pd = DIR_NONE;
 * break;
 * 
 * case T_WE: if (pd == DIR_LEFT || pd == DIR_RIGHT) continue; else pd =
 * DIR_NONE; break;
 * 
 * case T_NW: if (pd == DIR_DOWN) pd = DIR_LEFT; else if (pd == DIR_RIGHT) pd =
 * DIR_UP; else pd = DIR_NONE; break;
 * 
 * case T_SW: if (pd == DIR_UP) pd = DIR_LEFT; else if (pd == DIR_RIGHT) pd =
 * DIR_DOWN; else pd = DIR_NONE; break;
 * 
 * case T_NE: if (pd == DIR_DOWN) pd = DIR_RIGHT; else if (pd == DIR_LEFT) pd =
 * DIR_UP; else pd = DIR_NONE; break;
 * 
 * case T_SE: if (pd == DIR_UP) pd = DIR_RIGHT; else if (pd == DIR_LEFT) pd =
 * DIR_DOWN; else pd = DIR_NONE; break;
 * 
 * default: pd = DIR_NONE; } } }
 * 
 * return true; } case T_BROKEN: settile(newx, newy, T_FLOOR); return true;
 * 
 * 
 * case T_PANEL: swapo(destat(newx, newy)); checkstepoff(guyx, guyy); guyx =
 * newx; guyy = newy; return true;
 * 
 * case T_GREEN: { int destx, desty; if (travel(newx, newy, d, destx, desty)) {
 * if (tileat(destx, desty) == T_FLOOR) { settile(destx, desty, T_BLUE);
 * settile(newx, newy, T_FLOOR);
 * 
 * checkstepoff(guyx, guyy); guyx = newx; guyy = newy; return true; } else
 * return false; } else return false; }
 *  /* most pushable blocks use this case case T_RED: case T_NS: case T_NE: case
 * T_NW: case T_SE: case T_SW: case T_WE:
 * 
 * case T_LR: case T_UD:
 * 
 * case T_GREY: {
 * 
 * if (target == T_LR && (d == DIR_UP || d == DIR_DOWN)) return false; if
 * (target == T_UD && (d == DIR_LEFT || d == DIR_RIGHT)) return false;
 * 
 * int doswap = 0; int destx, desty; if (travel(newx, newy, d, destx, desty)) { /*
 * we're always stepping onto the panel that the block was on, so we don't need
 * to change its state. (if it's a regular panel, then don't change because our
 * feet are on it. if it's a colored panel, don't change because neither the man
 * nor the block can activate it.) But we do need to put a panel there instead
 * of floor. int replacement = (flagat(newx, newy) & TF_HASPANEL)?
 * realpanel(flagat(newx,newy)):T_FLOOR;
 * 
 * switch(tileat(destx, desty)) { case T_FLOOR: /* easy settile(destx, desty,
 * target); settile(newx, newy, replacement); break; case T_BPANEL: case
 * T_RPANEL: case T_GPANEL: /* anything but horiz/vert sliders if (target !=
 * T_LR && target != T_UD) { settile(destx, desty, target); settile(newx, newy,
 * replacement); } else return false; break; case T_ELECTRIC: /* Zap! if (target !=
 * T_LR && target != T_UD) settile(newx, newy, replacement); else return false;
 * break; case T_HOLE: /* only grey blocks into holes if (target == T_GREY) {
 * settile(destx, desty, T_FLOOR); settile(newx, newy, replacement); break; }
 * else return false; case T_PANEL: if (target != T_LR && target != T_UD) { /*
 * delay the swap doswap = 1; settile(destx, desty, target); settile(newx, newy,
 * replacement); } else return false; break; default: return false; }
 * checkstepoff(guyx, guyy);
 * 
 * if (doswap) swapo(destat(destx, desty)); guyx = newx; guyy = newy; return
 * true; } else return false; }
 * 
 * case T_BLUE: case T_HOLE: case T_LASER: /* should be dead case T_STOP: case
 * T_RIGHT: case T_LEFT: case T_UP: case T_DOWN: case T_ELECTRIC: case T_BLIGHT:
 * case T_RLIGHT: case T_GLIGHT: case T_RUP: case T_BUP: case T_GUP: case T_OFF:
 * case T_BLACK:
 * 
 * default: return false;
 *  } } else return false; }
 * 
 * 
 * 
 *  /* XXX assumes reasonable dimensions, at least. should instead create empty
 * level if the dimensions are that crazy/nonpositive
 * 
 * bool level::sanitize() { bool was_sane = true;
 * 
 * int len = w * h;
 * 
 * for (int i = 0; i < len; i ++) {
 *  /* a destination outside the level if (dests[i] >= len) { FSDEBUG
 * printf("insane: dest out of range: %d\n", dests[i]); was_sane = false;
 * dests[i] = 0; }
 *  /* an illegal tile if (tiles[i] >= NUM_TILES || tiles[i] < 0) { FSDEBUG
 * printf("insane: bad tile: %d\n", tiles[i]); was_sane = false; tiles[i] =
 * T_BLACK; }
 * 
 * if (otiles[i] >= NUM_TILES || tiles[i] < 0) { FSDEBUG printf("insane: bad
 * otile: %d\n", otiles[i]); was_sane = false; otiles[i] = T_BLACK; }
 *  /* FIXME: also flags }
 * 
 *  /* staring position outside the level if (guyx >= w) { was_sane = false;
 * guyx = w - 1; } if (guyy >= h) { was_sane = false; guyy = h - 1; }
 * 
 * if (guyx < 0) { was_sane = false; guyx = 0; } if (guyy < 0) { was_sane =
 * false; guyy = 0; }
 * 
 * return was_sane; }
 * 
 * level * level::fromstring(string s, bool allow_corrupted) {
 * 
 * int sw, sh;
 *  /* check magic! if (s.substr(0, ((string)LEVELMAGIC).length()) !=
 * LEVELMAGIC) return 0;
 * 
 * 
 * FSDEBUG printf("magic ok...");
 * 
 * unsigned int idx = ((string)LEVELMAGIC).length();
 * 
 * if (idx + 12 > s.length()) return 0; sw = shout(4, s, idx); sh = shout(4, s,
 * idx);
 * 
 * FSDEBUG printf("%d x %d...\n", sw, sh);
 * 
 * int ts = shout(4, s, idx); if (idx + ts > s.length()) return 0; string title =
 * s.substr(idx, ts);
 * 
 * idx += ts;
 * 
 * if (idx + 4 > s.length()) return 0; int as = shout(4, s, idx);
 * 
 * if (idx + as > s.length()) return 0; string author = s.substr(idx, as);
 * 
 * idx += as;
 * 
 * FSDEBUG printf("\"%s\" by \"%s\"...\n", title.c_str(), author.c_str());
 * 
 * if (idx + 8 > s.length()) return 0;
 * 
 * int gx = shout(4, s, idx); int gy = shout(4, s, idx);
 * 
 * FSDEBUG printf("guy: %d,%d\n", gx, gy);
 *  /* at this point we will be able to return some kind of level, even if parts
 * are corrupted
 * 
 * level * l = new level(); l->w = sw; l->h = sh;
 * 
 * l->corrupted = false; /* may change later
 * 
 * l->title = title; l->author = author; l->guyx = gx; l->guyy = gy;
 * 
 * FSDEBUG printf("tiles = rledecode(s, %d, %d)\n", idx, sw * sh);
 * 
 * l->tiles = rledecode(s, idx, sw * sh);
 * 
 * FSDEBUG printf("result %p. now idx is %d\n", l->tiles, idx);
 * 
 * l->otiles = rledecode(s, idx, sw * sh); l->dests = rledecode(s, idx, sw *
 * sh); l->flags = rledecode(s, idx, sw * sh);
 *  /* XXX support messages here.
 * 
 * if (l->tiles && l->otiles && l->dests && l->flags) {
 *  /* check level's sanity, one last time l->corrupted = !l->sanitize();
 * 
 * if (!l->corrupted || allow_corrupted) { FSDEBUG printf("success\n"); return
 * l; } else { l->destroy(); return 0; }
 *  } else if (l->tiles && allow_corrupted) { /* for anything not found, replace
 * with empty if (!l->otiles) { l->otiles = (int*)malloc(sw * sh * sizeof
 * (int)); if (l->otiles) memset(l->otiles, 0, sw * sh * sizeof (int)); }
 * 
 * if (!l->dests) { l->dests = (int*)malloc(sw * sh * sizeof (int)); if
 * (l->dests) memset(l->dests, 0, sw * sh * sizeof (int)); }
 * 
 * if (!l->flags) { l->flags = (int*)malloc(sw * sh * sizeof (int)); if
 * (l->flags) memset(l->flags, 0, sw * sh * sizeof (int)); }
 * 
 * if (l->otiles && l->dests && l->flags) { l->sanitize(); l->corrupted = true;
 * return l; } else { /* out of memory? l->destroy (); return 0; }
 *  } else { l->destroy(); return 0; } }
 * 
 * 
 *  /* RLE compression of integer arrays.
 * 
 * The first byte says how many bits are used to represent integers.
 * 
 * We can use any number of bits (0..32), with the following encoding:
 * 
 * high bit 1: byte & 0b00111111 gives the bit count, which must be <= 32.
 * 
 * high bit 0: then bit count is byte * 8. (this is for backwards compatibility
 * with the old byte-based scheme)
 * 
 * If the bit count is zero, then only the integer zero can be represented, and
 * it is represented by the empty bit string.
 * 
 * If the first and second highest bits are set, then the next 5 bits give us
 * the number of bits we use to read frame headers (called 'framebits' below).
 * Otherwise, this value is assumed to be 8.
 * 
 * Then, we have repeating frames to generate the expected number of ints.
 * 
 * A frame is:
 * 
 * framebits bits representing a run count 1-255, followed by an integer
 * (written with some number of bits, depending on the count above) which means
 * 'count' copies of the integer. The idea is that we can compress runs of equal
 * values.
 * 
 * or
 * 
 * framebits of 0, followed by framebits bits giving an anti-run count 1-255,
 * then 'count' integers each encoded as above. The idea here is to avoid counts
 * of '1' when the values are continually different.
 * 
 * 
 *  /* idx a starting position (measured in bytes) in 'string' for the
 * rle-encoded data, which is modified to point to the next byte after the data
 * if the call is successful. n is the number of integers we sould expect out.
 * int * level::rledecode(string s, unsigned int & idx_bytes, int n) { int * out =
 * (int*)malloc(n * sizeof (int)); int idx = idx_bytes * 8;
 * 
 * if (!out) return 0; extentf <int> eo(out);
 *  /* number of bytes used to represent one integer. unsigned int bytecount; if
 * (!bitbuffer::nbits(s, 8, idx, bytecount)) return 0; int bits;
 * 
 * unsigned int framebits = 8; if (bytecount & 128) { if (bytecount & 64) { if
 * (!bitbuffer::nbits(s, 5, idx, framebits)) return 0; } bits = bytecount & 63; }
 * else { if (bytecount > 4) { printf ("Bad file bytecount %d\n", bytecount);
 * return 0; } bits = bytecount * 8; }
 *  /* printf("bit count: %d\n", bits);
 * 
 * unsigned int run;
 *  /* out index int oi = 0;
 * 
 * while (oi < n) { if (!bitbuffer::nbits(s, framebits, idx, run)) return 0;
 *  /* printf("[%d] run: %d\n", idx, run); if (run == 0) { /* anti-run if
 * (!bitbuffer::nbits(s, framebits, idx, run)) return 0;
 *  /* printf(" .. [%d] anti %d\n", idx, run); for(unsigned int m = 0; m < run;
 * m ++) { unsigned int ch; if (!bitbuffer::nbits(s, bits, idx, ch)) return 0;
 * if (oi >= n) return 0; out[oi++] = ch; } } else { unsigned int ch; if
 * (!bitbuffer::nbits(s, bits, idx, ch)) return 0;
 * 
 * for (unsigned int m = 0; m < run; m ++) { if (oi >= n) return 0; out[oi++] =
 * ch; } } } eo.release (); idx_bytes = bitbuffer::ceil(idx); return out; }
 *  /* encode n ints in 'a', and return it as a string. This uses a greedy
 * strategy that is probably not optimal.
 * 
 * XXX this can be more efficient by using a reduced number of bits to write
 * frame headers. There is already support for this in rledecode
 * 
 * string level::rleencode(int n, int a[]) { int max = 0; for(int j = 0; j < n;
 * j ++) { if (a[j] > max) max = a[j]; }
 *  /* how many bytes to write a single item? (see the discussion at rleencode)
 * 
 * We avoid using "real" compression schemes (such as Huffman encoding) because
 * the overhead of dictionaries can often dwarf the size of what we're encoding
 * (ie, 200 move solutions).
 * 
 * 
 * 
 * int bits = 0;
 *  { unsigned int shift = max; for(int i = 0; i <= 32; i ++) { if (shift & 1)
 * bits = i + 1; shift >>= 1; } }
 *  /* printf("bits needed: %d\n", bits);
 * 
 * bitbuffer ou;
 *  /* new format has high bit set (see rledecode) ou.writebits(8, bits | 128);
 * 
 * enum { /* back == front NOTHING, /* back points to beginning of run RUN, /*
 * back points to beginning of antirun ANTIRUN, /* back points to char, front to
 * next... CHAR, /* done, exit on next loop EXIT, };
 * 
 * int mode = NOTHING; int back = 0, front = 0;
 * 
 * while(mode != EXIT) {
 * 
 * switch(mode) { case NOTHING: assert(back == front);
 * 
 * if (front >= n) mode = EXIT; /* done, no backlog else { mode = CHAR; front++; }
 * break; case CHAR: assert(back == (front-1));
 * 
 * if (front >= n) { /* write a single character ou.writebits(8, 1);
 * ou.writebits(bits, a[back]); mode = EXIT; } else { if (a[front] == a[back]) { /*
 * start run mode = RUN; front ++; } else { /* start antirun mode = ANTIRUN;
 * front ++; } } break; case RUN:
 * 
 * assert((front - back) >= 2); /* from back to front should be same char
 * 
 * if (front >= n || a[front] != a[back]) { /* write run. while ((front - back) >
 * 0) { int x = front - back; if (x > 255) x = 255;
 * 
 * ou.writebits(8, x); ou.writebits(bits, a[back]);
 * 
 * back += x; } if (front >= n) mode = EXIT; else mode = NOTHING; } else
 * front++;
 * 
 * break; case ANTIRUN: assert((front - back) >= 2);
 * 
 * if (front >= n || ((front - back) >= 3 && (a[front] == a[front - 1]) &&
 * (a[front] == a[front - 2]))) {
 * 
 * 
 * if (front >= n) { /* will write tail anti-run below mode = EXIT; } else { /*
 * must be here because we saw a run of 3. we don't want to include this run in
 * the anti-run front -= 2; /* after writing anti-run, we will be with back =
 * front and in NOTHING state, but we will detect a run. mode = NOTHING; }
 *  /* write anti-run, unless there's just one character while ((front - back) >
 * 0) { int x = front - back; if (x > 255) x = 255;
 * 
 * if (x == 1) { ou.writebits(8, 1); ou.writebits(bits, a[back]);
 * 
 * back++; } else { ou.writebits(8, 0); ou.writebits(8, x);
 * 
 * while(x--) { ou.writebits(bits, a[back]); back++; } } } break; } else
 * front++; } }
 * 
 * return ou.getstring(); }
 * 
 * string level::tostring() {
 * 
 * string ou;
 *  /* magic ou += (string)LEVELMAGIC;
 * 
 * ou += sizes(w); ou += sizes(h);
 * 
 * ou += sizes(title.length()); ou += title;
 * 
 * ou += sizes(author.length()); ou += author;
 * 
 * ou += sizes(guyx); ou += sizes(guyy);
 * 
 * ou += rleencode(w * h, tiles); ou += rleencode(w * h, otiles); ou +=
 * rleencode(w * h, dests); ou += rleencode(w * h, flags);
 * 
 * return ou; }
 *  /* deprecated int level::newtile(int old) { switch(old) { case 0x0b: return
 * T_STOP; case 0x0c: return T_RIGHT; case 0x0d: return T_LEFT; case 0x0e:
 * return T_UP; case 0x0f: return T_DOWN; case 0x1A: return T_NS; case 0x1B:
 * return T_NE; case 0x1C: return T_NW; case 0x1D: return T_SE; case 0x1E:
 * return T_SW; case 0x1F: return T_WE; case 0x21: return T_BLIGHT; case 0x22:
 * return T_RLIGHT; case 0x23: return T_GLIGHT; case 0x24: return T_BUP; case
 * 0x25: return T_BDOWN; case 0x26: return T_RUP; case 0x27: return T_RDOWN;
 * case 0x28: return T_GUP; case 0x29: return T_GDOWN; case 10: return T_PANEL;
 * case 16: return T_ROUGH; case 17: return T_ELECTRIC; case 18: return T_ON;
 * case 19: return T_OFF; case 1: return T_FLOOR; case 20: return T_TRANSPORT;
 * case 21: return T_BROKEN; case 22: return T_LR; case 23: return T_UD; case
 * 24: return T_0; case 25: return T_1; case 2: return T_RED; case 32: return
 * T_BUTTON; case 3: return T_BLUE; case 4: return T_GREY; case 5: return
 * T_GREEN; case 6: return T_EXIT; case 7: return T_HOLE; case 8: return T_GOLD;
 * case 0x9: return T_LASER; default: return T_STOP; /* ? } }
 *  /* deprecated level * level::fromoldstring(string s) { if (s.length() !=
 * 567) return 0;
 * 
 * level * n = level::blank(18, 10);
 * 
 * for(int i = 0; i < 180; i ++) { n->tiles[i] = level::newtile(s[i]); /* only
 * regular panels if (n->tiles[i] == T_PANEL) n->flags[i] = TF_HASPANEL; /* else
 * already 0 }
 *  /* otiles always floor
 * 
 * for(int j = 0; j < 180; j ++) { n->dests[j] = 18 * (-1 + (int)s[j+180+180]) +
 * (-1 + (int)s[j+180]); }
 * 
 * n->guyx = s[180+180+180] - 1; n->guyy = s[180+180+180+1] - 1;
 * 
 * n->title = s.substr(180+180+180+2, 25);
 *  /* chop trailing spaces from author int v; for(v = n->title.length() - 1; v >=
 * 0; v --) { if (n->title[v] != ' ') break; } n->title = n->title.substr(0, v +
 * 1);
 * 
 * n->author = "imported";
 * 
 * n->sanitize();
 * 
 * return n; }
 * 
 * void level::destroy() { free(tiles); free(otiles); free(dests); free(flags);
 * delete this; }
 * 
 * level * level::clone() {
 * 
 * level * n = new level();
 * 
 * n->title = title; n->author = author;
 * 
 * n->corrupted = corrupted;
 * 
 * n->h = h; n->w = w; n->guyx = guyx; n->guyy = guyy;
 * 
 * int bytes = w * h * sizeof(int);
 * 
 * n->tiles = (int*)malloc(bytes); n->otiles = (int*)malloc(bytes); n->dests =
 * (int*)malloc(bytes); n->flags = (int*)malloc(bytes);
 * 
 * memcpy(n->tiles, tiles, bytes); memcpy(n->otiles, otiles, bytes);
 * memcpy(n->dests, dests, bytes); memcpy(n->flags, flags, bytes);
 * 
 * return n; }
 * 
 * level * level::blank(int w, int h) { level * n = new level(); n->w = w; n->h =
 * h; n->guyx = 1; n->guyy = 1;
 * 
 * n->corrupted = false;
 * 
 * int bytes = w * h * sizeof(int);
 * 
 * n->tiles = (int*)malloc(bytes); n->otiles = (int*)malloc(bytes); n->dests =
 * (int*)malloc(bytes); n->flags = (int*)malloc(bytes);
 * 
 * for(int i = 0; i < w * h; i ++) { n->tiles[i] = T_FLOOR; n->otiles[i] =
 * T_FLOOR; n->dests[i] = 0; /* 0,0 n->flags[i] = 0; }
 * 
 * return n; }
 * 
 * level * level::defboard(int w, int h) { level * n = blank(w, h);
 *  /* just draw blue around it.
 *  /* top, bottom for(int i = 0; i < w; i++) { n->tiles[i] = T_BLUE;
 * n->tiles[(h-1) * w + i] = T_BLUE; }
 *  /* left, right for(int j = 0; j < h; j++) { n->tiles[j * w] = T_BLUE;
 * n->tiles[j * w + (w-1)] = T_BLUE; }
 * 
 * return n; }
 * 
 * bool level::verify(level * lev, solution * s) { level * l = lev->clone ();
 * 
 * bool won = l->play(s);
 * 
 * l->destroy();
 * 
 * return won; }
 * 
 * bool level::play(solution * s) { for(solution::iter i = solution::iter(s);
 * i.hasnext(); i.next()) {
 * 
 * dir d = i.item ();
 * 
 * if (move(d)) { /* potentially fail *after* each move int dummy; dir dumb; if
 * (isdead(dummy, dummy, dumb)) return false; if (iswon()) return true; } /*
 * else perhaps a 'strict' mode where this solution is rejected } /* solution is
 * over, but we didn't win or die return false; }
 *  /* must be called with sensible sizes (so that malloc won't fail) void
 * level::resize(int neww, int newh) {
 * 
 * int bytes = neww * newh * sizeof(int);
 * 
 * int * nt, * no, * nd, * nf;
 * 
 * nt = (int *)malloc(bytes); no = (int *)malloc(bytes); nd = (int
 * *)malloc(bytes); nf = (int *)malloc(bytes);
 * 
 * for(int x = 0; x < neww; x ++) for(int y = 0; y < newh; y ++) {
 * 
 * if (x < w && y < h) { nt[y * neww + x] = tiles[y * w + x]; no[y * neww + x] =
 * otiles[y * w + x]; nf[y * neww + x] = flags[y * w + x];
 *  /* set dests to point to the same place in the new level, if possible.
 * otherwise just point it to 0,0 int odx = dests[y * w + x] % w; int ody =
 * dests[y * w + x] / w; if (odx < neww && ody < newh && odx >= 0 && ody >= 0) {
 * nd[y * neww + x] = odx + (ody * neww); } else { nd[y * neww + x] = 0; } }
 * else { nt[y * neww + x] = T_FLOOR; no[y * neww + x] = T_FLOOR; nd[y * neww +
 * x] = 0; nf[y * neww + x] = 0; }
 *  }
 * 
 * free(tiles); free(otiles); free(dests); free(flags);
 * 
 * tiles = nt; otiles = no; dests = nd; flags = nf;
 * 
 * w = neww; h = newh;
 *  /* sanitization moves the guy back on the board, as well as making any
 * destinations point within the level sanitize();
 *  }
 */
}