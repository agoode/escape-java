/*
 * Created on Dec 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.spacebar.escape.util.Characters;
import org.spacebar.escape.util.IntTriple;
import org.spacebar.escape.util.StyleStack;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class LevelDraw {
    final static int SCALE_DOWN_FACTORS = 5;

    final static int SCALE_UP_FACTORS = 2;

    private final static int TILE_SIZE = 32;

    private final static BufferedImage[] player = ResourceUtils
            .loadScaledImages("player.png", SCALE_DOWN_FACTORS,
                    SCALE_UP_FACTORS);

    private final static BufferedImage[] tiles = ResourceUtils
            .loadScaledImages("tiles.png", SCALE_DOWN_FACTORS, SCALE_UP_FACTORS);

    private final static int TILES_ACROSS = 16;

    private final static BufferedImage font = ResourceUtils
    .loadImage("font.png");

    final static int FONT_HEIGHT = 16;

    final static int FONT_SPACE = 1;

    final static int FONT_WIDTH = 8;

    static double getScaleVal(int scale) {
        double scaleVal;

        if (scale < 0) {
            scaleVal = 1 * (double) (1 << -scale);
        } else {
            scaleVal = 1 / (double) (1 << scale);
        }
        return scaleVal;
    }

    static int getTileSize(int scale) {
        if (scale < 0) {
            return TILE_SIZE * (1 << -scale);
        } else {
            return TILE_SIZE / (1 << scale);
        }
    }

    static int getZoomIndex(int scale) {
        int zoom = scale;
        if (zoom < 0) {
            zoom = -scale + SCALE_DOWN_FACTORS - 1;
        }
        return zoom;
    }

    static public void paintAllLevel(Graphics2D g, Level theLevel, int xScroll,
            int yScroll, boolean showBizarro, int playerDir, int scale) {
        paintLevel(g, theLevel, xScroll, yScroll, showBizarro, scale);
        paintPlayer(g, theLevel, xScroll, yScroll, playerDir, scale);
        paintLaser(g, theLevel, xScroll, yScroll, scale);
    }

    static private void paintLaser(Graphics2D g2, Level theLevel, int xScroll,
            int yScroll, int scale) {
        IntTriple laser = theLevel.isDead();

        if (laser == null) {
            return;
        }

        double scaleVal = getScaleVal(scale);

        int d = laser.getD();

        int gx = (theLevel.getPlayerX() - xScroll) * TILE_SIZE
                + (TILE_SIZE >> 1);
        int gy = (theLevel.getPlayerY() - yScroll) * TILE_SIZE
                + (TILE_SIZE >> 1);

        int lx = (laser.getX() - xScroll) * TILE_SIZE;
        int ly = (laser.getY() - yScroll) * TILE_SIZE;

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

    static private void paintLevel(Graphics2D g2, Level theLevel, int xScroll,
            int yScroll, boolean showBizarro, int scale) {
        int zoom = getZoomIndex(scale);
        int tileSize = getTileSize(scale);
        //        System.out.println("tilesize: " + tileSize);
        //        System.out.println("zoom: " + zoom);

        for (int j = 0; j < theLevel.getHeight() - yScroll; j++) {
            for (int i = 0; i < theLevel.getWidth() - xScroll; i++) {
                int dx = i * tileSize;
                int dy = j * tileSize;

                int tile;
                if (showBizarro) {
                    tile = theLevel.oTileAt(i + xScroll, j + yScroll);
                } else {
                    tile = theLevel.tileAt(i + xScroll, j + yScroll);
                }

                paintTile(g2, zoom, tileSize, dx, dy, tile);
            }
        }
    }

    static private void paintPlayer(Graphics2D g2, Level theLevel, int xScroll,
            int yScroll, int playerDir, int scale) {
        int zoom = getZoomIndex(scale);
        int tileSize = getTileSize(scale);

        int dx = (theLevel.getPlayerX() - xScroll) * tileSize;
        int dy = (theLevel.getPlayerY() - yScroll) * tileSize;

        int sx, sy;
        switch (playerDir) {
        case Level.DIR_LEFT:
            sx = 0;
            sy = 0;
            break;
        case Level.DIR_UP:
            sx = tileSize;
            sy = 0;
            break;
        case Level.DIR_RIGHT:
            sx = tileSize;
            sy = tileSize;
            break;
        case Level.DIR_DOWN:
        default:
            sx = 0;
            sy = tileSize;
            break;
        }

        g2.drawImage(player[zoom], dx, dy, dx + tileSize, dy + tileSize, sx,
                sy, sx + tileSize, sy + tileSize, null);
    }

    static private void paintTile(Graphics2D g2, int zoom, int tileSize,
            int dx, int dy, int tile) {
        int sx = tile % TILES_ACROSS * tileSize;
        int sy = tile / TILES_ACROSS * tileSize;

        g2.drawImage(tiles[zoom], dx, dy, dx + tileSize, dy + tileSize, sx, sy,
                sx + tileSize, sy + tileSize, null);
    }

    static public void drawString(Graphics2D g2, String text) {
        StyleStack s = new StyleStack();
    
        Composite ac = g2.getComposite();
    
        int dx = 0;
        int dy = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '^') {
                i++;
                ch = text.charAt(i);
                switch (ch) {
                case '^':
                    break;
                case '<':
                    s.pop();
                    break;
                default:
                    s.push(ch);
                }
            } else {
                int tile = Characters.getIndexForChar(text.charAt(i));
    
                int sx = tile * (FONT_WIDTH + FONT_SPACE);
                int sy = s.getColor() * (FONT_HEIGHT);
    
                g2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, s.getAlphaValue()));
                g2.drawImage(font, dx, dy, dx + FONT_WIDTH, dy + FONT_HEIGHT,
                        sx, sy, sx + FONT_WIDTH, sy + FONT_HEIGHT, null);
                dx += FONT_WIDTH;
            }
        }
        g2.setComposite(ac);
    }
}