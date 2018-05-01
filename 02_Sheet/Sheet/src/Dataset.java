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
    public String getEntryInDataset(Attribute attribute, int row) {
        return data[row * columns + attribute.getColumnInDataset()];
    }

    /**
     * Getter of the DataSet data
     *
     * @return the data of the DataSet
     */
    protected String[] getData() {
        return data;
    }

    /**
     * Setter of the DataSet data
     *
     * @param data of the DataSet
     */
    protected void setData(String[] data) {
        this.data = data;
    }

    /**
     * Randomly extract a test-set from the dataset
     *
     * @param percentage as seed for the size of the test-set
     * @return the test-set as DataSet
     */
    public DataSet extractTestDataset(double percentage) {

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

}
