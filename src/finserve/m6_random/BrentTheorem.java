package finserve.m6_random;

/**
 * Brent's theorem for parallel transaction processing.
 * Given total work W, depth (span) D and P processors, the running time is
 * bounded by  T_P <= W/P + D,  which is work-optimal and asymptotically tight.
 */
public final class BrentTheorem {

    private BrentTheorem() { }

    /** Upper-bound running time T_P <= W/P + D. */
    public static double parallelTime(double work, double depth, int processors) {
        if (processors <= 0) {
            throw new IllegalArgumentException("processors must be positive");
        }
        return work / processors + depth;
    }

    /** Speedup relative to single-processor work, S = W / T_P. */
    public static double speedup(double work, double depth, int processors) {
        return work / parallelTime(work, depth, processors);
    }

    /** Parallel efficiency E = S / P. */
    public static double efficiency(double work, double depth, int processors) {
        return speedup(work, depth, processors) / processors;
    }
}
