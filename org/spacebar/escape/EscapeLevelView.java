/*
 * Created on Dec 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.spacebar.escape.util.BitInputStream;
import org.spacebar.escape.util.IntTriple;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EscapeLevelView extends JPanel {
    private final static BufferedImage tiles;

    private final static BufferedImage guy;
    static {
        URL tURL = ResourceUtils.getLocalResource("tiles.png");
        BufferedImage myTiles = null;
        try {
            myTiles = ImageIO.read(tURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tiles = myTiles;

        tURL = ResourceUtils.getLocalResource("player.png");
        BufferedImage myGuy = null;
        try {
            myGuy = ImageIO.read(tURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        guy = myGuy;
    }
    
    final static Effects effects;
    static {
        Effects e1 = new NESEffects();
        Effects e2 = new TextEffects();
        EffectsCombiner e = new EffectsCombiner();
        e.add(e1);
        e.add(e2);
        effects = e;
    }

    private final static int TILE_SIZE = 32;

    private final static int TILES_ACROSS = 16;

    Level theLevel;

    final File levelFile;
    
    boolean done;

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
        
        try {
            theLevel = new Level(new BitInputStream(new FileInputStream(levelFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            IntTriple laser = theLevel.isDead();
            if (laser != null) {
                effects.doLaser();
                System.out.println("dead");
                done = true;
            } else if (theLevel.isWon()) {
                effects.doExit();
                System.out.println("won");
                done = true;
            }
            repaint();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        drawLevel(g2);
        drawGuy(g2);
    }

    private void drawGuy(Graphics2D g2) {
        double x = theLevel.getGuyX() * TILE_SIZE;
        double y = theLevel.getGuyY() * TILE_SIZE;

        AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
        g2.drawRenderedImage(getGuy(), trans);
    }

    private void drawLevel(Graphics2D g2) {
        for (int j = 0; j < theLevel.getHeight(); j++) {
            for (int i = 0; i < theLevel.getWidth(); i++) {
                double x = i * TILE_SIZE;
                double y = j * TILE_SIZE;

                int tile = theLevel.tileAt(i, j);

                AffineTransform trans = AffineTransform.getTranslateInstance(x,
                        y);
                g2.drawRenderedImage(getTile(tile), trans);
            }
        }
    }

    private static BufferedImage getTile(int tile) {
        int x = tile % TILES_ACROSS * TILE_SIZE;
        int y = tile / TILES_ACROSS * TILE_SIZE;

        return tiles.getSubimage(x, y, TILE_SIZE, TILE_SIZE);
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