package de.unijena.DNAGraphUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements {@link GraphEncoding} interface.
 * Uses translations: A -> 1, C -> 2 and G -> 3 to split a integer designation to the single digits (1-3).
 * Translates the digits into the shortest sequence of this chars for a number.
 */
public class SumGraphEncoding implements GraphEncoding {
    final static int charATranslation = 1, charCTranslation = 2, charGTranslation = 5;

    /**
     * Extracts all vertices from edges into a list.
     *
     * @param edges contains the edge pairs of a {@link Graph}
     * @return list of vertices of the graph that have edges
     */
    private static ArrayList<Integer> getVerticesWithEdges(ArrayList<Pair<Integer, Integer>> edges) {
        ArrayList<Integer> vertices = new ArrayList<>();

        for (Pair<Integer, Integer> edge : edges) {
            int first = edge.getV1();
            int second = edge.getV2();

            if(!vertices.contains(first)){
                vertices.add(first);
            }
            if (!vertices.contains(second)){
                vertices.add(second);
            }
        }
        return vertices;
    }

    /**
     * Fills the dictionary with all vertices and their translation (numbers -> 0, 1, 2,...).
     *
     * @param vertices the vertices of a {@link Graph}
     * @param DNAVertices dictionary that translations will be put into
     */
    private static void createDictionary(ArrayList<Integer> vertices, Map<Integer, String> DNAVertices){
        for (Integer vert : vertices) {
            String vertString = getDNAString(vert + 1);

            DNAVertices.put(vert, vertString);
        }
    }

    /**
     * Converts a number to a string with the chars: "A" = 1, "C" = 2 and "G" = 3.
     *
     * @param vertNumber the designation number of a vert
     * @return the vert number as DNA string
     */
    private static String getDNAString(int vertNumber) {
        StringBuilder vertString = new StringBuilder();

        while (vertNumber != 0) {
            if (vertNumber - charGTranslation >= 0) {
                vertNumber -= charGTranslation;
                vertString.append("G");
            } else if (vertNumber - charCTranslation >= 0) {
                vertNumber -= charCTranslation;
                vertString.append("C");
            } else {
                vertNumber -= charATranslation;
                vertString.append("A");
            }
        }
        return vertString.toString();
    }

    /**
     * Encodes a graph into a DNA sequence while maintaining the order.
     *
     * @param graph a {@link Graph} object
     * @return graph encoded as DNA sequence
     */
    private static String convertToDNASequence(Graph graph){
        Map<Integer, String> DNAVertices = new HashMap<>();
        ArrayList<Integer> vertices = graph.getVertices();
        ArrayList<Pair<Integer, Integer>> edges = graph.getEdges();
        StringBuilder DNASequence = new StringBuilder();

        ArrayList<Integer> verticesWithEdges = getVerticesWithEdges(graph.getEdges());

        createDictionary(verticesWithEdges, DNAVertices);

        DNASequence.append(getDNAString(vertices.size()));
        DNASequence.append("T");

        for (Pair<Integer, Integer> edge : edges) {
            DNASequence.append(DNAVertices.get(edge.getV1()));
            DNASequence.append("T");
            DNASequence.append(DNAVertices.get(edge.getV2()));
            DNASequence.append("T");
        }

        return DNASequence.toString();
    }

    /**
     * Encodes a graph into a DNA sequence without preserving the order.
     *
     * @param graph a {@link Graph} object
     * @return graph encoded as DNA sequence
     */
    private static String convertToDNASequenceWithoutUnusedVerts(Graph graph){
        Map<Integer, String> DNAVertices = new HashMap<>();
        Map<Integer, Integer> vertsNumWithoutUnusedVerts = new HashMap<>();
        ArrayList<Integer> vertices = graph.getVertices();
        ArrayList<Pair<Integer, Integer>> edges = graph.getEdges();
        StringBuilder DNASequence = new StringBuilder();

        ArrayList<Integer> verticesWithEdges = getVerticesWithEdges(graph.getEdges());

        for (int i = 0; i < verticesWithEdges.size(); i++) {
            vertsNumWithoutUnusedVerts.put(verticesWithEdges.get(i), i);
        }

        for (int i = 0; i < verticesWithEdges.size(); i++) {
            verticesWithEdges.set(i, vertsNumWithoutUnusedVerts.get(verticesWithEdges.get(i)));
        }

        createDictionary(verticesWithEdges, DNAVertices);

        DNASequence.append(getDNAString(vertices.size()));
        DNASequence.append("T");

        for (Pair<Integer, Integer> edge : edges) {
            DNASequence.append(DNAVertices.get(vertsNumWithoutUnusedVerts.get(edge.getV1())));
            DNASequence.append("T");
            DNASequence.append(DNAVertices.get(vertsNumWithoutUnusedVerts.get(edge.getV2())));
            DNASequence.append("T");
        }

        return DNASequence.toString();
    }

    /**
     * Implements {@link GraphEncoding#toString(Graph, boolean)}.
     */
    public String toString(Graph graph, boolean preserveOrder) {
        if (preserveOrder){
            return convertToDNASequence(graph);
        }
        else
            return convertToDNASequenceWithoutUnusedVerts(graph);
    }

    /**
     * Converts DNA sequence string to a integer.
     * Converts the chars "A" to 1, "C" to 2 and "G" to 3.
     * Then sums up all values.
     *
     * @param vert the DNA sequence of a vert
     * @return the calculated integer for the vert
     */
    private static int DNASequenceToInt(String vert){
        int vertNum = 0;

        for (char s : vert.toCharArray()) {
            if(s == 'A'){
                vertNum += charATranslation;
            }
            else if(s == 'C'){
                vertNum += charCTranslation;
            }
            else if(s == 'G'){
                vertNum += charGTranslation;
            }
        }

        return vertNum;
    }

    /**
     * Converts the DNA sequences of the vertices to numbers.
     * Adds vertices that have an edge together as pair into list.
     *
     * @param verticesOfEdges contains all vertices of a graph (as DNA sequences) that have edges
     * @param edges contain all edge of a graph
     */
    private static void convertEdgesToList(String[] verticesOfEdges, ArrayList<Pair<Integer, Integer>> edges){
        for (int i = 0; i < verticesOfEdges.length; i += 2) {
            String firstVert = verticesOfEdges[i], secondVert = verticesOfEdges[i+1];

            edges.add(new Pair<>(DNASequenceToInt(firstVert), DNASequenceToInt(secondVert)));
        }
    }

    /**
     * Implements {@link GraphEncoding#load(Graph, String)}
     */
    public void load(Graph graph, String repr) {
        ArrayList<Integer> vertices = new ArrayList<>();
        ArrayList<Pair<Integer,Integer>> edges = new ArrayList<>();

        String[] splitString = repr.split("T", 2);
        String[] verticesOfEdges = splitString[1].split("T");
        String vertNumString = splitString[0];

        if(verticesOfEdges.length > 1) {
            convertEdgesToList(verticesOfEdges, edges);
        }

        int vertNum = DNASequenceToInt(vertNumString);

        for (int i = 1; i <= vertNum; i++) {
            vertices.add(i);
        }

        for (int i = 0; i < vertices.size(); i++) {
            vertices.set(i, vertices.get(i) - 1);
        }
        for (int i = 0; i < edges.size(); i++) {
            Pair<Integer,Integer> edge = edges.get(i);
            edges.set(i, new Pair<>(edge.getV1() - 1, edge.getV2() - 1));
        }

        graph.setVertices(vertices);
        graph.setEdges(edges);
    }
}
