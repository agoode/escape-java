package org.spacebar.escape.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

public class Solution {
    byte solution[];

    int size;

    String name;

    String author;

    Date date;

    public int length() {
        return size;
    }

    public Solution(String b64, boolean named) throws IOException {
        //            System.out.println("decoding " + b64);
        byte[] b = Base64.decode(b64);

        for (int i = 0; i < b.length; i++) {
            int v = b[i];
            if (v < 0) {
                v += 256;
            }
            //                System.out.print(Integer.toHexString(v) + " ");
        }
        //            System.out.println();

        BitInputStream in = new BitInputStream(new ByteArrayInputStream(b));

        if (named) {
            // date, name, author
            int len;

            // date
            int date = in.readInt();
            this.date = new Date(date * 1000L);

            // name
            len = in.readInt();
            name = Misc.getStringFromData(in, len);

            // author
            len = in.readInt();
            author = Misc.getStringFromData(in, len);

            // remaining length (not really important)
            len = in.readInt();
        }
        decodeSolution(in);
    }

    public Solution(BitInputStream in) throws IOException {
        decodeSolution(in);
    }

    private void decodeSolution(BitInputStream in) throws IOException {
        size = in.readInt();
        //            System.out.println("size: " + size);

        int sol[] = RunLengthEncoding.decode(in, size);
        solution = new byte[sol.length];
        for (int i = 0; i < sol.length; i++) {
            byte b = (byte) sol[i];
            solution[i] = b;
        }
    }

    public Solution() {
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("solution: ");

        if (name != null) {
            sb.append("\"" + name + "\" ");
        }
        if (author != null) {
            sb.append("by " + author + " ");
        }
        if (date != null) {
            sb.append("at " + date + " ");
        }

        sb.append("[");
        for (int i = 0; i < size; i++) {
            int d = solution[i];
            String s;
            switch (d) {
            case Entity.DIR_UP:
                //                            s = "up";
                s = "↑";
                break;
            case Entity.DIR_DOWN:
                //                            s = "down";
                s = "↓";
                break;
            case Entity.DIR_LEFT:
                //                            s = "left";
                s = "←";
                break;
            case Entity.DIR_RIGHT:
                //                            s = "right";
                s = "→";
                break;
            default:
                s = "?";
            }
            sb.append(s);
            if (i != size - 1) {
                sb.append(" ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public void addToSolution(int dir) {
        if (solution == null) {
            solution = new byte[16];
        }

        size++;

        if (size == solution.length) {
            // extend
            byte b[] = new byte[(int) (solution.length * 1.5)];
            System.arraycopy(solution, 0, b, 0, solution.length);
            solution = b;
        }

        solution[size - 1] = (byte) dir;
    }

    public int verify(Level l) {
        for (int i = 0; i < size; i++) {
            int d = solution[i];

            // bad move is bad
            if (!l.move(d)) {
                //                    System.out.println("bad move");
                return i;
            }

            // death is bad
            if (l.isDead()) {
                //                    System.out.println("bad dead");
                return i;
            }

            // early winning is bad
            if (l.isWon() && i != (size - 1)) {
                //                    System.out.println("early win");
                return i;
            }
            //                System.out.println(d);
        }
        if (l.isWon() && !l.isDead()) {
            return size;           // perfect
        } else {
            return -1;             // failed at last move
        }
    }
    
    
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public byte[] getSolution() {
        return solution;
    }
}