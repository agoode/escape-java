package org.spacebar.escape.common;

import static org.junit.Assert.assertEquals;
import static org.spacebar.escape.common.Characters.lines;

import org.junit.Test;

public class CharactersTest {

    @Test
    public void testLines() {
        assertEquals(0, lines(""));
        assertEquals(1, lines("\n"));
        assertEquals(1, lines("hello"));
        assertEquals(1, lines("hello\n"));
        assertEquals(2, lines("hello\nworld"));
        assertEquals(2, lines("hello\nworld\n"));
    }

}
