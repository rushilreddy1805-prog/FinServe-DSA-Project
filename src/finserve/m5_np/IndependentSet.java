package finserve.m5_np;

import java.util.List;

/**
 * Maximum Independent Set via reduction to Maximum Clique on the complement
 * graph: an independent set in G is exactly a clique in complement(G).
 * FinServe uses it to pick a maximum set of mutually-compatible operations.
 */
public final class IndependentSet {

    private IndependentSet() { }

    public static boolean[][] complement(int n, boolean[][] adj) {
        boolean[][] comp = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    comp[i][j] = !adj[i][j];
                }
            }
        }
        return comp;
    }

    public static List<Integer> maxIndependentSet(int n, boolean[][] adj) {
        return Clique.maxClique(n, complement(n, adj));
    }
}
