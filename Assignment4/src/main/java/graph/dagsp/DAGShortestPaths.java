package graph.dagsp;

import java.util.*;

/**
 * Shortest and Longest paths in a DAG using topological ordering.
 */
public class DAGShortestPaths {

    public static class Edge {
        public int to;
        public double w;
        public Edge(int to, double w) { this.to = to; this.w = w; }
    }

    public static class Pair {
        public final double[] dist;
        public final int[] parent;
        public Pair(double[] d, int[] p) { dist = d; parent = p; }
    }

    public static Pair shortestPaths(List<Edge>[] adj, List<Integer> topo, int src) {
        int n = adj.length;
        double[] dist = new double[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        dist[src] = 0.0;

        for (int u : topo) {
            if (dist[u] == Double.POSITIVE_INFINITY) continue;
            for (Edge e : adj[u]) {
                if (dist[e.to] > dist[u] + e.w) {
                    dist[e.to] = dist[u] + e.w;
                    parent[e.to] = u;
                }
            }
        }
        return new Pair(dist, parent);
    }

    public static Pair longestPaths(List<Edge>[] adj, List<Integer> topo, int src) {
        int n = adj.length;
        double[] dist = new double[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Double.NEGATIVE_INFINITY);
        Arrays.fill(parent, -1);
        dist[src] = 0.0;

        for (int u : topo) {
            if (dist[u] == Double.NEGATIVE_INFINITY) continue;
            for (Edge e : adj[u]) {
                if (dist[e.to] < dist[u] + e.w) {
                    dist[e.to] = dist[u] + e.w;
                    parent[e.to] = u;
                }
            }
        }
        return new Pair(dist, parent);
    }

    public static List<Integer> reconstructPath(int[] parent, int target) {
        LinkedList<Integer> st = new LinkedList<>();
        int cur = target;
        st.addFirst(cur);
        while (parent[cur] != -1) {
            cur = parent[cur];
            st.addFirst(cur);
        }
        return new ArrayList<>(st);
    }
}
