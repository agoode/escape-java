package org.spacebar.escape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.spacebar.escape.common.BitInputStream;
import org.spacebar.escape.common.PlayerInfo;

public class TestSolutions {
    public static void main(String[] args) {
        File f = new File(args[0]);
        
        try {
            BitInputStream in = new BitInputStream(new FileInputStream(f));
            
            PlayerInfo pi = new PlayerInfo(in);

            System.out.println(pi);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
