package de.unijena.DNAGraphUtils;

import java.util.*;
import java.util.stream.Collectors;

import static de.unijena.DNAGraphUtils.DNAHelper.*;

/**
 * Implements {@link GraphEncoding}. Uses canonical huffman code to encode adjacency list.
 */
public class HuffmanGraphEncoding implements GraphEncoding{
    /**
     * Counts how often each vertex is contained in an edge.
     *
     * @param edges List of all edges
     * @param vertCount Count of the vertices in the belonging graph
     * @return  List over {@link ComparablePair} which contains 1. the frequency of the node 2. the index of the node
     */
    private static ArrayList<ComparablePair<Integer, Integer>> getVertFrequencies(ArrayList<Pair<Integer, Integer>> edges, int vertCount){
        int[] vertFrequency = new int[vertCount];
        for (Pair<Integer, Integer> edge : edges) {
            ++vertFrequency[edge.getV1()];
            ++vertFrequency[edge.getV2()];
        }

        ArrayList<ComparablePair<Integer, Integer>> vertAndFrequencyPairs = new ArrayList<>();

        for (int i = 0; i < vertFrequency.length; i++) {
            vertAndFrequencyPairs.add(new ComparablePair<>(vertFrequency[i], i));
        }

        return vertAndFrequencyPairs;
    }

    /**
     * Recursively maps the nodes from the given Huffman Tree onto their respective depths in the Huffman tree.
     *
     * @param node Root of the current Huffman subtree, will be recursively deepened
     * @param depth Integer indicating the current depth in the Huffman tree
     * @param depthToNodes {@link Map} which will be used for saving the depth of each node
     */
    private static void generateDepthToNodesRecursive(TreeNode node, int depth, Map<Integer, TreeSet<Integer>> depthToNodes){
        TreeNode[] children = node.getChildren();
        if (children[0] == null) {
            if (!depthToNodes.containsKey(depth))
                depthToNodes.put(depth, new TreeSet<>());

            depthToNodes.get(depth).add(node.getValue());
        }
        else {
            for (TreeNode child : children) {
                if (child == null)
                    break;
                generateDepthToNodesRecursive(child, depth + 1, depthToNodes);
            }
        }
    }

    /**
     * Groups all nodes in a Huffman tree by their respective depths.
     *
     * @param root Root of the Huffman Tree
     * @return List of all Sets, where each set contains all Nodes belonging to one depth
     */
    private static ArrayList<TreeSet<Integer>> groupNodesByDepth(TreeNode root){
        TreeMap<Integer, TreeSet<Integer>> depthToNodes = new TreeMap<>();
        generateDepthToNodesRecursive(root, 1, depthToNodes);
        return convertIntegerMapToArray(depthToNodes);
    }

    /**
     * Converts a {@link Map} to a {@link ArrayList}, filling non-existing
     * entries with empty sets.
     *
     * @param map Source map
     * @return Array sourced from the given map
     */
    private static ArrayList<TreeSet<Integer>> convertIntegerMapToArray(Map<Integer, TreeSet<Integer>> map){
        int maxIndex = map.keySet().stream().max(Integer::compareTo).orElse(-1);
        ArrayList<TreeSet<Integer>> arr = new ArrayList<>();
        for (int i = 0; i <= maxIndex; i++) {
            arr.add(map.containsKey(i) ? map.get(i) : new TreeSet<>());
        }
        return arr;
    }

    /**
     * Generates the canonical Huffman codebook directly from the list of node-sets.
     *
     * @param depthToNodes Contains for each depth the set of all nodes in that depth
     * @return Canonical Huffman codebook
     */
    private static Map<Integer, String> generateVertMap(ArrayList<TreeSet<Integer>> depthToNodes){
        Map<Integer, String> decodingMap = new HashMap<>();
        int code = 0;
        for (TreeSet<Integer> depthToNode : depthToNodes) {
            for (Integer node : depthToNode) {
                decodingMap.put(node, toDNA(code, 4));
                code += 1;
            }
            code *= 4;
        }

        return decodingMap;
    }

    /**
     * Merge-Step in the Huffman tree generation algorithm. Merges the n least frequent node to 1 new one
     * with a frequency equal to the sum of its new child nodes.
     *
     * @param nodes {@link PriorityQueue} containing all non-leaf nodes in the current Huffman-step
     * @param n Count of nodes that should be combined
     */
    private static void mergeLeastFrequentHuffmanNodes(PriorityQueue<TreeNode> nodes, int n){
        TreeNode parentNode = new TreeNode();
        TreeNode[] childrenNodes = new TreeNode[4];
        int sumChildrenFrequency = 0;

        for (int i = 0; i < n; i++) {
            TreeNode child = nodes.poll();
            childrenNodes[i] = child;
            assert child != null;
            sumChildrenFrequency += child.getFrequency();
        }

        parentNode.setChildren(childrenNodes);
        parentNode.setFrequency(sumChildrenFrequency);

        nodes.add(parentNode);
    }

    /**
     * Generates a Huffman tree (with branching factor 4) from the given frequencies.
     *
     * @param frequencies List of all nodes with their respective frequencies
     * @return Root of the constructed Huffman tree
     */
    private static TreeNode generateHuffman4aryTree(ArrayList<ComparablePair<Integer, Integer>> frequencies) {
        PriorityQueue<TreeNode> probabilityList = new PriorityQueue<>();
        for (ComparablePair<Integer, Integer> probability : frequencies) {
            probabilityList.add(new TreeNode(probability.getV1(), probability.getV2()));
        }

        int usableSymbols = 4; // since we construct a 4-ary tree
        int initial_count = probabilityList.size() == 1 ? 1 : 2 + (probabilityList.size() - 2) % (usableSymbols - 1);
        mergeLeastFrequentHuffmanNodes(probabilityList, initial_count);
        while (probabilityList.size() !=  1) {
            mergeLeastFrequentHuffmanNodes(probabilityList,  usableSymbols);
        }

        return probabilityList.poll();
    }

    /**
     * Implements {@link GraphEncoding#toString(Graph, boolean)}.
     *
     * @param graph a {@link Graph} object
     * @param preserveOrder indicates wether the order needs to be preserved
     * @return DNA sequence of the graph
     */
    public String toString(Graph graph, boolean preserveOrder){
        ArrayList<Integer> vertices = graph.getVertices();
        ArrayList<Pair<Integer, Integer>> edges = graph.getEdges();
        ArrayList<ComparablePair<Integer, Integer>> vertAndFrequencyPairs = getVertFrequencies(edges, vertices.size());
        TreeNode tree = generateHuffman4aryTree(vertAndFrequencyPairs);
        ArrayList<TreeSet<Integer>> depthToNodes = groupNodesByDepth(tree);
        Map<Integer,String> map = generateVertMap(depthToNodes);

        StringBuilder result = new StringBuilder();

        if (preserveOrder) {
            // Trennzeichen T
            result.append("T");
            // Liste: Für jeden Knoten die Tiefe des Knotens im Huffman Baum
            appendList(vertices.stream().map(vert -> map.get(vert).length()).collect(Collectors.toList()), result);
        }
        else{
            // Liste: Für jede Huffman-Baum Tiefe: Anzahl der Knoten in der Tiefe
            appendList(depthToNodes.stream().map(TreeSet::size).collect(Collectors.toList()), result);
        }
        // Alle Kanten
        for (Pair<Integer,Integer> edge:graph.getEdges()){
            result.append(map.get(edge.getV1()));
            result.append(map.get(edge.getV2()));
        }
        return result.toString();
    }

    /**
     * Implements {@link GraphEncoding#load(Graph, String)}
     * @param graph a {@link Graph} object
     * @param repr the DNA sequence of a graph
     */
    public void load(Graph graph, String repr){
        if (repr.equals("TT") || repr.equals("TTT")) {
            return;
        }

        ArrayList<Integer> vertices = new ArrayList<>();
        ArrayList<Pair<Integer,Integer>> edges = new ArrayList<>();

        boolean isOrderPreserved = repr.charAt(0) == 'T';

        int vertCount;
        Map<Integer, TreeSet<Integer>> depthToNodes = new HashMap<>();
        if (isOrderPreserved) {
            // preserveOrder == true:
            // Trennzeichen T (schon überprüft)
            repr = repr.substring(1);
            // Liste: Für jeden Knoten die Tiefe des Knotens im Huffman Baum
            ArrayList<Integer> nodeDepths = new ArrayList<>();
            repr = parseList(nodeDepths, repr);
            vertCount = nodeDepths.size();
            for (int i = 0; i < nodeDepths.size(); i++) {
                int depth = nodeDepths.get(i);
                if (!depthToNodes.containsKey(depth))
                    depthToNodes.put(depth, new TreeSet<>());
                depthToNodes.get(depth).add(i);
            }
            // Trennzeichen T
            // Alle Kanten
        }
        else{
            // preserveOrder == false:
            // Länge k Länge der Kodierung der maximimalen Anzahl der Knoten pro Tiefe)
            ArrayList<Integer> countPerDepth = new ArrayList<>();
            repr = parseList(countPerDepth, repr);
            vertCount = 0;
            for (int count : countPerDepth) {
                TreeSet<Integer> set = new TreeSet<>();
                for (int j = 0; j < count; j++) {
                    set.add(vertCount);
                    vertCount++;
                }
                depthToNodes.put(depthToNodes.size(), set);
            }
            // Trennzeichen T
            // Alle Kanten
        }


        Map<Integer, String> map = generateVertMap(convertIntegerMapToArray(depthToNodes));
        Map<String, Integer> invMap = invertMap(map);
        for (int i = 0; i < vertCount; i++) {
            vertices.add(i);
        }

        int readIndex = 0;
        Integer firstVertOfEdge = null;
        for (int i = readIndex + 1; i <= repr.length(); i++) {
            String subStr = repr.subSequence(readIndex, i).toString();

            if (invMap.containsKey(subStr)) {
                readIndex = i;
                if (firstVertOfEdge == null) {
                    firstVertOfEdge = invMap.get(subStr);
                } else {
                    edges.add(new Pair<>(firstVertOfEdge, invMap.get(subStr)));
                    firstVertOfEdge = null;
                }
            }
        }

        graph.setEdges(edges);
        graph.setVertices(vertices);
    }
}
