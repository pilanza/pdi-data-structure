package com.pdi.image_processing.filter.Gray_Filter.graph_filter;

import java.util.ArrayList;
import java.util.List;

public class GraphNo {
    int value;
    List<GraphNo> neighbors;

    GraphNo(int value) {
        this.value = value;
        neighbors = new ArrayList<>();
    }
}
