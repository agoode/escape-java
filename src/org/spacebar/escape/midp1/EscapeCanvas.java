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

    private static final int TILES_ACROSS = 16;

    private static final int TILE_SIZE = 16;

    final byte origLevel[];

    Level theLevel;

    final static Image tiles[] = ResourceUtil.loadImages("/tiles", 4, ".png");

    final static Image player = ResourceUtil.loadImage("/player.png");

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

    static private void drawTile(Graphics g, int tile, int dx, int dy) {
        int si = tile / tiles.length;
        int sx = (tile % (TILES_ACROSS / tiles.length)) * TILE_SIZE;
        int sy = tile / TILES_ACROSS * TILE_SIZE;

        int px = dx - sx;
        int py = dy - sy;
        
        int cx = g.getClipX();
        int cy = g.getClipY();
        int cw = g.getClipWidth();
        int ch = g.getClipHeight();
        
        Image t = tiles[si];
        
        g.clipRect(dx, dy, TILE_SIZE, TILE_SIZE);
        g.drawImage(t, px, py, Graphics.TOP|Graphics.LEFT);
//        System.out.println("s: " + sx + " " + sy + ", d: " + dx + " " + dy
//                + ", p:" + px + " " + py);
        g.setClip(cx, cy, cw, ch);
    }

    protected void paint(Graphics g) {
        int h = getHeight();
        int w = getWidth();

        g.setColor(0, 0, 0);
        g.fillRect(0, 0, w, h);
        
        int lh = theLevel.getHeight();
        int lw = theLevel.getWidth();

        for (int j = 0; j < lh; j++) {
            for (int i = 0; i < lw; i++) {
                int dx = i * TILE_SIZE;
                int dy = j * TILE_SIZE;

                int t = theLevel.tileAt(i, j);
                drawTile(g, t, dx, dy);
            }
        }
        drawPlayer(g);

        g.setColor(255, 255, 255);
        g.drawString(theLevel.getTitle(), 0, 0, Graphics.TOP|Graphics.LEFT);
    }

    private void drawPlayer(Graphics g) {
        int dx = theLevel.getPlayerX() * TILE_SIZE;
        int dy = theLevel.getPlayerY() * TILE_SIZE;
        int sx = playerDir % 2 * TILE_SIZE;
        int sy = playerDir / 2 * TILE_SIZE;

//        dx = 0;
//        dy = 2 * TILE_SIZE;
        
        int cx = g.getClipX();
        int cy = g.getClipY();
        int cw = g.getClipWidth();
        int ch = g.getClipHeight();
        
        g.clipRect(dx, dy, TILE_SIZE, TILE_SIZE);
        g.drawImage(player, dx - sx, dy - sy, Graphics.TOP|Graphics.LEFT);
        g.setClip(cx, cy, cw, ch);
    }
}