import javax.xml.crypto.Data;
import java.util.*;

/**
 * This class save all dataset information of an arff file
 * Created by naedd on 22.04.2018.
 */
public class DataSet {

    private String[] data;
    private int rows;
    private int columns;

    /**
     * Constructor
     *
     * @param data
     * @param rows
     * @param columns
     */
    public DataSet(String[] data, int rows, int columns) {
        this.data = data;
        this.rows = rows;
        this.columns = columns;
    }

    public DataSet(DataSet dataset) {
        this.data = Arrays.copyOf(dataset.getData(), dataset.getData().length);
        this.rows = dataset.getRows();
        this.columns = dataset.getColumns();
    }


    /**
     * Compute the probabilities of all valid rows to the given classAttribute
     *
     * @param validRows
     * @param classAttribute
     * @return
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
     * Filter all rows with the given attribute value
     *
     * @param validRow
     * @param attribute
     * @return ArrayList<int[]>
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
     * @return int
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * Getter of columns
     *
     * @return int
     */
    public int getColumns() {
        return this.columns;
    }


    public String getEntryInDataset(Attribute attribute, int row) {
        return data[row * columns + attribute.getColumnInDataset()];
    }

    protected String[] getData() {
        return data;
    }

    protected void setData(String[] data) {
        this.data = data;
    }

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

    protected void copyDataSetLine(String[] target, int targetIndex, int row) {

        int startIndex = row * columns;
        int endIndex = startIndex + columns;
        for (int j = startIndex; j < endIndex; j++) {
            target[targetIndex] = data[j];
            targetIndex++;
        }
    }

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
