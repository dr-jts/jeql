package jeql.std.function;

import jeql.api.function.FunctionClass;

public class HashFunction implements FunctionClass {
  
  public static int hilbertIndex(int order, int x, int y) {
    // Fast Hilbert curve algorithm by http://threadlocalmutex.com/
    // Ported from C++ https://github.com/rawrunprotected/hilbert_curves (public
    // domain)

    // clamp order to [1, 16]
    int ord = order < 1 ? 1 : order;
    ord = ord > 16 ? 16 : ord;
    
    x = x << (16 - ord);
    y = y << (16 - ord);
    
    long a = x ^ y;
    long b = 0xFFFF ^ a;
    long c = 0xFFFF ^ (x | y);
    long d = x & (y ^ 0xFFFF);

    long A = a | (b >> 1);
    long B = (a >> 1) ^ a;
    long C = ((c >> 1) ^ (b & (d >> 1))) ^ c;
    long D = ((a & (c >> 1)) ^ (d >> 1)) ^ d;

    a = A;
    b = B;
    c = C;
    d = D;
    A = ((a & (a >> 2)) ^ (b & (b >> 2)));
    B = ((a & (b >> 2)) ^ (b & ((a ^ b) >> 2)));
    C ^= ((a & (c >> 2)) ^ (b & (d >> 2)));
    D ^= ((b & (c >> 2)) ^ ((a ^ b) & (d >> 2)));

    a = A;
    b = B;
    c = C;
    d = D;
    A = ((a & (a >> 4)) ^ (b & (b >> 4)));
    B = ((a & (b >> 4)) ^ (b & ((a ^ b) >> 4)));
    C ^= ((a & (c >> 4)) ^ (b & (d >> 4)));
    D ^= ((b & (c >> 4)) ^ ((a ^ b) & (d >> 4)));

    a = A;
    b = B;
    c = C;
    d = D;
    C ^= ((a & (c >> 8)) ^ (b & (d >> 8)));
    D ^= ((b & (c >> 8)) ^ ((a ^ b) & (d >> 8)));

    a = C ^ (C >> 1);
    b = D ^ (D >> 1);

    long i0 = x ^ y;
    long i1 = b | (0xFFFF ^ (i0 | a));

    i0 = (i0 | (i0 << 8)) & 0x00FF00FF;
    i0 = (i0 | (i0 << 4)) & 0x0F0F0F0F;
    i0 = (i0 | (i0 << 2)) & 0x33333333;
    i0 = (i0 | (i0 << 1)) & 0x55555555;

    i1 = (i1 | (i1 << 8)) & 0x00FF00FF;
    i1 = (i1 | (i1 << 4)) & 0x0F0F0F0F;
    i1 = (i1 | (i1 << 2)) & 0x33333333;
    i1 = (i1 | (i1 << 1)) & 0x55555555;

    long index = ((i1 << 1) | i0) >> (32 - 2 * ord);
    return (int) index;
  }
}
