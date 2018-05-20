package models.decisiontree;

import data.Attribute;

import java.util.ArrayList;

/**
 * Class to represent a models.decisiontree.Node of the DecisionTree
 *
 * Created by naedd on 29.04.2018.
 */
public class Node {


    private ArrayList<Node> childs;
    private Node parent;
    private double gain;
    private Attribute attribute;
    private String parentAttValue;
    private String resultValue;

    private int[] validRows;

    /**
     * Constructor of a DecisionTree models.decisiontree.Node
     *
     * @param parent node of this node
     * @param parentAttValue the attribute value of the parent node
     */
    public Node(Node parent, String parentAttValue, int[] validRows) {
        this.parent = parent;
        this.parentAttValue = parentAttValue;
        this.validRows = validRows;
    }

    /**
     * Add a child node to this node
     *
     * @param node which should have a new child node
     */
    public void addChild(Node node) {
        if (childs == null) {
            childs = new ArrayList<>();
        }
        childs.add(node);
    }

    /**
     * Remove the last child node of the decisionTree
     */
    public void removeLastChild() {
        childs.remove(childs.size() - 1);
    }

    /**
     * Return the child node for a given attribute value
     *
     * @param attributeValue you want to filter
     * @return the child node
     */
    public Node findChild(String attributeValue) {
        for (Node node : childs) {
            if (node.parentAttValue.equals(attributeValue)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Helpmethod to test if a node is the last node of a DecisionTree.
     * The last node of the tree save the result to the classAttribute
     *
     * @return true if it a result node else false
     */
    public boolean isResultNode() {
        return childs == null || childs.isEmpty();
    }

    /**
     * Return the result value of the this node
     *
     * @return the result value as String
     */
    public String getResultValue() {
        return resultValue;
    }

    /**
     * Set the result value of this node
     *
     * @param resultValue the value of the result to the classAttribute
     */
    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    /**
     * Getter of the parent node attribute value
     *
     * @return the attribute value of the parent node
     */
    public String getParentAttValue() {
        return parentAttValue;
    }

    /**
     * Setter of the parent node attribute value
     *
     * @param parentAttValue of the attribute value of the parent node
     */
    public void setParentAttValue(String parentAttValue) {
        this.parentAttValue = parentAttValue;
    }

    /**
     * Getter of the attribute of this node
     *
     * @return the attribute of this node
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * Setter of the attribute of this node
     *
     * @param attribute which this node should have
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * Getter of all child nodes of this node
     *
     * @return the list of all child nodes
     */
    public ArrayList<Node> getChilds() {
        return childs;
    }

    /**
     * Setter of all child nodes of this node
     *
     * @param childs which this node should have
     */
    public void setChilds(ArrayList<Node> childs) {
        this.childs = childs;
    }

    /**
     * Getter of the parent node
     *
     * @return the parent node of this node
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Setter of the parent node
     *
     * @param parent which this node should have
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Getter of the information gain of this node
     *
     * @return the information gain
     */
    public double getGain() {
        return gain;
    }

    /**
     * Setter of the information gain of this node
     *
     * @param gain which this node should have
     */
    public void setGain(double gain) {
        this.gain = gain;
    }


    public int[] getValidRows() {
        return validRows;
    }

    public void setValidRows(int[] validRows) {
        this.validRows = validRows;
    }
}
