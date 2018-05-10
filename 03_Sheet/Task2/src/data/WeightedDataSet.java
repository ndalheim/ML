package data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * Class to represent weighted Datasets
 * Created by ken on 06.05.2018.
 */
public class WeightedDataSet extends DataSet {

    private double[] weights;

    /**
     * Constructor of an WeightedDataSet
     *
     * @param data    of the arff file
     * @param weights for every instance
     * @param rows    number of instances
     * @param columns number of attributes
     */
    public WeightedDataSet(String[] data, double[] weights, int rows, int columns) {
        super(data, rows, columns);
        this.weights = weights;
    }

    /**
     * Constructor of an WeightedDataSet
     *
     * @param data    of the arff file
     * @param rows    number of instances
     * @param columns number of attributes
     */
    public WeightedDataSet(String[] data, int rows, int columns) {
        super(data, rows, columns);
        initializeWeightsEqually();
    }

    /**
     * Constructor of an WeightedDataSet
     *
     * @param dataset of the arff file
     */
    public WeightedDataSet(WeightedDataSet dataset) {
        super(dataset);
        this.weights = Arrays.copyOf(dataset.getWeights(), dataset.getWeights().length);
    }


    /**
     * Set every instance weight to 1.0
     */
    public void initializeWeightsEqually() {
        setWeights(new double[getRows()]);
        double weight = 1.0 / getWeights().length;
        Arrays.fill(getWeights(), weight);
    }

    /**
     * Initialize instance weights to zero
     */
    public void initializeWeightsWithZero() {
        setWeights(new double[getRows()]);
    }


    /**
     * Sample the dataset according to there respective weights.
     *
     * @return New sampled dataset.
     */
    public WeightedDataSet sample() {

        int[] sampleIndices = new int[getRows()];

        // Todo : delete seed
        Random random = new Random(12345);
        for (int i = 0; i < getRows(); i++) {

            double value = random.nextDouble();
            if (value == 1) {
                i--;
                continue;
            }

            double sum = 0;
            for (int j = 0; j < getRows(); j++) {
                sum += getWeights()[j];
                if (sum >= value) {
                    sampleIndices[i] = j;
                    break;
                }
            }
        }
        WeightedDataSet copy = copyIndices(sampleIndices);
        copy.normalizeWeights();
        return copy;
    }

    /**
     * Copy only specific indices/rows/instances of this WeightedDataSet
     *
     * @param indices of the the copied WeightedDataSet
     * @return WeightedDataSet with the specific indices
     */
    private WeightedDataSet copyIndices(int indices[]) {

        String[] data = new String[getData().length];
        double[] weights = new double[getWeights().length];

        for (int i = 0; i < indices.length; i++) {

            copyDataSetRow(data, i, indices[i]);
            weights[i] = getWeights()[indices[i]];
        }
        WeightedDataSet wds = new WeightedDataSet(data, weights,
                getRows(), getColumns());
        return wds;
    }

    /**
     * Normilize the weights of all instances.
     */
    public void normalizeWeights() {

        double sum = 0.0;
        for (int i = 0; i < getWeights().length; i++) {
            sum += getWeights()[i];
        }
        for (int i = 0; i < getWeights().length; i++) {
            getWeights()[i] /= sum;
        }
    }


    /**
     * Update the new weights of every instance row
     *
     * @param prediction     the new WeightedDataSet
     * @param classAttribute of the arff file
     * @param modelError     of the dataset
     */
    public void updateWeights(WeightedDataSet prediction,
                              Attribute classAttribute,
                              double modelError) {

        int classIndex = classAttribute.getColumnInDataset();
        for (int i = 0; i < getRows(); i++) {
            if (getData()[i * getColumns() + classIndex]
                    .equals(prediction.getData()[i * getColumns() + classIndex])) {
                getWeights()[i] *= modelError / (1.0 - modelError);
            }
        }
    }

    /**
     * Compute the model error of the predicted WeightedDataSet
     *
     * @param prediction     the predicted WeightedDataSet
     * @param classAttribute of the arff file
     * @return the model error of the predicted WeightedDataSet
     */
    public double computeModelError(WeightedDataSet prediction,
                                    Attribute classAttribute) {

        double error = 0;
        int classIndex = classAttribute.getColumnInDataset();
        for (int i = 0; i < getRows(); i++) {
            if (!getData()[i * getColumns() + classIndex]
                    .equals(prediction.getData()[i * getColumns() + classIndex])) {
                error += getWeights()[i];
            }
        }
        return error;
    }

    /**
     * Set the value and weight of a specific entry of the dataSet
     *
     * @param attribute as column of the entry
     * @param row       of the entry
     * @param value     which the entry should have
     * @param weight    which the entry should have
     */
    public void setEntryInDataSet(Attribute attribute, int row, String value, double weight) {
        super.setEntryInDataSet(attribute, row, value);
        getWeights()[row] = weight;
    }


    /**
     * Copy a specific percentage proportion of a WeightedDataSet
     *
     * @param percentage how much you want to copy
     * @return the new generated WeightedDataSet
     */
    public WeightedDataSet copyDataSet(double percentage) {

        Random random = new Random();
        int dataSetRows = (int) ((double) getRows() * percentage);
        HashSet<Integer> rowIndexes = new HashSet<>();
        for (int i = 0; i < dataSetRows; i++) {
            int index = random.nextInt(getRows());
            if (rowIndexes.contains(index)) {
                i--;
                continue;
            }
            rowIndexes.add(index);
        }

        String[] data = new String[dataSetRows * getColumns()];
        double[] dataWeights = new double[dataSetRows];
        int index = 0;
        for (int row : rowIndexes) {
            copyDataSetRow(data, index, row);
            dataWeights[index] = getWeights()[row];
            index += 1;
        }
        WeightedDataSet dataSet = new WeightedDataSet(data, dataWeights,
                dataSetRows, getColumns());
        return dataSet;
    }

    /**
     * Randomly extract a test-set from the dataset
     *
     * @param percentage as seed for the size of the test-set
     * @return the test-set as data.DataSet
     */
    @Override
    public WeightedDataSet extractTestDataSet(double percentage) {

        Random random = new Random();
        int testDataSetRows = (int) ((double) getRows() * percentage);
        HashSet<Integer> testRowIndexes = new HashSet<>();
        for (int i = 0; i < testDataSetRows; i++) {
            int index = random.nextInt(getRows());
            if (testRowIndexes.contains(index)) {
                i--;
                continue;
            }
            testRowIndexes.add(index);
        }

        String[] testData = new String[testDataSetRows * getColumns()];
        double[] testDataWeights = new double[testDataSetRows];
        int index = 0;
        for (int row : testRowIndexes) {
            copyDataSetRow(testData, index, row);
            testDataWeights[index] = getWeights()[row];
            index += 1;
        }
        WeightedDataSet testDataSet = new WeightedDataSet(testData, testDataWeights,
                testDataSetRows, getColumns());

        int trainingRows = getRows() - testDataSetRows;
        String[] trainingData = new String[trainingRows * getColumns()];
        double[] trainingDataWeights = new double[trainingRows];
        index = 0;
        for (int i = 0; i < getRows(); i++) {
            if (testRowIndexes.contains(i)) {
                continue;
            }
            copyDataSetRow(trainingData, index, i);
            trainingDataWeights[index] = getWeights()[i];
            index += 1;
        }
        setData(trainingData);
        setRows(trainingRows);
        setWeights(testDataWeights);
        return testDataSet;
    }


    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }
}
