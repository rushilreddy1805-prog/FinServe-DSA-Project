package finserve.m3_dp;

/**
 * Matrix-Chain Multiplication DP.
 * Models a sequence of chained transaction-analysis operations where the order
 * of multiplication (parenthesisation) changes the total scalar-operation cost.
 */
public final class MatrixChain {

    public static final class Plan {
        public final long minCost;
        public final String order;

        Plan(long minCost, String order) {
            this.minCost = minCost;
            this.order = order;
        }
    }

    private MatrixChain() { }

    /** p[i-1] x p[i] is the dimension of matrix i (i = 1..n). */
    public static Plan solve(int[] p) {
        int n = p.length - 1;
        long[][] m = new long[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];
        for (int len = 2; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                m[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long q = m[i][k] + m[k + 1][j] + (long) p[i - 1] * p[k] * p[j];
                    if (q < m[i][j]) {
                        m[i][j] = q;
                        s[i][j] = k;
                    }
                }
            }
        }
        String order = (n == 0) ? "" : buildOrder(s, 1, n);
        return new Plan(m[1][n], order);
    }

    private static String buildOrder(int[][] s, int i, int j) {
        if (i == j) {
            return "A" + i;
        }
        int k = s[i][j];
        return "(" + buildOrder(s, i, k) + " x " + buildOrder(s, k + 1, j) + ")";
    }
}
