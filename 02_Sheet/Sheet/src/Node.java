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
    private String resultValue;


    public Node(Node parent, String parentAttValue) {
        this.parent = parent;
        this.parentAttValue = parentAttValue;
    }

    public void addChild(Node node) {
        if (childs == null) {
            childs = new ArrayList<>();
        }
        childs.add(node);
    }

    public Node findChild(String attributeValue) {
        for (Node node : childs) {
            if (node.parentAttValue.equals(attributeValue)) {
                return node;
            }
        }
        return null;
    }

    public boolean isResultNode() {
        return childs == null || childs.isEmpty();
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
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
