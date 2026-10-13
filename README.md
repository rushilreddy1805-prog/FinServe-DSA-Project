# FinServe - Intelligent Banking Analytics Platform

An interactive, menu-driven Java CLI that demonstrates **26 algorithms** across
**6 DSA modules** over a realistic banking / financial-analytics domain model.

## How to run

**Windows (easiest):**
```
run.bat
```
`run.bat` finds a JDK, compiles everything into `out\`, then launches the CLI.

**Manual:**
```
javac -d out src\finserve\**\*.java      (or compile all .java under src)
java  -cp out finserve.Main
```

**IDE:** open the `src` folder as a project in IntelliJ / Eclipse / VS Code and
run `finserve.Main`.

> The code targets Java 8+ (`--release 8`), so it runs on any JDK/JRE 8 or newer.

## Project structure

```
DSA3 END SEM PROJECT/
|- build.bat                 Build script
|- run.bat                   Build + launch the CLI
|- README.md                 This file
|- src/finserve/
|   |- Main.java             Interactive menu-driven CLI (entry point)
|   |- model/                Domain classes: Customer, Account, Transaction, Document
|   |- util/
|   |   |- Console.java      Shared input / pretty-print helpers
|   |   |- DataStore.java    In-memory seed data + sample graph generators
|   |- m1_strings/           KMP, ZFunction, RabinKarp, AhoCorasick
|   |- m2_suffix/            SuffixArray, SAIS, KasaiLCP, SuffixAutomaton
|   |- m3_dp/                EditDistance, WeightedEditDistance, BitmaskDP, MatrixChain
|   |- m4_flow/              FlowNetwork, BipartiteMatching, MinCut
|   |- m5_np/                Sat3, Clique, IndependentSet, VertexCover
|   '- m6_random/            RandomizedQuickSort, MillerRabin, ReservoirSampling,
|                            BlellochScan, ParallelReduce, BrentTheorem
'- out/                      Compiled .class files (generated)
```

## Modules & algorithms

| Module | Algorithms | FinServe use case |
|--------|-----------|-------------------|
| M1 Strings | KMP, Z-Function, Rabin-Karp, Aho-Corasick | transaction/payment-code search, fraud-pattern detection |
| M2 Suffix | Suffix Array, SA-IS, Kasai LCP, Suffix Automaton | description/document indexing, repeated-pattern mining |
| M3 DP | Levenshtein, Damerau-Levenshtein, Weighted ED, Bitmask DP, Matrix-Chain | description correction, analyst-task assignment, operation ordering |
| M4 Flow | Ford-Fulkerson, Edmonds-Karp, Dinic, Bipartite Matching, Min-Cut | transaction-flow capacity, analyst/case assignment, critical channels |
| M5 NP | 3-SAT (DPLL), 3-SAT->CLIQUE, CLIQUE->IND-SET->V-COVER, 2-approx Vertex-Cover | financial rule constraints, conflict & dependency analysis |
| M6 Random/Parallel | Randomized QuickSort, Miller-Rabin, Reservoir Sampling, Blelloch Scan, Parallel Reduce, Brent's Theorem | transaction ranking, primality, streaming samples, aggregate stats |

## Documentation

All supporting material is in the [`docs/`](docs) folder:

- [`docs/FinServe_Problem_Statement_and_Explanation.txt`](docs/FinServe_Problem_Statement_and_Explanation.txt)
  - problem statement, dataset source & how to update it, and a simple-words
    "what it does / why this / why not the other" write-up for all 26 algorithms.
- [`docs/FinServe_Project_Explanation_and_Viva_QA.txt`](docs/FinServe_Project_Explanation_and_Viva_QA.txt)
  - detailed write-up of every algorithm, its complexity and 40 expected viva /
    reviewer questions with answers.
- [`docs/FinServe_Presentation.pptx`](docs/FinServe_Presentation.pptx)
  - an 8-slide project presentation.
