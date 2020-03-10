import java.nio.charset.StandardCharsets;

public class MurmurHash {

    /** Returns the MurmurHash3_x86_32 hash.
     * https://github.com/yonik/java_util/blob/master/src/util/hash/MurmurHash3.java
     */
    public static int murmurhash3_x86_32(final byte[] data, int offset, int len, int seed) {

        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;

        int h1 = seed;
        int roundedEnd = offset + (len & 0xfffffffc);  // round down to 4 byte block

        for (int i=offset; i<roundedEnd; i+=4) {
            // little endian load order
            int k1 = (data[i] & 0xff) | ((data[i+1] & 0xff) << 8) | ((data[i+2] & 0xff) << 16) | (data[i+3] << 24);
            k1 *= c1;
            k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
            k1 *= c2;

            h1 ^= k1;
            h1 = (h1 << 13) | (h1 >>> 19);  // ROTL32(h1,13);
            h1 = h1*5+0xe6546b64;
        }

        // tail
        int k1 = 0;

        switch(len & 0x03) {
            case 3:
                k1 = (data[roundedEnd + 2] & 0xff) << 16;
                // fallthrough
            case 2:
                k1 |= (data[roundedEnd + 1] & 0xff) << 8;
                // fallthrough
            case 1:
                k1 |= (data[roundedEnd] & 0xff);
                k1 *= c1;
                k1 = (k1 << 15) | (k1 >>> 17);  // ROTL32(k1,15);
                k1 *= c2;
                h1 ^= k1;
        }

        // finalization
        h1 ^= len;

        // fmix(h1);
        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return h1;
    }

    static public int murmur3_32(String key) {
        final byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        return murmurhash3_x86_32(bytes, 0, bytes.length, 0);
    }

    public static void test() {
        String[] sample_input = {
                "",
                "a",
                "bc",
                "def",
                "ghij",
                "klmno",
                "pqrstu",
                "vwxyz"
        };
        int[] expected = {
                0,
                1009084850,
                1328615756,
                -176587294,
                326125929,
                2107458430,
                -813194880,
                192452857
        };

        for (int i = 0; i < sample_input.length; ++i) {

            int res = murmur3_32(new String(sample_input[i]));
            if (res != expected[i]) {
                System.err.print("expected: ");
                System.err.print(expected[i]);
                System.err.print(" but got: ");
                System.err.println(res);
            }
        }
        System.out.println("testing done!");
    }

}
