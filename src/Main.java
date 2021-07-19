import de.unijena.DNAGraphUtils.*;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class Main {
    public static void main(String[] args){

        var graph = new Graph("G=({a,b,c},{(a,b),(a,c),(b,b),(b,c),(c,b)})", new NaturalGraphEncoding());
        var graph2 = new Graph("G=({a,b,c,d,e,f,g},{(a,d),(b,f),(b,b),(d,a),(b,g)})", new NaturalGraphEncoding());
        var graph3 = new Graph("G=({a,b,c,k,l,p,m,d,e,f,g,q},{(a,d),(b,f),(b,b),(d,a),(b,g)})", new NaturalGraphEncoding());
        var decodedGraph2Without = new de.unijena.DNAGraphUtils.Graph(graph2.toString(new SumGraphEncoding(), false), new SumGraphEncoding());

        System.out.println(GraphBenchmark.testIsomorphismPreservation(new SumGraphEncoding()));
        System.out.println(GraphBenchmark.testIsomorphismPreservation(new FixedLengthGraphEncoding()));
        System.out.println(GraphBenchmark.testIsomorphismPreservation(new HuffmanGraphEncoding()));

        GraphEncoding[] encodings = {new SumGraphEncoding(), new FixedLengthGraphEncoding(), new HuffmanGraphEncoding()};
        Random rand = new Random();
        rand.setSeed(2);
        GraphBenchmark.sampleEncodings(encodings, 100, rand);
    }
}
