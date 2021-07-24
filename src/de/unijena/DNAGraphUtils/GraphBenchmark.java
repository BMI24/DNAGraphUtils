package de.unijena.DNAGraphUtils;

import de.unijena.DNAGraphUtils.Graph;
import de.unijena.DNAGraphUtils.GraphEncoding;
import de.unijena.DNAGraphUtils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

/**
 * Benchmarks implementations of {@link GraphEncoding}.
 * Contains method to check for isomorphism with random graphs.
 * Contains method to generate for different implementations DNA sequences.
 */
public class GraphBenchmark {
    /**
     * Creates a {@link Graph} object with the verticesNumber and edgeNumber.
     * Creates the edges with random vertices (uniformly distributed).
     *
     * @param verticesNumber number of vertices that a graph should contain
     * @param edgeNumber number of edges that a graph should contain
     * @param rand Instance of {@link Random} which is used for random vertex selection. May be null
     * @return a new {@link Graph} object
     */
    public static Graph createGraph(int verticesNumber, int edgeNumber, Random rand){
        if (rand == null)
            rand = new Random();
        ArrayList<Integer> vertices = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> edges = new ArrayList<>();

        for (int i = 0; i < verticesNumber; i++) {
            vertices.add(i);
        }

        for (int i = 0; i < edgeNumber; i++) {
            Integer ranVert1 = vertices.get(rand.nextInt(vertices.size()));
            Integer ranVert2 = vertices.get(rand.nextInt(vertices.size()));

            edges.add(new Pair<>(ranVert1, ranVert2));
        }

        return new Graph(vertices, edges);
    }

    /**
     * Creates a {@link Graph} object with the verticesNumber and edgeNumber.
     * Creates the edges with random vertices (normally distributed with the mean and variance values).
     *
     * @param verticesNumber number of vertices that a graph should contain
     * @param edgeNumber number of edges that a graph should contain
     * @param mean value for vertex selection during edge creation
     * @param variance value for vertex selection during edge creation
     * @param rand Instance of {@link Random} which is used for random vertex selection. May be null
     * @return a new {@link Graph} object
     */
    public static Graph createGraph(int verticesNumber, int edgeNumber, double mean, double variance, Random rand){
        if (rand == null)
            rand = new Random();
        ArrayList<Integer> vertices = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> edges = new ArrayList<>();

        for (int i = 0; i < verticesNumber; i++) {
            vertices.add(i);
        }

        for (int i = 0; i < edgeNumber; i++) {
            int ranIndex1 = (int) (rand.nextGaussian() * variance + mean);
            ranIndex1 = Math.max(0, Math.min(vertices.size()-1, ranIndex1));
            Integer ranVert1 = vertices.get(ranIndex1);

            int ranIndex2 = (int) (rand.nextGaussian() * variance + mean);
            ranIndex2 = Math.max(0, Math.min(vertices.size()-1, ranIndex2));
            Integer ranVert2 = vertices.get(ranIndex2);

            edges.add(new Pair<>(ranVert1, ranVert2));
        }

        return new Graph(vertices, edges);
    }

    /**
     * Creates for a number of 1 to "maxVerticesNumber" vertices a number of "maxGraphNumber" different graphs.
     * Creates the same amount of graphs random uniformly and normally distributed.
     * Encodes with the implementation all graphs to DNA sequence with preserveOrder true and false.
     * Than decodes the DNA sequences and check if any generated graph is not isomorph to the corresponding
     * decoded graph.
     *
     * @param code contains an instance of an {@link GraphEncoding} implementation
     * @return true if every random graph is isomorph to the decoded counterpart, else false
     */
    public static boolean testIsomorphismPreservation(GraphEncoding code){
        int maxVerticesNumber = 10, maxGraphNumber = 10;
        Random rand = new Random();

        for (int i = 1; i < maxVerticesNumber; i++) {
            int maxEdgeNumber = i*i;

            for (int j = 0; j < maxGraphNumber; j++) {
                Graph randomGraph = createGraph(i, rand.nextInt(maxEdgeNumber), rand);

                String preserveOrderSequence = randomGraph.toString(code, true);
                String noOrderSequence = randomGraph.toString(code, false);

                Graph preserveOrderGraph = new Graph(preserveOrderSequence, code);
                Graph noOrderGraph = new Graph(noOrderSequence, code);

                if(!randomGraph.isIsomorphTo(preserveOrderGraph) || !randomGraph.isIsomorphTo(noOrderGraph)){
                    return false;
                }
            }
        }

        for (int i = 1; i < maxVerticesNumber; i++) {
            int maxEdgeNumber = i*i;
            double mean = i/2.d, variance = rand.nextDouble()*(i/2.d);

            for (int j = 0; j < maxGraphNumber; j++) {
                Graph randomGraph = createGraph(i, rand.nextInt(maxEdgeNumber), mean, variance, rand);

                String preserveOrderSequence = randomGraph.toString(code, true);
                String noOrderSequence = randomGraph.toString(code, false);

                Graph preserveOrderGraph = new Graph(preserveOrderSequence, code);
                Graph noOrderGraph = new Graph(noOrderSequence, code);

                if(!randomGraph.isIsomorphTo(preserveOrderGraph) || !randomGraph.isIsomorphTo(noOrderGraph)){
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Creates for every number of 1 to "maxVerticesCount" vertices a random uniformly distributed graph.
     * Encodes with the implementations all graphs to DNA sequence with preserveOrder true and false.
     * Creates CSV file with the information for every graph: number of vertices, number of edges,
     * graph as string and DNA sequence with preserveOrder true and false for every implementation in the list.
     * Saves CSV file into a given path.
     *
     * @param encodings list of instances of {@link GraphEncoding} implementations
     * @param maxVerticesCount number of the maximal count of vertices in a graph
     * @param rand Instance of {@link Random} which is used for random vertex selection. May be null
     * @return file path of the CSV file
     */
    //vllt. noch Durchschnitt berechnen wie viel besser eine Struktur war oder so um zurück zu geben wie gut diese abgeschnitten haben
    public static String sampleEncodings(GraphEncoding[] encodings, int maxVerticesCount, Random rand){
        if (rand == null)
            rand = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String folderPath = "benchmark";
        //noinspection ResultOfMethodCallIgnored
        new File(folderPath).mkdirs();
        String filePath = folderPath + "/" + LocalDateTime.now().format(formatter) +"-DNASequenceLength.csv";
        try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.append("VerticesInGraph").append(";");
            writer.append("EdgesInGraph").append(";");
            writer.append("graphString").append(";");

            for (int i = 0; i < encodings.length; i++) {
                writer.append("preserveOrder").append(encodings[i].getClass().getSimpleName()).append(";");
                writer.append("noOrder").append(encodings[i].getClass().getSimpleName());

                if(i+1 < encodings.length){
                    writer.append(";");
                }
            }
            writer.append("\n");

            for (int vertCount = 1; vertCount <= maxVerticesCount; vertCount++) {
                int edgeCount = (int) Math.ceil((vertCount * vertCount) / 4.d);

                Graph randomGraph = createGraph(vertCount, edgeCount, vertCount / 2d, vertCount * 0.25f, rand);

                writer.append(Integer.toString(vertCount)).append(';');
                writer.append(Integer.toString(edgeCount)).append(';');
                writer.append(randomGraph.toString()).append(';');

                for (int j = 0; j < encodings.length; j++) {
                    writer.append(randomGraph.toString(encodings[j], true)).append(';');
                    writer.append(randomGraph.toString(encodings[j], false));

                    if (j + 1 < encodings.length) {
                        writer.append(';');
                    }
                }

                writer.append('\n');
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return "";
        }

        return filePath;
    }
}
