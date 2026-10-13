package finserve.m4_flow;

import java.util.List;

/**
 * Minimum s-t cut (via the max-flow min-cut theorem).
 * Identifies the critical transaction channels whose combined capacity is the
 * bottleneck of the payment network: removing them disconnects source from sink.
 */
public final class MinCut {

    private MinCut() { }

    public static final class CutReport {
        public final int maxFlow;
        public final int cutCapacity;
        public final List<int[]> cutEdges;

        CutReport(int maxFlow, int cutCapacity, List<int[]> cutEdges) {
            this.maxFlow = maxFlow;
            this.cutCapacity = cutCapacity;
            this.cutEdges = cutEdges;
        }
    }

    /** Runs Dinic then extracts the corresponding minimum cut. */
    public static CutReport compute(FlowNetwork net, int s, int t) {
        int flow = net.dinic(s, t);
        FlowNetwork.MinCut cut = net.minCut();
        return new CutReport(flow, cut.capacity, cut.cutEdges);
    }
}
