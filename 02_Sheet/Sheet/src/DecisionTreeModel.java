import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to represent the model of a DecisionTree
 *
 * Created by naedd on 29.04.2018.
 */
public class DecisionTreeModel {

    private Node root;

    private int maxDepth;

    /**
     * Predict the class attribute values of a given test-set/ prediction-set
     *
     * @param predictionSet is the test-set of which you want to know the class attribute value
     * @param classAttribute is the attribute which use to test your decision tree
     */
    public void predict(DataSet predictionSet, Attribute classAttribute) {
        for (int i = 0; i < predictionSet.getRows(); i++) {
            predictRow(predictionSet, i * predictionSet.getColumns(), classAttribute);
        }
    }

    /**
     * Predict the class attribute value of a given row (instance)
     *
     * @param dataSet of the arff-file
     * @param rowStartIndex is the row you want to start your search
     * @param classAttribute is the attribute which use to test your decision tree
     */
    protected void predictRow(DataSet dataSet, int rowStartIndex, Attribute classAttribute) {

        String value = findClassAttributeValue(root, dataSet, rowStartIndex, classAttribute);
        dataSet.getData()[rowStartIndex + classAttribute.getColumnInDataset()] = value;
    }

    /**
     * Recursive method to find the value of the class-attribute for a given node
     *
     * @param node which class-attribute value you want to predict
     * @param dataSet of the arff-file
     * @param rowStartIndex is the row you want to start your search
     * @param classAttribute is the attribute which use to test your decision tree
     * @return
     */
    protected String findClassAttributeValue(Node node, DataSet dataSet, int rowStartIndex,
                                             Attribute classAttribute) {
        if (node.isResultNode()) {
            return node.getResultValue();
        }
        Attribute treeAttribute = node.getAttribute();
        String valueInDataSet = dataSet.getData()[rowStartIndex + treeAttribute.getColumnInDataset()];
        Node child = node.findChild(valueInDataSet);
        return findClassAttributeValue(child, dataSet, rowStartIndex, classAttribute);
    }


    public void printModel() {

        printModel(root.getChilds(), "");
    }

    /**
     * Print the DecisionTree Model visual on the Terminal
     *
     * @param childs are all nodes of the tree
     * @param prefix is the optional symbol in front of every node
     */
    protected void printModel(ArrayList<Node> childs, String prefix) {
        for (Node child : childs) {
            String line = prefix
                    + child.getParent().getAttribute().getName()
                    + " = "
                    + child.getParentAttValue();
            if (child.isResultNode()) {
                line = line + " : " + child.getResultValue();
                System.out.println(line);
            } else {
                System.out.println(line);
                printModel(child.getChilds(), prefix + " | ");
            }
        }
    }

    /**
     * Train DecisionTree Model with maximum depth.
     *
     * @param dataset of the arff file
     * @param attributes of the arff file
     * @param classAttribute is the attribute which use to test your decision tree
     */
    public void trainModel(DataSet dataset,
                           ArrayList<Attribute> attributes,
                           Attribute classAttribute) {

        trainModel(dataset, attributes, classAttribute, Integer.MAX_VALUE);

    }

    /**
     * Create the root node of the DecisionTree Model and all the other nodes.
     *
     * @param dataset of the arff file
     * @param attributes of the arff file
     * @param classAttribute is the attribute which use to test your decision tree
     * @param maxDepth of the decisionTree
     */
    public void trainModel(DataSet dataset,
                           ArrayList<Attribute> attributes,
                           Attribute classAttribute,
                           int maxDepth) {

        setMaxDepth(maxDepth);

        int[] validRows = new int[dataset.getRows()];
        Arrays.fill(validRows, 1);

        ArrayList<Attribute> remainingAttributes = new ArrayList<>(attributes);
        remainingAttributes.remove(classAttribute);

        Node root = new Node(null, null);
        trainModelOnSubset(dataset, remainingAttributes,
                validRows, root, classAttribute, 0);
        setRoot(root);
    }


    /**
     * Submethod of the trainModel-method to create all the childnodes of DecisionTreeModel-root node
     * on the remaining attributes and remaining valid rows.
     *
     * @param dataset of the arff file
     * @param remainingAttributes are all attributes which are left
     * @param validRows are all instances which are valid
     * @param newNode is the new generated Node in the decision tree
     * @param classAttribute is the attribute which use to test your decision tree
     * @param depth is the depth of the decision tree
     */
    protected void trainModelOnSubset(DataSet dataset,
                                      ArrayList<Attribute> remainingAttributes,
                                      int[] validRows,
                                      Node newNode,
                                      Attribute classAttribute,
                                      int depth) {

        double entropy = entropyOnSubset(dataset, validRows, classAttribute);
        if (entropy == 0.0) {
            int index = Utils.getIndexOfFirstOne(validRows);
            if (index >= 0) {
                newNode.setResultValue(dataset.getEntryInDataset(classAttribute, index));
            } else {
                newNode.setResultValue("Unknown");
            }
            return;
        }
        if(depth == maxDepth){

            double[] probs = dataset.computeProbabilities(validRows, classAttribute);
            int index = Utils.argMax(probs);
            newNode.setResultValue(classAttribute.getValues().get(index));
            return;
        }

        double maxGain = -1;
        Attribute maxAtt = null;
        for (Attribute attribute : remainingAttributes) {
            double gain = informationGain(dataset, validRows, classAttribute, attribute);
            if (gain > maxGain) {
                maxGain = gain;
                maxAtt = attribute;
            }
        }

        newNode.setAttribute(maxAtt);
        newNode.setGain(maxGain);


        ArrayList<int[]> validRowsForChilds = dataset.filterForAttributeValues(validRows, maxAtt);
        for (int i = 0; i < maxAtt.getValues().size(); i++) {

            String value = maxAtt.getValues().get(i);

            Node child = new Node(newNode, value);
            newNode.addChild(child);

            ArrayList<Attribute> childRemainingAtts = new ArrayList<>(remainingAttributes);
            childRemainingAtts.remove(maxAtt);
            trainModelOnSubset(dataset, childRemainingAtts,
                    validRowsForChilds.get(i), child, classAttribute,
                    depth + 1);
        }

    }

    /**
     * Compute the entropy on a given subset and returns it.
     *
     * @param dataset of the arff-file
     * @param validRows are all valid instances
     * @param classAttribute is the attribute which use to test our decision tree
     * @return the computed entropy for the given subset
     */
    protected double entropyOnSubset(DataSet dataset, int[] validRows, Attribute classAttribute) {

        double[] probs = dataset.computeProbabilities(validRows, classAttribute);

        double entropy = 0.0;
        double logConstant = Math.log10(2);
        for (int i = 0; i < probs.length; i++) {
            double element = probs[i];
            if (element != 0.0) {
                entropy -= element * (Math.log10(element) / logConstant);
            }
        }
        return entropy;
    }

    /**
     * Compute information gain for a given attribute and returns it.
     *
     * @param dataset of the arff-file
     * @param validRows  are all valid instances
     * @param classAttribute is the attribute which use to test our decision tree
     * @param attribute for which we compute the information gain
     * @return the information gain of the given attribute
     */
    protected double informationGain(DataSet dataset,
                                     int[] validRows,
                                     Attribute classAttribute,
                                     Attribute attribute) {

        double gain = entropyOnSubset(dataset, validRows, classAttribute);
        int parentNum = Utils.countOnes(validRows);
        ArrayList<int[]> valuesValidRows = dataset.filterForAttributeValues(validRows, attribute);
        for (int i = 0; i < valuesValidRows.size(); i++) {
            double entropy = entropyOnSubset(dataset, valuesValidRows.get(i), classAttribute);
            int childNum = Utils.countOnes(valuesValidRows.get(i));
            gain -= (double) childNum / (double) parentNum * entropy;
        }

        return gain;
    }

    /**
     * Return the DecisionTree root node
     * @return root node
     */
    protected Node getRoot() {
        return root;
    }

    /**
     * Set the DecisionTree root node
     * @param root of the DecisionTree Model
     */
    protected void setRoot(Node root) {
        this.root = root;
    }

    /**
     * Return the max depth of the DecisionTree which was set
     * @return the max DecisionTree depth
     */
    protected int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Set the max depth of the DecisionTree
     * @param maxDepth of the DecisionTree
     */
    protected void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
