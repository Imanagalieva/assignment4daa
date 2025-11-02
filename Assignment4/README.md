# Assignment 4 — Smart City / Smart Campus Scheduling (Java)

## Project contents
- `graph.scc.TarjanSCC` — Tarjan's algorithm for SCC.
- `graph.topo.TopoSort` — Kahn topological sort.
- `graph.dagsp.DAGShortestPaths` — shortest and longest (critical) paths on a DAG.
- `utils.GraphLoader` — reads JSON graphs from `/data`.
- `Main` — runner that executes SCC -> condensation -> topo -> DAG-SP, and prints results.

## Building and running
Using Maven:
```
mvn package
java -cp target/assignment4-1.0-SNAPSHOT.jar Main data/datasets/tasks1.json
```
Or in IDE: run `Main` and pass path to a dataset JSON (default uses `data/datasets/tasks1.json`).

## Notes
- Weight model: edge weights are used.
- Condensation: multiple edges between components use minimum weight.
- Longest path computed via DP on topological order.
