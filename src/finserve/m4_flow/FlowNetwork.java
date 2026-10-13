package finserve.m4_flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * Directed capacity network used to model transaction flow between accounts.
 * Provides three classic maximum-flow algorithms plus min-cut extraction:
 *   - Ford-Fulkerson (DFS augmenting paths)
 *   - Edmonds-Karp   (BFS augmenting paths)
 *   - Dinic          (level graph + blocking flow)
 */
public final class FlowNetwork {

    public static final class Edge {
        int to;
        int rev;
        int cap;      // residual capacity
        final int originalCap;

        Edge(int to, int rev, int cap, int originalCap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.originalCap = originalCap;
        }
    }

    public static final class MinCut {
        public final int capacity;
        public final boolean[] sourceSide;
        public final List<int[]> cutEdges; // {u, v, cap}

        MinCut(int capacity, boolean[] sourceSide, List<int[]> cutEdges) {
            this.capacity = capacity;
            this.sourceSide = sourceSide;
            this.cutEdges = cutEdges;
        }
    }

    private final int n;
    private final List<int[]> edges = new ArrayList<int[]>();
    private final String[] labels;
    private List<Edge>[] graph;
    private List<Edge> forwardRefs;
    private int lastFlow;
    private int source;
    private int sink;

    @SuppressWarnings("unchecked")
    public FlowNetwork(int n, String[] labels) {
        this.n = n;
        this.labels = labels != null ? labels : defaultLabels(n);
        this.graph = new List[n];
    }

    private static String[] defaultLabels(int n) {
        String[] l = new String[n];
        for (int i = 0; i < n; i++) {
            l[i] = "N" + i;
        }
        return l;
    }

    public int nodes() {
        return n;
    }

    public String label(int i) {
        return labels[i];
    }

    public void addEdge(int u, int v, int cap) {
        edges.add(new int[]{u, v, cap});
    }

    public int edgeCount() {
        return edges.size();
    }

    public int maxFlowValue() {
        return lastFlow;
    }

    @SuppressWarnings("unchecked")
    private void buildResidual() {
        graph = new List[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<Edge>();
        }
        forwardRefs = new ArrayList<Edge>();
        for (int[] e : edges) {
            int u = e[0];
            int v = e[1];
            int c = e[2];
            Edge a = new Edge(v, graph[v].size(), c, c);
            Edge b = new Edge(u, graph[u].size(), 0, 0);
            graph[u].add(a);
            graph[v].add(b);
            forwardRefs.add(a);
        }
    }

    // ------------------------------------------------------------------ FF

    public int fordFulkerson(int s, int t) {
        buildResidual();
        source = s;
        sink = t;
        int flow = 0;
        while (true) {
            boolean[] visited = new boolean[n];
            int pushed = dfsFF(s, t, Integer.MAX_VALUE, visited);
            if (pushed == 0) {
                break;
            }
            flow += pushed;
        }
        lastFlow = flow;
        return flow;
    }

    private int dfsFF(int u, int t, int pushed, boolean[] visited) {
        if (u == t) {
            return pushed;
        }
        visited[u] = true;
        for (Edge e : graph[u]) {
            if (!visited[e.to] && e.cap > 0) {
                int tr = dfsFF(e.to, t, Math.min(pushed, e.cap), visited);
                if (tr > 0) {
                    e.cap -= tr;
                    graph[e.to].get(e.rev).cap += tr;
                    return tr;
                }
            }
        }
        return 0;
    }

    // ------------------------------------------------------------- Edmonds-Karp

    public int edmondsKarp(int s, int t) {
        buildResidual();
        source = s;
        sink = t;
        int flow = 0;
        while (true) {
            int[] parentEdge = new int[n];
            int[] parentNode = new int[n];
            Arrays.fill(parentEdge, -1);
            Arrays.fill(parentNode, -1);
            Queue<Integer> queue = new ArrayDeque<Integer>();
            queue.add(s);
            parentNode[s] = s;
            boolean found = false;
            while (!queue.isEmpty() && !found) {
                int u = queue.poll();
                for (int i = 0; i < graph[u].size(); i++) {
                    Edge e = graph[u].get(i);
                    if (e.cap > 0 && parentNode[e.to] == -1) {
                        parentNode[e.to] = u;
                        parentEdge[e.to] = i;
                        if (e.to == t) {
                            found = true;
                            break;
                        }
                        queue.add(e.to);
                    }
                }
            }
            if (!found) {
                break;
            }
            int bottleneck = Integer.MAX_VALUE;
            for (int v = t; v != s; v = parentNode[v]) {
                Edge e = graph[parentNode[v]].get(parentEdge[v]);
                bottleneck = Math.min(bottleneck, e.cap);
            }
            for (int v = t; v != s; v = parentNode[v]) {
                Edge e = graph[parentNode[v]].get(parentEdge[v]);
                e.cap -= bottleneck;
                graph[e.to].get(e.rev).cap += bottleneck;
            }
            flow += bottleneck;
        }
        lastFlow = flow;
        return flow;
    }

    // ------------------------------------------------------------------ Dinic

    public int dinic(int s, int t) {
        buildResidual();
        source = s;
        sink = t;
        int flow = 0;
        int[] level = new int[n];
        int[] iter = new int[n];
        while (bfsLevel(s, t, level)) {
            Arrays.fill(iter, 0);
            int f;
            while ((f = dfsDinic(s, t, Integer.MAX_VALUE, level, iter)) > 0) {
                flow += f;
            }
        }
        lastFlow = flow;
        return flow;
    }

    private boolean bfsLevel(int s, int t, int[] level) {
        Arrays.fill(level, -1);
        Queue<Integer> queue = new ArrayDeque<Integer>();
        level[s] = 0;
        queue.add(s);
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (Edge e : graph[u]) {
                if (e.cap > 0 && level[e.to] < 0) {
                    level[e.to] = level[u] + 1;
                    queue.add(e.to);
                }
            }
        }
        return level[t] >= 0;
    }

    private int dfsDinic(int u, int t, int pushed, int[] level, int[] iter) {
        if (u == t) {
            return pushed;
        }
        for (; iter[u] < graph[u].size(); iter[u]++) {
            Edge e = graph[u].get(iter[u]);
            if (e.cap > 0 && level[u] < level[e.to]) {
                int tr = dfsDinic(e.to, t, Math.min(pushed, e.cap), level, iter);
                if (tr > 0) {
                    e.cap -= tr;
                    graph[e.to].get(e.rev).cap += tr;
                    return tr;
                }
            }
        }
        return 0;
    }

    // --------------------------------------------------------------- Min cut

    /**
     * Extracts the minimum s-t cut from the residual graph of the last max-flow
     * run. Must call one of the max-flow methods first.
     */
    public MinCut minCut() {
        boolean[] reachable = new boolean[n];
        Queue<Integer> queue = new ArrayDeque<Integer>();
        reachable[source] = true;
        queue.add(source);
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (Edge e : graph[u]) {
                if (e.cap > 0 && !reachable[e.to]) {
                    reachable[e.to] = true;
                    queue.add(e.to);
                }
            }
        }
        List<int[]> cut = new ArrayList<int[]>();
        int capacity = 0;
        for (int[] e : edges) {
            if (reachable[e[0]] && !reachable[e[1]]) {
                cut.add(new int[]{e[0], e[1], e[2]});
                capacity += e[2];
            }
        }
        return new MinCut(capacity, reachable, cut);
    }

    /** Flow currently routed on each original edge (after a max-flow run). */
    public List<int[]> edgeFlows() {
        List<int[]> result = new ArrayList<int[]>();
        for (int i = 0; i < edges.size(); i++) {
            int[] e = edges.get(i);
            Edge fwd = forwardRefs.get(i);
            int used = fwd.originalCap - fwd.cap;
            result.add(new int[]{e[0], e[1], e[2], used});
        }
        return result;
    }
}
