package finserve.m2_suffix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Suffix array of a transaction-description corpus using prefix doubling
 * (O(n log^2 n) with comparison sort, simple and robust). Also provides
 * binary-search based substring search.
 */
public final class SuffixArray {

    private SuffixArray() { }

    public static int[] build(String s) {
        int n = s.length();
        Integer[] sa = new Integer[n];
        int[] rank = new int[n];
        int[] tmp = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = i;
            rank[i] = s.charAt(i);
        }
        for (int k = 1; k < n; k <<= 1) {
            final int kk = k;
            final int[] r = rank;
            Arrays.sort(sa, new Comparator<Integer>() {
                public int compare(Integer a, Integer b) {
                    if (r[a] != r[b]) {
                        return r[a] - r[b];
                    }
                    int ra = (a + kk < r.length) ? r[a + kk] : -1;
                    int rb = (b + kk < r.length) ? r[b + kk] : -1;
                    return ra - rb;
                }
            });
            tmp[sa[0]] = 0;
            for (int i = 1; i < n; i++) {
                tmp[sa[i]] = tmp[sa[i - 1]] + (compare(sa[i - 1], sa[i], rank, k) < 0 ? 1 : 0);
            }
            System.arraycopy(tmp, 0, rank, 0, n);
            if (rank[sa[n - 1]] == n - 1) {
                break;
            }
        }
        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            out[i] = sa[i];
        }
        return out;
    }

    private static int compare(int a, int b, int[] rank, int k) {
        if (rank[a] != rank[b]) {
            return rank[a] - rank[b];
        }
        int ra = (a + k < rank.length) ? rank[a + k] : -1;
        int rb = (b + k < rank.length) ? rank[b + k] : -1;
        return ra - rb;
    }

    /** Naive O(n^2 log n) builder kept for correctness cross-checking. */
    public static int[] buildNaive(String s) {
        int n = s.length();
        Integer[] sa = new Integer[n];
        for (int i = 0; i < n; i++) {
            sa[i] = i;
        }
        final String str = s;
        Arrays.sort(sa, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                return str.substring(a).compareTo(str.substring(b));
            }
        });
        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            out[i] = sa[i];
        }
        return out;
    }

    /** All positions where pattern occurs, found via two binary searches. */
    public static List<Integer> search(String s, int[] sa, String pattern) {
        List<Integer> hits = new ArrayList<Integer>();
        int n = sa.length;
        int lo = lowerBound(s, sa, pattern);
        for (int i = lo; i < n; i++) {
            String suffix = s.substring(sa[i], Math.min(s.length(), sa[i] + pattern.length()));
            if (suffix.equals(pattern)) {
                hits.add(sa[i]);
            } else {
                break;
            }
        }
        return hits;
    }

    private static int lowerBound(String s, int[] sa, String pattern) {
        int lo = 0;
        int hi = sa.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            String suffix = s.substring(sa[mid], Math.min(s.length(), sa[mid] + pattern.length()));
            if (suffix.compareTo(pattern) < 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }
}
