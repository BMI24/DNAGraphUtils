package de.unijena.DNAGraphUtils;

/**
 * Generic pair class.
 * To create pair object with every types you want.
 *
 * @param <T> first type to add
 * @param <V> second type to add
 */
public class Pair<T,V>{
    private T v1;
    private V v2;

    /**
     * Constructs a new instance from a given instance.
     *
     * @param p instance to clone
     */
    public Pair(Pair<T,V> p){
        this.v1 = p.v1;
        this.v2 = p.v2;
    }

    /**
     * Constructs a new instance with the given elements
     *
     * @param v1 first element
     * @param v2 second element
     */
    public Pair(T v1, V v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    /**
     * @return value of V1
     */
    public T getV1() {
        return v1;
    }

    /**
     * @return value of v2
     */
    public V getV2() {
        return v2;
    }

    /**
     * @param v1 value to set V1 to
     */
    public void setV1(T v1) {
        this.v1 = v1;
    }

    /**
     * @param v2 value to set V2 to
     */
    public void setV2(V v2) {
        this.v2 = v2;
    }
}