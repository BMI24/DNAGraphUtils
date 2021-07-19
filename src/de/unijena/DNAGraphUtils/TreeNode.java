package de.unijena.DNAGraphUtils;

/**
 * Represents a node in a Huffman tree with 4 children.
 */
public class TreeNode implements Comparable<TreeNode>{
    private int frequency;
    private int value;
    private TreeNode[] children;

    /**
     * Default empty constructor.
     */
    public TreeNode() {
    }

    /**
     * Constructs a new instance with an intial frequency and value.
     *
     * @param frequency initial frequency
     * @param value initial value
     */
    public TreeNode(int frequency, int value) {
        this.frequency = frequency;
        this.value = value;
        this.children = new TreeNode[4];
    }

    /**
     * @param value value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @param frequency frequency to set
     */
    public void setFrequency(int frequency){
        this.frequency = frequency;
    }

    /**
     * @param children children array to set. Must be exactly of length 4
     */
    public void setChildren(TreeNode[] children) {
        if (children.length != 4)
            throw new IllegalArgumentException("Children array must have a length of 4");
        this.children = children;
    }

    /**
     * @return current value
     */
    public int getValue() {
        return value;
    }

    /**
     * @return current frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @return children array with length 4
     */
    public TreeNode[] getChildren() {
        return children;
    }

    /**
     * Implements {@link Comparable<TreeNode>}.
     *
     * @param other {@link TreeNode} to compare to
     * @return {@see Comparable}
     */
    @Override
    public int compareTo(TreeNode other) {
        return Integer.compare(this.getFrequency(), other.getFrequency());
    }
}
