package finserve.m6_random;

import java.util.Random;

/**
 * Randomized QuickSort used to rank transactions by amount / risk.
 * A random pivot gives expected O(n log n) time regardless of input order,
 * defeating adversarial inputs.
 */
public final class RandomizedQuickSort {

    public static final class Stats {
        public final long comparisons;
        public final int maxDepth;

        Stats(long comparisons, int maxDepth) {
            this.comparisons = comparisons;
            this.maxDepth = maxDepth;
        }
    }

    private long comparisons;
    private int depth;
    private int maxDepth;

    private RandomizedQuickSort() { }

    public static Stats sort(int[] a, long seed) {
        RandomizedQuickSort sorter = new RandomizedQuickSort();
        sorter.quicksort(a, 0, a.length - 1, new Random(seed));
        return new Stats(sorter.comparisons, sorter.maxDepth);
    }

    public static Stats sort(int[] a) {
        return sort(a, System.nanoTime());
    }

    private void quicksort(int[] a, int lo, int hi, Random rnd) {
        if (lo >= hi) {
            return;
        }
        depth++;
        if (depth > maxDepth) {
            maxDepth = depth;
        }
        int pivotIndex = lo + rnd.nextInt(hi - lo + 1);
        int pivot = a[pivotIndex];
        int p = partition(a, lo, hi, pivot);
        quicksort(a, lo, p - 1, rnd);
        quicksort(a, p + 1, hi, rnd);
        depth--;
    }

    /** Three-way partition (handles duplicates) returning the split point. */
    private int partition(int[] a, int lo, int hi, int pivot) {
        int lt = lo;
        int gt = hi;
        int i = lo;
        while (i <= gt) {
            comparisons++;
            if (a[i] < pivot) {
                swap(a, lt++, i++);
            } else if (a[i] > pivot) {
                swap(a, i, gt--);
            } else {
                i++;
            }
        }
        return lt;
    }

    private void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
