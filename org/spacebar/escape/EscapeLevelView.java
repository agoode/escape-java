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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;

import org.spacebar.escape.util.BitInputStream;
import org.spacebar.escape.util.CharacterMap;
import org.spacebar.escape.util.IntTriple;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EscapeLevelView extends JPanel {
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
        e.add(e1);
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
        setBackground(Color.BLACK);

        levelFile = f;
        initLevel();

        guyDir = Level.DIR_DOWN;

        // setup keys
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "goLeft");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "goDown");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "goRight");
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
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
                repaint();
            } else if (theLevel.isWon()) {
                effects.doExit();
                System.out.println("won");
                done = true;
            }
            repairDamage();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        AffineTransform trans = g2.getTransform();

        g2.translate(LEVEL_MARGIN, LEVEL_MARGIN);
        paintLevel(g2);
        paintGuy(g2);
        paintLaser(g2, laser);

        g2.setTransform(trans);

        g2.translate(FONT_X_MARGIN, FONT_Y_MARGIN);
        paintTitle(g2);
    }

    private void paintTitle(Graphics2D g2) {
        String text = theLevel.getTitle() + " by " + theLevel.getAuthor();

        AffineTransform a = AffineTransform.getTranslateInstance(0, 0);
        for (int i = 0; i < text.length(); i++) {
            int ch = CharacterMap.getIndexForChar(text.charAt(i));
            g2.drawRenderedImage(getFontTile(ch), a);
            a.translate(FONT_WIDTH, 0);
        }
    }

    private void paintGuy(Graphics2D g2) {
        double x = theLevel.getGuyX() * TILE_SIZE;
        double y = theLevel.getGuyY() * TILE_SIZE;

        AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
        g2.drawRenderedImage(getGuy(), trans);
    }

    private void paintLevel(Graphics2D g2) {
        for (int j = 0; j < theLevel.getHeight(); j++) {
            for (int i = 0; i < theLevel.getWidth(); i++) {
                double x = i * TILE_SIZE;
                double y = j * TILE_SIZE;

                Rectangle clip = g2.getClipBounds();
                if (clip.intersects(x, y, TILE_SIZE, TILE_SIZE)) {
                    int tile = theLevel.tileAt(i, j);

                    AffineTransform trans = AffineTransform
                            .getTranslateInstance(x, y);
                    g2.drawRenderedImage(getTile(tile), trans);
                }
            }
        }
    }

    private void paintLaser(Graphics2D g2, IntTriple laser) {
        if (laser == null) {
            return;
        }

        int d = laser.getD();

        float gx = theLevel.getGuyX() * TILE_SIZE + TILE_SIZE * 0.5f;
        float gy = theLevel.getGuyY() * TILE_SIZE + TILE_SIZE * 0.5f;

        float lx = laser.getX() * TILE_SIZE;
        float ly = laser.getY() * TILE_SIZE;

        Rectangle2D.Float outer, inner;
        switch (d) {
        case Level.DIR_DOWN:
            lx += TILE_SIZE * 0.5;
            ly += TILE_SIZE;
            outer = new Rectangle2D.Float(lx - 1, ly, 3, gy - ly);
            inner = new Rectangle2D.Float(lx, ly, 1, gy - ly);
            break;
        case Level.DIR_UP:
            outer = new Rectangle2D.Float(gx - 1, gy + 1, 3, ly - gy);
            inner = new Rectangle2D.Float(gx, gy + 1, 1, ly - gy);
            break;
        case Level.DIR_RIGHT:
            lx += TILE_SIZE;
            ly += TILE_SIZE * 0.5;
            outer = new Rectangle2D.Float(lx, ly - 1, gx - lx, 3);
            inner = new Rectangle2D.Float(lx, ly, gx - lx, 1);
            break;
        case Level.DIR_LEFT:
            outer = new Rectangle2D.Float(gx + 1, gy - 1, lx - gx, 3);
            inner = new Rectangle2D.Float(gx + 1, gy, lx - gx, 1);
            break;
        default:
            inner = outer = null;
        }
        g2.setColor(Color.RED);
        g2.fill(outer);
        g2.setColor(Color.WHITE);
        g2.fill(inner);
    }

    void repairDamage() {
        for (int j = 0; j < theLevel.getHeight(); j++) {
            for (int i = 0; i < theLevel.getWidth(); i++) {
                if (theLevel.isDirty(i, j)) {
                    repaint(getTileBounds(i, j));
                }
            }
        }
        RepaintManager.currentManager(this).paintDirtyRegions();
        theLevel.clearDirty();
    }

    private Rectangle getTileBounds(int x, int y) {
        int w = TILE_SIZE;
        int h = TILE_SIZE;
        int myX = LEVEL_MARGIN + x * TILE_SIZE;
        int myY = LEVEL_MARGIN + y * TILE_SIZE;

        return new Rectangle(myX, myY, w, h);
    }

    private static BufferedImage getTile(int tile) {
        int x = tile % TILES_ACROSS * TILE_SIZE;
        int y = tile / TILES_ACROSS * TILE_SIZE;

        return tiles.getSubimage(x, y, TILE_SIZE, TILE_SIZE);
    }

    private static BufferedImage getFontTile(int c) {
        int tile = c;

        int x = tile * (FONT_WIDTH + FONT_SPACE);
        int y = 0;

        System.out.println(c + " " + x + " " + y + " " + tile);

        return font.getSubimage(x, y, FONT_WIDTH + FONT_SPACE, FONT_HEIGHT);
    }

    private BufferedImage getGuy() {
        int x;
        int y;

        switch (guyDir) {
        case Level.DIR_LEFT:
            x = 0;
            y = 0;
            break;
        case Level.DIR_UP:
            x = TILE_SIZE;
            y = 0;
            break;
        case Level.DIR_RIGHT:
            x = TILE_SIZE;
            y = TILE_SIZE;
            break;
        case Level.DIR_DOWN:
        default:
            x = 0;
            y = TILE_SIZE;
            break;
        }

        return guy.getSubimage(x, y, TILE_SIZE, TILE_SIZE);
    }
}