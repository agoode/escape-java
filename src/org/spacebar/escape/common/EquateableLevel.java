package org.spacebar.escape.common;

import java.io.IOException;
import java.io.InputStream;

import org.spacebar.escape.common.hash.FNV32;

public class EquateableLevel extends org.spacebar.escape.common.Level {
    public EquateableLevel(Level l) {
        super(l);
    }

    public EquateableLevel(BitInputStream in) throws IOException {
        super(in);
    }

    public EquateableLevel(LevelManip manip) {
        super(manip);
    }

    public EquateableLevel(InputStream in, int width, int height)
            throws IOException {
        super(in, width, height);
    }

    public int hashCode() {
        // Like Tom,
        /*
         * ignore title, author, w/h, dests, flags, since these don't change.
         * also ignore botd and guyd, which are presentational.
         */

        FNV32 hash = new FNV32();

        // player
        hash.fnv32((byte) player.getX());
        hash.fnv32((byte) player.getY());

        // tiles, oTiles
        // hash.fnv32(width);
        // hash.fnv32(height);

        for (int i = 0; i < playboard.length; i++) {
            hash.fnv32(tileAt(i));
            hash.fnv32(oTileAt(i));
            // hash.fnv32(flags[i]);
            // hash.fnv32(dests[i]);
        }

        // bots
        // hash.fnv32((byte) bots.length);
        for (int i = 0; i < bots.length; i++) {
            Bot b = bots[i];
            hash.fnv32(b.getBotType());
            if (b.isBomb()) {
                hash.fnv32(b.getBombTimer());
            }
            hash.fnv32((byte) b.getX());
            hash.fnv32((byte) b.getY());
        }

        return hash.hval;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof EquateableLevel)) {
            return false;
        }

        EquateableLevel l = (EquateableLevel) obj;

        // entities
        if (!player.equals(l.player)) {
            return false;
        }

        // XXX skip deleted
        for (int i = 0; i < bots.length; i++) {
            if (!bots[i].equals(l.bots[i])) {
                return false;
            }
        }

        /*
         * // metadata if (!author.equals(l.author)) { return false; } if
         * (!title.equals(l.title)) { return false; }
         */

        if (width != l.width || playboard.length != l.playboard.length) {
            return false;
        }

        // tiles
        if (playboard == l.playboard) {
            return true;
        }

        for (int i = 0; i < playboard.length; i++) {
            if (playboard[i] != l.playboard[i]) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        int height = getHeight();
        return "[\"" + title + "\" by " + author + " (" + width + "x" + height
                + ")" + " player: (" + this.player.getX() + ","
                + this.player.getY() + ")]";
    }
}
