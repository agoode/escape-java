/*
 * Created on Dec 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
abstract public class DoubleBufferCanvas extends JComponent {

    private BufferedImage backBuffer;

    DoubleBufferCanvas() {
        super();

        setOpaque(true);
    }

    abstract protected void bufferPaint(Graphics2D g);

    public void bufferRepaint() {
        if (backBuffer == null) {
            return;
        }
        System.out.println("*** buffer repaint");
        
        Graphics2D g2 = backBuffer.createGraphics();
        bufferPaint(g2);
        g2.dispose();
        repaint();
    }

    private void initBackBuffer() {
        System.out.println("*** initializing back buffer");
        backBuffer = (BufferedImage) createImage(getWidth(), getHeight());
        //        System.out.println("backBuffer: " + backBuffer);
    }

    protected void paintComponent(Graphics g) {
        if (backBuffer == null || backBuffer.getHeight() != getHeight()
                || backBuffer.getWidth() != getWidth()) {
            initBackBuffer();
            Graphics2D g2 = backBuffer.createGraphics();
            bufferPaint(g2);
            g2.dispose();
        }

        g.drawImage(backBuffer, 0, 0, this);
    }

}