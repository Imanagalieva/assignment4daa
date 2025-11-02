package utils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple graph loader for tasks JSON format.
 */
public class GraphLoader {

    public static class InputEdge {
        public int u;
        public int v;
        public double w;
    }

    public static class GraphSpec {
        public boolean directed;
        public int n;
        public List<InputEdge> edges = new ArrayList<>();
        public Integer source;
        @SerializedName("weight_model")
        public String weightModel;
    }

    public static GraphSpec load(String path) throws Exception {
        Gson gson = new Gson();
        try (Reader r = new FileReader(path)) {
            return gson.fromJson(r, GraphSpec.class);
        }
    }
}
