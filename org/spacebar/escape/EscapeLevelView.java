/*
 * Created on Dec 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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

    final static Effects effects;
    static {
        Effects e1 = new NESEffects();
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

        Action m;

        m = new Mover(Level.DIR_DOWN);
        getActionMap().put("goDown", m);
        m = new Mover(Level.DIR_LEFT);
        getActionMap().put("goLeft", m);
        m = new Mover(Level.DIR_RIGHT);
        getActionMap().put("goRight", m);
        m = new Mover(Level.DIR_UP);
        getActionMap().put("goUp", m);
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

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (backBuffer == null
                || (backBuffer.getHeight() != getHeight() && backBuffer
                        .getWidth() != getWidth())) {
            backBuffer = (BufferedImage) createImage(getWidth(), getHeight());
            Graphics2D backG = backBuffer.createGraphics();
            bufferPaint(backG);
            backG.dispose();
        }

        // blit
        g2.drawImage(backBuffer, 0, 0, this);
    }

    private void bufferPaint(Graphics2D g2) {
        AffineTransform trans = g2.getTransform();

        g2.translate(LEVEL_MARGIN, LEVEL_MARGIN);
        paintLevel(g2);
        paintGuy(g2);
        paintLaser(g2, laser);

        g2.setTransform(trans);

        g2.translate(FONT_X_MARGIN, FONT_Y_MARGIN);
        paintTitle(g2);
        g2.setTransform(trans);
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
            outer = new Rectangle(lx - 1, ly, 3, gy - ly);
            inner = new Rectangle(lx, ly, 1, gy - ly);
            break;
        case Level.DIR_UP:
            outer = new Rectangle(gx - 1, gy + 1, 3, ly - gy);
            inner = new Rectangle(gx, gy + 1, 1, ly - gy);
            break;
        case Level.DIR_RIGHT:
            lx += TILE_SIZE;
            ly += TILE_SIZE >> 1;
            outer = new Rectangle(lx, ly - 1, gx - lx, 3);
            inner = new Rectangle(lx, ly, gx - lx, 1);
            break;
        case Level.DIR_LEFT:
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
//        //        RepaintManager.currentManager(this).paintDirtyRegions();
        Graphics2D g2 = backBuffer.createGraphics();
        bufferPaint(g2);
        g2.dispose();
        repaint();
        theLevel.clearDirty();
    }
}