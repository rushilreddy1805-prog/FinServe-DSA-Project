package finserve.m5_np;

import java.util.ArrayList;
import java.util.List;

/**
 * 3-SAT / CNF satisfiability via DPLL.
 * FinServe models regulatory constraint sets as CNF formulas: each clause is a
 * rule such as "if KYC missing then block transfer or flag review".
 *
 * Literal encoding: +v means variable v is true, -v means variable v is false.
 * Variable indices run 1..nVars.
 */
public final class Sat3 {

    private Sat3() { }

    /** Returns a satisfying assignment (index 1..n) or null if UNSAT. */
    public static int[] solve(int nVars, List<int[]> clauses) {
        int[] assign = new int[nVars + 1];
        boolean sat = dpll(clauses, assign);
        return sat ? assign : null;
    }

    private static boolean dpll(List<int[]> clauses, int[] assign) {
        boolean allSatisfied = true;
        for (int[] clause : clauses) {
            boolean satisfied = false;
            int unassigned = 0;
            int lastLit = 0;
            for (int lit : clause) {
                int v = Math.abs(lit);
                int val = assign[v];
                if (val != 0) {
                    if ((lit > 0 && val == 1) || (lit < 0 && val == -1)) {
                        satisfied = true;
                        break;
                    }
                } else {
                    unassigned++;
                    lastLit = lit;
                }
            }
            if (satisfied) {
                continue;
            }
            if (unassigned == 0) {
                return false; // conflicting clause
            }
            allSatisfied = false;
            if (unassigned == 1) {
                int v = Math.abs(lastLit);
                assign[v] = lastLit > 0 ? 1 : -1;
                if (dpll(clauses, assign)) {
                    return true;
                }
                assign[v] = 0;
                return false;
            }
        }
        if (allSatisfied) {
            return true;
        }
        int v = 0;
        for (int i = 1; i < assign.length; i++) {
            if (assign[i] == 0) {
                v = i;
                break;
            }
        }
        if (v == 0) {
            return true;
        }
        for (int value = 1; value >= -1; value -= 2) {
            assign[v] = value;
            if (dpll(clauses, assign)) {
                return true;
            }
            assign[v] = 0;
        }
        return false;
    }

    /** Brute-force checker for small formulas (used to validate DPLL). */
    public static boolean bruteForce(int nVars, List<int[]> clauses) {
        int total = 1 << nVars;
        for (int mask = 0; mask < total; mask++) {
            boolean ok = true;
            for (int[] clause : clauses) {
                boolean clauseSat = false;
                for (int lit : clause) {
                    int v = Math.abs(lit) - 1;
                    boolean bit = ((mask >> v) & 1) == 1;
                    if ((lit > 0 && bit) || (lit < 0 && !bit)) {
                        clauseSat = true;
                        break;
                    }
                }
                if (!clauseSat) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return true;
            }
        }
        return false;
    }

    public static String assignmentToString(int[] assign) {
        if (assign == null) {
            return "UNSAT";
        }
        StringBuilder sb = new StringBuilder();
        for (int v = 1; v < assign.length; v++) {
            if (v > 1) {
                sb.append(", ");
            }
            sb.append("x").append(v).append("=").append(assign[v] == 1);
        }
        return sb.toString();
    }

    public static String clauseToString(int[] clause) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < clause.length; i++) {
            if (i > 0) {
                sb.append(" OR ");
            }
            int lit = clause[i];
            sb.append(lit > 0 ? "x" + lit : "!x" + Math.abs(lit));
        }
        return sb.append(")").toString();
    }

    /** Builds a list of clauses from a compact code, e.g. "1 -2 3 | -1 2 4". */
    public static List<int[]> parse(String spec) {
        List<int[]> clauses = new ArrayList<int[]>();
        String[] groups = spec.split("\\|");
        for (String g : groups) {
            String[] parts = g.trim().split("\\s+");
            List<Integer> lits = new ArrayList<Integer>();
            for (String p : parts) {
                if (!p.isEmpty()) {
                    lits.add(Integer.parseInt(p));
                }
            }
            int[] c = new int[lits.size()];
            for (int i = 0; i < c.length; i++) {
                c[i] = lits.get(i);
            }
            clauses.add(c);
        }
        return clauses;
    }
}
