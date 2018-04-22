import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by naedd on 22.04.2018.
 */
public class Dataset {

    private String[] data;

    private int rows;
    private int colums;

    public Dataset(String[] data, int rows, int columns) {
        this.data = data;
        this.rows = rows;
        this.colums = columns;
    }


    public double[] computeProbabilities(int[] validRows, Attribute classAttribute){

        double[] probs = new double[classAttribute.values.size()];
        for(int i = 0; i < rows; i++){
            if(validRows[i] > 0) {
                int index = classAttribute.values.indexOf(data[i * colums + classAttribute.columnInDataset]);
                probs[index] = probs[index] + 1;
            }
        }
        for(int i = 0; i < probs.length; i++){
            probs[i] = probs[i] / (double) Utils.countOnes(validRows);
        }
        return probs;
    }

    public ArrayList<int[]> filterForAttributeValues(int[] validRow, Attribute attribute){

        ArrayList<int[]> valueIndices = new ArrayList<>(attribute.values.size());
        for(int i = 0; i < attribute.values.size(); i++){
            valueIndices.add(new int[rows]);
            Arrays.fill(valueIndices.get(i), -1);
        }
        for(int i = 0; i < rows; i++){
            if(validRow[i] > 0) {
                String value = data[i * colums + attribute.columnInDataset];
                valueIndices.get(attribute.values.indexOf(value))[i] = 1;
            }
        }
        return valueIndices;
    }

}
