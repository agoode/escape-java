/*
 * Created on Dec 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.spacebar.escape.util.BitInputStream;
import org.spacebar.escape.util.CharacterMap;
import org.spacebar.escape.util.IntTriple;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EscapeLevelView extends JComponent {
    private final static BufferedImage tiles = ResourceUtils
            .loadImage("tiles.png");

    private final static BufferedImage guy = ResourceUtils
            .loadImage("player.png");

    private final static BufferedImage font = ResourceUtils
            .loadImage("font.png");

    static {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferCapabilities bc = gc.getBufferCapabilities();

        System.out.println("page flipping available: " + bc.isPageFlipping());
        System.out
                .println("full screen required: " + bc.isFullScreenRequired());

        System.out.println("tiles: " + tiles);
        System.out.println("guy: " + guy);
        System.out.println("font: " + font);
    }

    final static Effects effects;
    static {
        //        Effects e1 = new NESEffects();
        Effects e2 = new TextEffects();
        CompoundEffects e = new CompoundEffects();
        //        e.add(e1);
        e.add(e2);
        effects = e;
    }

    private final static int TILE_SIZE = 32;

    private final static int TILES_ACROSS = 16;

    private final static int FONT_WIDTH = 8;

    private final static int FONT_SPACE = 1;

    private final static int FONT_HEIGHT = 16;

    private final static int FONT_X_MARGIN = 4;

    private final static int FONT_Y_MARGIN = 2;

    private final static int LEVEL_MARGIN = 12;

    Level theLevel;

    final File levelFile;

    boolean done;

    IntTriple laser;

    private int guyDir;

    private BufferedImage backBuffer;

    int scale = 0;

    double scaleVal = 1.0;

    /**
     * @return Returns the dir.
     */
    public int getGuyDir() {
        return guyDir;
    }

    /**
     * @param dir
     *            The dir to set.
     */
    public void setGuyDir(int dir) {
        if (dir != Level.DIR_DOWN && dir != Level.DIR_LEFT
                && dir != Level.DIR_RIGHT && dir != Level.DIR_UP) {
            throw new IllegalArgumentException("Bad direction");
        }
        this.guyDir = dir;
    }

    public EscapeLevelView(File f) {
        super();

        setOpaque(true);

        levelFile = f;
        initLevel();

        guyDir = Level.DIR_DOWN;

        // setup keys
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "goLeft");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "goDown");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "goRight");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "goUp");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, 0),
                        "scaleUp");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, 0),
                "scaleDown");

        Action a;

        a = new Mover(Level.DIR_DOWN);
        getActionMap().put("goDown", a);
        a = new Mover(Level.DIR_LEFT);
        getActionMap().put("goLeft", a);
        a = new Mover(Level.DIR_RIGHT);
        getActionMap().put("goRight", a);
        a = new Mover(Level.DIR_UP);
        getActionMap().put("goUp", a);
        a = new Scaler(false);
        getActionMap().put("scaleUp", a);
        a = new Scaler(true);
        getActionMap().put("scaleDown", a);
    }

    /**
     * @throws IOException
     * @throws FileNotFoundException
     */
    void initLevel() {
        done = false;
        laser = null;

        try {
            theLevel = new Level(new BitInputStream(new FileInputStream(
                    levelFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        repaint();
    }

    private class Mover extends AbstractAction {
        final int dir;

        public Mover(int dir) {
            this.dir = dir;
        }

        public void actionPerformed(ActionEvent e) {
            if (done) {
                initLevel();
            }

            theLevel.move(dir, effects);
            setGuyDir(dir);
            laser = theLevel.isDead();
            if (laser != null) {
                effects.doLaser();
                System.out.println("dead");
                done = true;
            } else if (theLevel.isWon()) {
                effects.doExit();
                System.out.println("won");
                done = true;
            }
            repairDamage();
        }
    }

    private class Scaler extends AbstractAction {
        final boolean smaller;

        public Scaler(boolean smaller) {
            this.smaller = smaller;
        }

        public void actionPerformed(ActionEvent e) {
            if (smaller) {
                scale++;
                if (scale > 5) {
                    scale = 5;
                }
            } else {
                scale--;
                if (scale < -1) {
                    scale = -1;
                }
            }
            if (scale < 0) {
                scaleVal = 1 * (double) (1 << -scale);
            } else {
                scaleVal = 1 / (double) (1 << scale);
            }

            bufferPaint();
            repaint();
        }
    }

    protected void paintComponent(Graphics g) {
        if (backBuffer == null || backBuffer.getHeight() != getHeight()
                || backBuffer.getWidth() != getWidth()) {
            initBackBuffer();
            bufferPaint();
        }

        g.drawImage(backBuffer, 0, 0, this);
    }

    private void initBackBuffer() {
        System.out.println("initializing back buffer");
        backBuffer = (BufferedImage) createImage(getWidth(), getHeight());
        System.out.println("backBuffer: " + backBuffer);
    }

    void bufferPaint() {
        bufferPaint(null);
    }

    private void bufferPaint(Rectangle clip) {
        Graphics2D g2 = backBuffer.createGraphics();
        g2.clip(clip);

        g2.setBackground(Color.BLACK);
        g2.clearRect(0, 0, backBuffer.getWidth(), backBuffer.getHeight());

        AffineTransform origAT = g2.getTransform();

        g2.translate(LEVEL_MARGIN, LEVEL_MARGIN);

        if (scaleVal < 1.0) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }

        g2.scale(scaleVal, scaleVal);
        paintLevel(g2);
        paintGuy(g2);

        g2.setTransform(origAT);
        g2.translate(LEVEL_MARGIN, LEVEL_MARGIN);
        paintLaser(g2, laser);

        g2.setTransform(origAT);
        g2.translate(FONT_X_MARGIN, FONT_Y_MARGIN);
        paintTitle(g2);

        g2.dispose();
    }

    private void paintTitle(Graphics2D g2) {
        String text = theLevel.getTitle() + " by " + theLevel.getAuthor();

        int dx = 0;
        int dy = 0;
        for (int i = 0; i < text.length(); i++) {
            int ch = CharacterMap.getIndexForChar(text.charAt(i));
            int tile = ch;

            int sx = tile * (FONT_WIDTH + FONT_SPACE);
            int sy = 0;

            g2.drawImage(font, dx, dy, dx + FONT_WIDTH, dy + FONT_HEIGHT, sx,
                    sy, sx + FONT_WIDTH, sy + FONT_HEIGHT, this);
            dx += FONT_WIDTH;
        }
    }

    private void paintGuy(Graphics2D g2) {
        int dx = theLevel.getGuyX() * TILE_SIZE;
        int dy = theLevel.getGuyY() * TILE_SIZE;

        int sx, sy;
        switch (guyDir) {
        case Level.DIR_LEFT:
            sx = 0;
            sy = 0;
            break;
        case Level.DIR_UP:
            sx = TILE_SIZE;
            sy = 0;
            break;
        case Level.DIR_RIGHT:
            sx = TILE_SIZE;
            sy = TILE_SIZE;
            break;
        case Level.DIR_DOWN:
        default:
            sx = 0;
            sy = TILE_SIZE;
            break;
        }

        g2.drawImage(guy, dx, dy, dx + TILE_SIZE, dy + TILE_SIZE, sx, sy, sx
                + TILE_SIZE, sy + TILE_SIZE, this);
    }

    private void paintLevel(Graphics2D g2) {
        for (int j = 0; j < theLevel.getHeight(); j++) {
            for (int i = 0; i < theLevel.getWidth(); i++) {
                int dx = i * TILE_SIZE;
                int dy = j * TILE_SIZE;

                Rectangle clip = g2.getClipBounds();
                if (clip == null
                        || clip.intersects(dx, dy, TILE_SIZE, TILE_SIZE)) {
                    int tile = theLevel.tileAt(i, j);

                    int sx = tile % TILES_ACROSS * TILE_SIZE;
                    int sy = tile / TILES_ACROSS * TILE_SIZE;

                    g2.drawImage(tiles, dx, dy, dx + TILE_SIZE, dy + TILE_SIZE,
                            sx, sy, sx + TILE_SIZE, sy + TILE_SIZE, this);
                }
            }
        }
    }

    private void paintLaser(Graphics2D g2, IntTriple laser) {
        if (laser == null) {
            return;
        }

        int d = laser.getD();

        int gx = theLevel.getGuyX() * TILE_SIZE + (TILE_SIZE >> 1);
        int gy = theLevel.getGuyY() * TILE_SIZE + (TILE_SIZE >> 1);

        int lx = laser.getX() * TILE_SIZE;
        int ly = laser.getY() * TILE_SIZE;

        Rectangle outer, inner;

        switch (d) {
        case Level.DIR_DOWN:
            lx += TILE_SIZE >> 1;
            ly += TILE_SIZE;

            lx *= scaleVal;
            ly *= scaleVal;
            gx *= scaleVal;
            gy *= scaleVal;

            outer = new Rectangle(lx - 1, ly, 3, gy - ly);
            inner = new Rectangle(lx, ly, 1, gy - ly);
            break;
        case Level.DIR_UP:
            lx *= scaleVal;
            ly *= scaleVal;
            gx *= scaleVal;
            gy *= scaleVal;

            outer = new Rectangle(gx - 1, gy + 1, 3, ly - gy);
            inner = new Rectangle(gx, gy + 1, 1, ly - gy);
            break;
        case Level.DIR_RIGHT:
            lx += TILE_SIZE;
            ly += TILE_SIZE >> 1;

            lx *= scaleVal;
            ly *= scaleVal;
            gx *= scaleVal;
            gy *= scaleVal;

            outer = new Rectangle(lx, ly - 1, gx - lx, 3);
            inner = new Rectangle(lx, ly, gx - lx, 1);
            break;
        case Level.DIR_LEFT:
            lx *= scaleVal;
            ly *= scaleVal;
            gx *= scaleVal;
            gy *= scaleVal;

            outer = new Rectangle(gx + 1, gy - 1, lx - gx, 3);
            inner = new Rectangle(gx + 1, gy, lx - gx, 1);
            break;
        default:
            outer = inner = null;
        }

        g2.setColor(Color.RED);
        g2.fill(outer);
        g2.setColor(Color.WHITE);
        g2.fill(inner);
    }

    void repairDamage() {
        //        for (int j = 0; j < theLevel.getHeight(); j++) {
        //            for (int i = 0; i < theLevel.getWidth(); i++) {
        //                if (theLevel.isDirty(i, j)) {
        //                    repaint(getTileBounds(i, j));
        //                }
        //            }
        //        }
        //        // RepaintManager.currentManager(this).paintDirtyRegions();
        bufferPaint();
        repaint();
        theLevel.clearDirty();
    }
}