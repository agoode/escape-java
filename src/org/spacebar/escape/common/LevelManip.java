package org.spacebar.escape.common;

public class LevelManip {
    String title;

    String author;

    int tiles[][];

    int oTiles[][];

    int flags[][];

    int dests[][];

    int w;

    int h;

    Player player;

    Bot bots[];

    public LevelManip(Level l) {
        title = l.getTitle();
        author = l.getAuthor();

        w = l.width;
        h = l.height;

        tiles = new int[w][];
        oTiles = new int[w][];
        flags = new int[w][];
        dests = new int[w][];
        
        for (int x = 0; x < w; x++) {
            int tileCol[] = new int[h];
            int oTileCol[] = new int[h];
            int destCol[] = new int[h];
            int flagCol[] = new int[h];
            for (int y = 0; y < h; y++) {
                tileCol[y] = l.tileAt(x, y);
                oTileCol[y] = l.oTileAt(x, y);
                flagCol[y] = l.flagAt(x, y);
                destCol[y] = l.destAt(x, y);
            }
            tiles[x] = tileCol;
            oTiles[x] = oTileCol;
            flags[x] = flagCol;
            dests[x] = destCol;
        }

        player = new Player(l.player.getX(), l.player.getY(), l.player.getDir());
        bots = new Bot[l.bots.length];
        for (int i = 0; i < bots.length; i++) {
            Bot b = l.bots[i];
            bots[i] = new Bot(b.getX(), b.getY(), b.getDir(), b.getBotType());
        }
    }

    // spreadsheet style reference update
    public void deleteRow(final int row) {
        // row is y
        h--;

        // delete row
        for (int x = 0; x < tiles.length; x++) {
            tiles[x] = deleteFrom(tiles[x], row);
            oTiles[x] = deleteFrom(oTiles[x], row);
            flags[x] = deleteFrom(flags[x], row);
            dests[x] = deleteFrom(dests[x], row);
        }

        // update entities
        slideEntUp(player, row);
        for (int i = 0; i < bots.length; i++) {
            slideEntUp(bots[i], row);
        }

        // update dests
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int dest = dests[x][y];
                int tx = dest % w;
                int ty = dest / w;

                if (ty > row) {
                    ty--;
                    dests[x][y] = ty * w + tx;
                }
            }
        }
    }

    private void slideEntUp(Entity e, final int row) {
        int ey = e.getY();
        if (ey > row) {
            e.setY(ey - 1);
        }
    }

    public void deleteCol(final int col) {
        // col is x
        w--;
        
        // delete col
        tiles = deleteFrom(tiles, col);
        oTiles = deleteFrom(oTiles, col);
        flags = deleteFrom(flags, col);
        dests = deleteFrom(dests, col);

        // update entities
        slideEntLeft(player, col);
        for (int i = 0; i < bots.length; i++) {
            slideEntLeft(bots[i], col);
        }

        // update dests
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int dest = dests[x][y];
                int tx = dest % w;
                int ty = dest / w;

                if (tx > col) {
                    tx--;
                    dests[x][y] = ty * w + tx;
                }
            }
        }
    }

    public void optimize() {
        // find and remove redundant rows and columns
        Level oldLevel = new Level(this);
        
    }
    
    private void slideEntLeft(Entity e, int col) {
        int ex = e.getX();
        if (ex > col) {
            e.setX(ex - 1);
        }
    }

    private static int[][] deleteFrom(int[][] data, int idx) {
        int newData[][] = new int[data.length - 1][];
        int j = 0;
        for (int i = 0; i < newData.length; i++) {
            if (j == idx) {
                j++;
            }

            newData[i] = data[j];

            j++;
        }
        return newData;
    }
    
    private static int[] deleteFrom(int[] data, int idx) {
        int newData[] = new int[data.length - 1];
        int j = 0;
        for (int i = 0; i < newData.length; i++) {
            if (j == idx) {
                j++;
            }

            newData[i] = data[j];

            j++;
        }
        return newData;
    }
}
