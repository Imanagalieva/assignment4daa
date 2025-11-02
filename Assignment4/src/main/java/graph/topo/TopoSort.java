package graph.topo;

import java.util.*;

/**
 * Topological sort for DAG using Kahn's algorithm.
 */
public class TopoSort {

    public static List<Integer> kahn(List<Integer>[] adj) {
        int n = adj.length;
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : adj[u]) indeg[v]++;
        }
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) q.add(i);

        List<Integer> topo = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.remove();
            topo.add(u);
            for (int v : adj[u]) {
                indeg[v]--;
                if (indeg[v] == 0) q.add(v);
            }
        }
        if (topo.size() != n) return Collections.emptyList();
        return topo;
    }
}
