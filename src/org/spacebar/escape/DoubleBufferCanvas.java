/*
 * Created on Dec 26, 2004
 */
package org.spacebar.escape;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

abstract public class DoubleBufferCanvas extends JComponent {

    private VolatileImage backBuffer;

    private Overlay overlay;

    private boolean paintingActive = true;

    protected Continuation theWayOut;
    
    DoubleBufferCanvas(Continuation c) {
        super();
        setDoubleBuffered(false);
        setOpaque(true);
        
        theWayOut = c;
    }

    abstract protected void bufferPaint(Graphics2D g);

    public void bufferRepaint() {
        if (backBuffer == null) {
            return;
        } else {
            renderOffscreen();
            repaint();
        }
    }

    public Overlay getOverlay() {
        return overlay;
    }

    final private void initBackBuffer() {
        //        System.out.println("*** initializing back buffer");
        backBuffer = createVolatileImage(getWidth(), getHeight());
        //        System.out.println("backBuffer: " + backBuffer);
    }

    public boolean isPaintingActive() {
        return paintingActive;
    }

    final private void myBufferPaint(Graphics2D g) {
        synchronized (backBuffer) {
            bufferPaint(g);
            if (overlay != null) {
                overlay.draw(g, getWidth(), getHeight());
            }
        }
    }

    final protected void paintComponent(Graphics g) {
        if (!paintingActive) {
            return;
        }

        if (backBuffer == null || backBuffer.getHeight() != getHeight()
                || backBuffer.getWidth() != getWidth()) {
            initBackBuffer();
            renderOffscreen();
        }

        do {
            int returnCode = backBuffer.validate(getGraphicsConfiguration());
            if (returnCode == VolatileImage.IMAGE_RESTORED) {
                renderOffscreen(); // restore contents
            } else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                initBackBuffer();
                renderOffscreen();
            }

            // don't draw if this is being updated
            synchronized (backBuffer) {
                g.drawImage(backBuffer, 0, 0, this);
            }
        } while (backBuffer.contentsLost());
    }

    final private void renderOffscreen() {
        if (!paintingActive) {
            return;
        }

        do {
            if (backBuffer.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
                initBackBuffer();
            }
            Graphics2D g = backBuffer.createGraphics();
            myBufferPaint(g);
            g.dispose();
        } while (backBuffer.contentsLost());
    }

    public void setOverlay(Overlay o) {
        overlay = o;
        bufferRepaint();
    }

    public void setPaintingActive(boolean b) {
        paintingActive = b;
        if (!paintingActive) {
            backBuffer = null;
        } else {
            repaint();
        }
        System.out.println("paintingActive:" + paintingActive);
    }

    protected void addAction(String keyStroke, String name, Action a) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(keyStroke), name);
        getActionMap().put(name, a);
    }
}