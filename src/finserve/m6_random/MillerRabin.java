package finserve.m6_random;

import java.math.BigInteger;
import java.util.Random;

/**
 * Miller-Rabin probabilistic primality test.
 * FinServe uses it for cryptographic / large-number computations such as
 * validating account-number checksums and generating keys.
 */
public final class MillerRabin {

    private static final int[] SMALL_PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

    private MillerRabin() { }

    /** Deterministic for all 64-bit values using the first 12 primes as bases. */
    public static boolean isPrime(long n) {
        if (n < 2) {
            return false;
        }
        for (int p : SMALL_PRIMES) {
            if (n == p) {
                return true;
            }
            if (n % p == 0) {
                return false;
            }
        }
        long d = n - 1;
        int r = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            r++;
        }
        for (int a : SMALL_PRIMES) {
            if (a >= n) {
                continue;
            }
            if (!witness(a, d, r, n)) {
                return false;
            }
        }
        return true;
    }

    private static boolean witness(long a, long d, int r, long n) {
        long x = modPow(a, d, n);
        if (x == 1 || x == n - 1) {
            return true;
        }
        for (int i = 0; i < r - 1; i++) {
            x = modMul(x, x, n);
            if (x == n - 1) {
                return true;
            }
        }
        return false;
    }

    private static long modMul(long a, long b, long mod) {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).longValue();
    }

    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        long b = base % mod;
        long e = exp;
        while (e > 0) {
            if ((e & 1) == 1) {
                result = modMul(result, b, mod);
            }
            b = modMul(b, b, mod);
            e >>= 1;
        }
        return result;
    }

    /** Randomized Miller-Rabin for arbitrarily large integers (BigInteger). */
    public static boolean isProbablePrime(BigInteger n, int rounds) {
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return false;
        }
        for (int p : SMALL_PRIMES) {
            BigInteger bp = BigInteger.valueOf(p);
            if (n.equals(bp)) {
                return true;
            }
            if (n.mod(bp).signum() == 0) {
                return false;
            }
        }
        BigInteger nMinus1 = n.subtract(BigInteger.ONE);
        BigInteger d = nMinus1;
        int r = 0;
        while (!d.testBit(0)) {
            d = d.shiftRight(1);
            r++;
        }
        Random rnd = new Random(42);
        for (int i = 0; i < rounds; i++) {
            BigInteger a = new BigInteger(n.bitLength(), rnd).mod(n.subtract(BigInteger.ONE)).add(BigInteger.ONE);
            BigInteger x = a.modPow(d, n);
            if (x.equals(BigInteger.ONE) || x.equals(nMinus1)) {
                continue;
            }
            boolean composite = true;
            for (int j = 0; j < r - 1; j++) {
                x = x.multiply(x).mod(n);
                if (x.equals(nMinus1)) {
                    composite = false;
                    break;
                }
            }
            if (composite) {
                return false;
            }
        }
        return true;
    }
}
