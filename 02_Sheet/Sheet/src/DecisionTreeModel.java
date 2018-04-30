import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by naedd on 29.04.2018.
 */
public class DecisionTreeModel {

    private Node root;

    private int maxDeth;

    public void predict(DataSet predictionSet, Attribute classAttribute) {
        for (int i = 0; i < predictionSet.getRows(); i++) {
            predictRow(predictionSet, i * predictionSet.getColumns(), classAttribute);
        }
    }

    protected void predictRow(DataSet dataSet, int rowStartIndex, Attribute classAttribute) {

        String value = findClassAttributeValue(root, dataSet, rowStartIndex, classAttribute);
        dataSet.getData()[rowStartIndex + classAttribute.getColumnInDataset()] = value;
    }

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

    public void trainModel(DataSet dataset,
                           ArrayList<Attribute> attributes,
                           Attribute classAttribute) {

        trainModel(dataset, attributes, classAttribute, Integer.MAX_VALUE);

    }

    public void trainModel(DataSet dataset,
                           ArrayList<Attribute> attributes,
                           Attribute classAttribute,
                           int maxDepth) {

        setMaxDeth(maxDepth);

        int[] validRows = new int[dataset.getRows()];
        Arrays.fill(validRows, 1);

        ArrayList<Attribute> remainingAttributes = new ArrayList<>(attributes);
        remainingAttributes.remove(classAttribute);

        Node root = new Node(null, null);
        trainModelOnSubset(dataset, remainingAttributes,
                validRows, root, classAttribute, 0);
        setRoot(root);
    }


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
     * Compute entropy on subset
     *
     * @param dataset
     * @param validRows
     * @param classAttribute
     * @return double
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
     * Compute information gain
     *
     * @param dataset
     * @param validRows
     * @param classAttribute
     * @param attribute
     * @return double
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

    protected Node getRoot() {
        return root;
    }

    protected void setRoot(Node root) {
        this.root = root;
    }

    protected int getMaxDeth() {
        return maxDeth;
    }

    protected void setMaxDeth(int maxDeth) {
        this.maxDeth = maxDeth;
    }
}
