package finserve.m2_suffix;

/**
 * Kasai's algorithm: computes the LCP (Longest Common Prefix) array from a
 * suffix array in O(n). Used to mine repeated transaction-description patterns.
 */
public final class KasaiLCP {

    private KasaiLCP() { }

    /** lcp[i] = LCP of suffixes sa[i-1] and sa[i]; lcp[0] = 0. */
    public static int[] build(String s, int[] sa) {
        int n = s.length();
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }
        int[] lcp = new int[n];
        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--;
                }
            } else {
                h = 0;
            }
        }
        return lcp;
    }

    /** Longest repeated substring found through the largest LCP value. */
    public static String longestRepeatedSubstring(String s, int[] sa, int[] lcp) {
        int best = 0;
        int bestIndex = -1;
        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > best) {
                best = lcp[i];
                bestIndex = i;
            }
        }
        if (bestIndex < 0 || best == 0) {
            return "";
        }
        int pos = sa[bestIndex];
        return s.substring(pos, pos + best);
    }
}
