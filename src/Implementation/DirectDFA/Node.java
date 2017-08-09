package Implementation.DirectDFA;

import java.util.List;

/**
 * Created by Gabriel Brolo on 08/08/2017.
 */
public class Node {

    private boolean isPosition;
    private boolean isLast;
    private int position;
    private String symbol;

    private boolean isParent;
    private Node leftChild;
    private Node rightChild;
    private Node onlyChild;

    private boolean nullable;
    private List<Integer> firstPos;
    private List<Integer> lastPos;

    public Node(String symbol, Node leftChild, Node rightChild) {
        this.symbol = symbol;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public Node(String symbol, Node onlyChild) {
        this.symbol = symbol;
        this.onlyChild = onlyChild;
    }

    public Node(String symbol) {
        this.symbol = symbol;
    }

    public void setIsPosition(boolean isPosition) { this.isPosition = isPosition; }
    public void setPosition(int position) { this.position = position; }
    public void setIsParent(boolean isParent) { this.isPosition = isParent; }
    public void setFirstPos(List<Integer> firstPos) { this.firstPos = firstPos; }
    public void setLastPos(List<Integer> lastPos) { this.lastPos = lastPos; }
    public void setNullable(boolean nullable) { this.nullable = nullable; }
    public void setLast(boolean isLast) { this.isLast = isLast; }

    public boolean getIsPosition() { return this.isPosition; }
    public int getPosition() { return this.position; }
    public boolean getIsParent() { return this.isParent; }
    public List<Integer> getFirstPos() { return  this.firstPos; }
    public List<Integer> getLastPos() { return this.lastPos; }
    public boolean getNullable() { return this.nullable; }
    public String getSymbol() { return this.symbol; }
    public Node getLeftChild() { return this.leftChild; }
    public Node getRightChild() { return this.rightChild; }
    public Node getOnlyChild() { return this.onlyChild; }
    public boolean isLast() { return this.isLast; }
}
