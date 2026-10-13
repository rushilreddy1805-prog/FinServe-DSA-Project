package finserve.m3_dp;

import java.util.Arrays;

/**
 * Bitmask DP over subsets.
 * FinServe uses it to pick the best combination/assignment of financial
 * operations (e.g. assign N analysts to N fraud cases at minimum cost).
 */
public final class BitmaskDP {

    public static final class Assignment {
        public final int cost;
        public final int[] assign; // assign[i] = task chosen for person i

        Assignment(int cost, int[] assign) {
            this.cost = cost;
            this.assign = assign;
        }
    }

    private BitmaskDP() { }

    /**
     * Minimum-cost perfect assignment. cost[i][j] = cost of giving task j to
     * person i. O(n * 2^n).
     */
    public static Assignment minCostAssignment(int[][] cost) {
        int n = cost.length;
        int full = 1 << n;
        int[] dp = new int[full];
        int[] parent = new int[full];
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        Arrays.fill(parent, -1);
        dp[0] = 0;
        for (int mask = 0; mask < full; mask++) {
            int i = Integer.bitCount(mask); // assign person i next
            if (i >= n) {
                continue;
            }
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) == 0) {
                    int next = mask | (1 << j);
                    int val = dp[mask] + cost[i][j];
                    if (val < dp[next]) {
                        dp[next] = val;
                        parent[next] = mask;
                    }
                }
            }
        }
        int[] assign = new int[n];
        int mask = full - 1;
        for (int i = n - 1; i >= 0; i--) {
            int prev = parent[mask];
            int added = mask ^ prev;
            int j = Integer.numberOfTrailingZeros(added);
            assign[i] = j;
            mask = prev;
        }
        return new Assignment(dp[full - 1], assign);
    }

    /**
     * Maximum value obtainable choosing a subset of financial operations whose
     * total resource usage must not exceed a budget (0/1 knapsack via bitmask).
     * Only practical for small n; demonstrates subset enumeration.
     */
    public static int maxValueSubset(int[] value, int[] weight, int budget) {
        int n = value.length;
        int full = 1 << n;
        int best = 0;
        for (int mask = 0; mask < full; mask++) {
            int w = 0;
            int v = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    w += weight[i];
                    v += value[i];
                }
            }
            if (w <= budget && v > best) {
                best = v;
            }
        }
        return best;
    }

    /** Counts set bits (helper exposed for the CLI summary). */
    public static int popcount(int x) {
        return Integer.bitCount(x);
    }
}
