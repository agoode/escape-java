/*
 * Created on Dec 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape.util;

/**
 * @author adam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CharacterMap {
    public static final int COLOR_WHITE = 0;
    public static final int COLOR_BLUE = 1;
    public static final int COLOR_RED = 2;
    public static final int COLOR_YELLOW = 3;
    public static final int COLOR_GRAY = 4;
    public static final int COLOR_GREEN = 5;
    
    private static final String chars = " ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789`'=[]\\;',./~!@#$%^&*()_+{}|:\"<>?";
    private static final int[] items = new int[256];
    static {
        for (int i = 0; i < chars.length(); i++) {
            items[chars.charAt(i)] = i;
        }
    }
    
    public static int getIndexForChar(char c) {
        return items[c];
    }
}
