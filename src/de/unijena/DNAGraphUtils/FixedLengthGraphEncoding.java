package de.unijena.DNAGraphUtils;

import de.unijena.DNAGraphUtils.GraphEncoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.Collections;
import static de.unijena.DNAGraphUtils.DNAHelper.*;

/**
 * Implements {@link GraphEncoding}. Uses a fixed length for each vertex to encode adjacency list.
 */
public class FixedLengthGraphEncoding implements GraphEncoding {
    /**
     * Encodes a graph to a DNA sequence with the option to preserve the current vertex order.
     *
     * @param graph a {@link Graph} object
     * @param preserveOrder indicates wether the order needs to be preserved
     * @return DNA sequence of the graph
     */
    public String toString(Graph graph, boolean preserveOrder) {
        StringBuilder result = new StringBuilder();
        if (graph.getVertices().isEmpty())
            return "C";
        HashSet<Integer> usedVerts = new HashSet<>();
        for (Pair<Integer,Integer> edge:graph.getEdges()) {
            usedVerts.add(edge.getV1());
            usedVerts.add(edge.getV2());
        }
        if (graph.getEdges().isEmpty() || usedVerts.isEmpty()){
            result.append('C');
            result.append(toDNA(graph.getVertices().size(), 4));
            return result.toString();
        }
        int uselessVertsCount = graph.getVertices().size() - usedVerts.size();

        int lastUsedVert = 0;
        Map<Integer, Integer> vertToRepr = new HashMap<>();
        ArrayList<Integer> vertices = graph.getVertices();
        for (int i = 0; i < vertices.size(); i++) {
            Integer vert = vertices.get(i);
            boolean isUsed = usedVerts.contains(vert);
            if (isUsed) {
                vertToRepr.put(vert, vertToRepr.size() + 1);
                lastUsedVert = i;
            } else if (preserveOrder) {
                vertToRepr.put(vert, vertToRepr.size() + 1);
            }
        }
        // log4
        int reprLength = (int)Math.ceil(Math.log(preserveOrder ? graph.getVertices().size() : usedVerts.size()) + 1 / Math.log(4));
        result.append(toDNA(0, 4, reprLength));
        result.append('C');
        for (Pair<Integer,Integer> edge:graph.getEdges()){
            result.append(toDNA(vertToRepr.get(edge.getV1()), 4, reprLength));
            result.append(toDNA(vertToRepr.get(edge.getV2()), 4, reprLength));
        }

        result.append(toDNA(0, 4, reprLength));
        if (preserveOrder)
            result.append(toDNA(graph.getVertices().size() - lastUsedVert - 1, 4));
        else
            result.append(toDNA(uselessVertsCount, 4));

        return result.toString();
    }

    /**
     * Implements {@link GraphEncoding#load(Graph, String)}.
     *
     * @param graph a {@link Graph} object
     * @param repr the DNA sequence of a graph
     */
    public void load(Graph graph, String repr) {
        String header = findFirstOccurance(repr, "^A*C");
        int vertReprLength = header.length() - 1;
        if (vertReprLength == 0){
            int vertCount = parseDNA(repr.substring(1),4);
            for (int i = 0; i < vertCount; i++) {
                graph.getVertices().add(i);
            }
            return;
        }
        String zeroVert = header.substring(0, header.length() - 1);
        Map<String, Integer> vertReprToVert = new HashMap<>();

        int maxVert = 0;
        int i;
        for (i = header.length(); i + vertReprLength * 2 <= repr.length(); i += vertReprLength * 2) {
            String firstVert = repr.substring(i, i + vertReprLength);
            if (firstVert.equals(zeroVert))
                break;
            String secondVert = repr.substring(i + vertReprLength, i + 2 * vertReprLength);
            if (!vertReprToVert.containsKey(firstVert)) {
                vertReprToVert.put(firstVert, parseDNA(firstVert, 4) - 1);
                graph.getVertices().add(vertReprToVert.get(firstVert));
            }
            if (!vertReprToVert.containsKey(secondVert)) {
                vertReprToVert.put(secondVert, parseDNA(secondVert, 4) - 1);
                graph.getVertices().add(vertReprToVert.get(secondVert));
            }
            maxVert = Math.max(maxVert, Math.max(vertReprToVert.get(firstVert), vertReprToVert.get(secondVert)));
            graph.getEdges().add(new Pair<>(vertReprToVert.get(firstVert), vertReprToVert.get(secondVert)));
        }
        i += vertReprLength;
        for (int j = 0; j < maxVert; j++) {
            if (!graph.getVertices().contains(j))
                graph.getVertices().add(j);
        }

        int tailVerts = parseDNA(repr.substring(i), 4);
        for (int j = 0; j < tailVerts; j++) {
            graph.getVertices().add(graph.getVertices().size());
        }
        Collections.sort(graph.getVertices());
    }
}
