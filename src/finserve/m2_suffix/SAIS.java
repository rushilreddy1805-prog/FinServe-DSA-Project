package finserve.m2_suffix;

import java.util.Arrays;

/**
 * SA-IS: linear time suffix array construction (Suffix Array Induced Sorting).
 * Used for large-scale financial-document indexing where O(n log n) is too slow.
 *
 * Reference: G. Nong, S. Zhang and W. H. Chan, "Two Efficient Algorithms for
 * Linear Time Suffix Array Construction". Implementation follows the ACL
 * formulation.
 */
public final class SAIS {

    private SAIS() { }

    /** Builds the suffix array of a string (no explicit sentinel required). */
    public static int[] build(String str) {
        int n = str.length();
        if (n == 0) {
            return new int[0];
        }
        int[] s = new int[n];
        int upper = 0;
        for (int i = 0; i < n; i++) {
            s[i] = str.charAt(i);
            if (s[i] > upper) {
                upper = s[i];
            }
        }
        // Keep s[i] <= upper - 1 so bucket index s[i] + 1 stays in range.
        return saIs(s, upper + 1);
    }

    private static int[] saIs(int[] s, int upper) {
        int n = s.length;
        if (n == 0) {
            return new int[0];
        }
        if (n == 1) {
            return new int[]{0};
        }
        if (n == 2) {
            return (s[0] < s[1]) ? new int[]{0, 1} : new int[]{1, 0};
        }

        int[] sa = new int[n];
        boolean[] ls = new boolean[n]; // true = S-type
        for (int i = n - 2; i >= 0; i--) {
            ls[i] = (s[i] == s[i + 1]) ? ls[i + 1] : (s[i] < s[i + 1]);
        }

        int[] sumL = new int[upper + 1];
        int[] sumS = new int[upper + 1];
        for (int i = 0; i < n; i++) {
            if (!ls[i]) {
                sumS[s[i]]++;
            } else {
                sumL[s[i] + 1]++;
            }
        }
        for (int i = 0; i <= upper; i++) {
            sumS[i] += sumL[i];
            if (i < upper) {
                sumL[i + 1] += sumS[i];
            }
        }

        int[] lmsMap = new int[n + 1];
        Arrays.fill(lmsMap, -1);
        int m = 0;
        for (int i = 1; i < n; i++) {
            if (!ls[i - 1] && ls[i]) {
                lmsMap[i] = m++;
            }
        }
        int[] lms = new int[m];
        int idx = 0;
        for (int i = 1; i < n; i++) {
            if (!ls[i - 1] && ls[i]) {
                lms[idx++] = i;
            }
        }

        induce(s, sa, ls, sumL, sumS, upper, lms, n);

        if (m > 0) {
            int[] sortedLms = new int[m];
            int p = 0;
            for (int v : sa) {
                if (v >= 0 && lmsMap[v] != -1) {
                    sortedLms[p++] = v;
                }
            }
            int[] recS = new int[m];
            int recUpper = 0;
            recS[lmsMap[sortedLms[0]]] = 0;
            for (int i = 1; i < m; i++) {
                int l = sortedLms[i - 1];
                int r = sortedLms[i];
                int endL = (lmsMap[l] + 1 < m) ? lms[lmsMap[l] + 1] : n;
                int endR = (lmsMap[r] + 1 < m) ? lms[lmsMap[r] + 1] : n;
                boolean same = true;
                if (endL - l != endR - r) {
                    same = false;
                } else {
                    while (l < endL) {
                        if (s[l] != s[r]) {
                            break;
                        }
                        l++;
                        r++;
                    }
                    if (l == n || s[l] != s[r]) {
                        same = false;
                    }
                }
                if (!same) {
                    recUpper++;
                }
                recS[lmsMap[sortedLms[i]]] = recUpper;
            }

            int[] recSa = saIs(recS, recUpper + 1);
            for (int i = 0; i < m; i++) {
                sortedLms[i] = lms[recSa[i]];
            }
            induce(s, sa, ls, sumL, sumS, upper, sortedLms, n);
        }
        return sa;
    }

    private static void induce(int[] s, int[] sa, boolean[] ls, int[] sumL, int[] sumS,
                               int upper, int[] lms, int n) {
        Arrays.fill(sa, -1);
        int[] buf = new int[upper + 1];
        System.arraycopy(sumS, 0, buf, 0, upper + 1);
        for (int d : lms) {
            if (d == n) {
                continue;
            }
            sa[buf[s[d]]++] = d;
        }
        System.arraycopy(sumL, 0, buf, 0, upper + 1);
        sa[buf[s[n - 1]]++] = n - 1;
        for (int i = 0; i < n; i++) {
            int v = sa[i];
            if (v >= 1 && !ls[v - 1]) {
                sa[buf[s[v - 1]]++] = v - 1;
            }
        }
        System.arraycopy(sumL, 0, buf, 0, upper + 1);
        for (int i = n - 1; i >= 0; i--) {
            int v = sa[i];
            if (v >= 1 && ls[v - 1]) {
                sa[--buf[s[v - 1] + 1]] = v - 1;
            }
        }
    }
}
