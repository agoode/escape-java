package org.spacebar.escape;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.spacebar.escape.common.*;
import org.spacebar.escape.common.BitInputStream;
import org.spacebar.escape.common.Level;
import org.spacebar.escape.common.Misc;
import org.spacebar.escape.common.hash.MD5;
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

        try {
            // given a directory, search all player files and all level
            // files and try the solutions on all of them
            Map levels = new HashMap();

            System.out.print("Loading...");
            System.out.flush();
            getAllStuff(f, levels);
            System.out.println(" " + levels.size() + " levels loaded");

            for (int a = 1; a < args.length; a++) {
                File pf = new File(args[a]);
                
                // for each solution, verify
                PlayerInfo pi = new PlayerInfo(new BitInputStream(
                        new FileInputStream(pf)));

                System.out.println("*** Player: " + pi);
                Map s = pi.getSolutions();

                // the levels we have solutions for
                for (Iterator iterator = s.keySet().iterator(); iterator
                        .hasNext();) {
                    MD5 md5 = (MD5) iterator.next();
                    Level l = (Level) levels.get(md5);

                    if (l == null) {
                        System.out.println(" " + md5 + " ?");
                        continue; // solution for unknown level?
                    }

                    List sols = (List) s.get(md5);

                    // the solutions for this level
                    for (Iterator iterator2 = sols.iterator(); iterator2
                            .hasNext();) {
                        Solution sol = (Solution) iterator2.next();

                        System.out.print(" " + l);
                        System.out.print(" " + sol.length() + " moves");
                        System.out.flush();

                        //                        DrawnLevel d = new DrawnLevel(l);
                        int result = sol.verify(l);
                        if (result == sol.length()) {
                            System.out.println(" \tOK");
                        } else if (result == -1) {
                            System.out.println(" \tFAILED at end");
                        } else {
                            System.out.println(" \tFAILED at move " + result);
                        }
                        //                        d.dispose();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getAllStuff(File f, Map levels) throws IOException {
        if (f.isDirectory()) {
            File files[] = f.listFiles(ff);

            for (int i = 0; i < files.length; i++) {
                getAllStuff(files[i], levels);
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
            levels.put(md5, new Level(new BitInputStream(
                    new ByteArrayInputStream(l))));
        }
    }
}
