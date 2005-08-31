/*
 * Created on Dec 28, 2004
 */
package org.spacebar.escape.midp1;

import java.io.ByteArrayInputStream;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

import org.spacebar.escape.common.*;

/**
 * @author adam
 */
public class EscapeCanvas extends Canvas implements CommandListener {

    private static final byte TILES_ACROSS = 16;

    private static final byte TILE_SIZE = 8;

    private static final int TILES_BUFFERED = 8;

    private static final int PLAYER_BORDER = 3;

    private static final Font font = Font.getFont(Font.FACE_PROPORTIONAL,
            Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

    // private static final byte NUM_IMAGES = 4;

    private static int yNudge = font.getBaselinePosition();

    boolean done;

    final BitInputStream levelStream;

    final Display display;

    volatile Level theLevel;

    final static Image tiles = ResourceUtil.loadImage("/t.png");

    final static Image player = ResourceUtil.loadImage("/p.png");

    final static Image bots = ResourceUtil.loadImage("/b.png");

    final static Command BACK_COMMAND = new Command("Back", Command.BACK, 1);

    final static Command EXIT_COMMAND = new Command("Exit", Command.EXIT, 1);

    final static Command RESTART_COMMAND = new Command("Restart",
            Command.SCREEN, 1);

    final static Command SCROLL_COMMAND = new Command("Scroll", Command.SCREEN,
            1);

    final static Command UNDO_COMMAND = new Command("Undo", Command.SCREEN, 1);

    private Image levelBuffer;

    private int bufW;

    private int bufH;

    private int playerDir;

    final private MIDlet theApp;

    final private Continuation theWayOut;

    private int xScroll;

    private int yScroll;

    private Effects e;

    private boolean inFreeScroll = false;

    EscapeCanvas(byte[] level, MIDlet m, Continuation c) {
        theApp = m;
        theWayOut = c;
        display = Display.getDisplay(theApp);

        playerDir = Entity.DIR_DOWN;

        levelStream = new BitInputStream(new ByteArrayInputStream(level));

        setCommandListener(this);

        initCommands();

        e = new DefaultEffects() {
            public void requestRedraw() {
                repaint();
                serviceRepaints();
            }
        };
        
        initLevel();
    }

    private void initCommands() {
        addCommand(RESTART_COMMAND);
        addCommand(SCROLL_COMMAND);
        addCommand(UNDO_COMMAND);
        addCommand(BACK_COMMAND);
    }

    // clobbers clip
    static private void drawTile(Graphics g, int tile) {
        int sx = tile % TILES_ACROSS * TILE_SIZE;
        int sy = tile / TILES_ACROSS * TILE_SIZE;

        // int cx = g.getClipX();
        // int cy = g.getClipY();
        // int cw = g.getClipWidth();
        // int ch = g.getClipHeight();

        g.setClip(0, 0, TILE_SIZE, TILE_SIZE);
        g.drawImage(tiles, -sx, -sy, Graphics.TOP | Graphics.LEFT);

        // System.out.println("s: " + sx + " " + sy + ", d: " + dx + " " + dy
        // + ", p:" + px + " " + py);
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

        if (theLevel.getDirty().isAnyDirty()) {
            drawLevel();
        }

        // nudge
        g.translate(0, yNudge);

        // level
        g.translate(-xScroll * TILE_SIZE, -yScroll * TILE_SIZE);
        g.drawImage(levelBuffer, 0, 0, Graphics.TOP | Graphics.LEFT);
        drawPlayer(g);
        drawBots(g);
        drawLaser(g);

        // title
        g.setFont(font);
        g.setColor(255, 255, 255);
        g.drawString(theLevel.getTitle(), -g.getTranslateX(), -g
                .getTranslateY(), Graphics.TOP | Graphics.LEFT);
    }

    private void drawBots(Graphics g) {
        final int bc = theLevel.getBotCount();
        for (int i = 0; i < bc; i++) {
            final int dx = theLevel.getBotX(i) * TILE_SIZE;
            final int dy = (theLevel.getBotY(i) + 1) * TILE_SIZE;

            g.translate(dx, dy);
            final int ch = g.getClipHeight();
            final int cw = g.getClipWidth();
            final int cx = g.getClipX();
            final int cy = g.getClipY();

            final int bh = bots.getHeight();
            g.clipRect(0, -bh, TILE_SIZE, bh);
            g.drawImage(bots, -theLevel.getBotType(i) * TILE_SIZE, 0,
                    Graphics.BOTTOM | Graphics.LEFT);
            g.setClip(cx, cy, cw, ch);
            g.translate(-dx, -dy);
        }
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
        // bufW = Math.min(theLevel.getWidth(), tilesAcross + TILES_BUFFERED);
        // bufH = Math.min(theLevel.getHeight(), tilesDown + TILES_BUFFERED);
        bufW = theLevel.getWidth();
        bufH = theLevel.getHeight();
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

        Level.DirtyList dirty = theLevel.getDirty();
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
        theLevel.getDirty().clearDirty();
    }

    // assume translation is at 0,0 of level
    private void drawPlayer(Graphics g) {
        final int dx = theLevel.getPlayerX() * TILE_SIZE;
        final int dy = (theLevel.getPlayerY() + 1) * TILE_SIZE;

        g.translate(dx, dy);

        final int ch = g.getClipHeight();
        final int cw = g.getClipWidth();
        final int cx = g.getClipX();
        final int cy = g.getClipY();

        final int ph = player.getHeight();
        g.clipRect(0, -ph, TILE_SIZE, ph);
        g.drawImage(player, -TILE_SIZE * (playerDir - 1), 0, Graphics.BOTTOM
                | Graphics.LEFT);
        g.setClip(cx, cy, cw, ch);

        g.translate(-dx, -dy);
    }

    private void drawLaser(Graphics g) {
        IntTriple laser = theLevel.getLaser();
        if (laser == null) {
            return;
        }

        int lx = laser.x * TILE_SIZE + TILE_SIZE / 2;
        int ly = laser.y * TILE_SIZE + TILE_SIZE / 2;
        int px = theLevel.getPlayerX() * TILE_SIZE + TILE_SIZE / 2;
        int py = theLevel.getPlayerY() * TILE_SIZE + TILE_SIZE / 2;

        switch (laser.d) {
        case Entity.DIR_DOWN:
            ly += TILE_SIZE / 2;
            break;
        case Entity.DIR_UP:
            ly -= TILE_SIZE / 2;
            break;
        case Entity.DIR_RIGHT:
            lx += TILE_SIZE / 2;
            break;
        case Entity.DIR_LEFT:
            lx -= TILE_SIZE / 2;
            break;
        }

        g.setColor(255, 0, 0);
        g.drawLine(lx, ly, px, py);
    }

    protected void keyPressed(int keyCode) {
        switch (getGameAction(keyCode)) {
        case LEFT:
            doMove(Entity.DIR_LEFT);
            break;
        case RIGHT:
            doMove(Entity.DIR_RIGHT);
            break;
        case UP:
            doMove(Entity.DIR_UP);
            break;
        case DOWN:
            doMove(Entity.DIR_DOWN);
            break;
        // case FIRE:
        // initLevel();
        // break;
        }
    }

    // TODO correct scrolling with potemkin thing
    void updateScroll() {
        int w = theLevel.getWidth();
        int h = theLevel.getHeight();

        final int paintedTilesAcross = Math.min(getWidth() / TILE_SIZE, w);
        final int paintedTilesDown = Math.min((getHeight() - yNudge)
                / TILE_SIZE, h);

        if (!inFreeScroll) {
            int playerBorderX = PLAYER_BORDER;
            int playerBorderY = PLAYER_BORDER;

            if (paintedTilesAcross < playerBorderX * 2 - 1) {
                playerBorderX = paintedTilesAcross / 2 + 1;
                if (playerBorderX < 0) {
                    playerBorderX = 0;
                }
            }

            if (paintedTilesDown < playerBorderY * 2 - 1) {
                playerBorderY = paintedTilesDown / 2 + 1;
                if (playerBorderY < 0) {
                    playerBorderY = 0;
                }
            }

            // System.out.println("pbx: " + playerBorderX + ", pby: " +
            // playerBorderY);

            final int playerX = theLevel.getPlayerX();
            final int playerY = theLevel.getPlayerY();
            final int playerScreenX = playerX - xScroll;
            final int playerScreenY = playerY - yScroll;

            // System.out.println("psx: " + playerScreenX + ", psy: " +
            // playerScreenY);

            if (playerScreenX < playerBorderX) {
                // System.out.println("scroll left!");
                xScroll = playerX - playerBorderX;
            } else if (playerScreenX > (paintedTilesAcross - 1) - playerBorderX) {
                // System.out.println("scroll right!");
                xScroll = playerX - (paintedTilesAcross - 1) + playerBorderX;
            }

            if (playerScreenY < playerBorderY) {
                // System.out.println("scroll up!");
                yScroll = playerY - playerBorderY;
            } else if (playerScreenY > (paintedTilesDown - 1) - playerBorderY) {
                // System.out.println("scroll down!");
                yScroll = playerY - (paintedTilesDown - 1) + playerBorderY;
            }
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

        // System.out.println("pta: " + paintedTilesAcross + ", ptd: "
        // + paintedTilesDown + ", xs: " + xScroll + ", ys: " + yScroll);
    }

    private void doMove(byte dir) {
        if (theLevel == null || done) {
            return;
        }

        if (inFreeScroll) {
            switch (dir) {
            case Entity.DIR_DOWN:
                yScroll++;
                break;
            case Entity.DIR_UP:
                yScroll--;
                break;
            case Entity.DIR_LEFT:
                xScroll--;
                break;
            case Entity.DIR_RIGHT:
                xScroll++;
                break;
            }
            updateScroll();
            repaint();
            return;
        }

        playerDir = dir;

        theLevel.move(dir, e);
        updateScroll();

        if (theLevel.isDead()) {
            done = true;
            playBackgroundAlertSound(AlertType.ERROR);
        } else if (theLevel.isWon()) {
            done = true;
            playBackgroundAlertSound(AlertType.INFO);
        }
        repaint();
    }

    
    private void playBackgroundAlertSound(final AlertType a) {
        display.callSerially(Backgrounder.makeAlertPlayTask(display, a));
    }

    protected void keyRepeated(int keyCode) {
        keyPressed(keyCode);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == RESTART_COMMAND) {
            initLevel();
        } else if (c == BACK_COMMAND) {
            if (inFreeScroll) {
                initCommands();
                inFreeScroll = false;
                updateScroll();
                repaint();
            } else {
                theWayOut.invoke();
            }
        } else if (c == SCROLL_COMMAND) {
            beginFreeScroll();
        }
    }

    private void initLevel() {
        display.callSerially(Backgrounder.makeLoadLevelTask(this));
    }

    private void beginFreeScroll() {
        // change menu commands
        removeCommand(SCROLL_COMMAND);
        removeCommand(RESTART_COMMAND);
        removeCommand(UNDO_COMMAND);

        // scrolling!
        inFreeScroll = true;
    }
}
