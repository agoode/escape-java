/*
 * Created on Dec 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

import javax.swing.JComponent;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
abstract public class DoubleBufferCanvas extends JComponent {

    private VolatileImage backBuffer;

    private Overlay overlay;

    DoubleBufferCanvas() {
        super();
        setBackground(Color.BLACK);
        setOpaque(true);
    }

    final private void myBufferPaint(Graphics2D g) {
        synchronized (backBuffer) {
            bufferPaint(g);
            if (overlay != null) {
                overlay.draw(g, getWidth(), getHeight());
            }
        }
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

    public void setOverlay(Overlay o) {
        overlay = o;
        bufferRepaint();
    }

    public Overlay getOverlay() {
        return overlay;
    }

    final private void initBackBuffer() {
        //        System.out.println("*** initializing back buffer");
        backBuffer = createVolatileImage(getWidth(), getHeight());
        //        System.out.println("backBuffer: " + backBuffer);
    }

    final private void renderOffscreen() {
        do {
            if (backBuffer.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
                initBackBuffer();
            }
            Graphics2D g = backBuffer.createGraphics();
            myBufferPaint(g);
            g.dispose();
        } while (backBuffer.contentsLost());
    }

    final protected void paintComponent(Graphics g) {
        if (backBuffer == null) {
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

        // avoid horrible flickering
        if (backBuffer.getHeight() != getHeight()
                || backBuffer.getWidth() != getWidth()) {
            initBackBuffer();
            bufferRepaint();
        }
    }
}