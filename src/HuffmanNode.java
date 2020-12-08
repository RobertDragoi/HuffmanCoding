
public class HuffmanNode {
    int data;
    char c;

    HuffmanNode left;
    HuffmanNode right;

    public HuffmanNode(int data, char c, HuffmanNode left, HuffmanNode right) {
        this.data = data;
        this.c = c;
        this.left = left;
        this.right = right;
    }

    public HuffmanNode() {
    }

}