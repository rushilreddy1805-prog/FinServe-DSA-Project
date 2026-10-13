package finserve.m6_random;

import java.util.Arrays;

/**
 * Blelloch's work-efficient parallel prefix (scan) algorithm.
 * Simulates the up-sweep / down-sweep phases used to compute cumulative
 * transaction statistics (running totals) in parallel with O(n) work and
 * O(log n) depth.
 */
public final class BlellochScan {

    public static final class ScanResult {
        public final long[] exclusive;
        public final long[] inclusive;
        public final int paddedLength;

        ScanResult(long[] exclusive, long[] inclusive, int paddedLength) {
            this.exclusive = exclusive;
            this.inclusive = inclusive;
            this.paddedLength = paddedLength;
        }
    }

    private BlellochScan() { }

    public static ScanResult scan(long[] input) {
        int n = input.length;
        if (n == 0) {
            return new ScanResult(new long[0], new long[0], 0);
        }
        int m = 1;
        while (m < n) {
            m <<= 1;
        }
        long[] a = new long[m];
        System.arraycopy(input, 0, a, 0, n);

        // Up-sweep (reduce) phase.
        for (int d = 1; d < m; d <<= 1) {
            for (int i = 0; i < m; i += 2 * d) {
                a[i + 2 * d - 1] += a[i + d - 1];
            }
        }

        // Down-sweep phase.
        a[m - 1] = 0;
        for (int d = m / 2; d >= 1; d >>= 1) {
            for (int i = 0; i < m; i += 2 * d) {
                long t = a[i + d - 1];
                a[i + d - 1] = a[i + 2 * d - 1];
                a[i + 2 * d - 1] += t;
            }
        }

        long[] exclusive = Arrays.copyOf(a, n);
        long[] inclusive = new long[n];
        for (int i = 0; i < n; i++) {
            inclusive[i] = exclusive[i] + input[i];
        }
        return new ScanResult(exclusive, inclusive, m);
    }
}
