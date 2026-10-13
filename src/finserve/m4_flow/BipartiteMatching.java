package finserve.m4_flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Bipartite matching (Kuhn's augmenting-path algorithm).
 * Used to assign financial analysts to fraud cases so that the maximum number
 * of cases (within skill compatibility) is covered.
 */
public final class BipartiteMatching {

    public static final class Result {
        public final int matchingSize;
        public final int[] analystToCase; // -1 if unmatched
        public final int[] caseToAnalyst; // -1 if unmatched

        Result(int matchingSize, int[] analystToCase, int[] caseToAnalyst) {
            this.matchingSize = matchingSize;
            this.analystToCase = analystToCase;
            this.caseToAnalyst = caseToAnalyst;
        }
    }

    private BipartiteMatching() { }

    /**
     * @param leftSize number of analysts
     * @param adj      adj[i] = cases analyst i is qualified for
     */
    public static Result maxMatching(int leftSize, List<List<Integer>> adj) {
        int[] matchCase = new int[adj.size() > 0 ? maxRight(adj) + 1 : 0];
        Arrays.fill(matchCase, -1);
        int[] matchAnalyst = new int[leftSize];
        Arrays.fill(matchAnalyst, -1);

        int matching = 0;
        for (int u = 0; u < leftSize; u++) {
            boolean[] visited = new boolean[matchCase.length];
            if (tryKuhn(u, adj, matchCase, visited)) {
                matching++;
            }
        }
        for (int c = 0; c < matchCase.length; c++) {
            if (matchCase[c] != -1) {
                matchAnalyst[matchCase[c]] = c;
            }
        }
        return new Result(matching, matchAnalyst, matchCase);
    }

    private static boolean tryKuhn(int u, List<List<Integer>> adj, int[] matchCase, boolean[] visited) {
        for (int c : adj.get(u)) {
            if (!visited[c]) {
                visited[c] = true;
                if (matchCase[c] == -1 || tryKuhn(matchCase[c], adj, matchCase, visited)) {
                    matchCase[c] = u;
                    return true;
                }
            }
        }
        return false;
    }

    private static int maxRight(List<List<Integer>> adj) {
        int max = 0;
        for (List<Integer> row : adj) {
            for (int c : row) {
                if (c > max) {
                    max = c;
                }
            }
        }
        return max;
    }

    /** Convenience builder for the demo: symmetric compatibility matrix. */
    public static List<List<Integer>> fromMatrix(boolean[][] compatible) {
        List<List<Integer>> adj = new ArrayList<List<Integer>>();
        for (boolean[] row : compatible) {
            List<Integer> list = new ArrayList<Integer>();
            for (int j = 0; j < row.length; j++) {
                if (row[j]) {
                    list.add(j);
                }
            }
            adj.add(list);
        }
        return adj;
    }
}
