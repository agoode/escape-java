package org.spacebar.escape;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.spacebar.escape.common.BitInputStream;
import org.spacebar.escape.common.Level;
import org.spacebar.escape.common.Misc;
import org.spacebar.escape.common.Level.Solution;
import org.spacebar.escape.common.hash.MD5;
import org.spacebar.escape.j2se.DrawnLevel;
import org.spacebar.escape.j2se.PlayerInfo;

public class TestSolutions {

    private static FileFilter ff = new FileFilter() {
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }

            String n = pathname.getName().toLowerCase();
            return n.endsWith(".esx") || n.endsWith(".esp");
        }
    };

    public static void main(String[] args) {
        File f = new File(args[0]);
        if (!f.isDirectory()) {
            System.out
                    .println("Please specify a directory with players and levels");
            System.exit(1);
        }

        try {
            // given a directory, search all player files and all level
            // files and try the solutions on all of them
            Map levels = new HashMap();

            List players = new ArrayList();

            System.out.print("Loading  [");
            System.out.flush();
            getAllStuff(f, levels, players);
            System.out.println(" ]  " + levels.size() + " levels, "
                    + players.size() + " players");

            // for each solution in each player, verify
            for (Iterator iter = players.iterator(); iter.hasNext();) {
                PlayerInfo pi = (PlayerInfo) iter.next();
                Map s = pi.getSolutions();
                for (Iterator iterator = s.keySet().iterator(); iterator
                        .hasNext();) {
                    MD5 md5 = (MD5) iterator.next();
                    Level l = (Level) levels.get(md5);

                    if (l == null) {
                        System.out.println(md5 + " doesn't have a level");
                        continue;   // solution for unknown level?
                    }
                    
                    List sols = (List) s.get(md5);

                    for (Iterator iterator2 = sols.iterator(); iterator2
                            .hasNext();) {
                        Solution sol = (Solution) iterator2.next();

                        System.out.print(l);
                        System.out.flush();
                        
                        DrawnLevel d = new DrawnLevel(l);
                        boolean result = sol.verify(d);
                        if (result) {
                            System.out.println(" OK");
                        } else {
                            System.out.println(" BAD");
                        }
                        d.dispose();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getAllStuff(File f, Map levels, List players)
            throws IOException {
        if (f.isDirectory()) {
            File files[] = f.listFiles(ff);

            for (int i = 0; i < files.length; i++) {
                getAllStuff(files[i], levels, players);
            }
        } else if (f.getName().toLowerCase().endsWith(".esx")) {
            // level
            System.out.print(" " + f.getName());
            System.out.flush();

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
        } else if (f.getName().toLowerCase().endsWith(".esp")) {
            // player
            System.out.print(" " + f.getName());
            System.out.flush();

            PlayerInfo p = new PlayerInfo(new BitInputStream(
                    new FileInputStream(f)));
            players.add(p);
            
//            System.out.println(p);
        }
    }
}
