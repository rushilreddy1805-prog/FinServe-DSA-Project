package finserve.m3_dp;

/**
 * Weighted edit distance: insertion, deletion and substitution carry different
 * (configurable) costs, modelling the real cost of fixing financial data errors.
 * Can also penalise substitutions by how different decimal digits are.
 */
public final class WeightedEditDistance {

    private WeightedEditDistance() { }

    public static int distance(String a, String b, int insertCost, int deleteCost, int subCost) {
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i * deleteCost;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j * insertCost;
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int rep = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : subCost;
                int best = Math.min(dp[i - 1][j] + deleteCost, dp[i][j - 1] + insertCost);
                best = Math.min(best, dp[i - 1][j - 1] + rep);
                dp[i][j] = best;
            }
        }
        return dp[n][m];
    }

    /**
     * Digit-aware variant: substituting one digit for another costs the
     * absolute difference of the two digits, so changing 1 -> 2 is cheaper than
     * 1 -> 9. Non-digit substitutions use the supplied default cost.
     */
    public static int digitAwareDistance(String a, String b, int insertCost, int deleteCost, int defaultSubCost) {
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i * deleteCost;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j * insertCost;
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                char ca = a.charAt(i - 1);
                char cb = b.charAt(j - 1);
                int rep;
                if (ca == cb) {
                    rep = 0;
                } else if (Character.isDigit(ca) && Character.isDigit(cb)) {
                    rep = Math.abs(ca - cb);
                } else {
                    rep = defaultSubCost;
                }
                int best = Math.min(dp[i - 1][j] + deleteCost, dp[i][j - 1] + insertCost);
                best = Math.min(best, dp[i - 1][j - 1] + rep);
                dp[i][j] = best;
            }
        }
        return dp[n][m];
    }
}
