package graph.scc;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Tarjan's algorithm for SCC.
 */
public class TarjanSCC {
    private final List<Integer>[] g;
    private final int n;

    private int time = 0;
    private int[] disc;
    private int[] low;
    private boolean[] inStack;
    private Stack<Integer> stack;
    private final List<List<Integer>> components = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public TarjanSCC(List<Integer>[] graph) {
        this.g = graph;
        this.n = graph.length;
        disc = new int[n];
        low = new int[n];
        inStack = new boolean[n];
        stack = new Stack<>();
        for (int i = 0; i < n; i++) disc[i] = -1;
    }

    public List<List<Integer>> run() {
        for (int v = 0; v < n; v++) {
            if (disc[v] == -1) dfs(v);
        }
        return components;
    }

    private void dfs(int u) {
        disc[u] = low[u] = time++;
        stack.push(u);
        inStack[u] = true;

        for (int v : g[u]) {
            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (inStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> comp = new ArrayList<>();
            while (true) {
                int w = stack.pop();
                inStack[w] = false;
                comp.add(w);
                if (w == u) break;
            }
            components.add(comp);
        }
    }
}
