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

    final static Image tiles = ResourceUtil.loadImage("/tiles8x8i.png");

    final static Image player = ResourceUtil.loadImage("/player8x8i.png");

    private Image levelBuffer;
    private int bufW;
    private int bufH;
    
    private boolean needsRedraw;
    
    private byte playerDir;

    EscapeCanvas(byte[] level) throws IOException {
        origLevel = level;
        initLevel();
    }

    void initLevel() throws IOException {
        theLevel = new Level(new BitInputStream(new ByteArrayInputStream(
                origLevel)));
        playerDir = Level.DIR_DOWN;
        needsRedraw = true;
    }

    // clobbers clip
    static private void drawTile(Graphics g, int tile) {
        int sx = tile % TILES_ACROSS * TILE_SIZE;
        int sy = tile / TILES_ACROSS * TILE_SIZE;

//        int cx = g.getClipX();
//        int cy = g.getClipY();
//        int cw = g.getClipWidth();
//        int ch = g.getClipHeight();


        g.setClip(0, 0, TILE_SIZE, TILE_SIZE);
        g.drawImage(tiles, -sx, -sy, Graphics.TOP | Graphics.LEFT);

        //        System.out.println("s: " + sx + " " + sy + ", d: " + dx + " " + dy
        //                + ", p:" + px + " " + py);
    }

    protected void paint(Graphics g) {
        if (levelBuffer == null) {
            initLevelBuffer();
        }

        if (needsRedraw) {
            drawLevel();
        }
        
        int h = getHeight();
        int w = getWidth();

        g.setColor(0, 0, 0);
        g.fillRect(0, 0, w, h);

        // nudge
        g.translate(-TILE_SIZE / 2, font.getBaselinePosition());
        g.drawImage(levelBuffer, 0, 0, Graphics.TOP | Graphics.LEFT);
        drawPlayer(g);

        g.setFont(font);
        g.setColor(255, 255, 255);
        g.drawString(theLevel.getTitle(), -g.getTranslateX(), -g
                .getTranslateY(), Graphics.TOP | Graphics.LEFT);
    }

    private void initLevelBuffer() {
        bufW = Math.min(theLevel.getWidth() * TILE_SIZE, getWidth() * 2);
        bufH = Math.min(theLevel.getHeight() * TILE_SIZE, getHeight() * 2);
        levelBuffer = Image.createImage(bufW, bufH);
        
        // clear
        Graphics g = levelBuffer.getGraphics();
        g.setColor(0);
        g.fillRect(0, 0, bufW, bufH);
    }
    
    private void drawLevel() {
        int lw = theLevel.getWidth() - 1;
        int lh = theLevel.getHeight() - 1;

        Graphics g = levelBuffer.getGraphics();
        
        // skip to end, and go in reverse
        g.translate((lw + 1) * TILE_SIZE, (lh + 1) * TILE_SIZE);
        int origX = g.getTranslateX();
        for (int j = lh; j >= 0; j--) {
            g.translate(origX - g.getTranslateX(), -TILE_SIZE);
            for (int i = lw; i >= 0; i--) {
                g.translate(-TILE_SIZE, 0);
                int t = theLevel.tileAt(i, j);
                drawTile(g, t);
            }
        }
        
        needsRedraw = false;
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