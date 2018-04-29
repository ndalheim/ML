import java.util.ArrayList;

/**
 * Created by naedd on 29.04.2018.
 */
public class Node {


    private ArrayList<Node> childs;
    private Node parent;

    private double gain;

    private Attribute attribute;
    private String parentAttValue;

    public Node(Node parent, String parentAttValue) {
        this.parent = parent;
        this.parentAttValue = parentAttValue;
    }

    public String getParentAttValue() {
        return parentAttValue;
    }

    public void setParentAttValue(String parentAttValue) {
        this.parentAttValue = parentAttValue;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public ArrayList<Node> getChilds() {
        return childs;
    }

    public void setChilds(ArrayList<Node> childs) {
        this.childs = childs;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }
}
