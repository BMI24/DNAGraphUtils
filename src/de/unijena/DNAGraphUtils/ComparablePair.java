package de.unijena.DNAGraphUtils;

/**
 * Implementation of "Pair".
 * Adds comparability to the pair objects (compares only the first value in pair).
 *
 * @param <T> first type to add (every type that is comparable)
 * @param <V> second type to add (every type)
 */
public class ComparablePair<T extends Comparable<T>,V> extends Pair<T,V> implements Comparable<Pair<T,V>>{
    public ComparablePair(T key, V value) {
        super(key, value);
    }

    @Override
    public int compareTo(Pair<T,V> other) {
        return this.getV1().compareTo(other.getV1());
    }
}
