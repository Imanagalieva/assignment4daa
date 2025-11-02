# Assignment 4 — Smart City / Smart Campus Scheduling

## Overview
This project implements:
- Strongly Connected Components (Tarjan's algorithm)
- Condensation graph construction (SCC -> DAG)
- Topological ordering (Kahn)
- Shortest paths and longest (critical) path computations on DAGs

Weight model: **edge weights**.

## Implementation details
- TarjanSCC (package `graph.scc`) computes SCCs and returns a list of components.
- Condensation graph keeps single directed edges between components; if multiple edges exist, the minimum weight is used.
- Topological ordering of the condensation DAG is computed using Kahn's algorithm (`graph.topo.TopoSort`).
- Shortest and longest path computations (`graph.dagsp.DAGShortestPaths`) use dynamic programming over the topological order.

## Datasets
Nine datasets are provided under `data/datasets/`:
- 3 Small (n in 6..10) — simple cases with 0..2 cycles
- 3 Medium (n in 10..20) — mixed structures with multiple SCCs
- 3 Large (n in 20..50) — performance and timing tests

Each dataset JSON contains:
```
{
 "directed": true,
 "n": <nodes>,
 "edges": [{"u":<int>,"v":<int>,"w":<double>}, ...],
 "source": <node>,
 "weight_model": "edge"
}
```

## How to run
Build with Maven:
```
mvn package
java -cp target/assignment4-1.0-SNAPSHOT.jar Main data/datasets/tasks1.json
```

To test all datasets, run `Main` with each dataset path and capture output.

## Metrics & instrumentation
- Timing: use `System.nanoTime()` in additional instrumentation (not included by default, but easy to add).
- Counters: DFS visits/edges, Kahn queue ops, relaxations — add as needed in respective classes.

## Results example (for dataset tasks1.json)
- SCCs printed with sizes
- Condensation DAG adjacency
- Topological order of components
- Shortest distances from chosen source component
- Longest (critical) path and its expansion to original tasks

## Conclusion
- Tarjan + condensation is efficient for detecting cycles and compressing graphs.
- On the condensation DAG, shortest and longest path algorithms (O(V+E)) are optimal.
- For dense graphs the bottleneck is memory and edge iteration; for sparse graphs algorithms are fast.
