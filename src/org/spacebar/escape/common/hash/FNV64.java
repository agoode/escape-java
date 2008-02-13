package org.spacebar.escape.common.hash;

public class FNV64 {
    /*
     * fnv - Fowler/Noll/Vo- hash code **
     * 
     * Fowler/Noll/Vo- hash
     * 
     * The basis of this hash algorithm was taken from an idea sent as reviewer
     * comments to the IEEE POSIX P1003.2 committee by:
     * 
     * Phong Vo (http://www.research.att.com/info/kpv/) Glenn Fowler
     * (http://www.research.att.com/~gsf/)
     * 
     * In a subsequent ballot round:
     * 
     * Landon Curt Noll (http://www.isthe.com/chongo/)
     * 
     * improved on their algorithm. Some people tried this hash and found that
     * it worked rather well. In an EMail message to Landon, they named it the
     * ``Fowler/Noll/Vo'' or FNV hash.
     * 
     * FNV hashes are designed to be fast while maintaining a low collision
     * rate. The FNV speed allows one to quickly hash lots of data while
     * maintaining a reasonable collision rate. See:
     * 
     * http://www.isthe.com/chongo/tech/comp/fnv/index.html
     * 
     * for more details as well as other forms of the FNV hash. **
     * 
     * NOTE: The FNV-0 historic hash is not recommended. One should use the
     * FNV-1 hash instead. **
     * 
     * Please do not copyright this code. This code is in the public domain.
     * 
     * LANDON CURT NOLL DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
     * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO
     * EVENT SHALL LANDON CURT NOLL BE LIABLE FOR ANY SPECIAL, INDIRECT OR
     * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
     * USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
     * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
     * PERFORMANCE OF THIS SOFTWARE.
     * 
     * By: chongo <Landon Curt Noll> /\oo/\ http://www.isthe.com/chongo/
     * 
     * Share and Enjoy! :-)
     */

    /*
     * 64 bit FNV-1 non-zero initial basis
     *
     * The FNV-1 initial basis is the FNV-0 hash of the following 32 octets:
     *
     *              chongo <Landon Curt Noll> /\../\
     *
     * NOTE: The \'s above are not back-slashing escape characters.
     * They are literal ASCII  backslash 0x5c characters.
     *
     * NOTE: The FNV-1a initial basis is the same value as FNV-1 by definition.
     */
    final public static long FNV1_64_INIT = 0xcbf29ce484222325L;

    final public static long FNV_64_PRIME = 0x100000001b3L;

    public long hval = FNV1_64_INIT;

    final public void fnv64(byte octet) {
        /* multiply by the 64 bit FNV magic prime mod 2^64 */
        hval *= FNV_64_PRIME;

        /* xor the bottom with the current octet */
        hval ^= octet & 0xFF; // stupid sign extension
    }

    final public void fnv64(int dword) {
        fnv64((byte) (dword & 0xFF));
        dword >>= 8;
        fnv64((byte) (dword & 0xFF));
        dword >>= 8;
        fnv64((byte) (dword & 0xFF));
        dword >>= 8;
        fnv64((byte) (dword & 0xFF));
    }

    final public void reset() {
        hval = FNV1_64_INIT;
    }
    
    @Override
    public int hashCode() {
        return (int) (hval & 0xFFFFFFFF);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FNV64) {
            FNV64 f = (FNV64) obj;
            return hval == f.hval;
        }
        return false;
    }
}
