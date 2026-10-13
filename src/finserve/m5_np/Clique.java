package finserve.m5_np;

import java.util.ArrayList;
import java.util.List;

/**
 * Maximum / k-Clique search plus the 3-SAT -> CLIQUE reduction.
 * FinServe uses cliques to find groups of mutually-conflicting constraints that
 * must be resolved together.
 */
public final class Clique {

    private Clique() { }

    /** Returns a maximum clique as a list of vertices (branch and bound). */
    public static List<Integer> maxClique(int n, boolean[][] adj) {
        List<Integer> best = new ArrayList<Integer>();
        List<Integer> current = new ArrayList<Integer>();
        int[] degree = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (adj[i][j]) {
                    degree[i]++;
                }
            }
        }
        expand(0, n, adj, degree, current, best);
        return best;
    }

    private static void expand(int start, int n, boolean[][] adj, int[] degree,
                               List<Integer> current, List<Integer> best) {
        if (start == n) {
            if (current.size() > best.size()) {
                best.clear();
                best.addAll(current);
            }
            return;
        }
        if (current.size() + (n - start) <= best.size()) {
            return; // bound
        }
        // Try including vertex 'start'
        boolean canAdd = true;
        for (int v : current) {
            if (!adj[v][start]) {
                canAdd = false;
                break;
            }
        }
        if (canAdd) {
            current.add(start);
            expand(start + 1, n, adj, degree, current, best);
            current.remove(current.size() - 1);
        }
        // Try excluding vertex 'start'
        expand(start + 1, n, adj, degree, current, best);
    }

    public static boolean hasCliqueOfSize(int n, boolean[][] adj, int k) {
        return maxClique(n, adj).size() >= k;
    }

    // ------------------------------------------------ 3-SAT -> CLIQUE reduction

    public static final class Reduction {
        public final int vertices;
        public final boolean[][] adj;
        public final List<String> vertexLabels;
        public final int k;

        Reduction(int vertices, boolean[][] adj, List<String> vertexLabels, int k) {
            this.vertices = vertices;
            this.adj = adj;
            this.vertexLabels = vertexLabels;
            this.k = k;
        }
    }

    /**
     * Constructs the standard reduction: one triangle per clause (its 3
     * literals), edges between literals of different clauses unless they are
     * complementary. The formula is satisfiable iff the graph has an m-clique
     * (m = number of clauses).
     */
    public static Reduction reduceFrom3Sat(List<int[]> clauses) {
        List<Integer> literals = new ArrayList<Integer>();
        List<Integer> clauseId = new ArrayList<Integer>();
        for (int c = 0; c < clauses.size(); c++) {
            for (int lit : clauses.get(c)) {
                literals.add(lit);
                clauseId.add(c);
            }
        }
        int vCount = literals.size();
        boolean[][] adj = new boolean[vCount][vCount];
        List<String> labels = new ArrayList<String>();
        for (int i = 0; i < vCount; i++) {
            labels.add(clauseId.get(i) + ":" + literalName(literals.get(i)));
        }
        for (int i = 0; i < vCount; i++) {
            for (int j = i + 1; j < vCount; j++) {
                if (!clauseId.get(i).equals(clauseId.get(j))
                        && literals.get(i) != -literals.get(j)) {
                    adj[i][j] = true;
                    adj[j][i] = true;
                }
            }
        }
        return new Reduction(vCount, adj, labels, clauses.size());
    }

    private static String literalName(int lit) {
        return lit > 0 ? "x" + lit : "!x" + Math.abs(lit);
    }
}
