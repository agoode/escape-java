package org.spacebar.escape.common.hash;

import java.io.IOException;
import java.io.InputStream;

import org.spacebar.escape.common.Misc;

public class MD5 {
    final byte data[];

    public MD5(String s) {
        if (s.length() != 32) {
            throw new IllegalArgumentException("String must be exactly 32 characters");
        }
        data = new byte[16];
        for (int i = 0; i < data.length; i++) {
            String str = s.substring(2 * i, 2 * i + 1);
            data[i] = Byte.parseByte(str, 16);
        }
    }
    
    public MD5(byte data[]) {
        this.data = new byte[16];
        if (data.length != this.data.length) {
            throw new IllegalArgumentException("Must have " + this.data.length
                    + " bytes of data");
        }
        System.arraycopy(data, 0, this.data, 0, this.data.length);
    }

    public MD5(InputStream in) throws IOException {
        data = Misc.getBytesFromStream(in, 16);
    }

    public boolean equals(Object obj) {
        if (obj instanceof MD5) {
            MD5 m = (MD5) obj;

            for (int i = 0; i < data.length; i++) {
                if (data[i] != m.data[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        // as good as anything
        return data[0] + data[1] << 8 + data[2] << 16 + data[3] << 24;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String s = Integer.toHexString(data[i] & 0xFF);
            if (s.length() == 1) {
                sb.append(0);
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
