package org.spacebar.escape.common.hash;

import java.io.DataInput;
import java.io.IOException;

public class MD5 {
    final byte data[] = new byte[16];

    public MD5(String s) {
        if (s.length() != 32) {
            throw new IllegalArgumentException("String must be exactly 32 characters");
        }
        for (int i = 0; i < data.length; i++) {
            int idx = 2 * i;
            String str = s.substring(idx, idx + 2);
            data[i] = (byte) (Integer.parseInt(str, 16) & 0xFF);
        }
    }
    
    public MD5(byte data[]) {
        if (data.length != this.data.length) {
            throw new IllegalArgumentException("Must have " + this.data.length
                    + " bytes of data");
        }
        System.arraycopy(data, 0, this.data, 0, this.data.length);
    }

    public MD5(DataInput in) throws IOException {
        in.readFully(data);
    }

    @Override
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

    @Override
    public int hashCode() {
        // as good as anything
        return data[0] + data[1] << 8 + data[2] << 16 + data[3] << 24;
    }
    
    @Override
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
