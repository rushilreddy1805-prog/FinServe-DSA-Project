package finserve.m6_random;

import java.util.ArrayList;
import java.util.List;

/**
 * Parallel tree reduction for financial aggregation (e.g. total transaction
 * volume). Shows the O(log n) depth / O(n) work structure.
 */
public final class ParallelReduce {

    private ParallelReduce() { }

    /** Sequential reference sum. */
    public static long sum(int[] a) {
        long s = 0;
        for (int v : a) {
            s += v;
        }
        return s;
    }

    /** Simulates the tree: returns the value present at each level. */
    public static List<long[]> levels(int[] a) {
        List<long[]> result = new ArrayList<long[]>();
        if (a.length == 0) {
            return result;
        }
        long[] cur = new long[a.length];
        for (int i = 0; i < a.length; i++) {
            cur[i] = a[i];
        }
        result.add(cur.clone());
        while (cur.length > 1) {
            int nextLen = (cur.length + 1) / 2;
            long[] next = new long[nextLen];
            for (int i = 0; i < nextLen; i++) {
                long left = cur[2 * i];
                long right = (2 * i + 1 < cur.length) ? cur[2 * i + 1] : 0;
                next[i] = left + right;
            }
            result.add(next);
            cur = next;
        }
        return result;
    }

    public static long work(int n) {
        return n <= 0 ? 0 : n - 1;
    }

    public static int depth(int n) {
        int d = 0;
        int x = Math.max(1, n);
        while (x > 1) {
            x = (x + 1) / 2;
            d++;
        }
        return d;
    }
}
