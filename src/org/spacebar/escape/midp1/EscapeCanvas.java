/*
 * Created on Dec 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.midp1;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.spacebar.escape.common.BitInputStream;
import org.spacebar.escape.common.Level;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EscapeCanvas extends Canvas {

    private static final byte TILES_ACROSS = 16;

    private static final byte TILE_SIZE = 8;

    private static final Font font = Font.getFont(Font.FACE_PROPORTIONAL,
            Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

    //    private static final byte NUM_IMAGES = 4;

    final byte origLevel[];

    Level theLevel;

    //    final static Image tiles[] = ResourceUtil.loadImages("/tiles",
    // NUM_IMAGES, ".png");
    final static Image tiles[];

    final static Image player = ResourceUtil.loadImage("/player8x8i.png");

    static {
        Image temp = ResourceUtil.loadImage("/tiles8x8i.png");
        tiles = ResourceUtil.unpackTiles(temp, TILE_SIZE, TILE_SIZE,
                Level.LAST_T + 1, TILES_ACROSS);
    }

    private byte playerDir;

    EscapeCanvas(byte[] level) throws IOException {
        origLevel = level;
        initLevel();
    }

    void initLevel() throws IOException {
        theLevel = new Level(new BitInputStream(new ByteArrayInputStream(
                origLevel)));
        playerDir = Level.DIR_DOWN;
    }

    static private void drawTile(Graphics g, int tile) {
        //        int si = (tile % TILES_ACROSS) / tiles.length;
        //        int sx = (tile % (TILES_ACROSS / tiles.length)) * TILE_SIZE;

        //        int cx = g.getClipX();
        //        int cy = g.getClipY();
        //        int cw = g.getClipWidth();
        //        int ch = g.getClipHeight();

        //        Image t = tiles[si];

        //        g.clipRect(0, 0, TILE_SIZE, TILE_SIZE);
        g.drawImage(tiles[tile], 0, 0, Graphics.TOP | Graphics.LEFT);

        //        System.out.println("s: " + sx + " " + sy + ", d: " + dx + " " + dy
        //                + ", p:" + px + " " + py);
        //        g.setClip(cx, cy, cw, ch);
    }

    protected void paint(Graphics g) {
        int h = getHeight();
        int w = getWidth();

        g.setColor(0, 0, 0);
        g.fillRect(0, 0, w, h);

        // nudge
        g.translate(0, font.getBaselinePosition());
        drawLevel(g);
        drawPlayer(g);

        g.setFont(font);
        g.setColor(255, 255, 255);
        g.drawString(theLevel.getTitle(), -g.getTranslateX(), -g
                .getTranslateY(), Graphics.TOP | Graphics.LEFT);
    }

    private void drawLevel(Graphics g) {
        int lw = theLevel.getWidth();
        int lh = theLevel.getHeight();

        // skip to end, and go in reverse
        g.translate(lw * TILE_SIZE, lh * TILE_SIZE);
        int origX = g.getTranslateX();
        for (int j = lh - 1; j >= 0; j--) {
            g.translate(origX - g.getTranslateX(), -TILE_SIZE);
            for (int i = lw - 1; i >= 0; i--) {
                g.translate(-TILE_SIZE, 0);
                int t = theLevel.tileAt(i, j);
                drawTile(g, t);
            }
        }
    }

    // assume translation is at 0,0 of level
    private void drawPlayer(Graphics g) {
        int dx = theLevel.getPlayerX() * TILE_SIZE;
        int dy = theLevel.getPlayerY() * TILE_SIZE;

        int sx = 0;
        int sy = 0;

        switch (playerDir) {
        case Level.DIR_LEFT:
            sx = 0;
            sy = 0;
            break;
        case Level.DIR_UP:
            sx = -TILE_SIZE;
            sy = 0;
            break;
        case Level.DIR_DOWN:
            sx = 0;
            sy = -TILE_SIZE;
            break;
        case Level.DIR_RIGHT:
            sx = -TILE_SIZE;
            sy = -TILE_SIZE;
            break;
        }

        g.translate(dx, dy);

        int ch = g.getClipHeight();
        int cw = g.getClipWidth();
        int cx = g.getClipX();
        int cy = g.getClipY();

        g.clipRect(0, 0, TILE_SIZE, TILE_SIZE);
        g.drawImage(player, sx, sy, Graphics.TOP | Graphics.LEFT);
        g.setClip(cx, cy, cw, ch);
    }
}