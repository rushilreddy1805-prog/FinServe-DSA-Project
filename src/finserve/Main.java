package finserve;

import finserve.m1_strings.AhoCorasick;
import finserve.m1_strings.KMP;
import finserve.m1_strings.RabinKarp;
import finserve.m1_strings.ZFunction;
import finserve.m2_suffix.KasaiLCP;
import finserve.m2_suffix.SAIS;
import finserve.m2_suffix.SuffixArray;
import finserve.m2_suffix.SuffixAutomaton;
import finserve.m3_dp.BitmaskDP;
import finserve.m3_dp.EditDistance;
import finserve.m3_dp.MatrixChain;
import finserve.m3_dp.WeightedEditDistance;
import finserve.m4_flow.BipartiteMatching;
import finserve.m4_flow.FlowNetwork;
import finserve.m4_flow.MinCut;
import finserve.m5_np.Clique;
import finserve.m5_np.IndependentSet;
import finserve.m5_np.Sat3;
import finserve.m5_np.VertexCover;
import finserve.m6_random.BlellochScan;
import finserve.m6_random.BrentTheorem;
import finserve.m6_random.MillerRabin;
import finserve.m6_random.ParallelReduce;
import finserve.m6_random.RandomizedQuickSort;
import finserve.m6_random.ReservoirSampling;
import finserve.model.Account;
import finserve.model.Customer;
import finserve.model.Document;
import finserve.model.Transaction;
import finserve.util.Console;
import finserve.util.DataStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * FinServe - Intelligent Banking Analytics Platform.
 * Interactive menu driven CLI demonstrating all six DSA modules.
 */
public final class Main {

    public static void main(String[] args) {
        banner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = Console.readInt("Select an option: ", 0, 8);
            switch (choice) {
                case 1: module1(); break;
                case 2: module2(); break;
                case 3: module3(); break;
                case 4: module4(); break;
                case 5: module5(); break;
                case 6: module6(); break;
                case 7: showData(); break;
                case 8: about(); break;
                case 0:
                    running = false;
                    Console.line();
                    Console.info("Thank you for using FinServe. Goodbye!");
                    Console.line();
                    break;
                default: break;
            }
        }
    }

    // ------------------------------------------------------------------ menu

    private static void banner() {
        Console.line();
        System.out.println("   FINSERVE  -  Intelligent Banking Analytics Platform");
        System.out.println("   DSA End-Semester Project  |  Modules M1-M6");
        Console.line();
    }

    private static void printMainMenu() {
        Console.section("MAIN MENU  -  What would you like to do?");
        System.out.println("   1. Search & detect things in text      [Module M1]");
        System.out.println("   2. Build an index to look things up    [Module M2]");
        System.out.println("   3. Fix errors & plan operations        [Module M3]");
        System.out.println("   4. Route money & assign work           [Module M4]");
        System.out.println("   5. Check rules & find groups           [Module M5]");
        System.out.println("   6. Rank, sample & speed up             [Module M6]");
        System.out.println("   7. Show Banking Data");
        System.out.println("   8. About FinServe");
        System.out.println("   0. Exit");
    }

    // -------------------------------------------------------------- module 1

    private static void module1() {
        while (true) {
            Console.title("SEARCH & DETECT IN TEXT  (Module M1 / CO1)");
            System.out.println("   1. Find a payment or transaction code in text");
            System.out.println("        (Algorithm: KMP)");
            System.out.println("   2. Find a repeated pattern in descriptions");
            System.out.println("        (Algorithm: Z-Function)");
            System.out.println("   3. Find a financial code using fast hashing");
            System.out.println("        (Algorithm: Rabin-Karp)");
            System.out.println("   4. Find many fraud patterns in a single scan");
            System.out.println("        (Algorithm: Aho-Corasick)");
            System.out.println("   0. Back");
            int c = Console.readInt("   Select: ", 0, 4);
            if (c == 0) {
                return;
            }
            if (c == 1) {
                runKmp();
            } else if (c == 2) {
                runZ();
            } else if (c == 3) {
                runRabinKarp();
            } else {
                runAhoCorasick();
            }
            Console.pause();
        }
    }

    private static void runKmp() {
        Console.section("Find a payment / transaction code in text  (Algorithm: KMP)");
        String text = askText("Enter text", DataStore.transactionCorpus());
        String pattern = ask("Enter pattern/code to search", "NEFT");
        int[] lps = KMP.buildFailure(pattern);
        List<Integer> hits = KMP.search(text, pattern);
        Console.info("Search helper table: " + Arrays.toString(lps));
        Console.result("Found " + hits.size() + " occurrence(s) of \"" + pattern + "\" at indices " + hits);
    }

    private static void runZ() {
        Console.section("Find a repeated pattern in descriptions  (Algorithm: Z-Function)");
        String text = askText("Enter text", DataStore.transactionCorpus());
        String pattern = ask("Enter pattern to search", "transfer");
        List<Integer> hits = ZFunction.search(text, pattern);
        int[] z = ZFunction.compute(pattern + "$" + text);
        Console.info("Pattern-match table size = " + z.length);
        Console.result("Found " + hits.size() + " occurrence(s) of \"" + pattern + "\" at indices " + hits);
    }

    private static void runRabinKarp() {
        Console.section("Find a financial code using fast hashing  (Algorithm: Rabin-Karp)");
        String text = askText("Enter text", DataStore.transactionCorpus());
        String pattern = ask("Enter financial code", "IMPS");
        List<Integer> hits = RabinKarp.search(text, pattern);
        Console.result("Found " + hits.size() + " occurrence(s) of \"" + pattern + "\" at indices " + hits);
    }

    private static void runAhoCorasick() {
        Console.section("Find many fraud patterns in a single scan  (Algorithm: Aho-Corasick)");
        String text = askText("Enter text to scan", DataStore.documentCorpus());
        Console.info("Fraud patterns: " + DataStore.fraudPatterns());
        AhoCorasick ac = new AhoCorasick(DataStore.fraudPatterns());
        List<AhoCorasick.Match> matches = ac.search(text);
        Console.info("Pattern scanner built with " + ac.nodeCount() + " states.");
        if (matches.isEmpty()) {
            Console.warn("No fraud patterns found.");
        }
        for (AhoCorasick.Match m : matches) {
            Console.result("Fraud pattern found: " + m.toString());
        }
        Console.result("Total fraud-pattern hits = " + matches.size());
    }

    // -------------------------------------------------------------- module 2

    private static void module2() {
        while (true) {
            Console.title("BUILD AN INDEX TO LOOK THINGS UP  (Module M2 / CO2)");
            System.out.println("   1. Build a searchable index of transaction descriptions");
            System.out.println("        (Algorithm: Suffix Array)");
            System.out.println("   2. Build an index for large documents very fast");
            System.out.println("        (Algorithm: SA-IS)");
            System.out.println("   3. Find the longest repeated phrase");
            System.out.println("        (Algorithm: Kasai LCP)");
            System.out.println("   4. Check whether a phrase exists and how often");
            System.out.println("        (Algorithm: Suffix Automaton)");
            System.out.println("   0. Back");
            int c = Console.readInt("   Select: ", 0, 4);
            if (c == 0) {
                return;
            }
            if (c == 1) {
                runSuffixArray();
            } else if (c == 2) {
                runSais();
            } else if (c == 3) {
                runKasai();
            } else {
                runSuffixAutomaton();
            }
            Console.pause();
        }
    }

    private static void runSuffixArray() {
        Console.section("Build a searchable index of transaction descriptions  (Algorithm: Suffix Array)");
        String text = askText("Enter text", DataStore.transactionCorpus());
        int[] sa = SuffixArray.build(text);
        int show = Math.min(15, sa.length);
        Console.info("Indexed " + sa.length + " positions. First " + show + " (in sorted order):");
        for (int i = 0; i < show; i++) {
            String suffix = text.substring(sa[i]);
            if (suffix.length() > 50) {
                suffix = suffix.substring(0, 50) + "...";
            }
            System.out.printf("     [%3d] position=%3d  %s%n", i, sa[i], suffix);
        }
        String pattern = ask("Enter substring to search", "NEFT");
        List<Integer> hits = SuffixArray.search(text, sa, pattern);
        Console.result("Occurrences of \"" + pattern + "\" at " + hits);
    }

    private static void runSais() {
        Console.section("Build an index for large documents very fast  (Algorithm: SA-IS)");
        String text = askText("Enter text", DataStore.documentCorpus());
        long t0 = System.nanoTime();
        int[] sa = SAIS.build(text);
        long t1 = System.nanoTime();
        Console.info("Indexed " + sa.length + " positions in " + (t1 - t0) / 1000 + " microseconds.");
        int[] naive = SuffixArray.buildNaive(text);
        boolean same = Arrays.equals(sa, naive);
        Console.result("Index verified correct (matches a simple check): " + same);
        Console.info("First 10 positions (in sorted order):");
        for (int i = 0; i < Math.min(10, sa.length); i++) {
            String suffix = text.substring(sa[i]);
            if (suffix.length() > 45) {
                suffix = suffix.substring(0, 45) + "...";
            }
            System.out.printf("     [%3d] position=%3d  %s%n", i, sa[i], suffix);
        }
    }

    private static void runKasai() {
        Console.section("Find the longest repeated phrase  (Algorithm: Kasai LCP)");
        String text = askText("Enter text", DataStore.transactionCorpus());
        int[] sa = SuffixArray.build(text);
        int[] lcp = KasaiLCP.build(text, sa);
        int show = Math.min(12, lcp.length);
        Console.info("Common-prefix lengths between neighbours (first " + show + "): "
                + Arrays.toString(Arrays.copyOf(lcp, show)));
        String repeated = KasaiLCP.longestRepeatedSubstring(text, sa, lcp);
        Console.result("Longest repeated phrase = \"" + repeated + "\" (length " + repeated.length() + ")");
    }

    private static void runSuffixAutomaton() {
        Console.section("Check whether a phrase exists and how often  (Algorithm: Suffix Automaton)");
        String text = askText("Enter text", DataStore.documentCorpus());
        SuffixAutomaton sam = new SuffixAutomaton(text);
        Console.info("Search machine built with " + sam.stateCount() + " states.");
        Console.info("Number of distinct phrases = " + sam.distinctSubstrings());
        Console.info("Longest repeated phrase = \"" + sam.longestRepeatedSubstring() + "\"");
        String pattern = ask("Enter phrase to test", "transfer");
        Console.result("Phrase \"" + pattern + "\" exists = " + sam.contains(pattern));
        Console.result("Phrase \"" + pattern + "\" occurs " + sam.countOccurrences(pattern) + " time(s)");
    }

    // -------------------------------------------------------------- module 3

    private static void module3() {
        while (true) {
            Console.title("FIX ERRORS & PLAN OPERATIONS  (Module M3 / CO3)");
            System.out.println("   1. Measure/correct how different two texts are");
            System.out.println("        (Algorithm: Levenshtein / Damerau-Levenshtein)");
            System.out.println("   2. Compare codes where some mistakes cost more");
            System.out.println("        (Algorithm: Weighted Edit Distance)");
            System.out.println("   3. Assign cases to analysts at the lowest cost");
            System.out.println("        (Algorithm: Bitmask DP)");
            System.out.println("   4. Find the cheapest order to run operations");
            System.out.println("        (Algorithm: Matrix-Chain Multiplication)");
            System.out.println("   0. Back");
            int c = Console.readInt("   Select: ", 0, 4);
            if (c == 0) {
                return;
            }
            if (c == 1) {
                runEditDistance();
            } else if (c == 2) {
                runWeightedEdit();
            } else if (c == 3) {
                runBitmask();
            } else {
                runMatrixChain();
            }
            Console.pause();
        }
    }

    private static void runEditDistance() {
        Console.section("Measure / correct how different two texts are  (Algorithm: Levenshtein, Damerau-Levenshtein)");
        String a = ask("Enter first description", "NEFT transfer salary");
        String b = ask("Enter second description", "NEFT trnasfer salery");
        Console.info("Basic difference (edits needed) = " + EditDistance.levenshtein(a, b));
        Console.info("Difference allowing swapped letters = " + EditDistance.damerauLevenshtein(a, b));
        EditDistance.Result r = EditDistance.withOps(a, b);
        Console.info("Step-by-step fixes:");
        for (String op : r.ops) {
            System.out.println("       " + op);
        }
    }

    private static void runWeightedEdit() {
        Console.section("Compare codes where some mistakes cost more  (Algorithm: Weighted Edit Distance)");
        String a = ask("Enter first value", "ACC1001");
        String b = ask("Enter second value", "ACC1091");
        int ins = Console.readInt("Cost of adding a character: ", 0);
        int del = Console.readInt("Cost of removing a character: ", 0);
        int sub = Console.readInt("Cost of changing a character: ", 0);
        Console.info("Total difference cost = " + WeightedEditDistance.distance(a, b, ins, del, sub));
        Console.info("Digit-aware cost (nearer digits are cheaper) = "
                + WeightedEditDistance.digitAwareDistance(a, b, ins, del, sub));
    }

    private static void runBitmask() {
        Console.section("Assign cases to analysts at the lowest cost  (Algorithm: Bitmask DP)");
        int[][] cost = {
                {9, 2, 7, 8},
                {6, 4, 3, 7},
                {5, 8, 1, 8},
                {7, 6, 9, 4}
        };
        Console.info("Cost of giving each case to each analyst:");
        for (int[] row : cost) {
            Console.info("   " + Arrays.toString(row));
        }
        BitmaskDP.Assignment asg = BitmaskDP.minCostAssignment(cost);
        Console.result("Lowest total cost = " + asg.cost);
        for (int i = 0; i < asg.assign.length; i++) {
            Console.info("   Analyst " + i + " happens to case " + asg.assign[i]
                    + " (cost " + cost[i][asg.assign[i]] + ")");
        }
        int[] value = {60, 100, 120, 90};
        int[] weight = {10, 20, 30, 15};
        int budget = 45;
        Console.info("Picking operations within a budget:");
        Console.info("   values=" + Arrays.toString(value) + " costs=" + Arrays.toString(weight)
                + " budget=" + budget);
        Console.result("Best total value within budget = " + BitmaskDP.maxValueSubset(value, weight, budget));
    }

    private static void runMatrixChain() {
        Console.section("Find the cheapest order to run operations  (Algorithm: Matrix-Chain Multiplication)");
        int[] p = Console.readIntArray("Enter sizes of each step, n+1 numbers, e.g. 10,30,5,60");
        MatrixChain.Plan plan = MatrixChain.solve(p);
        Console.result("Fewest multiplications = " + plan.minCost);
        Console.result("Best order = " + plan.order);
    }

    // -------------------------------------------------------------- module 4

    private static void module4() {
        while (true) {
            Console.title("ROUTE MONEY & ASSIGN WORK  (Module M4 / CO4)");
            System.out.println("   1. Find how much money can flow through the network");
            System.out.println("        (Algorithm: Ford-Fulkerson)");
            System.out.println("   2. Same, using the shortest-path method");
            System.out.println("        (Algorithm: Edmonds-Karp)");
            System.out.println("   3. Same, for large networks");
            System.out.println("        (Algorithm: Dinic's algorithm)");
            System.out.println("   4. Assign analysts to fraud cases");
            System.out.println("        (Algorithm: Bipartite Matching)");
            System.out.println("   5. Find the bottleneck (critical) channels");
            System.out.println("        (Algorithm: Min-Cut)");
            System.out.println("   0. Back");
            int c = Console.readInt("   Select: ", 0, 5);
            if (c == 0) {
                return;
            }
            if (c == 4) {
                runBipartite();
            } else if (c == 5) {
                runMinCut();
            } else {
                runFlow(c);
            }
            Console.pause();
        }
    }

    private static FlowNetwork buildTransactionNetwork() {
        List<String> accounts = DataStore.accountNumbers();
        int n = accounts.size();
        String[] labels = accounts.toArray(new String[0]);
        FlowNetwork net = new FlowNetwork(n, labels);
        int[][] randomCap = DataStore.randomCapacityGraph(n, 90, 2026);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (randomCap[i][j] > 0) {
                    net.addEdge(i, j, randomCap[i][j]);
                }
            }
        }
        // Guarantee source -> sink connectivity with a backbone chain.
        for (int i = 0; i + 1 < n; i++) {
            net.addEdge(i, i + 1, 60 + i * 5);
        }
        return net;
    }

    private static void runFlow(int which) {
        FlowNetwork net = buildTransactionNetwork();
        int source = 0;
        int sink = net.nodes() - 1;
        String algo;
        if (which == 1) {
            algo = "Ford-Fulkerson";
        } else if (which == 2) {
            algo = "Edmonds-Karp";
        } else {
            algo = "Dinic's algorithm";
        }
        Console.section("Find how much money can flow through the network  (Algorithm: " + algo + ")");
        Console.info("Network: " + net.nodes() + " accounts, " + net.edgeCount() + " channels.");
        Console.info("Money moves from " + net.label(source) + " to " + net.label(sink) + ".");
        int flow;
        long t0 = System.nanoTime();
        if (which == 1) {
            flow = net.fordFulkerson(source, sink);
        } else if (which == 2) {
            flow = net.edmondsKarp(source, sink);
        } else {
            flow = net.dinic(source, sink);
        }
        long t1 = System.nanoTime();
        Console.result("Maximum money that can flow = " + flow);
        Console.info("Time taken = " + (t1 - t0) / 1000 + " microseconds");
        Console.info("Channels used (amount routed / channel limit):");
        for (int[] e : net.edgeFlows()) {
            if (e[3] > 0) {
                System.out.printf("     %-10s -> %-10s  %4d / %-4d%n",
                        net.label(e[0]), net.label(e[1]), e[3], e[2]);
            }
        }
    }

    private static void runBipartite() {
        Console.section("Assign analysts to fraud cases  (Algorithm: Bipartite Matching)");
        String[] analysts = {"Analyst-A", "Analyst-B", "Analyst-C", "Analyst-D", "Analyst-E", "Analyst-F"};
        boolean[][] compatible = {
                {true, true, false, false, false, true},
                {false, true, true, false, false, false},
                {true, false, false, true, true, false},
                {false, false, true, true, false, false},
                {false, false, false, true, true, true},
                {true, false, false, false, true, false}
        };
        Console.info("Compatibility matrix:");
        for (int i = 0; i < analysts.length; i++) {
            StringBuilder sb = new StringBuilder("   " + analysts[i] + " : ");
            for (int j = 0; j < compatible[i].length; j++) {
                sb.append(compatible[i][j] ? (j + 1) : '-').append(' ');
            }
            Console.info(sb.toString());
        }
        List<List<Integer>> adj = BipartiteMatching.fromMatrix(compatible);
        BipartiteMatching.Result r = BipartiteMatching.maxMatching(analysts.length, adj);
        Console.result("Maximum cases assigned = " + r.matchingSize + " / " + analysts.length);
        for (int i = 0; i < r.analystToCase.length; i++) {
            if (r.analystToCase[i] >= 0) {
                Console.info("   " + analysts[i] + " -> Case " + (r.analystToCase[i] + 1));
            } else {
                Console.warn("   " + analysts[i] + " -> unassigned");
            }
        }
    }

    private static void runMinCut() {
        Console.section("Find the bottleneck (critical) channels  (Algorithm: Min-Cut)");
        FlowNetwork net = buildTransactionNetwork();
        int source = 0;
        int sink = net.nodes() - 1;
        Console.info("Network: " + net.nodes() + " accounts, " + net.edgeCount() + " channels.");
        Console.info("Money moves from " + net.label(source) + " to " + net.label(sink) + ".");
        MinCut.CutReport rep = MinCut.compute(net, source, sink);
        Console.result("Maximum flow = minimum cut = " + rep.cutCapacity);
        Console.info("Critical channels (removing all of them blocks every payment):");
        for (int[] e : rep.cutEdges) {
            System.out.printf("     %-10s -> %-10s  limit %-4d%n",
                    net.label(e[0]), net.label(e[1]), e[2]);
        }
        Console.info("Number of critical channels = " + rep.cutEdges.size());
    }

    // -------------------------------------------------------------- module 5

    private static void module5() {
        while (true) {
            Console.title("CHECK RULES & FIND GROUPS  (Module M5 / CO5)");
            System.out.println("   1. Check whether financial rules can all be satisfied");
            System.out.println("        (Algorithm: SAT / 3-SAT)");
            System.out.println("   2. Turn rule conflicts into a group-finding problem");
            System.out.println("        (Algorithm: 3-SAT -> Clique)");
            System.out.println("   3. Find mutually-compatible / conflicting groups");
            System.out.println("        (Algorithm: Clique -> Independent Set -> Vertex Cover)");
            System.out.println("   4. Cover every dependency with few control points");
            System.out.println("        (Algorithm: Vertex-Cover 2-approximation)");
            System.out.println("   0. Back");
            int c = Console.readInt("   Select: ", 0, 4);
            if (c == 0) {
                return;
            }
            if (c == 1) {
                runSat();
            } else if (c == 2) {
                run3SatToClique();
            } else if (c == 3) {
                runCliqueChain();
            } else {
                runVertexCoverApprox();
            }
            Console.pause();
        }
    }

    private static List<int[]> sampleFormula() {
        List<int[]> clauses = new ArrayList<int[]>();
        clauses.add(new int[]{1, 2, -3});
        clauses.add(new int[]{-1, 3, 4});
        clauses.add(new int[]{-2, -4, 1});
        return clauses;
    }

    private static void runSat() {
        Console.section("Check whether financial rules can all be satisfied  (Algorithm: SAT / 3-SAT, DPLL)");
        Console.info("Type rules as a formula: numbers are conditions, '-' means NOT, '|' splits rules.");
        Console.info("Example:  1 2 -3 | -1 3 4 | -2 -4 1");
        String spec = askText("Rule formula", "1 2 -3 | -1 3 4 | -2 -4 1");
        List<int[]> clauses = Sat3.parse(spec);
        int nVars = 0;
        for (int[] c : clauses) {
            for (int lit : c) {
                nVars = Math.max(nVars, Math.abs(lit));
            }
        }
        Console.info("Conditions = " + nVars + ", rules = " + clauses.size());
        int[] assign = Sat3.solve(nVars, clauses);
        boolean brute = Sat3.bruteForce(nVars, clauses);
        Console.result("A valid solution found: " + Sat3.assignmentToString(assign));
        Console.result("Independent brute-force check says satisfiable = " + brute);
    }

    private static void run3SatToClique() {
        Console.section("Turn rule conflicts into a group-finding problem  (Algorithm: 3-SAT -> Clique)");
        List<int[]> clauses = sampleFormula();
        Console.info("Rule formula:");
        for (int[] c : clauses) {
            Console.info("   " + Sat3.clauseToString(c));
        }
        Clique.Reduction red = Clique.reduceFrom3Sat(clauses);
        Console.info("Built a graph: " + red.vertices + " nodes (3 per rule).");
        Console.info("Allowed pairings between rules (compatible conditions):");
        for (int i = 0; i < red.vertices; i++) {
            StringBuilder sb = new StringBuilder("   " + red.vertexLabels.get(i) + " : ");
            for (int j = 0; j < red.vertices; j++) {
                if (red.adj[i][j]) {
                    sb.append(red.vertexLabels.get(j)).append(' ');
                }
            }
            Console.info(sb.toString());
        }
        List<Integer> clique = Clique.maxClique(red.vertices, red.adj);
        Console.result("Largest compatible group = " + clique.size()
                + " (need " + red.k + " to satisfy all rules)");
        Console.info("Chosen conditions: " + labelsOf(red, clique));
        int[] assign = new int[10];
        for (int v : clique) {
            String label = red.vertexLabels.get(v);
            String litName = label.substring(label.indexOf(':') + 1);
            boolean neg = litName.startsWith("!");
            int var = Integer.parseInt(litName.replace("!", "").replace("x", ""));
            assign[var] = neg ? -1 : 1;
        }
        Console.result("Solution implied by the group: " + Sat3.assignmentToString(trim(assign)));
    }

    private static List<String> labelsOf(Clique.Reduction red, List<Integer> vs) {
        List<String> out = new ArrayList<String>();
        for (int v : vs) {
            out.add(red.vertexLabels.get(v));
        }
        return out;
    }

    private static int[] trim(int[] assign) {
        int last = 0;
        for (int i = 1; i < assign.length; i++) {
            if (assign[i] != 0) {
                last = i;
            }
        }
        return Arrays.copyOf(assign, last + 1);
    }

    private static void runCliqueChain() {
        Console.section("Find compatible / conflicting groups  (Algorithm: Clique -> Independent Set -> Vertex Cover)");
        int n = 6;
        boolean[][] adj = {
                {false, true, true, false, false, false},
                {true, false, true, true, false, false},
                {true, true, false, false, true, false},
                {false, true, false, false, true, true},
                {false, false, true, true, false, true},
                {false, false, false, true, true, false}
        };
        Console.info("Relationship graph (who connects to whom, nodes 0..5):");
        printGraph(n, adj);
        List<Integer> clique = Clique.maxClique(n, adj);
        Console.result("Largest fully-connected group (Clique) = " + clique + " (size " + clique.size() + ")");
        List<Integer> mis = IndependentSet.maxIndependentSet(n, adj);
        Console.result("Largest group with no connections (Independent Set) = " + mis + " (size " + mis.size() + ")");
        List<Integer> vc = VertexCover.minVertexCoverExact(n, adj);
        Console.result("Smallest set touching every connection (Vertex Cover) = " + vc + " (size " + vc.size() + ")");
        Console.info("Check: independent + cover sizes = " + (mis.size() + vc.size()) + " = total nodes = " + n);
    }

    private static void printGraph(int n, boolean[][] adj) {
        for (int i = 0; i < n; i++) {
            StringBuilder sb = new StringBuilder("   " + i + " : ");
            for (int j = 0; j < n; j++) {
                if (adj[i][j]) {
                    sb.append(j).append(' ');
                }
            }
            Console.info(sb.toString());
        }
    }

    private static void runVertexCoverApprox() {
        Console.section("Cover every dependency with few control points  (Algorithm: Vertex-Cover 2-approximation)");
        int n = 7;
        List<int[]> edges = new ArrayList<int[]>();
        edges.add(new int[]{0, 1});
        edges.add(new int[]{1, 2});
        edges.add(new int[]{2, 3});
        edges.add(new int[]{3, 4});
        edges.add(new int[]{4, 5});
        edges.add(new int[]{5, 6});
        edges.add(new int[]{1, 5});
        boolean[][] adj = new boolean[n][n];
        for (int[] e : edges) {
            adj[e[0]][e[1]] = true;
            adj[e[1]][e[0]] = true;
        }
        Console.info("Dependency links: " + edges.size());
        List<Integer> exact = VertexCover.minVertexCoverExact(n, adj);
        List<Integer> approx = VertexCover.approx2(n, edges);
        Console.result("Small set covering all links (exact) = " + exact + " (size " + exact.size() + ")");
        Console.result("Fast approximation = " + approx + " (size " + approx.size() + ")");
        Console.info("Approximation ratio = " + String.format("%.2f", (double) approx.size() / exact.size())
                + "  (guaranteed <= 2)");
        Console.info("Approximate set is valid (covers all links): "
                + VertexCover.isVertexCover(n, edges, approx));
    }

    // -------------------------------------------------------------- module 6

    private static void module6() {
        while (true) {
            Console.title("RANK, SAMPLE & SPEED UP  (Module M6 / CO6)");
            System.out.println("   1. Rank transactions quickly");
            System.out.println("        (Algorithm: Randomized QuickSort)");
            System.out.println("   2. Check whether a big number is prime");
            System.out.println("        (Algorithm: Miller-Rabin)");
            System.out.println("   3. Pick a fair random sample from a live stream");
            System.out.println("        (Algorithm: Reservoir Sampling)");
            System.out.println("   4. Running totals / cumulative statistics");
            System.out.println("        (Algorithm: Blelloch Scan)");
            System.out.println("   5. Add up all amounts in parallel");
            System.out.println("        (Algorithm: Parallel Reduce)");
            System.out.println("   6. Estimate parallel speed-up");
            System.out.println("        (Algorithm: Brent's Theorem)");
            System.out.println("   0. Back");
            int c = Console.readInt("   Select: ", 0, 6);
            if (c == 0) {
                return;
            }
            if (c == 1) {
                runQuickSort();
            } else if (c == 2) {
                runMillerRabin();
            } else if (c == 3) {
                runReservoir();
            } else if (c == 4) {
                runBlelloch();
            } else if (c == 5) {
                runReduce();
            } else {
                runBrent();
            }
            Console.pause();
        }
    }

    private static void runQuickSort() {
        Console.section("Rank transactions quickly  (Algorithm: Randomized QuickSort)");
        int[] arr = Console.readIntArray("Enter comma separated integers (amounts / risk scores)");
        int[] copy = arr.clone();
        RandomizedQuickSort.Stats stats = RandomizedQuickSort.sort(arr, 2026);
        Arrays.sort(copy);
        Console.result("Sorted = " + Arrays.toString(arr));
        Console.info("Result verified correct: " + Arrays.equals(arr, copy));
        Console.info("Comparisons made = " + stats.comparisons + ", deepest nesting = " + stats.maxDepth);
    }

    private static void runMillerRabin() {
        Console.section("Check whether a big number is prime  (Algorithm: Miller-Rabin)");
        long n = Console.readLong("Enter an integer to test");
        Console.result(n + " is prime = " + MillerRabin.isPrime(n));
        Console.info("First 15 primes: ");
        StringBuilder sb = new StringBuilder("     ");
        int count = 0;
        for (long v = 2; count < 15; v++) {
            if (MillerRabin.isPrime(v)) {
                sb.append(v).append(' ');
                count++;
            }
        }
        Console.info(sb.toString());
        java.math.BigInteger big = java.math.BigInteger.valueOf(2).pow(89).subtract(java.math.BigInteger.ONE);
        Console.result("Huge number 2^89 - 1 is prime = " + MillerRabin.isProbablePrime(big, 20));
    }

    private static void runReservoir() {
        Console.section("Pick a fair random sample from a live stream  (Algorithm: Reservoir Sampling)");
        int k = Console.readInt("How many transactions to sample: ", 1, DataStore.transactions().size());
        ReservoirSampling.Reservoir<String> res = new ReservoirSampling.Reservoir<String>(k, 7);
        for (Transaction t : DataStore.transactions()) {
            res.add(t.getTxnId() + "(" + t.getAmount() + ")");
        }
        Console.info("Read " + res.seen() + " transactions from the stream.");
        Console.result("Random sample of " + k + ": " + res.snapshot());
    }

    private static void runBlelloch() {
        Console.section("Running totals / cumulative statistics  (Algorithm: Blelloch Scan)");
        long[] input = {3, 1, 7, 0, 4, 1, 6, 3};
        Console.info("Input amounts = " + Arrays.toString(input));
        BlellochScan.ScanResult r = BlellochScan.scan(input);
        Console.result("Running total BEFORE each item = " + Arrays.toString(r.exclusive));
        Console.result("Running total UP TO each item  = " + Arrays.toString(r.inclusive));
        Console.info("Padded to next power of two = " + r.paddedLength);
    }

    private static void runReduce() {
        Console.section("Add up all amounts in parallel  (Algorithm: Parallel Reduce)");
        int[] a = {12000, 45000, 7500, 250000, 9999, 32000, 15000, 88000};
        Console.info("Amounts = " + Arrays.toString(a));
        List<long[]> levels = ParallelReduce.levels(a);
        for (int i = 0; i < levels.size(); i++) {
            Console.info("Level " + i + ": " + Arrays.toString(levels.get(i)));
        }
        Console.result("Total amount = " + ParallelReduce.sum(a));
        Console.info("Work = " + ParallelReduce.work(a.length) + ", depth = " + ParallelReduce.depth(a.length));
    }

    private static void runBrent() {
        Console.section("Estimate parallel speed-up  (Algorithm: Brent's Theorem)");
        double work = Console.readDouble("Enter total work W: ");
        double depth = Console.readDouble("Enter depth (span) D: ");
        int p = Console.readInt("Enter number of processors P: ", 1);
        Console.result("Estimated parallel run time T_P <= W/P + D = " + BrentTheorem.parallelTime(work, depth, p));
        Console.result("Speed-up S = " + String.format("%.3f", BrentTheorem.speedup(work, depth, p)));
        Console.result("Efficiency E = " + String.format("%.3f", BrentTheorem.efficiency(work, depth, p)));
    }

    // ------------------------------------------------------------------ data

    private static void showData() {
        Console.title("FINSERVE BANKING DATA");
        Console.section("Customers");
        for (Customer c : DataStore.customers()) {
            Console.info(c.toString());
        }
        Console.section("Accounts");
        for (Account a : DataStore.accounts()) {
            Console.info(a.toString());
        }
        Console.section("Transactions");
        for (Transaction t : DataStore.transactions()) {
            Console.info(t.toString());
        }
        Console.section("Documents");
        for (Document d : DataStore.documents()) {
            Console.info(d.toString());
        }
        Console.section("Fraud Patterns");
        Console.info(DataStore.fraudPatterns().toString());
        Console.pause();
    }

    private static void about() {
        Console.title("ABOUT FINSERVE");
        Console.info("FinServe models a banking & financial analytics platform.");
        Console.info("Each menu option performs a real banking task; the algorithm used is");
        Console.info("shown in brackets so you can learn what powers it:");
        Console.info("   M1  Search & detect text");
        Console.info("        (KMP, Z-Function, Rabin-Karp, Aho-Corasick)");
        Console.info("   M2  Analyse large documents");
        Console.info("        (Suffix Array, SA-IS, Kasai LCP, Suffix Automaton)");
        Console.info("   M3  Compare & assign with least cost");
        Console.info("        (Levenshtein, Weighted ED, Bitmask DP, Matrix-Chain)");
        Console.info("   M4  Route money & assign work");
        Console.info("        (Ford-Fulkerson, Edmonds-Karp, Dinic, Matching, Min-Cut)");
        Console.info("   M5  Check rules & find groups");
        Console.info("        (3-SAT, 3-SAT->Clique, IS, Vertex-Cover, 2-approx)");
        Console.info("   M6  Rank, sample & speed up");
        Console.info("        (QuickSort, Miller-Rabin, Reservoir, Blelloch, Reduce, Brent)");
        Console.section("Domain Model");
        Console.info("Customers -> Accounts -> Transactions; Documents + Fraud Patterns.");
        Console.info("Network flow uses accounts as nodes and transaction volume as capacity.");
        Console.pause();
    }

    // --------------------------------------------------------------- helpers

    private static String ask(String prompt, String def) {
        String s = Console.readLine(prompt + " [default: " + def + "]: ");
        return s.isEmpty() ? def : s;
    }

    private static String askText(String prompt, String def) {
        String s = Console.readLine(prompt + " [ENTER = use sample data]: ");
        return s.isEmpty() ? def : s;
    }

    private Main() { }
}
