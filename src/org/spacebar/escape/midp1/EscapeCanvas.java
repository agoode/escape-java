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

    private static final Font font = Font.getFont(Font.FACE_MONOSPACE,
            Font.STYLE_PLAIN, Font.SIZE_SMALL);

    //    private static final byte NUM_IMAGES = 4;

    final byte origLevel[];

    Level theLevel;

    //    final static Image tiles[] = ResourceUtil.loadImages("/tiles",
    // NUM_IMAGES, ".png");
    final static Image tiles = ResourceUtil.loadImage("/tiles8x8.png");

    final static Image player = ResourceUtil.loadImage("/player8x8.png");

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
        int sx = tile % TILES_ACROSS * TILE_SIZE;
        int sy = tile / TILES_ACROSS * TILE_SIZE;

        int cx = g.getClipX();
        int cy = g.getClipY();
        int cw = g.getClipWidth();
        int ch = g.getClipHeight();

        //        Image t = tiles[si];

        g.clipRect(0, 0, TILE_SIZE, TILE_SIZE);
        g.drawImage(tiles, -sx, -sy, Graphics.TOP | Graphics.LEFT);
        //        System.out.println("s: " + sx + " " + sy + ", d: " + dx + " " + dy
        //                + ", p:" + px + " " + py);
        g.setClip(cx, cy, cw, ch);
    }

    protected void paint(Graphics g) {
        int h = getHeight();
        int w = getWidth();

        g.setFont(font);

        g.setColor(0, 0, 0);
        g.fillRect(0, 0, w, h);

        int lw = theLevel.getWidth();
        int lh = theLevel.getHeight();

        g.translate((lw - 1) * TILE_SIZE, (lh - 1) * TILE_SIZE);
        int origX = g.getTranslateX();
        for (int j = lh - 1; j >= 0; j--) {
            g.translate(origX - g.getTranslateX(), 0);
            for (int i = lw - 1; i >= 0; i--) {

                int t = theLevel.tileAt(i, j);
                drawTile(g, t);
                g.translate(-TILE_SIZE, 0);
            }
            g.translate(0, -TILE_SIZE);
        }

        g.translate(-g.getTranslateX(), -g.getTranslateY());
        drawPlayer(g);

        g.setColor(255, 255, 255);
        g.translate(-g.getTranslateX(), -g.getTranslateY());
        g.drawString(theLevel.getTitle(), 0, 0, Graphics.TOP | Graphics.LEFT);
    }

    private void drawPlayer(Graphics g) {
        int dx = theLevel.getPlayerX() * TILE_SIZE;
        int dy = theLevel.getPlayerY() * TILE_SIZE;
        int sx = playerDir % 2 * TILE_SIZE;
        int sy = playerDir / 2 * TILE_SIZE;

        //        dx = 0;
        //        dy = 2 * TILE_SIZE;

        g.translate(dx, dy);
        
        int cx = g.getClipX();
        int cy = g.getClipY();
        int cw = g.getClipWidth();
        int ch = g.getClipHeight();

        g.clipRect(0, 0, TILE_SIZE, TILE_SIZE);
        g.drawImage(player, -sx, -sy, Graphics.TOP | Graphics.LEFT);
        g.setClip(cx, cy, cw, ch);
    }
}