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

    DoubleBufferCanvas() {
        super();
        setBackground(Color.BLACK);
        setOpaque(true);
    }

    private void synchronizedBufferPaint(Graphics2D g) {
        synchronized (backBuffer) {
            bufferPaint(g);
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

    private void initBackBuffer() {
        //        System.out.println("*** initializing back buffer");
        backBuffer = createVolatileImage(getWidth(), getHeight());
        //        System.out.println("backBuffer: " + backBuffer);
    }

    private void renderOffscreen() {
        do {
            if (backBuffer.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
                initBackBuffer();
            }
            Graphics2D g = backBuffer.createGraphics();
            synchronizedBufferPaint(g);
            g.dispose();
        } while (backBuffer.contentsLost());
    }

    protected void paintComponent(Graphics g) {
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
}