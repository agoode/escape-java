/*
 * Created on Dec 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.midp1;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.microedition.lcdui.*;

import org.spacebar.escape.common.BitInputStream;
import org.spacebar.escape.common.Continuation;
import org.spacebar.escape.common.IntTriple;
import org.spacebar.escape.common.Level;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EscapeCanvas extends Canvas implements CommandListener {

    private static final byte TILES_ACROSS = 16;

    private static final byte TILE_SIZE = 8;

    private static final int TILES_BUFFERED = 8;

    private static final Font font = Font.getFont(Font.FACE_PROPORTIONAL,
            Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

    //    private static final byte NUM_IMAGES = 4;

    boolean done;

    final byte origLevel[];

    final Display display;

    volatile Level theLevel;

    final static Image tiles = ResourceUtil.loadImage("/tiles8x8i.png");

    final static Image player = ResourceUtil.loadImage("/player8x8i.png");

    private Image levelBuffer;

    private int bufW;

    private int bufH;

    private int playerDir;
    
    final private Escape theApp;
    
    final private Continuation theWayOut;

    final Loader loader = new Loader();
    
    EscapeCanvas(byte[] level, Escape m, Continuation c) {
        theApp = m;
        theWayOut = c;
        display = Display.getDisplay(theApp);
        origLevel = level;
        playerDir = Level.DIR_DOWN;

        setCommandListener(this);
        
        addCommand(Escape.RESTART_COMMAND);
        addCommand(Escape.BACK_COMMAND);

        initLevel();
    }

    class Loader implements Runnable {
        public void run() {
            new Thread() {
                public void run() {
                    try {
                        theLevel = new Level(new BitInputStream(
                                new ByteArrayInputStream(origLevel)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    done = false;
                    repaint();
                }
            }.start();
        }
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
        int w = getWidth();
        int h = getHeight();

        if (theLevel == null) {
            paintLoading(g, w, h);
            return;
        }

        if (levelBuffer == null) {
            initLevelBuffer();
        }

        g.setColor(0, 0, 0);
        g.fillRect(0, 0, w, h);

        if (theLevel.dirty.isAnyDirty()) {
            drawLevel();
        }

        // nudge
        g.translate(-TILE_SIZE / 2, font.getBaselinePosition());

        g.drawImage(levelBuffer, 0, 0, Graphics.TOP | Graphics.LEFT);
        drawPlayer(g);
        drawLaser(g);

        g.setFont(font);
        g.setColor(255, 255, 255);
        g.drawString(theLevel.getTitle(), -g.getTranslateX(), -g
                .getTranslateY(), Graphics.TOP | Graphics.LEFT);
    }

    private void paintLoading(Graphics g, int w, int h) {
        g.setColor(0, 0, 0);
        g.fillRect(0, 0, w, h);

        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,
                Font.SIZE_LARGE));
        g.setColor(13, 147, 209);
        g.drawString("Loading", w / 2, h / 2, Graphics.BASELINE
                | Graphics.HCENTER);
    }

    private void initLevelBuffer() {
        int w = getWidth();
        int h = getHeight();

        int tilesAcross = w / TILE_SIZE;
        int tilesDown = h / TILE_SIZE;

        // allocate either the size of the level or enough for the
        // screen + more
        bufW = Math.min(theLevel.getWidth(), tilesAcross + TILES_BUFFERED);
        bufH = Math.min(theLevel.getHeight(), tilesDown + TILES_BUFFERED);
        levelBuffer = Image.createImage(bufW * TILE_SIZE, bufH * TILE_SIZE);

        // clear
        clearLevelBuffer();
    }

    private void clearLevelBuffer() {
        Graphics g = levelBuffer.getGraphics();
        g.setColor(0);
        g.fillRect(0, 0, bufW * TILE_SIZE, bufH * TILE_SIZE);
    }

    private void drawLevel() {
        Level theLevel = this.theLevel;

        int lw = theLevel.getWidth();
        int lh = theLevel.getHeight();

        int n = lh * lw;

        Level.DirtyList dirty = theLevel.dirty;
        Graphics g = levelBuffer.getGraphics();

        int w = lw * TILE_SIZE;
        int h = lh * TILE_SIZE;

        // skip to end, and go in reverse
        g.translate(0, h);

        for (int i = n - 1; i >= 0; i--) {
            if (i % lw == lw - 1) {
                // back to the right and up one
                g.translate(w, -TILE_SIZE);
            }
            g.translate(-TILE_SIZE, 0);
            if (dirty.isDirty(i)) {
                int t = theLevel.tileAt(i);
                drawTile(g, t);
            }
        }
        theLevel.dirty.clearDirty();
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

        g.translate(-dx, -dy);
    }

    private void drawLaser(Graphics g) {
        IntTriple laser = theLevel.isDead();
        if (laser == null) {
            return;
        }

        int lx = laser.x * TILE_SIZE + TILE_SIZE / 2;
        int ly = laser.y * TILE_SIZE + TILE_SIZE / 2;
        int px = theLevel.getPlayerX() * TILE_SIZE + TILE_SIZE / 2;
        int py = theLevel.getPlayerY() * TILE_SIZE + TILE_SIZE / 2;

        switch (laser.d) {
        case Level.DIR_DOWN:
            ly += TILE_SIZE / 2;
            break;
        case Level.DIR_UP:
            ly -= TILE_SIZE / 2;
            break;
        case Level.DIR_RIGHT:
            lx += TILE_SIZE / 2;
            break;
        case Level.DIR_LEFT:
            lx -= TILE_SIZE / 2;
            break;
        }

        g.setColor(255, 0, 0);
        g.drawLine(lx, ly, px, py);
    }

    protected void keyPressed(int keyCode) {
        switch (getGameAction(keyCode)) {
        case LEFT:
            doMove(Level.DIR_LEFT);
            break;
        case RIGHT:
            doMove(Level.DIR_RIGHT);
            break;
        case UP:
            doMove(Level.DIR_UP);
            break;
        case DOWN:
            doMove(Level.DIR_DOWN);
            break;
        case FIRE:
            initLevel();
            break;
        }
    }

    private void initLevel() {
        theLevel = null;
        display.callSerially(loader);
        repaint();
    }

    private void doMove(int dir) {
        if (theLevel == null || done) {
            return;
        }

        playerDir = dir;

        theLevel.move(dir, null);

        if (theLevel.isDead() != null) {
            done = true;
            display.callSerially(new Runnable() {
                public void run() {
                    AlertType.ERROR.playSound(display);
                }
            });
        } else if (theLevel.isWon()) {
            done = true;
            display.callSerially(new Runnable() {
                public void run() {
                    AlertType.INFO.playSound(display);
                }
            });
        }
        repaint();
    }

    protected void keyRepeated(int keyCode) {
        keyPressed(keyCode);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == Escape.RESTART_COMMAND) {
            initLevel();
        } else if (c == Escape.EXIT_COMMAND) {
            theWayOut.invoke();
        }
    }
}