package finserve.m6_random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Reservoir sampling: uniformly sample k items from a continuous transaction
 * stream using O(k) memory, without knowing the stream length in advance.
 */
public final class ReservoirSampling {

    private ReservoirSampling() { }

    public static <T> List<T> sample(List<T> stream, int k, long seed) {
        List<T> reservoir = new ArrayList<T>(k);
        Random rnd = new Random(seed);
        int seen = 0;
        for (T item : stream) {
            if (seen < k) {
                reservoir.add(item);
            } else {
                int j = rnd.nextInt(seen + 1);
                if (j < k) {
                    reservoir.set(j, item);
                }
            }
            seen++;
        }
        return reservoir;
    }

    public static int[] sample(int[] stream, int k, long seed) {
        int[] reservoir = new int[Math.min(k, stream.length)];
        Random rnd = new Random(seed);
        for (int i = 0; i < stream.length; i++) {
            if (i < k) {
                reservoir[i] = stream[i];
            } else {
                int j = rnd.nextInt(i + 1);
                if (j < k) {
                    reservoir[j] = stream[i];
                }
            }
        }
        return reservoir;
    }

    /** Online reservoir for a live/continuous stream. */
    public static final class Reservoir<T> {
        private final int k;
        private final List<T> items = new ArrayList<T>();
        private final Random rnd;
        private long count;

        public Reservoir(int k, long seed) {
            this.k = k;
            this.rnd = new Random(seed);
        }

        public void add(T value) {
            count++;
            if (items.size() < k) {
                items.add(value);
            } else {
                int j = rnd.nextInt((int) Math.min(Integer.MAX_VALUE, count));
                if (j < k) {
                    items.set(j, value);
                }
            }
        }

        public List<T> snapshot() {
            return new ArrayList<T>(items);
        }

        public long seen() {
            return count;
        }
    }
}
