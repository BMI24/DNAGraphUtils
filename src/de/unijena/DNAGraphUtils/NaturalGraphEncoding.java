package de.unijena.DNAGraphUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements {@link GraphEncoding}. Used to transfer from and to the natural Form e.g. G=({a,b,c},{(a,b),(a,d)})
 */
public class NaturalGraphEncoding implements GraphEncoding {
    /**
     * Implements {@link GraphEncoding::load}
     * @param graph a {@link Graph} object
     * @param repr the DNA sequence of a graph
     */
    public void load(Graph graph, String repr) {
        ArrayList<String> graphElements = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=\\{).+?(?=})")
                .matcher(repr);
        while (m.find()){
            graphElements.add(m.group());
        }

        String vertsRepr = "";
        String edgesRepr = "";

        if (!graphElements.get(0).equals("},{")){
            vertsRepr = graphElements.get(0);
        }
        if (graphElements.size() > 1){
            edgesRepr = graphElements.get(1).replaceAll("[()]", "");
        }

        Map<String, Integer> vertToInt = new HashMap<>();

        if (!vertsRepr.equals("")) {
            ArrayList<Integer> vertices = new ArrayList<>();

            for (String vert : vertsRepr.split(",")) {
                Integer vertRepr = vertToInt.size();
                vertToInt.put(vert, vertRepr);
                vertices.add(vertRepr);
            }

            graph.setVertices(vertices);
        }
        else {
            graph.setVertices(new ArrayList<>());
        }

        ArrayList<Pair<Integer,Integer>> edges = new ArrayList<>();

        if (!edgesRepr.equals("")){
            String[] edgesElements = edgesRepr.split(",");

            for (int i = 0; i < edgesElements.length; i+=2) {
                Integer v1 = vertToInt.get(edgesElements[i]);
                Integer v2 = vertToInt.get(edgesElements[i + 1]);
                edges.add(new Pair<>(v1, v2));
            }

            graph.setEdges(edges);
        }
        else {
            graph.setEdges(new ArrayList<>());
        }
    }

    /**
     * Implements {@link GraphEncoding::toString}.
     *
     * @param graph a {@link Graph} object
     * @param preserveOrder indicates whether the order needs to be preserved
     * @return DNA sequence of the graph
     */
    public String toString(Graph graph, boolean preserveOrder){
        StringBuilder sb = new StringBuilder();
        sb.append("G=({");

        for (int vert : graph.getVertices()){
            sb.append(vert).append(",");
        }

        if (graph.getVertices().size() > 0)
            sb.deleteCharAt(sb.length()-1);

        sb.append("},{");

        for (Pair<Integer, Integer> edge : graph.getEdges()) {
            sb.append("(").append(edge.getV1()).append(",").append(edge.getV2()).append("),");
        }
        if (graph.getEdges().size() > 0)
            sb.deleteCharAt(sb.length()-1);

        sb.append("})");

        return sb.toString();
    }
}
