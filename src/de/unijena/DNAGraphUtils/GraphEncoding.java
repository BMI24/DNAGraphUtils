package de.unijena.DNAGraphUtils;

/**
 * Interface for classes that are capable of encoding {@link Graph} objects to DNA sequences
 * and decodes DNA sequences to {@link Graph} objects.
 */
public interface GraphEncoding {
    /**
     * Encodes a graph to a DNA sequence with the option to preserve the current vertex order.
     *
     * @param graph a {@link Graph} object
     * @param preserveOrder indicates whether the order needs to be preserved
     * @return DNA sequence of the graph
     */
    String toString(Graph graph, boolean preserveOrder);

    /**
     * Decodes a DNA sequence to a {@link Graph} object.
     *
     * @param graph a {@link Graph} object
     * @param repr the DNA sequence of a graph
     */
    void load(Graph graph, String repr);
}
