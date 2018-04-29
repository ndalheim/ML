import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class save all dataset information of an arff file
 * Created by naedd on 22.04.2018.
 */
public class Dataset {

    private String[] data;
    private int rows;
    private int columns;

    /**
     * Constructor
     * @param data
     * @param rows
     * @param columns
     */
    public Dataset(String[] data, int rows, int columns) {
        this.data = data;
        this.rows = rows;
        this.columns = columns;
    }


    /**
     * Compute the probabilities of all valid rows to the given classAttribute
     * @param validRows
     * @param classAttribute
     * @return
     */
    public double[] computeProbabilities(int[] validRows, Attribute classAttribute){

        double[] probs = new double[classAttribute.values.size()];
        for(int i = 0; i < rows; i++){
            if(validRows[i] > 0) {
                int index = classAttribute.values.indexOf(data[i * columns + classAttribute.columnInDataset]);
                probs[index] = probs[index] + 1;
            }
        }
        for(int i = 0; i < probs.length; i++){
            probs[i] = probs[i] / (double) Utils.countOnes(validRows);
        }
        return probs;
    }

    /**
     * Filter all rows with the given attribute value
     * @param validRow
     * @param attribute
     * @return ArrayList<int[]>
     */
    public ArrayList<int[]> filterForAttributeValues(int[] validRow, Attribute attribute){

        ArrayList<int[]> valueIndices = new ArrayList<>(attribute.values.size());
        for(int i = 0; i < attribute.values.size(); i++){
            valueIndices.add(new int[rows]);
            Arrays.fill(valueIndices.get(i), -1);
        }
        for(int i = 0; i < rows; i++){
            if(validRow[i] > 0) {
                String value = data[i * columns + attribute.columnInDataset];
                valueIndices.get(attribute.values.indexOf(value))[i] = 1;
            }
        }
        return valueIndices;
    }

    /**
     * Getter of rows
     * @return int
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * Getter of columns
     * @return int
     */
    public int getColumns() {
        return this.columns;
    }

}
