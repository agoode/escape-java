package org.spacebar.escape;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.spacebar.escape.common.BitInputStream;
import org.spacebar.escape.common.Level;
import org.spacebar.escape.common.Misc;
import org.spacebar.escape.common.Solution;
import org.spacebar.escape.common.hash.MD5;
import org.spacebar.escape.j2se.EscapeFrame;
import org.spacebar.escape.j2se.PlayerInfo;

public class TestSolutions {

    private static FileFilter ff = new FileFilter() {
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }

            String n = pathname.getName().toLowerCase();
            return n.endsWith(".esx");
        }
    };

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: " + TestSolutions.class.getName()
                    + " levels_dir player.esp [player2.esp ... ]");
            System.exit(1);
        }

        File f = new File(args[0]);
        if (!f.isDirectory()) {
            System.out.println("Please specify a directory with levels");
            System.exit(1);
        }

        int good = 0;
        int bad = 0;
        int unknown = 0;

        try {
            // given a directory, search all player files and all level
            // files and try the solutions on all of them
            Map levels = new HashMap();
            Map levelsToFiles = new HashMap();

            System.out.print("Loading...");
            System.out.flush();
            getAllStuff(f, levels, levelsToFiles);
            System.out.println(" " + levels.size() + " levels loaded");

            int maxLevelString = 0;

            for (int a = 1; a < args.length; a++) {
                File pf = new File(args[a]);

                // for each solution, verify
                PlayerInfo pi = new PlayerInfo(new BitInputStream(
                        new FileInputStream(pf)));

                System.out.println("*** Player: " + pi);
                Map s = pi.getSolutions();

                Map levelsToSolutions = new HashMap();

                // the levels we have solutions for
                for (Iterator iterator = s.keySet().iterator(); iterator
                        .hasNext();) {
                    MD5 md5 = (MD5) iterator.next();
                    Level l = (Level) levels.get(md5);

                    if (l == null) {
                        System.out.println(" " + md5 + " ?");
                        unknown++;
                        continue; // solution for unknown level?
                    }

                    List sols = (List) s.get(md5);
                    levelsToSolutions.put(l, sols);
                    String str = getStringForLevel(l, levelsToFiles);
                    maxLevelString = Math.max(maxLevelString, str.length());
                }

                final int mls = maxLevelString;

                // the solutions for this level
                for (Iterator i = levelsToSolutions.keySet().iterator(); i
                        .hasNext();) {
                    Level l = (Level) i.next();
                    List sols = (List) levelsToSolutions.get(l);

                    for (Iterator iter = sols.iterator(); iter.hasNext();) {
                        final Solution sol = (Solution) iter.next();

                        final String ls = getStringForLevel(l, levelsToFiles);
                        System.out.print(" " + ls);
                        System.out.flush();

                        Level l2 = new Level(l);
                        int result = sol.verify(l2);

                        int pad = mls - ls.length() + 5;
                        while (pad-- > 0) {
                            System.out.print(" ");
                        }

                        if (result == sol.length()) {
                            System.out.println("OK");
                            good++;
                        } else if (result == -1) {
                            System.out.println("BAD at " + sol.length()
                                    + " (end)");
                            bad++;
//                            new EscapeFrame(l2);
                        } else {
                            System.out.println("BAD at " + result);
                            bad++;
//                            new EscapeFrame(l2);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(good + " good, " + bad + " bad, " + unknown
                    + " unknown");
        }
    }

    private static String getStringForLevel(Level l, Map levelsToFiles) {
        File f = (File) levelsToFiles.get(l);
        return l.toString() + " [" + f.getName() + "]";
    }

    private static void getAllStuff(File f, Map levels, Map levelsToFiles)
            throws IOException {
        if (f.isDirectory()) {
            File files[] = f.listFiles(ff);

            for (int i = 0; i < files.length; i++) {
                getAllStuff(files[i], levels, levelsToFiles);
            }
        } else {
            // level
            byte l[] = Misc.getByteArrayFromInputStream(new FileInputStream(f));
            MessageDigest m = null;

            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            MD5 md5 = new MD5(m.digest(l));
            Level ll = new Level(
                    new BitInputStream(new ByteArrayInputStream(l)));
            levels.put(md5, ll);
            levelsToFiles.put(ll, f);
        }
    }
}
