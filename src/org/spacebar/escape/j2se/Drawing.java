/*
 * Created on Dec 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.j2se;

import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.spacebar.escape.common.Characters;
import org.spacebar.escape.common.IntTriple;
import org.spacebar.escape.common.Level;
import org.spacebar.escape.common.StyleStack;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Drawing {
    final static int SCALE_DOWN_FACTORS = 5;

    final static int SCALE_UP_FACTORS = 2;

    private final static int TILE_SIZE = 32;

    private final static int PLAYER_FRAMES = 5;

    private final static BufferedImage[] player;
    static {
        String names[] = { "animation/walk_forward_0.png",
                "animation/walk_forward_a.png",
                "animation/walk_forward_a2.png",
                "animation/walk_forward_b.png",
                "animation/walk_forward_b2.png",

                "animation/walk_right_0.png", "animation/walk_right_a.png",
                "animation/walk_right_a2.png", "animation/walk_right_b.png",
                "animation/walk_right_b2.png",

                "animation/walk_left_0.png", "animation/walk_left_a.png",
                "animation/walk_left_a2.png", "animation/walk_left_b.png",
                "animation/walk_left_b2.png",

                "animation/walk_backward_0.png",
                "animation/walk_backward_a.png",
                "animation/walk_backward_a2.png",
                "animation/walk_backward_b.png",
                "animation/walk_backward_b2.png" };

        // load the animations for the player
        BufferedImage playerAnim = ResourceUtil.stitchHoriz(names);

        player = ResourceUtil.createScaledImages(playerAnim,
                SCALE_DOWN_FACTORS, SCALE_UP_FACTORS);
    }

    private final static BufferedImage[] tiles = ResourceUtil.loadScaledImages(
            "tiles.png", SCALE_DOWN_FACTORS, SCALE_UP_FACTORS);

    private final static int TILES_ACROSS = 16;

    private final static BufferedImage font = ResourceUtil
            .loadImage("font.png");

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

        int d = laser.d;

        int gx = (theLevel.getPlayerX() - xScroll) * TILE_SIZE
                + (TILE_SIZE >> 1);
        int gy = (theLevel.getPlayerY() - yScroll) * TILE_SIZE
                + (TILE_SIZE >> 1);

        int lx = (laser.x - xScroll) * TILE_SIZE;
        int ly = (laser.y - yScroll) * TILE_SIZE;

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

        Rectangle clip = g2.getClipBounds();
        ROW: for (int j = 0; j < theLevel.getHeight() - yScroll; j++) {
            COL: for (int i = 0; i < theLevel.getWidth() - xScroll; i++) {
                int dx = i * tileSize;
                int dy = j * tileSize;

                if (clip != null
                        && !clip.intersects(dx, dy, tileSize, tileSize)) {
                    continue COL;
                }

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
        int height = player[zoom].getHeight();

        int dx = (theLevel.getPlayerX() - xScroll) * tileSize;
        int dy = (theLevel.getPlayerY() - yScroll) * tileSize
                - (height - tileSize);

        int sx;
        switch (playerDir) {
        case Level.DIR_DOWN:
            sx = 0;
            break;
        case Level.DIR_RIGHT:
            sx = PLAYER_FRAMES * tileSize;
            break;
        case Level.DIR_LEFT:
            sx = 2 * PLAYER_FRAMES * tileSize;
            break;
        case Level.DIR_UP:
        default:
            sx = 3 * PLAYER_FRAMES * tileSize;
            break;
        }

        g2.drawImage(player[zoom], dx, dy, dx + tileSize, dy + height, sx, 0,
                sx + tileSize, height, null);
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

        //        System.out.println("drawString clip: " + g2.getClip());

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
            } else if (ch == '\n') {
                dx = 0;
                dy += Characters.FONT_HEIGHT;
            } else {
                int tile = Characters.getIndexForChar(text.charAt(i));

                int sx = tile * (Characters.FONT_WIDTH);
                int sy = s.getColor() * (Characters.FONT_HEIGHT);

                g2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, s.getAlphaValue() / 255f));
                g2.drawImage(font, dx, dy, dx + Characters.FONT_WIDTH, dy
                        + Characters.FONT_HEIGHT, sx, sy, sx
                        + Characters.FONT_WIDTH, sy + Characters.FONT_HEIGHT,
                        null);
                dx += Characters.FONT_ADVANCE;
            }
        }
        g2.setComposite(ac);
    }
}