import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by naedd on 22.04.2018.
 */
public class Main {

    public static void main (String... args){

        String[] data = {
                "D1", "Sunny", "Hot", "High", "Weak", "No",
                "D2", "Sunny", "Hot", "High", "Strong", "No",
                "D3", "Overcast", "Hot", "High", "Weak", "Yes",
                "D4", "Rain", "Mild", "High", "Weak", "Yes",
                "D5", "Rain", "Cool", "Normal", "Weak", "Yes",
                "D6", "Rain", "Cool", "Normal", "Strong", "No",
                "D7", "Overcast", "Cool", "Normal", "Strong", "Yes",
                "D8", "Sunny", "Mild", "High", "Weak", "No",
                "D9", "Sunny", "Cool", "Normal", "Weak", "Yes",
                "D10", "Rain", "Mild", "Normal", "Weak", "Yes",
                "D11", "Sunny", "Mild", "Normal", "Strong", "Yes",
                "D12", "Overcast", "Mild", "High", "Strong", "Yes",
                "D13", "Overcast", "Hot", "Normal", "Weak", "Yes",
                "D14", "Rain", "Mild", "High", "Strong", "No"
        };
        Dataset dataset = new Dataset(data, 14, 6);

        //Day Outlook Temp. Hum. Wind PlayT.
        ArrayList<String> values = new ArrayList<>();
        values.add("High");
        values.add("Normal");
        Attribute attribute = new Attribute("Huminity", values, 3);

        ArrayList<String> classAttValues = new ArrayList<>();
        classAttValues.add("Yes");
        classAttValues.add("No");
        Attribute classAttribute = new Attribute("PlayT", classAttValues, 5);

        int[] validRows = new int[14];
        Arrays.fill(validRows, 1);

        System.out.println(informationGain(dataset, validRows, classAttribute, attribute));
    }

    public static double entropyOnSubset(Dataset dataset, int[] validRows, Attribute classAttribute){

        double[] probs = dataset.computeProbabilities(validRows, classAttribute);

        double entropy = 0.0;
        double logConstant = Math.log10(2);
        for(int i = 0; i < probs.length; i++) {
            double element = probs[i];
            entropy -= element * (Math.log10(element) / logConstant);
        }
        return entropy;
    }

    public static double informationGain(Dataset dataset,
                                  int[] validRows,
                                  Attribute classAttribute,
                                  Attribute attribute){

        double gain = entropyOnSubset(dataset, validRows, classAttribute);
        int parentNum = Utils.countOnes(validRows);
        ArrayList<int[]> valuesValidRows = dataset.filterForAttributeValues(validRows, attribute);
        for(int i = 0; i < valuesValidRows.size(); i++){
            double entropy = entropyOnSubset(dataset, valuesValidRows.get(i), classAttribute);
            int childNum = Utils.countOnes(valuesValidRows.get(i));
            gain -= (double) childNum / (double) parentNum * entropy;
        }

        return gain;
    }





}
