package com.pdi.image_processing.filter.Gray_Filter.graph_filter;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class GraphFilter extends WritableImage {

    public GraphFilter(Image image, int depth) {
        super((int) image.getWidth(), (int) image.getHeight());
        GraphImage graph = new GraphImage(image);

        for (int x = 0; x < graph.width; x++) {
            for (int y = 0; y < graph.height; y++) {
                List<Integer> values = bfsCollect(graph.nodes[x][y], depth);
                int median = median(values);
                getPixelWriter().setColor(x, y, Color.gray(median / 255.0));
            }
        }
    }

    private List<Integer> bfsCollect(GraphNo root, int depth) {
        List<Integer> values = new ArrayList<>();
        Set<GraphNo> visited = new HashSet<>();
        Queue<GraphNo> queue = new ArrayDeque<>();
        Queue<Integer> depthQueue = new ArrayDeque<>();

        queue.add(root);
        depthQueue.add(0);
        visited.add(root);

        while (!queue.isEmpty()) {
            GraphNo current = queue.poll();
            int d = depthQueue.poll();
            values.add(current.value);

            if (d < depth) {
                for (GraphNo neighbor : current.neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                        depthQueue.add(d + 1);
                    }
                }
            }
        }
        return values;
    }

    private int median(List<Integer> values) {
        values.sort(null);
        return values.get(values.size() / 2);
    }
}
