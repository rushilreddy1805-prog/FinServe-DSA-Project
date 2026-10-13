package finserve.m1_strings;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * Aho-Corasick automaton for detecting MANY fraud patterns in one pass over a
 * transaction/document stream. Linear in (text length + total matches).
 */
public final class AhoCorasick {

    public static final int ALPHABET = 256;

    public static final class Node {
        int[] next = new int[ALPHABET];
        int fail;
        List<Integer> out = new ArrayList<Integer>();

        Node() {
            Arrays.fill(next, -1);
        }
    }

    public static final class Match {
        public final String pattern;
        public final int endIndex; // inclusive index in text

        Match(String pattern, int endIndex) {
            this.pattern = pattern;
            this.endIndex = endIndex;
        }

        public int startIndex() {
            return endIndex - pattern.length() + 1;
        }

        @Override
        public String toString() {
            return "'" + pattern + "' @ [" + startIndex() + ".." + endIndex + "]";
        }
    }

    private final List<Node> trie = new ArrayList<Node>();
    private final List<String> patterns = new ArrayList<String>();

    public AhoCorasick(List<String> pats) {
        trie.add(new Node());
        for (String p : pats) {
            if (p == null || p.isEmpty()) {
                continue;
            }
            addPattern(p, patterns.size());
            patterns.add(p);
        }
        buildFailureLinks();
    }

    private void addPattern(String p, int index) {
        int state = 0;
        for (int i = 0; i < p.length(); i++) {
            int c = p.charAt(i) & 0xFF;
            if (trie.get(state).next[c] == -1) {
                trie.get(state).next[c] = trie.size();
                trie.add(new Node());
            }
            state = trie.get(state).next[c];
        }
        trie.get(state).out.add(index);
    }

    private void buildFailureLinks() {
        Queue<Integer> queue = new ArrayDeque<Integer>();
        Node root = trie.get(0);
        for (int c = 0; c < ALPHABET; c++) {
            int child = root.next[c];
            if (child != -1) {
                trie.get(child).fail = 0;
                queue.add(child);
            } else {
                root.next[c] = 0;
            }
        }
        while (!queue.isEmpty()) {
            int state = queue.poll();
            Node cur = trie.get(state);
            for (int c = 0; c < ALPHABET; c++) {
                int child = cur.next[c];
                if (child != -1) {
                    int fail = cur.fail;
                    Node failNode = trie.get(fail);
                    while (failNode.next[c] == -1) {
                        fail = trie.get(fail).fail;
                        failNode = trie.get(fail);
                    }
                    int target = trie.get(fail).next[c];
                    trie.get(child).fail = (target == child) ? 0 : target;
                    trie.get(child).out.addAll(trie.get(trie.get(child).fail).out);
                    queue.add(child);
                }
            }
        }
    }

    /** Scans text once and reports every fraud pattern occurrence. */
    public List<Match> search(String text) {
        List<Match> matches = new ArrayList<Match>();
        int state = 0;
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i) & 0xFF;
            while (trie.get(state).next[c] == -1) {
                state = trie.get(state).fail;
            }
            state = trie.get(state).next[c];
            for (int idx : trie.get(state).out) {
                matches.add(new Match(patterns.get(idx), i));
            }
        }
        return matches;
    }

    public int nodeCount() {
        return trie.size();
    }

    public List<String> patterns() {
        return patterns;
    }
}
