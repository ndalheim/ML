package data;

import java.lang.reflect.Array;
import java.util.*;

/**
 * This class save all dataset information of an arff file
 *
 * Created by naedd on 22.04.2018.
 */
public class DataSet {

    private String[] data;
    private int rows;
    private int columns;

    /**
     * Constructor of an dataset
     *
     * @param data of the arff-file
     * @param rows is the number of instances of the arff-file
     * @param columns is the number of attributes of the arff-file
     */
    public DataSet(String[] data, int rows, int columns) {
        this.data = data;
        this.rows = rows;
        this.columns = columns;
    }

    /**
     * Constructor of an dataset
     *
     * @param dataset of the arff-file
     */
    public DataSet(DataSet dataset) {
        this.data = Arrays.copyOf(dataset.getData(), dataset.getData().length);
        this.rows = dataset.getRows();
        this.columns = dataset.getColumns();
    }


    public DataSet(DataSet dataSet, int[] rows){
        this.rows = rows.length;
        this.columns = dataSet.getColumns();
        this.data = copyDataSetFromSource(dataSet, rows);
    }

    private String[] copyDataSetFromSource(DataSet dataSet, int[] rows){
        String[] buffer = new String[this.rows * columns];
        for(int i = 0;  i < rows.length; i++){
            dataSet.copyDataSetRow(buffer, i, rows[i]);
        }
        return buffer;
    }


    /**
     * Compute the probabilities of all valid rows to the given classAttribute
     *
     * @param validRows are all valid instances which you only want to use for computation
     * @param classAttribute is the attribute which use to test your decision tree
     * @return the probabilities of all valid rows
     */
    public double[] computeProbabilities(int[] validRows, Attribute classAttribute) {

        double[] probs = new double[classAttribute.getValues().size()];
        for (int i = 0; i < rows; i++) {
            if (validRows[i] > 0) {
                int index = classAttribute.getValues().indexOf(data[i * columns + classAttribute.getColumnInDataset()]);
                probs[index] = probs[index] + 1;
            }
        }
        for (int i = 0; i < probs.length; i++) {
            int ones = Utils.countOnes(validRows);
            if (ones != 0) {
                probs[i] = probs[i] / (double) ones;
            }
        }
        return probs;
    }

    /**
     * Filter all rows which have the given attribute value
     *
     * @param validRow are all valid instances which you only want to use for computation
     * @param attribute which you want to filter
     * @return all rows which have the given attribute value as ArrayList<int[]>
     */
    public ArrayList<int[]> filterForAttributeValues(int[] validRow, Attribute attribute) {

        ArrayList<int[]> valueIndices = new ArrayList<>(attribute.getValues().size());
        for (int i = 0; i < attribute.getValues().size(); i++) {
            valueIndices.add(new int[rows]);
            Arrays.fill(valueIndices.get(i), -1);
        }
        for (int i = 0; i < rows; i++) {
            if (validRow[i] > 0) {
                String value = data[i * columns + attribute.getColumnInDataset()];
                valueIndices.get(attribute.getValues().indexOf(value))[i] = 1;
            }
        }
        return valueIndices;
    }

    /**
     * Getter of rows
     *
     * @return number of rows/ instances
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * Getter of columns
     *
     * @return number of columns/ attributes
     */
    public int getColumns() {
        return this.columns;
    }

    /**
     * Get the content from a specific dataset entry
     *
     * @param attribute for the column number
     * @param row for the row number
     * @return the content of the specific dataset entry
     */
    public String getEntryInDataSet(Attribute attribute, int row) {
        return data[row * columns + attribute.getColumnInDataset()];
    }

    public String getEntryInDataSet(int row, int column){
        return data[row * columns + column];
    }

    public void setEntryInDataSet(Attribute attribute, int row, String value) {
        data[row * columns + attribute.getColumnInDataset()] = value;
    }

    /**
     * Getter of the data.DataSet data
     *
     * @return the data of the data.DataSet
     */
    public String[] getData() {
        return data;
    }

    /**
     * Setter of the data.DataSet data
     *
     * @param data of the data.DataSet
     */
    protected void setData(String[] data) {
        this.data = data;
    }

    /**
     * Randomly extract a test-set from the dataset
     *
     * @param percentage as seed for the size of the test-set
     * @return the test-set as data.DataSet
     */
    public DataSet extractTestDataSet(double percentage) {

        Random random = new Random();
        int testDataSetRows = (int) ((double) rows * percentage);
        HashSet<Integer> testRowIndexes = new HashSet<>();
        for (int i = 0; i < testDataSetRows; i++) {
            int index = random.nextInt(rows);
            if (testRowIndexes.contains(index)) {
                i--;
                continue;
            }
            testRowIndexes.add(index);
        }

        String[] testData = new String[testDataSetRows * columns];
        int index = 0;
        for (int row : testRowIndexes) {
            copyDataSetLine(testData, index, row);
            index = index + columns;
        }
        DataSet testDataSet = new DataSet(testData, testDataSetRows, columns);

        int trainingRows = rows - testDataSetRows;
        String[] trainingData = new String[trainingRows * columns];
        index = 0;
        for (int i = 0; i < rows; i++) {
            if (testRowIndexes.contains(i)) {
                continue;
            }
            copyDataSetLine(trainingData, index, i);
            index = index + columns;
        }
        data = trainingData;
        rows = trainingRows;
        return testDataSet;
    }

    /**
     * Helpmethod to copy the dataset data
     *
     * @param target where you want to copy the
     * @param targetIndex which the copied data should be saved
     * @param row where the copy should start
     */
    protected void copyDataSetLine(String[] target, int targetIndex, int row) {

        int startIndex = row * columns;
        int endIndex = startIndex + columns;
        for (int j = startIndex; j < endIndex; j++) {
            target[targetIndex] = data[j];
            targetIndex++;
        }
    }

    /**
     * Copy the instance of a specific dataset row
     * @param target the object where to save the copied instance
     * @param targetRow the new row index
     * @param row of the to copy row
     */
    protected void copyDataSetRow(String[] target, int targetRow, int row) {

        int targetIndex = targetRow * columns;
        int startIndex = row * columns;
        int endIndex = startIndex + columns;
        for (int j = startIndex; j < endIndex; j++) {
            target[targetIndex] = data[j];
            targetIndex++;
        }
    }

    /**
     * Compute the accuracy of the predicted set to the classAttribute
     *
     * @param predictionSet is the test-set you want to compare
     * @param classAttribute is the attribute which use to test your decision tree
     * @return the percentage of correct predicted class attribute values of the test-set
     */
    public double computeAccuracy(DataSet predictionSet, Attribute classAttribute) {
        if (rows != predictionSet.rows || columns != predictionSet.columns) {
            throw new IllegalArgumentException("Datasets do not fit together.");
        }

        int counter = 0;
        for (int i = 0; i < rows; i++) {
            int index = i * columns + classAttribute.getColumnInDataset();
            if (data[index].equals(predictionSet.getData()[index])) {
                counter++;
            }
        }
        return (double) counter / rows;
    }

    public WeightedDataSet transformToWeightedDataSet(){
        return new WeightedDataSet(getData(), getRows(), getColumns());
    }

    /**
     * Build a FoldDataSetTuple for the given Class attribute value
     * @param classAttribute is the attribute which use to test your decision tree
     * @param k is the array position of the special class attribute value
     * @return a fold dataset tuple
     * @throws Exception
     */
    public FoldDataSetsTuple buildFolds(Attribute classAttribute, int k) throws Exception {

        this.shuffle();

        FoldDataSetsTuple tuple = new FoldDataSetsTuple();
        tuple.folds = stratification(classAttribute, k);
        tuple.compFolds = buildComplements(tuple.folds);
        tuple.numFolds = k;

        for(int i = 0; i < tuple.folds.length; i++){
            tuple.folds[i].shuffle();
        }
        for(int i = 0; i < tuple.compFolds.length; i++){
            tuple.compFolds[i].shuffle();
        }
        return tuple;
    }


    /**
     * Splits the entire dataset into a list of datasets based on the value of the class attribute
     * @param classAttribute is the attribute which use to test your decision tree
     * @param k is the array position of the special class attribute value
     * @return a array full of dataset folds
     */
    public FoldDataSet[] stratification(Attribute classAttribute, int k){

            ArrayList<Integer>[] valuesIndices = new ArrayList[classAttribute.getValues().size()];
            for(int i = 0; i < classAttribute.getValues().size(); i++){
                valuesIndices[i] = new ArrayList<>();
            }
            for(int i = 0; i < rows; i++){
                valuesIndices[classAttribute.getValues().indexOf(this.getEntryInDataSet(classAttribute, i))].add(i);
            }
            ArrayList<Integer>[] kDataSetRows = new ArrayList[k];
            for(int i = 0; i < k; i++){
                kDataSetRows[i] = new ArrayList<>();
            }
            int index = 0;
            for(int i = 0; i < valuesIndices.length; i++){
                for(Integer row : valuesIndices[i]){
                    kDataSetRows[index].add(row);
                    index++;
                    index %= k;
                }
            }
            FoldDataSet[] kDataSets = new FoldDataSet[k];
            for(int i = 0; i < k; i++){
                kDataSets[i] = new FoldDataSet(this, Utils.toIntArray(kDataSetRows[i]));
            }
            return kDataSets;
    }

    /**
     * Build the trainingsets out of the complement folds from the test set
     * @param foldDataSets are alle testsets
     * @return all trainingsets for all testsets
     */
    public FoldDataSet[] buildComplements(FoldDataSet[] foldDataSets){

        FoldDataSet[] compFolds = new FoldDataSet[foldDataSets.length];
        for(int i = 0; i < foldDataSets.length; i++){
            compFolds[i] = foldDataSets[i].buildComplement();
        }
        return  compFolds;
    }


    /**
     * Helper method to randomize the order of the instances in a fold
     * @throws Exception
     */
    public void shuffle() throws Exception {
        if(this instanceof WeightedDataSet){
            throw new Exception("Method not supported.");
        }

        ArrayList<Integer> indices = new ArrayList<>(rows);
        for(int i = 0; i < rows; i++){
            indices.add(i);
        }
        Collections.shuffle(indices);
        data = copyDataSetFromSource(this, Utils.toIntArray(indices));
    }


    protected void setRows(int rows) {
        this.rows = rows;
    }

    protected void setColumns(int columns) {
        this.columns = columns;
    }

}
