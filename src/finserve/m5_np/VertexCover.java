package finserve.m5_np;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Vertex Cover: exact (via CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER) and the
 * classic 2-approximation (maximal matching).
 * FinServe uses vertex cover to select a minimum set of control points that
 * "cover" every risky dependency (edge) in the operation graph.
 */
public final class VertexCover {

    private VertexCover() { }

    /**
     * Exact minimum vertex cover: complement of a maximum independent set.
     * A set C is a vertex cover iff V \ C is an independent set.
     */
    public static List<Integer> minVertexCoverExact(int n, boolean[][] adj) {
        List<Integer> mis = IndependentSet.maxIndependentSet(n, adj);
        Set<Integer> inSet = new HashSet<Integer>(mis);
        List<Integer> cover = new ArrayList<Integer>();
        for (int v = 0; v < n; v++) {
            if (!inSet.contains(v)) {
                cover.add(v);
            }
        }
        return cover;
    }

    /**
     * 2-approximation: greedily take both endpoints of every edge of a maximal
     * matching, then include every vertex that touches these edges.
     */
    public static List<Integer> approx2(int n, List<int[]> edges) {
        boolean[] usedVertex = new boolean[n];
        for (int[] e : edges) {
            int u = e[0];
            int v = e[1];
            if (!usedVertex[u] && !usedVertex[v]) {
                usedVertex[u] = true;
                usedVertex[v] = true;
            }
        }
        List<Integer> cover = new ArrayList<Integer>();
        for (int v = 0; v < n; v++) {
            if (usedVertex[v]) {
                cover.add(v);
            }
        }
        return cover;
    }

    public static boolean isVertexCover(int n, List<int[]> edges, List<Integer> cover) {
        Set<Integer> set = new HashSet<Integer>(cover);
        for (int[] e : edges) {
            if (!set.contains(e[0]) && !set.contains(e[1])) {
                return false;
            }
        }
        return true;
    }
}
