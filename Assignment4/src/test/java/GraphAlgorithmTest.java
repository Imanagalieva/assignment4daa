import graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphAlgorithmTest {

    @Test
    public void testTarjanSimpleCycle() {
        int n = 3;
        @SuppressWarnings("unchecked")
        List<Integer>[] g = new ArrayList[n];
        for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
        g[0].add(1); g[1].add(2); g[2].add(0);

        TarjanSCC t = new TarjanSCC(g);
        List<List<Integer>> comps = t.run();
        assertEquals(1, comps.size());
        assertEquals(3, comps.get(0).size());
    }
}
