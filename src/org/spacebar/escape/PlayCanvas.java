/*
 * Created on Dec 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.*;
import java.awt.event.ActionEvent;
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
import org.spacebar.escape.util.Characters;
import org.spacebar.escape.util.StyleStack;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PlayCanvas extends DoubleBufferCanvas {

    private class Mover extends AbstractAction {
        final int dir;

        public Mover(int dir) {
            this.dir = dir;
        }

        public void actionPerformed(ActionEvent e) {
            if (!done) {
                if (theLevel.move(dir, effects)) {
                    // append to solution
                    addToSolution(dir);
                }
                setPlayerDir(dir);

                if ((theLevel.isDead()) != null) {
                    effects.doLaser();
                    System.out.println("dead");
                    done = true;
                } else if (theLevel.isWon()) {
                    effects.doExit();
                    System.out.println("won in " + solutionCount + " steps");
                    System.out.print("solution: [");
                    for (int i = 0; i < solutionCount; i++) {
                        int d = solution[i];
                        String s;
                        switch (d) {
                        case Level.DIR_UP:
                            //                            s = "up";
                            s = "↑";
                            break;
                        case Level.DIR_DOWN:
                            //                            s = "down";
                            s = "↓";
                            break;
                        case Level.DIR_LEFT:
                            //                            s = "left";
                            s = "←";
                            break;
                        case Level.DIR_RIGHT:
                            //                            s = "right";
                            s = "→";
                            break;
                        default:
                            s = "?";
                        }
                        System.out.print(s);
                        if (i != solutionCount - 1) {
                            System.out.print(" ");
                        }
                    }
                    System.out.println("]");
                    done = true;
                }
                bufferRepaint();
            }
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
                if (scale > LevelDraw.SCALE_DOWN_FACTORS - 1) {
                    scale = LevelDraw.SCALE_DOWN_FACTORS - 1;
                }
            } else {
                scale--;
                if (scale < -LevelDraw.SCALE_UP_FACTORS) {
                    scale = -LevelDraw.SCALE_UP_FACTORS;
                }
            }

            //            System.out.println("scale: " + scale + ", scaleVal: " +
            // scaleVal);

            bufferRepaint();
        }
    }

    final static Effects effects;

    private final static BufferedImage font = ResourceUtils
            .loadImage("font.png");

    private final static int FONT_HEIGHT = 16;

    private final static int FONT_SPACE = 1;

    private final static int FONT_WIDTH = 8;

    private final static int FONT_MARGIN = 2;

    private final static int PLAYER_BORDER = 2;

    private final static int LEVEL_MARGIN = 12;

    static {
        //        Effects e1 = new NESEffects();
        Effects e2 = new TextEffects();
        CompoundEffects e = new CompoundEffects();
        //        e.add(e1);
        e.add(e2);
        effects = e;
    }

    boolean done;

    boolean showBizarro;

    private int playerDir;

    //    IntTriple laser;

    final File levelFile;

    int scale = 0;

    //    double scaleVal = 1.0;

    Level theLevel;

    private int xScroll;

    private int yScroll;

    private int paintedTilesAcross;

    private int paintedTilesDown;

    byte solution[];

    int solutionCount;

    public PlayCanvas(File f) {
        super();

        levelFile = f;
        initLevel();

        playerDir = Level.DIR_DOWN;

        setupKeys();
    }

    protected void bufferPaint(Graphics2D g) {
        int w = getWidth();
        int h = getHeight();

        updateScroll();

        // clear
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, w, h);

        // save clip, setup for drawing level
        Shape clip = g.getClip();
        g.clip(new Rectangle(LEVEL_MARGIN, LEVEL_MARGIN, w - 2 * LEVEL_MARGIN,
                h - 2 * LEVEL_MARGIN));

        // save transform and translate
        AffineTransform origAT = g.getTransform();
        g.translate(LEVEL_MARGIN, LEVEL_MARGIN);

        // paint things within boundaries of level
        paintAllLevel(g);

        // restore clip and draw the rest
        g.setClip(clip);
        g.setTransform(origAT);
        paintArrows(g);

        // restore transform and draw title
        g.setTransform(origAT);
        g.translate(FONT_MARGIN, FONT_MARGIN);
        paintTitle(g);
    }

    static private void drawString(Graphics2D g2, String text) {
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

    /**
     * @return Returns the dir.
     */
    public int getPlayerDir() {
        return playerDir;
    }

    /**
     * @throws IOException
     * @throws FileNotFoundException
     */
    void initLevel() {
        try {
            theLevel = new Level(new BitInputStream(new FileInputStream(
                    levelFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        done = false;
        showBizarro = false;
        solution = null;
        solutionCount = 0;

        bufferRepaint();
    }

    private void paintAllLevel(Graphics2D g) {
        LevelDraw.paintAllLevel(g, theLevel, xScroll, yScroll, showBizarro,
                playerDir, scale);
    }

    private void paintArrows(Graphics2D g) {
        int h = getHeight();
        int w = getWidth();

        AffineTransform t = g.getTransform();

        if (xScroll > 0) {
            // left arrow
            int x = 3;
            int y = h / 2;
            g.translate(x, y);
            drawString(g, Characters.PICS + Characters.ARROWL + Characters.POP);
            g.setTransform(t);
        }
        if (yScroll > 0) {
            // top arrow
            int x = w / 2;
            int y = -4;
            g.translate(x, y);
            drawString(g, Characters.PICS + Characters.ARROWU + Characters.POP);
            g.setTransform(t);
        }
        if (paintedTilesAcross + xScroll < theLevel.getWidth()) {
            // right arrow
            int x = w - LEVEL_MARGIN;
            int y = h / 2;
            g.translate(x, y);
            drawString(g, Characters.PICS + Characters.ARROWR + Characters.POP);
            g.setTransform(t);
        }
        if (paintedTilesDown + yScroll < theLevel.getHeight()) {
            // down arrow
            int x = w / 2;
            int y = h - LEVEL_MARGIN;
            g.translate(x, y);
            drawString(g, Characters.PICS + Characters.ARROWD + Characters.POP);
            g.setTransform(t);
        }
    }

    private void paintTitle(Graphics2D g2) {
        String text = theLevel.getTitle() + " " + Characters.GRAY + "by "
                + Characters.POP + Characters.BLUE + theLevel.getAuthor()
                + Characters.POP;

        drawString(g2, text);
    }

    void updateScroll() {
        int tileSize = LevelDraw.getTileSize(scale);

        // keep at least LEVEL_MARGIN everywhere
        paintedTilesAcross = (int) ((getWidth() - (2 * LEVEL_MARGIN)) / tileSize);
        paintedTilesDown = (int) ((getHeight() - (2 * LEVEL_MARGIN)) / tileSize);

        int w = theLevel.getWidth();
        int h = theLevel.getHeight();
        if (paintedTilesAcross > w) {
            paintedTilesAcross = w;
        }
        if (paintedTilesDown > h) {
            paintedTilesDown = h;
        }

        int playerBorderX = PLAYER_BORDER;
        int playerBorderY = PLAYER_BORDER;

        if (paintedTilesAcross < playerBorderX * 2 + 1) {
            playerBorderX = Math.round(paintedTilesAcross / 2f) - 1;
            if (playerBorderX < 0) {
                playerBorderX = 0;
            }
        }

        if (paintedTilesDown < playerBorderY * 2 + 1) {
            playerBorderY = Math.round(paintedTilesDown / 2f) - 1;
            if (playerBorderY < 0) {
                playerBorderY = 0;
            }
        }

        //        System.out.println("pbx: " + playerBorderX + ", pby: " +
        // playerBorderY);

        final int playerX = theLevel.getPlayerX();
        final int playerY = theLevel.getPlayerY();
        final int playerScreenX = playerX - xScroll;
        final int playerScreenY = playerY - yScroll;

        if (playerScreenX < playerBorderX) {
            //            System.out.println("scroll left!");
            xScroll = playerX - playerBorderX;
        } else if (playerScreenX > (paintedTilesAcross - 1) - playerBorderX) {
            //            System.out.println("scroll right!");
            xScroll = playerX - (paintedTilesAcross - 1) + playerBorderX;
        }

        if (playerScreenY < playerBorderY) {
            //            System.out.println("scroll up!");
            yScroll = playerY - playerBorderY;
        } else if (playerScreenY > (paintedTilesDown - 1) - playerBorderY) {
            //            System.out.println("scroll down!");
            yScroll = playerY - (paintedTilesDown - 1) + playerBorderY;
        }

        // normalize
        final int maxXScroll = w - paintedTilesAcross;
        final int maxYScroll = h - paintedTilesDown;

        if (xScroll < 0) {
            xScroll = 0;
        } else if (xScroll > maxXScroll) {
            xScroll = maxXScroll;
        }

        if (yScroll < 0) {
            yScroll = 0;
        } else if (yScroll > maxYScroll) {
            yScroll = maxYScroll;
        }

        //        System.out.println("pta: " + paintedTilesAcross + ", ptd: "
        //                + paintedTilesDown + ", xs: " + xScroll + ", ys: " + yScroll);
    }

    /**
     * @param dir
     *            The dir to set.
     */
    void setPlayerDir(int dir) {
        if (dir != Level.DIR_DOWN && dir != Level.DIR_LEFT
                && dir != Level.DIR_RIGHT && dir != Level.DIR_UP) {
            throw new IllegalArgumentException("Bad direction");
        }
        this.playerDir = dir;
    }

    void addToSolution(int dir) {
        if (solution == null) {
            solution = new byte[16];
        }

        solutionCount++;

        if (solutionCount == solution.length) {
            // extend
            byte b[] = new byte[(int) (solution.length * 1.5)];
            System.arraycopy(solution, 0, b, 0, solution.length);
            solution = b;
        }

        solution[solutionCount - 1] = (byte) dir;
    }

    private void setupKeys() {
        // moving
        addAction("LEFT", "goLeft", new Mover(Level.DIR_LEFT));
        addAction("DOWN", "goDown", new Mover(Level.DIR_DOWN));
        addAction("RIGHT", "goRight", new Mover(Level.DIR_RIGHT));
        addAction("UP", "goUp", new Mover(Level.DIR_UP));

        // scaling
        addAction("CLOSE_BRACKET", "scaleUp", new Scaler(false));
        addAction("OPEN_BRACKET", "scaleDown", new Scaler(true));

        // bizarro
        addAction("Y", "toggleAlt", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                showBizarro = !showBizarro;
                bufferRepaint();
            }
        });

        // restart
        addAction("ENTER", "restart", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                initLevel();
            }
        });

        // quit
        addAction("ESCAPE", "exit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void addAction(String keyStroke, String name, Action a) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(keyStroke), name);
        getActionMap().put(name, a);
    }
}