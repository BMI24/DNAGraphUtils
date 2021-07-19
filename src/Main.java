import de.unijena.DNAGraphUtils.*;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class Main {
    public static void main(String[] args){

        var graph = new Graph("G=({a,b,c},{(a,b),(a,c),(b,b),(b,c),(c,b)})", new NaturalGraphEncoding());
        var graph2 = new Graph("G=({a,b,c,d,e,f,g},{(a,d),(b,f),(b,b),(d,a),(b,g)})", new NaturalGraphEncoding());
        var graph3 = new Graph("G=({a,b,c,k,l,p,m,d,e,f,g,q},{(a,d),(b,f),(b,b),(d,a),(b,g)})", new NaturalGraphEncoding());
        var decodedGraph2Without = new de.unijena.DNAGraphUtils.Graph(graph2.toString(new SumGraphEncoding(), false), new SumGraphEncoding());

        var graph5 = new Graph("G=({},{})", new NaturalGraphEncoding());
        var graph6 = new Graph("G=({a,b,c,d,e},{})", new NaturalGraphEncoding());

        //var graph4 = new Graph("G=({a,b,c},{(b,b)})", new NaturalGraphEncoding());
        //graph4.toString(new FixedLengthGraphEncoding(), false);
        //System.out.println(new Graph(graph3.toString(Graph.CODE_FIXED_LENGTH, true), Graph.CODE_FIXED_LENGTH).isIsomorphTo(graph3));

        //System.out.println(graph.toString(new SumGraphEncoding(), true));
        //System.out.println(graph2.toString(new SumGraphEncoding(), true));
        //System.out.println(graph2.toString(new SumGraphEncoding(), false));
        //System.out.println(graph3.toString(new SumGraphEncoding(), true));
        //System.out.println(graph3.toString(new SumGraphEncoding(), false));

        //var decodedGraph2 = new de.unijena.DNAGraphUtils.Graph(graph2.toString(new SumGraphEncoding(), true), new SumGraphEncoding());
        //var decodedGraph3 = new de.unijena.DNAGraphUtils.Graph(graph3.toString(new SumGraphEncoding(), true), new SumGraphEncoding());
        //var decodedGraph3Without = new de.unijena.DNAGraphUtils.Graph(graph3.toString(new SumGraphEncoding(), false), new SumGraphEncoding());
        //System.out.println("Decoded Graph2: " + decodedGraph2);
        //System.out.println("Encoded Graph2: " + graph2.toString(new SumGraphEncoding(), false));
        //System.out.println("Decoded Graph2: " + decodedGraph2Without);
        //System.out.println("Decoded Graph3: " + decodedGraph3);
        //System.out.println("Encoded Graph3: " + decodedGraph3Without);

        //System.out.println(graph2.isIsomorphTo(decodedGraph2));
        //System.out.println(graph2.isIsomorphTo(decodedGraph2Without));

        /*System.out.println(graph.toString(de.unijena.DNAGraphUtils.Graph.CODE_FIXED_LENGTH, true));
        System.out.println(graph2.toString(de.unijena.DNAGraphUtils.Graph.CODE_FIXED_LENGTH, true));
        System.out.println(graph3.toString(de.unijena.DNAGraphUtils.Graph.CODE_FIXED_LENGTH, true));*/

        //System.out.println("G1 T " + graph.toString(de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN, true));
        //System.out.println("G1 T " + new de.unijena.DNAGraphUtils.Graph(graph.toString(de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN, true), de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN));
        //System.out.println("G1 F " + new de.unijena.DNAGraphUtils.Graph(graph.toString(de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN, false), de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN));
        //System.out.println("G2 T " + graph2.toString(de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN, true));
        //System.out.println("G2 T " + new de.unijena.DNAGraphUtils.Graph(graph2.toString(de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN, true), de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN));
        /*System.out.println("G2 F " + graph2.toString(de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN, false));
        System.out.println("G2 F " + new de.unijena.DNAGraphUtils.Graph(graph2.toString(de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN, false), de.unijena.DNAGraphUtils.Graph.CODE_HUFFMAN));*/

        //System.out.println(graph3.isIsomorphTo(new Graph(graph3.toString(Graph.CODE_HUFFMAN, true), Graph.CODE_HUFFMAN)));

        //System.out.println(graph2.isIsomorphTo(new de.unijena.DNAGraphUtils.Graph(graph2.toString(de.unijena.DNAGraphUtils.Graph.CODE_SUM, false), de.unijena.DNAGraphUtils.Graph.CODE_SUM)));
        //System.out.println(graph3.isIsomorphTo(new de.unijena.DNAGraphUtils.Graph(graph3.toString(de.unijena.DNAGraphUtils.Graph.CODE_SUM, false), de.unijena.DNAGraphUtils.Graph.CODE_SUM)));

        //System.out.println(createGraph(3, 5));
        //System.out.println(createGraph(3, 5));
        //System.out.println(createGraph(3, 10));
        //System.out.println(createGraph(10, 15));
        //System.out.println("\n");
        //System.out.println(createGraph(3, 5, 1, 0.25));
        //System.out.println(createGraph(3, 5, 1, 0.5));
        //System.out.println(createGraph(3, 10, 1, 0.3));
        //boolean allTrue = true;
        /*for (int i = 0; i < 100; i++) {
            var testGraph = createGraph(10, 200, 5, 3);
            //System.out.println(testGraph.toString(de.unijena.DNAGraphUtils.Graph.CODE_SUM, true));
            //System.out.println(testGraph.toString(Graph.CODE_FIXED_LENGTH, true));
            System.out.println(testGraph.toString(Graph.CODE_HUFFMAN, true));
            allTrue = allTrue && testGraph.isIsomorphTo(new Graph(testGraph.toString(Graph.CODE_HUFFMAN, true), Graph.CODE_HUFFMAN));
        }
        System.out.println(allTrue);*/

        System.out.println(GraphBenchmark.testIsomorphismPreservation(new SumGraphEncoding()));
        System.out.println(GraphBenchmark.testIsomorphismPreservation(new FixedLengthGraphEncoding()));
        System.out.println(GraphBenchmark.testIsomorphismPreservation(new HuffmanGraphEncoding()));

        GraphEncoding[] encodings = {new SumGraphEncoding(), new FixedLengthGraphEncoding(), new HuffmanGraphEncoding()};
        GraphEncoding[] encodings2 = {new SumGraphEncoding(), new HuffmanGraphEncoding()};
        Random rand = new Random();
        rand.setSeed(2);
        GraphBenchmark.sampleEncodings(encodings, 100, rand);
        //de.unijena.DNAGraphUtils.GraphBenchmark.compareDNASequenceLengthToCSV(encodings2, 10);
    }
}
