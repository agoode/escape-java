/*
 * Created on Dec 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

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
        ClassLoader c = EscapeMain.class.getClassLoader();
        URL tURL = c.getResource("org/spacebar/escape/resources/tiles.png");
        BufferedImage myTiles = null;
        try {
            myTiles = ImageIO.read(tURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tiles = myTiles;

        tURL = c.getResource("org/spacebar/escape/resources/player.png");
        BufferedImage myGuy = null;
        try {
            myGuy = ImageIO.read(tURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        guy = myGuy;
    }

    private final static int TILE_SIZE = 32;

    private final static int TILES_ACROSS = 16;

    private final Level theLevel;

    private int dir;

    /**
     * @return Returns the dir.
     */
    public int getDir() {
        return dir;
    }

    /**
     * @param dir
     *            The dir to set.
     */
    public void setDir(int dir) {
        if (dir != Level.DIR_DOWN && dir != Level.DIR_LEFT
                && dir != Level.DIR_RIGHT && dir != Level.DIR_UP) {
            throw new IllegalArgumentException("Bad direction");
        }
        this.dir = dir;
    }

    public EscapeLevelView(Level l) {
        super();
        theLevel = l;
        dir = Level.DIR_DOWN;
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

        switch (dir) {
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
        case Level.DIR_DOWN:
        default:
            x = 0;
            y = TILE_SIZE;
            break;
        }
        
        return guy.getSubimage(x, y, TILE_SIZE, TILE_SIZE);
    }
}