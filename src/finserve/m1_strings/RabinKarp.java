package finserve.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Rabin-Karp rolling-hash search. Used to search financial codes where many
 * candidates must be filtered quickly by hash before a character check.
 */
public final class RabinKarp {

    private static final long BASE = 256L;
    private static final long MOD = 1000000007L;

    private RabinKarp() { }

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

        long highPower = 1;
        for (int i = 0; i < m - 1; i++) {
            highPower = (highPower * BASE) % MOD;
        }

        long patternHash = 0;
        long windowHash = 0;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * BASE + pattern.charAt(i)) % MOD;
            windowHash = (windowHash * BASE + text.charAt(i)) % MOD;
        }

        for (int i = 0; i <= n - m; i++) {
            if (patternHash == windowHash && regionMatches(text, i, pattern)) {
                hits.add(i);
            }
            if (i < n - m) {
                windowHash = (windowHash - text.charAt(i) * highPower) % MOD;
                if (windowHash < 0) {
                    windowHash += MOD;
                }
                windowHash = (windowHash * BASE + text.charAt(i + m)) % MOD;
            }
        }
        return hits;
    }

    private static boolean regionMatches(String text, int start, String pattern) {
        for (int k = 0; k < pattern.length(); k++) {
            if (text.charAt(start + k) != pattern.charAt(k)) {
                return false;
            }
        }
        return true;
    }
}
