package finserve.m2_suffix;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Suffix Automaton: a minimal DFA accepting every substring of the text in
 * O(n) states. Used by FinServe for fast substring / pattern existence queries
 * over a financial document or transaction corpus.
 */
public final class SuffixAutomaton {

    private final String text;
    private final int[] len;
    private final int[] link;
    private final int[] endPos; // one end index (inclusive) per state
    private final long[] occ;
    private final List<Map<Character, Integer>> next;
    private int last;
    private int size;
    private final int capacity;

    public SuffixAutomaton(String text) {
        this.text = text;
        capacity = Math.max(2, 2 * text.length() + 2);
        len = new int[capacity];
        link = new int[capacity];
        endPos = new int[capacity];
        occ = new long[capacity];
        next = new ArrayList<Map<Character, Integer>>(capacity);
        for (int i = 0; i < capacity; i++) {
            next.add(null);
        }
        link[0] = -1;
        next.set(0, new HashMap<Character, Integer>());
        last = 0;
        size = 1;
        for (int i = 0; i < text.length(); i++) {
            extend(text.charAt(i), i);
        }
        computeOccurrences();
    }

    private void extend(char c, int index) {
        int cur = size++;
        len[cur] = len[last] + 1;
        endPos[cur] = index;
        next.set(cur, new HashMap<Character, Integer>());
        occ[cur] = 1;
        int p = last;
        while (p != -1 && !next.get(p).containsKey(c)) {
            next.get(p).put(c, cur);
            p = link[p];
        }
        if (p == -1) {
            link[cur] = 0;
        } else {
            int q = next.get(p).get(c);
            if (len[p] + 1 == len[q]) {
                link[cur] = q;
            } else {
                int clone = size++;
                len[clone] = len[p] + 1;
                next.set(clone, new HashMap<Character, Integer>(next.get(q)));
                link[clone] = link[q];
                endPos[clone] = endPos[q];
                occ[clone] = 0;
                while (p != -1 && next.get(p).containsKey(c)
                        && next.get(p).get(c) == q) {
                    next.get(p).put(c, clone);
                    p = link[p];
                }
                link[q] = clone;
                link[cur] = clone;
            }
        }
        last = cur;
    }

    private void computeOccurrences() {
        Integer[] order = new Integer[size];
        for (int i = 0; i < size; i++) {
            order[i] = i;
        }
        final int[] lenRef = len;
        java.util.Arrays.sort(order, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                return lenRef[b] - lenRef[a];
            }
        });
        for (int idx = 0; idx < size; idx++) {
            int v = order[idx];
            if (link[v] != -1) {
                occ[link[v]] += occ[v];
            }
        }
    }

    /** True iff pattern is a substring of the original text. O(|pattern|). */
    public boolean contains(String pattern) {
        int state = 0;
        for (int i = 0; i < pattern.length(); i++) {
            Integer nx = next.get(state).get(pattern.charAt(i));
            if (nx == null) {
                return false;
            }
            state = nx;
        }
        return true;
    }

    /** Number of occurrences of pattern in the text (0 if absent). */
    public long countOccurrences(String pattern) {
        int state = 0;
        for (int i = 0; i < pattern.length(); i++) {
            Integer nx = next.get(state).get(pattern.charAt(i));
            if (nx == null) {
                return 0L;
            }
            state = nx;
        }
        return occ[state];
    }

    /** Number of distinct substrings = sum(len[v] - len[link[v]]). */
    public long distinctSubstrings() {
        long total = 0;
        for (int v = 1; v < size; v++) {
            total += len[v] - len[link[v]];
        }
        return total;
    }

    /** Longest substring occurring at least twice (empty if none). */
    public String longestRepeatedSubstring() {
        int bestLen = 0;
        int bestState = -1;
        for (int v = 1; v < size; v++) {
            if (occ[v] >= 2 && len[v] > bestLen) {
                bestLen = len[v];
                bestState = v;
            }
        }
        if (bestState == -1 || bestLen == 0) {
            return "";
        }
        int end = endPos[bestState];
        int start = end - bestLen + 1;
        return text.substring(start, end + 1);
    }

    public int stateCount() {
        return size;
    }
}
