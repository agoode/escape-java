package org.spacebar.escape.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.spacebar.escape.common.hash.FNV32;

public class EquateableLevel extends org.spacebar.escape.common.Level {
    public static class HeuristicData {
        public final int map[][];
    
        public final boolean boundaries[][];
    
        public final boolean useless[][];
    
        public HeuristicData(int[][] map, boolean[][] boundaries,
                boolean[][] useless) {
            this.boundaries = copy2D(boundaries);
            this.useless = copy2D(useless);
            this.map = copy2D(map);
        }
    
        private static boolean[][] copy2D(boolean in[][]) {
            boolean r[][] = new boolean[in.length][];
            for (int i = 0; i < r.length; i++) {
                boolean in2[] = in[i];
                boolean r2[] = new boolean[in2.length];
                System.arraycopy(in2, 0, r2, 0, r2.length);
                r[i] = r2;
            }
            return r;
        }
    
        private static int[][] copy2D(int in[][]) {
            int r[][] = new int[in.length][];
            for (int i = 0; i < r.length; i++) {
                int in2[] = in[i];
                int r2[] = new int[in2.length];
                System.arraycopy(in2, 0, r2, 0, r2.length);
                r[i] = r2;
            }
            return r;
        }
    
        public static void printHmap(int hmap[][]) {
            // print
            for (int x = 0; x < hmap[0].length; x++) {
                for (int y = 0; y < hmap.length; y++) {
                    int val = hmap[y][x];
                    String s;
                    if (val < Integer.MAX_VALUE / 2) {
                        s = Integer.toString(val);
                    } else {
                        s = "*";
                    }
                    System.out.print(s + " ");
                    if (s.length() == 1) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
        }
    }

    public EquateableLevel(Level l) {
        super(l);
    }

    public EquateableLevel(BitInputStream in) throws IOException {
        super(in);
    }

    public EquateableLevel(LevelManip manip) {
        super(manip);
    }

    public EquateableLevel(InputStream in, int width, int height)
            throws IOException {
        super(in, width, height);
    }

    @Override
    public int hashCode() {
        // Like Tom,
        /*
         * ignore title, author, w/h, dests, flags, since these don't change.
         * also ignore botd and guyd, which are presentational.
         */

        FNV32 hash = new FNV32();

        // player
        hash.fnv32((byte) player.x);
        hash.fnv32((byte) player.y);

        // tiles, oTiles
        // hash.fnv32(width);
        // hash.fnv32(height);

        for (int i = 0; i < playboard.length; i++) {
            hash.fnv32((byte) playboard[i]);
            // hash.fnv32(tileAt(i));
            // hash.fnv32(oTileAt(i));
            // // hash.fnv32(flags[i]);
            // // hash.fnv32(dests[i]);
        }

        // bots
        // hash.fnv32((byte) bots.length);
        for (int i = 0; i < goodBots.length; i++) {
            Bot b = goodBots[i];
            hash.fnv32(b.getBotType());
            hash.fnv32(b.getBombTimer());
            hash.fnv32((byte) b.x);
            hash.fnv32((byte) b.y);
        }

        for (int i = 0; i < brokenBots.length; i++) {
            Bot b = brokenBots[i];
            hash.fnv32((byte) b.x);
            hash.fnv32((byte) b.y);
        }

        return hash.hval;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof EquateableLevel)) {
            return false;
        }

        EquateableLevel l = (EquateableLevel) obj;

        // entities
        if (!player.equals(l.player)) {
            return false;
        }

        if (goodBots.length != l.goodBots.length
                || brokenBots.length != l.brokenBots.length) {
            return false;
        }

        // good bots
        for (int i = 0; i < goodBots.length; i++) {
            if (!goodBots[i].equals(l.goodBots[i])) {
                return false;
            }
        }
        
        // broken bots
        for (int i = 0; i < brokenBots.length; i++) {
            if (!brokenBots[i].equals(l.brokenBots[i])) {
                return false;
            }
        }

        /*
         * // metadata if (!author.equals(l.author)) { return false; } if
         * (!title.equals(l.title)) { return false; }
         */

        if (width != l.width || playboard.length != l.playboard.length) {
            return false;
        }

        // tiles
        if (playboard == l.playboard) {
            return true;
        }

        for (int i = 0; i < playboard.length; i++) {
            if (playboard[i] != l.playboard[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        int height = getHeight();
        return "[\"" + title + "\" by " + author + " (" + width + "x" + height
                + ")" + " player: (" + this.player.x + ","
                + this.player.y + ")]";
    }

    public HeuristicData computeHeuristicMap() {
        Level l = this;
        boolean hasBombs = hasBombs();
    
        // get number of hugbots, they can push us closer
        int hugbots = 0;
        for (int i = 0; i < l.goodBots.length; i++) {
            int bot = l.goodBots[i].getBotType();
            if (bot == Entity.B_HUGBOT || bot == Entity.B_HUGBOT_ASLEEP) {
                hugbots++;
            }
        }
    
        double hmap[][] = new double[l.getWidth()][l.getHeight()];
        boolean boundaries[][] = new boolean[l.getWidth()][l.getHeight()];
        boolean useless[][] = new boolean[l.getWidth()][l.getHeight()];
    
        int w = l.getWidth();
        int h = l.getHeight();
        boolean panelDests[][] = new boolean[w][h];
        boolean transportDests[][] = new boolean[w][h];
    
        Vector reverseTransDests[][] = new Vector[w][h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                reverseTransDests[x][y] = new Vector();
            }
        }
    
        // initialize
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                hmap[x][y] = Integer.MAX_VALUE / 2; // avoid overflow!!
                int dest = l.destAt(x, y);
                int tx = dest % w;
                int ty = dest / w;
                int t = l.tileAt(x, y);
                int o = l.oTileAt(x, y);
                if (t == T_TRANSPORT || o == T_TRANSPORT) {
                    // XXX see if transport is in bizarro world
                    // but is never a dest itself
                    // System.out.println(x + "," + y + " -> " + tx + "," + ty);
                    transportDests[tx][ty] = true;
                    reverseTransDests[tx][ty]
                            .addElement(new Integer(y * w + x));
                    // System.out.println("revDests: (" + tx + "," + ty + ") <-
                    // ("
                    // + x + "," + y + ")");
    
                }
                if (isPanel(t) || isPanel(o)) {
                    // XXX see if panel is in bizarro world
                    // but is never a dest itself
                    panelDests[tx][ty] = true;
                }
            }
        }
    
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int t = l.tileAt(x, y);
                int o = l.oTileAt(x, y);
                if (!transportDests[x][y] && isImmovableTile(t, hasBombs)
                        && (isImmovableTile(o, hasBombs) || !panelDests[x][y])
                        && !player.isAt(x, y)) {
                    boundaries[x][y] = true;
                }
                if (!transportDests[x][y] && isUselessTile(t, hasBombs)
                        && (isUselessTile(o, hasBombs) || !panelDests[x][y])
                        && !player.isAt(x, y) && !isBotAt(x, y)) {
                    useless[x][y] = true;
                }
            }
        }
    
        // find each exit item
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (isPossibleExit(l, x, y, panelDests[x][y])) {
                    sprinkle(l, hugbots, hmap, 0, boundaries,
                            reverseTransDests, x, y);
                }
            }
        }
    
        // account for transporters XXX lev177
        // need to build reverse dest map and work that way
        // for (int y = 0; y < h; y++) {
        // for (int x = 0; x < w; x++) {
        // Vector revDests = reverseTransDests[x][y];
        // for (int i = 0; i < revDests.size(); i++) {
        // int src = ((Integer) revDests.elementAt(i)).intValue();
        // int xs = src % w;
        // int ys = src / w;
        //
        // sprinkle(l, hugbots, hmap, hmap[x][y], boundaries, xs, ys);
        // }
        // }
        // }
    
        int hmap2[][] = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                hmap2[i][j] = (int) hmap[i][j];
            }
        }
        return new HeuristicData(hmap2, boundaries, useless);
    }

    private void sprinkle(Level l, int hugbots, double[][] hmap, double init,
            boolean[][] boundaries, Vector[][] reverseTransDests, int x, int y) {
        int w = l.width;
        int h = l.getHeight();
    
        hmap[x][y] = init;
        init += 1;
        double seed = init + (1.0 / (hugbots + 1));
    
        Vector revDests = reverseTransDests[x][y];
        for (int i = 0; i < revDests.size(); i++) {
            int src = ((Integer) revDests.elementAt(i)).intValue();
            int xs = src % w;
            int ys = src / w;
            hmap[xs][ys] = init - 1;
            doBrushFire(hmap, l, xs, ys, seed, hugbots + 1, reverseTransDests,
                    boundaries);
        }
    
        if (x != 0) {
            hmap[x - 1][y] = init;
            doBrushFire(hmap, l, x - 1, y, seed, hugbots + 1,
                    reverseTransDests, boundaries);
        }
        if (x != w - 1) {
            hmap[x + 1][y] = init;
            doBrushFire(hmap, l, x + 1, y, seed, hugbots + 1,
                    reverseTransDests, boundaries);
        }
        if (y != 0) {
            hmap[x][y - 1] = init;
            doBrushFire(hmap, l, x, y - 1, seed, hugbots + 1,
                    reverseTransDests, boundaries);
        }
        if (y != h - 1) {
            hmap[x][y + 1] = init;
            doBrushFire(hmap, l, x, y + 1, seed, hugbots + 1,
                    reverseTransDests, boundaries);
        }
    }

    private boolean hasBombs() {
        for (int i = 0; i < goodBots.length; i++) {
            if (goodBots[i].isBomb()) {
                return true;
            }
        }
        return false;
    }

    static private boolean isUselessTile(int t, boolean hasBombs) {
        boolean bombable = hasBombs && isBombable(t);
        return (t == T_BLUE || t == T_STOP || t == T_RIGHT || t == T_LEFT
                || t == T_UP || t == T_DOWN || t == T_OFF || t == T_BLACK)
                && !bombable;
    }

    static private boolean isPossibleExit(Level l, int x, int y,
            boolean isPanelTarget) {
        int t = l.tileAt(x, y);
        int o = l.oTileAt(x, y);
        // XXX check to see if heartframers are accessible ?
        return t == T_EXIT || t == T_SLEEPINGDOOR
                || (isPanelTarget && (o == T_EXIT || o == T_SLEEPINGDOOR));
    }

    private static boolean isImmovableTile(int t, boolean hasBombs) {
        boolean bombable = hasBombs && isBombable(t);
        return (t == T_BLUE || t == T_LASER || t == T_STOP || t == T_RIGHT
                || t == T_LEFT || t == T_UP || t == T_DOWN || t == T_ON
                || t == T_OFF || t == T_0 || t == T_1 || t == T_BUTTON
                || t == T_BLIGHT || t == T_RLIGHT || t == T_GLIGHT || t == T_BLACK)
                && !bombable;
    }

    // !
    static private void doBrushFire(double maze[][], Level l, int x, int y,
            double val, int divisor, Vector[][] reverseTransDests,
            boolean boundaries[][]) {
        Vector revDests = reverseTransDests[x][y];
        int w = l.width;
        for (int i = 0; i < revDests.size(); i++) {
            int src = ((Integer) revDests.elementAt(i)).intValue();
            int xs = src % w;
            int ys = src / w;
            doBrushFire2(maze, l, xs, ys, divisor, reverseTransDests,
                    boundaries, val - (1.0 / divisor)); // subtract because of
            // transporter
        }
        doBrushFire2(maze, l, x, y + 1, divisor, reverseTransDests, boundaries,
                val);
        doBrushFire2(maze, l, x, y - 1, divisor, reverseTransDests, boundaries,
                val);
        doBrushFire2(maze, l, x + 1, y, divisor, reverseTransDests, boundaries,
                val);
        doBrushFire2(maze, l, x - 1, y, divisor, reverseTransDests, boundaries,
                val);
    }

    private static void doBrushFire2(double[][] maze, Level l, int x, int y,
            int divisor, Vector[][] reverseTransDests, boolean[][] boundaries,
            double val) {
        if (x >= 0 && y >= 0 && x < l.getWidth() && y < l.getHeight()
                && !boundaries[x][y] && val < maze[x][y]) {
            maze[x][y] = val;
            // System.out.println("(" + x + "," + y + "): " + val);
            doBrushFire(maze, l, x, y, val + (1.0 / divisor), divisor,
                    reverseTransDests, boundaries);
        }
    }
}
