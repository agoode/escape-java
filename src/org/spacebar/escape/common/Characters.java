/*
 * Created on Dec 23, 2004
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
public class Characters {
    public static final String WHITE = "^0";

    public static final String BLUE = "^1";

    public static final String RED = "^2";

    public static final String YELLOW = "^3";

    public static final String GRAY = "^4";

    public static final String GREEN = "^5";

    public static final String POP = "^<";

    public static final String ALPHA100 = "^#";

    public static final String ALPHA50 = "^$";

    public static final String ALPHA25 = "^%";

    /* there are some non-ascii symbols in the font */
    public static final String CHECKMARK = "\u00F2";

    public static final String ESC = "\u00F3";

    public static final String HEART = "\u00F4";

    public static final String LCMARK1 = "\u00F5";

    public static final String LCMARK2 = "\u00F6";

    public static final String LCHECKMARK = LCMARK1 + LCMARK2;

    /* BAR_0 ... BAR_10 are guaranteed to be consecutive */
    public static final String BAR_0 = "\u00E0";

    public static final String BAR_1 = "\u00E1";

    public static final String BAR_2 = "\u00E2";

    public static final String BAR_3 = "\u00E3";

    public static final String BAR_4 = "\u00E4";

    public static final String BAR_5 = "\u00E5";

    public static final String BAR_6 = "\u00E6";

    public static final String BAR_7 = "\u00E7";

    public static final String BAR_8 = "\u00E8";

    public static final String BAR_9 = "\u00E9";

    public static final String BAR_10 = "\u00EA";

    public static final String BARSTART = "\u00EB";

    public static final String FONTCHARS = " ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789`-=[]\\;',./~!@#$%^&*()_+{}|:\"<>?"
            + CHECKMARK
            + ESC
            + HEART
            + LCMARK1
            + LCMARK2
            + BAR_0
            + BAR_1
            + BAR_2
            + BAR_3
            + BAR_4
            + BAR_5
            + BAR_6
            + BAR_7
            + BAR_8
            + BAR_9
            + BAR_10 + BARSTART;

    /*
     * additionally, one style just holds little helper images instead of
     * letters
     */
    public static final String PICS = "^6";

    public static final String ARROWR = "A";

    public static final String ARROWU = "B";

    public static final String ARROWD = "C";

    public static final String ARROWL = "D";

    public static final String EXCICON1 = "E";

    public static final String EXCICON2 = "F";

    public static final String EXCICON = EXCICON1 + EXCICON2;

    public static final String SKULLICON1 = "G";

    public static final String SKULLICON2 = "H";

    public static final String SKULLICON = SKULLICON1 + SKULLICON2;

    public static final String THUMBICON1 = "I";

    public static final String THUMBICON2 = "J";

    public static final String THUMBICON = THUMBICON1 + THUMBICON2;

    public static final String BUGICON1 = "K";

    public static final String BUGICON2 = "L";

    public static final String BUGICON = BUGICON1 + BUGICON2;

    public static final String QICON1 = "M";

    public static final String QICON2 = "N";

    public static final String QICON = QICON1 + QICON2;

    public static final String XICON1 = "O";

    public static final String XICON2 = "P";

    public static final String XICON = XICON1 + XICON2;

    public static final String BARLEFT = "Q";

    public static final String BAR = "R";

    public static final String BARRIGHT = "S";

    public static final String SLIDELEFT = "T";

    public static final String SLIDE = "U";

    public static final String SLIDERIGHT = "V";

    public static final String SLIDEKNOB = "W";

    public static final int FONTSTYLES = 7;

    private static final int[] items = new int[256];

    public final static int FONT_HEIGHT = 16;

    public final static int FONT_WIDTH = 9;

    public final static int FONT_ADVANCE = 8;
    static {
        for (int i = 0; i < FONTCHARS.length(); i++) {
            items[FONTCHARS.charAt(i)] = i;
        }
    }

    public static int getIndexForChar(char c) {
        return items[c];
    }

    public static int width(String s) {
        if (s == null) {
            return 0;
        }
        
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '^') {
                i++;
                if (s.charAt(i) == '^') {
                    result++;
                }
            } else {
                result++;
            }
        }
        if (result == 0) {
            return 0;
        } else {
            return (result - 1) * FONT_ADVANCE + FONT_WIDTH;
        }
    }

    // by example:
    // "" returns 0
    // "\n" returns 1
    // "hello" returns 1
    // "hello\n" returns 1
    // "hello\nworld" returns 2
    // "hello\nworld\n" returns 2
    public static int lines(String s) {
        int i = 0;
        int sofar = 0;

        final int M_FINDANY = 0;
        final int M_STEADY = 1;

        int m = M_FINDANY;

        for (;; i++) {
            if (i >= s.length())
                return sofar;
            switch (m) {
            case M_FINDANY:
                if (s.charAt(i) == '\n') {
                    sofar++;
                    continue;
                } else {
                    sofar++;
                    m = M_STEADY;
                    continue;
                }
            case M_STEADY:
                if (s.charAt(i) == '\n') {
                    m = M_FINDANY;
                    continue;
                }
            }
        }
    }
}