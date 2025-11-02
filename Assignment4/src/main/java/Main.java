import graph.dagsp.DAGShortestPaths;
import graph.dagsp.DAGShortestPaths.Edge;
import graph.scc.TarjanSCC;
import graph.topo.TopoSort;
import utils.GraphLoader;

import java.util.*;

/**
 * Main runner: loads dataset, runs SCC -> condensation -> topo -> DAG-SP.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String defaultPath = "data/datasets/tasks1.json";
        String path = args.length > 0 ? args[0] : defaultPath;

        GraphLoader.GraphSpec spec = GraphLoader.load(path);
        int n = spec.n;
        boolean directed = spec.directed;
        List<GraphLoader.InputEdge> edges = spec.edges;

        @SuppressWarnings("unchecked")
        List<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (GraphLoader.InputEdge e : edges) {
            adj[e.u].add(e.v);
            if (!directed) adj[e.v].add(e.u);
        }

        System.out.println("Loaded graph: n=" + n + ", m=" + edges.size() + ", directed=" + directed);

        TarjanSCC tarjan = new TarjanSCC(adj);
        List<List<Integer>> comps = tarjan.run();

        System.out.println("\nStrongly Connected Components (count=" + comps.size() + "):");
        Map<Integer,Integer> nodeToComp = new HashMap<>();
        for (int i = 0; i < comps.size(); i++) {
            List<Integer> comp = comps.get(i);
            System.out.println("C" + i + " (size=" + comp.size() + "): " + comp);
            for (int v : comp) nodeToComp.put(v, i);
        }

        int cn = comps.size();
        @SuppressWarnings("unchecked")
        List<DAGShortestPaths.Edge>[] cadj = new ArrayList[cn];
        for (int i = 0; i < cn; i++) cadj[i] = new ArrayList<>();
        Map<String, Double> minEdge = new HashMap<>();
        for (GraphLoader.InputEdge e : edges) {
            int cu = nodeToComp.get(e.u);
            int cv = nodeToComp.get(e.v);
            if (cu != cv) {
                String key = cu + "->" + cv;
                double prev = minEdge.getOrDefault(key, Double.POSITIVE_INFINITY);
                if (e.w < prev) minEdge.put(key, e.w);
            }
        }
        for (Map.Entry<String, Double> en : minEdge.entrySet()) {
            String[] parts = en.getKey().split("->");
            int u = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);
            double w = en.getValue();
            cadj[u].add(new Edge(v, w));
        }

        System.out.println("\nCondensation graph adjacency:");
        for (int i = 0; i < cn; i++) {
            System.out.print("C" + i + " -> ");
            List<String> outs = new ArrayList<>();
            for (Edge e : cadj[i]) outs.add("C" + e.to + "(w=" + e.w + ")");
            System.out.println(outs);
        }

        @SuppressWarnings("unchecked")
        List<Integer>[] cadjInt = new ArrayList[cn];
        for (int i = 0; i < cn; i++) {
            cadjInt[i] = new ArrayList<>();
            for (Edge e : cadj[i]) cadjInt[i].add(e.to);
        }

        List<Integer> topo = TopoSort.kahn(cadjInt);
        if (topo.isEmpty()) {
            System.out.println("\nERROR: condensation graph has a cycle (shouldn't happen).");
        } else {
            System.out.println("\nTopological order of components: " + topo);
            List<Integer> derivedTasks = new ArrayList<>();
            for (int compId : topo) {
                List<Integer> comp = comps.get(compId);
                Collections.sort(comp);
                derivedTasks.addAll(comp);
            }
            System.out.println("Derived order of original tasks (after SCC compression): " + derivedTasks);
        }

        int sourceNode = spec.source != null ? spec.source : 0;
        int sourceComp = nodeToComp.get(sourceNode);
        System.out.println("\nSource node: " + sourceNode + " (component C" + sourceComp + ")");

        if (topo.isEmpty()) {
            System.out.println("Can't run DAG-SP because topo order missing.");
            return;
        }

        DAGShortestPaths.Pair sp = DAGShortestPaths.shortestPaths(cadj, topo, sourceComp);
        DAGShortestPaths.Pair lp = DAGShortestPaths.longestPaths(cadj, topo, sourceComp);

        System.out.println("\nShortest distances from C" + sourceComp + ":");
        for (int i = 0; i < cn; i++) {
            double d = sp.dist[i];
            System.out.printf("C%d : %s\n", i, (d == Double.POSITIVE_INFINITY ? "INF" : String.format("%.3f", d)));
        }

        System.out.println("\nLongest distances (critical path lengths) from C" + sourceComp + ":");
        for (int i = 0; i < cn; i++) {
            double d = lp.dist[i];
            System.out.printf("C%d : %s\n", i, (d == Double.NEGATIVE_INFINITY ? "-INF" : String.format("%.3f", d)));
        }

        double best = Double.NEGATIVE_INFINITY;
        int bestId = -1;
        for (int i = 0; i < cn; i++) {
            if (lp.dist[i] > best) { best = lp.dist[i]; bestId = i; }
        }
        if (bestId != -1 && best > Double.NEGATIVE_INFINITY) {
            List<Integer> compPath = DAGShortestPaths.reconstructPath(lp.parent, bestId);
            System.out.println("\nCritical path (components): " + compPath + " length=" + String.format("%.3f", best));
            List<Integer> expanded = new ArrayList<>();
            for (int cid : compPath) {
                List<Integer> comp = comps.get(cid);
                Collections.sort(comp);
                expanded.addAll(comp);
            }
            System.out.println("Critical path expanded to original tasks: " + expanded);
        } else {
            System.out.println("\nNo reachable nodes for longest path from source.");
        }

        int targetComp = (cn - 1 >= 0) ? (cn - 1) : sourceComp;
        if (sp.dist[targetComp] != Double.POSITIVE_INFINITY) {
            List<Integer> compPath = DAGShortestPaths.reconstructPath(sp.parent, targetComp);
            System.out.println("\nOne shortest path to C" + targetComp + " : " + compPath + " length=" + String.format("%.3f", sp.dist[targetComp]));
        } else {
            System.out.println("\nTarget C" + targetComp + " unreachable from source (shortest).");
        }
    }
}
