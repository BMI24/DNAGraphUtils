package de.unijena.DNAGraphUtils;

import java.util.ArrayList;
import java.util.Map;
import de.unijena.DNAGraphUtils.Pair;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Contains important aspects of a classical graph.
 * Provides method to test for graph isomorphism.
 */
public class Graph {
    private ArrayList<Integer> vertices;
    private ArrayList<Pair<Integer, Integer>> edges;

    /**
     * Decodes the repr to a Graph object with the given encoding.
     *
     * @param repr DNA sequence of a Graph object
     * @param encoding the instance of an {@link GraphEncoding} implementation
     */
    public Graph(String repr, GraphEncoding encoding) {
        this();
        encoding.load(this, repr);
    }

    /**
     * Creates an empty instance of a Graph.
     */
    public Graph() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Creates a new Graph with the vertices and edges.
     *
     * @param vertices list of integer (belongs to a graph)
     * @param edges list of integer pairs (belongs to a graph)
     */
    public Graph(ArrayList<Integer> vertices, ArrayList<Pair<Integer, Integer>> edges){
        this.vertices = vertices;
        this.edges = edges;
    }

    /**
     * @return the vertices of the Graph object
     */
    public ArrayList<Integer> getVertices() {
        return vertices;
    }

    /**
     * @return the edges of the Graph object
     */
    public ArrayList<Pair<Integer, Integer>> getEdges() {
        return edges;
    }

    /**
     * @return the natural representation of the Graph object
     */
    @Override
    public String toString(){
        return toString(new NaturalGraphEncoding(), true);
    }

    /**
     * Encodes a graph to a DNA sequence with the option to preserve the current vertex order.
     *
     * @param code the instance of an {@link GraphEncoding} implementation
     * @param preserveOrder indicates whether the order needs to be preserved
     */
    public String toString(GraphEncoding code, boolean preserveOrder) {
        return code.toString(this, preserveOrder);
    }

    /**
     * Sets the vertices of the Graph object.
     *
     * @param vertices list of integer (belongs to a graph)
     */
    public void setVertices(ArrayList<Integer> vertices) {
        this.vertices = vertices;
    }

    /**
     * Sets the edges of the Graph object.
     *
     * @param edges list of integer pairs (belongs to a graph)
     */
    public void setEdges(ArrayList<Pair<Integer, Integer>> edges) {
        this.edges = edges;
    }

    /**
     * Searches for all vertices in g2 the vertices in g1 with same frequencies.
     * Saves this found vertices as possible translations.
     *
     * @param g1Frequencies frequencies of all vertices in edges in g1
     * @param g2Frequencies frequencies of all vertices in edges in g2
     * @return the possible translations for all vertices in g2
     */
    private ArrayList<ArrayList<Integer>> findPossibleVerticesTranslations(int[] g1Frequencies, int[] g2Frequencies) {
        ArrayList<ArrayList<Integer>> secVerticesPossibleTranslations = new ArrayList<>();

        for (int g2Frequency : g2Frequencies) {
            ArrayList<Integer> possibleTranslations = new ArrayList<>();

            for (int j = 0; j < g1Frequencies.length; j++) {
                if (g2Frequency == g1Frequencies[j]) {
                    possibleTranslations.add(j);
                }
            }

            secVerticesPossibleTranslations.add(possibleTranslations);
        }
        return secVerticesPossibleTranslations;
    }

    /**
     * Goes through all translation combinations and adds the new translation number.
     * Only adds the resulting combinations, that contains every entry only once to list.
     *
     * @param translationCombinations contains combinations of numbers as translations for vertices in g2
     * @param newTranslationCombinations contains new combinations of numbers as translations for vertices in g2
     * @param translation contains a number as possible translation for a vert in g2
     */
    private void addNewCombinationsToList(ArrayList<ArrayList<Integer>> translationCombinations, ArrayList<ArrayList<Integer>> newTranslationCombinations, Integer translation) {
        for (ArrayList<Integer> combination : translationCombinations) {
            ArrayList<Integer> newCombination = new ArrayList<>(combination);
            newCombination.add(translation);

            Map<Object, Long> numberCount = newCombination.stream().collect(groupingBy(e -> e, counting()));
            boolean forbiddenCombination = false;

            for (Map.Entry<Object, Long> count : numberCount.entrySet()) {
                if(count.getValue() > 1){
                    forbiddenCombination = true;
                    break;
                }
            }

            if(!forbiddenCombination) {
                newTranslationCombinations.add(newCombination);
            }
        }
    }

    /**
     * Adds for every vert in g2 a possible translation to a combination.
     * Goal is to add this combinations to a list where all possible combinations of the translations are inside.
     * But only adds combinations that contains every entry only once.
     *
     * @param possibleTranslationsG2Vertices contains for every g2 vert the vertices of g1 into which they could be translated
     * @return list of all correct translation combinations
     */
    private ArrayList<ArrayList<Integer>> findTranslationCombinations(ArrayList<ArrayList<Integer>> possibleTranslationsG2Vertices) {
        ArrayList<ArrayList<Integer>> translationCombinations = new ArrayList<>();

        for (int i = 0; i < possibleTranslationsG2Vertices.size(); i++) {
            ArrayList<Integer> vertTranslations = possibleTranslationsG2Vertices.get(i);
            ArrayList<ArrayList<Integer>> newTranslationCombinations = new ArrayList<>();

            for (Integer translation : vertTranslations) {
                if (i == 0) {
                    ArrayList<Integer> translationList = new ArrayList<>();
                    translationList.add(translation);

                    translationCombinations.add(translationList);
                } else {
                    addNewCombinationsToList(translationCombinations, newTranslationCombinations, translation);
                }
            }

            int SIZE_LIMIT = 100000;
            if (newTranslationCombinations.size() > SIZE_LIMIT)
                throw new RuntimeException("Graphs cant be checked for isomorphism with the implemented algorithm.");

            if(!newTranslationCombinations.isEmpty()) {
                translationCombinations = newTranslationCombinations;
            }
        }
        return translationCombinations;
    }

    /**
     * With the translation combinations, the vertices in the g2 edge get translated (get new numbers).
     * If a translation let the edges of g2 be the same as the edges of g1, g1 and g2 are isomorph.
     *
     * @param other the de.unijena.DNAGraphUtils.Graph g2
     * @param g2Edges all edges in g2
     * @param translationCombinations contains combinations of numbers as translations for vertices in g2
     * @return true if a translation of the vertices in g2 let g1 and g2 be isomorph to each other,
     *         false if there is no translation to let them be isomorph
     */
    private boolean checkIfIsomorph(Graph other, ArrayList<Pair<Integer, Integer>> g2Edges, ArrayList<ArrayList<Integer>> translationCombinations) {
        ArrayList<Pair<Integer, Integer>> prevG2Edges = new ArrayList<>();

        for (Pair<Integer, Integer> edge : g2Edges) {
            prevG2Edges.add(new Pair<>(edge));
        }

        for (ArrayList<Integer> combination : translationCombinations) {
            for (int i = 0; i < prevG2Edges.size(); i++) {
                Pair<Integer, Integer> prevG2Edge = prevG2Edges.get(i);
                Pair<Integer, Integer> g2Edge = g2Edges.get(i);

                g2Edge.setV1(combination.get(prevG2Edge.getV1()));
                g2Edge.setV2(combination.get(prevG2Edge.getV2()));
            }

            if(this.toString().equals(other.toString())){
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if de.unijena.DNAGraphUtils.Graph "this" as g1 and "other" as g2 are isomorph to each other.
     * Translates the vert numbers of the edges in g2 depending on the vert numbers of the edges in g1.
     * This happens based on the frequencies of the nodes.
     *
     * @param other a de.unijena.DNAGraphUtils.Graph with vertices and edges
     * @return true if g1 and g2 are isomorph to each other, false if not
     */
    public boolean isIsomorphTo(Graph other){
        //System.out.println("startFirst:  " + this);
        //System.out.println("startSecond: " + other);

        if (this.getEdges().size() != other.getEdges().size()
                || this.getVertices().size() != other.getVertices().size()) {
            return false;
        }

        if(this.toString().equals(other.toString())){
            return true;
        }


        int[] g1Frequencies = new int[this.getVertices().size()];
        int[] g2Frequencies = new int[other.getVertices().size()];
        ArrayList<Pair<Integer, Integer>> g1Edges = this.getEdges();
        ArrayList<Pair<Integer, Integer>> g2Edges = other.getEdges();

        for (Pair<Integer, Integer> edge : g1Edges) {
            ++g1Frequencies[edge.getV1()];
            ++g1Frequencies[edge.getV2()];
        }

        for (Pair<Integer, Integer> edge : g2Edges) {
            ++g2Frequencies[edge.getV1()];
            ++g2Frequencies[edge.getV2()];
        }

        ArrayList<ArrayList<Integer>> possibleTranslationsG2Vertices = findPossibleVerticesTranslations(g1Frequencies, g2Frequencies);

        ArrayList<ArrayList<Integer>> translationCombinations = findTranslationCombinations(possibleTranslationsG2Vertices);

        return checkIfIsomorph(other, g2Edges, translationCombinations);
    }
}
