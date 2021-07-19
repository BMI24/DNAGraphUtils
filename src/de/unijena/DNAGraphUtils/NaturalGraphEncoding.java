package de.unijena.DNAGraphUtils;

import de.unijena.DNAGraphUtils.Graph;
import de.unijena.DNAGraphUtils.GraphEncoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;
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
        String[] graphElements = Pattern.compile("(?<=\\{).+?(?=\\})")
                .matcher(repr)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);

        String vertsRepr = "";
        String edgesRepr = "";

        if (!graphElements[0].equals("},{")){
            vertsRepr = graphElements[0];
        }
        if (graphElements.length > 1){
            edgesRepr = graphElements[1].replaceAll("[()]", "");
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
                var v1 = vertToInt.get(edgesElements[i]);
                var v2 = vertToInt.get(edgesElements[i + 1]);
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
     * @param preserveOrder indicates wether the order needs to be preserved
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
