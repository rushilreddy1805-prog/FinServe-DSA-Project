package finserve.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Z-function based matching. Used to detect repeated transaction-description
 * patterns in O(n + m).
 */
public final class ZFunction {

    private ZFunction() { }

    /** Returns the Z-array: z[i] = length of longest substring starting at i
     *  that is also a prefix of s. */
    public static int[] compute(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0;
        int r = 0;
        for (int i = 1; i < n; i++) {
            if (i < r) {
                z[i] = Math.min(r - i, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] > r) {
                l = i;
                r = i + z[i];
            }
        }
        return z;
    }

    /** Finds all occurrences of pattern in text using the pattern#text trick. */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> hits = new ArrayList<Integer>();
        if (pattern == null || pattern.isEmpty() || text == null) {
            return hits;
        }
        String combined = pattern + "$" + text;
        int[] z = compute(combined);
        int m = pattern.length();
        for (int i = 0; i < combined.length(); i++) {
            if (z[i] == m) {
                hits.add(i - m - 1);
            }
        }
        return hits;
    }
}
