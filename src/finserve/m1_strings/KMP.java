package finserve.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt string matching.
 * Used by FinServe to locate transaction / payment codes fast (O(n + m)).
 */
public final class KMP {

    private KMP() { }

    /** Builds the longest-proper-prefix-suffix (failure) array for a pattern. */
    public static int[] buildFailure(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    /** Returns every starting index (0-based) of pattern inside text. */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> hits = new ArrayList<Integer>();
        if (pattern == null || pattern.isEmpty() || text == null) {
            return hits;
        }
        int n = text.length();
        int m = pattern.length();
        if (m > n) {
            return hits;
        }
        int[] lps = buildFailure(pattern);
        int i = 0;
        int j = 0;
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    hits.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j != 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return hits;
    }
}
