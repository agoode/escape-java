/*
 * Created on Dec 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.common;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class StyleStack {
    public static final int COLOR_WHITE = 0;

    public static final int COLOR_BLUE = 1;

    public static final int COLOR_RED = 2;

    public static final int COLOR_YELLOW = 3;

    public static final int COLOR_GRAY = 4;

    public static final int COLOR_GREEN = 5;

    public static final int ALPHA_100 = 0;

    public static final int ALPHA_50 = 1;

    public static final int ALPHA_25 = 2;

    private int alpha;

    private int color;

    private FontAttribute top;

    public void push(char c) {
        FontAttribute next;
        if (c >= '#' && c <= '\'') {
            next = new FontAttribute(FontAttribute.TYPE_ALPHA, alpha, top);
            alpha = c - '#';
        } else {
            next = new FontAttribute(FontAttribute.TYPE_COLOR, color, top);
            color = c - '0';
        }
        top = next;
    }

    public void pop() {
        if (top == null) {
            System.out.println("Popping empty StyleStack!");
            return;
        }

        int what = top.getWhat();
        switch (what) {
        case FontAttribute.TYPE_ALPHA:
            alpha = top.getValue();
            break;
        case FontAttribute.TYPE_COLOR:
            color = top.getValue();
            break;
        }
        top = top.getNext();
    }

    public int getAlpha() {
        return alpha;
    }

    public int getAlphaValue() {
        int result;
        switch (alpha) {
        case ALPHA_100:
            result = 255;
        case ALPHA_50:
            result = (int) (0.5 * 255);
        case ALPHA_25:
            result = (int) (0.25 * 255);
        default:
            result = 255;
        }
        return result;
    }

    public int getColor() {
        return color;
    }

    private static class FontAttribute {
        public static final int TYPE_COLOR = 0;

        public static final int TYPE_ALPHA = 1;

        private int what;

        private int value;

        FontAttribute next;

        public FontAttribute(int what, int value, FontAttribute next) {
            if (what != TYPE_ALPHA && what != TYPE_COLOR) {
                throw new IllegalArgumentException(
                        "what must be TYPE_ALPHA or TYPE_COLOR");
            }

            this.what = what;
            this.value = value;
            this.next = next;
        }

        public int getWhat() {
            return what;
        }

        public int getValue() {
            return value;
        }

        public FontAttribute getNext() {
            return next;
        }
    }
}