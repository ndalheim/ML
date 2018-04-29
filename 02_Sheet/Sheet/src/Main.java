import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by naedd on 22.04.2018.
 */
public class Main {

    public static void main (String... args){

        System.out.println("WELCOME TO ML \n");
        System.out.println("Please enter the filepath of your arff-file: ...");
        Scanner scan = new Scanner(System.in);
        String filename = scan.nextLine();

        try {
            // Read Arff File
            Data data = Utils.arffFileReader(new File(filename));
            Dataset dataset = data.getDataset();
            ArrayList<Attribute> attributes = data.getAttributes();
            System.out.println("\n Arff-File successfully loaded! \n");

            // Select attribute for class attribute
            System.out.println("Please enter the number of the classAttribute you want to select:");
            System.out.println("Example:\n @attribute outlook TYPE = Number 0 \n @attribute temperature TYPE = Number 1 ... \n");
            String input  = scan.nextLine();
            int numClassAttribute = Integer.parseInt(input);
            Attribute classAttribute = attributes.get(numClassAttribute);

            // Select attribute to compute information gain
            System.out.println("Please enter the number of the Attribute you want to compute the information gain:");
            System.out.println("Example:\n @attribute outlook TYPE = Number 0 \n @attribute temperature TYPE = Number 1 ... \n");
            input  = scan.nextLine();
            int numAttribute = Integer.parseInt(input);
            Attribute attribute = attributes.get(numAttribute);

            int[] validRows = new int[dataset.getRows()];
            Arrays.fill(validRows, 1);
            System.out.println("Information Gain: " + informationGain(dataset, validRows, classAttribute, attribute));

        } catch(Exception e) {
            e.printStackTrace();
        }

        // C:/Users/naedd/Documents/Uni/SoSe18/ML/01_Sheet/weather.nominal.arff

// TEST DATA
//        String[] data = {
//                "D1", "Sunny", "Hot", "High", "Weak", "No",
//                "D2", "Sunny", "Hot", "High", "Strong", "No",
//                "D3", "Overcast", "Hot", "High", "Weak", "Yes",
//                "D4", "Rain", "Mild", "High", "Weak", "Yes",
//                "D5", "Rain", "Cool", "Normal", "Weak", "Yes",
//                "D6", "Rain", "Cool", "Normal", "Strong", "No",
//                "D7", "Overcast", "Cool", "Normal", "Strong", "Yes",
//                "D8", "Sunny", "Mild", "High", "Weak", "No",
//                "D9", "Sunny", "Cool", "Normal", "Weak", "Yes",
//                "D10", "Rain", "Mild", "Normal", "Weak", "Yes",
//                "D11", "Sunny", "Mild", "Normal", "Strong", "Yes",
//                "D12", "Overcast", "Mild", "High", "Strong", "Yes",
//                "D13", "Overcast", "Hot", "Normal", "Weak", "Yes",
//                "D14", "Rain", "Mild", "High", "Strong", "No"
//        };
//        Dataset dataset = new Dataset(data, 14, 6);
//
//        //Day Outlook Temp. Hum. Wind PlayT.
//        ArrayList<String> values = new ArrayList<>();
//        values.add("High");
//        values.add("Normal");
//        Attribute attribute = new Attribute("Huminity", values, 3);
//
//        ArrayList<String> classAttValues = new ArrayList<>();
//        classAttValues.add("Yes");
//        classAttValues.add("No");
//        Attribute classAttribute = new Attribute("PlayT", classAttValues, 5);

    }

    public static Node trainModel(Dataset dataset,
                                  ArrayList<Attribute> attributes,
                                  Attribute classAttribute){

        int[] validRows = new int[dataset.getRows()];
        Arrays.fill(validRows, 1);

        ArrayList<Attribute> remainingAttributes = new ArrayList<>(attributes);
        remainingAttributes.remove(classAttribute);

        Node root = new Node(null, null);
        trainModelOnSubset(dataset, remainingAttributes, validRows, root, classAttribute);
        return root;
    }


    private static void trainModelOnSubset(Dataset dataset,
                                           ArrayList<Attribute> remainingAttributes,
                                           int[] validRows,
                                           Node newNode,
                                           Attribute classAttribute){

        double maxGain = -1;
        Attribute maxAtt = null;
        for(Attribute attribute : remainingAttributes){
            double gain = informationGain(dataset, validRows, classAttribute, attribute);
            if(gain > maxGain){
                maxGain = gain;
                maxAtt = attribute;
            }
        }

        newNode.setAttribute(maxAtt);
        newNode.setGain(maxGain);

        ArrayList<int[]> validRowsForChilds = dataset.filterForAttributeValues(validRows, maxAtt);
        for(int i = 0; i < maxAtt.getValues().size(); i++) {

            String value = maxAtt.getValues().get(i);

            Node child = new Node(newNode, value);
            newNode.getChilds().add(child);

            ArrayList<Attribute> childRemainingAtts = new ArrayList<>(remainingAttributes);
            childRemainingAtts.remove(maxAtt);
            trainModelOnSubset(dataset, childRemainingAtts, validRowsForChilds.get(i), child, classAttribute);
        }
    }

    /**
     * Compute entropy on subset
     * @param dataset
     * @param validRows
     * @param classAttribute
     * @return double
     */
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

    /**
     * Compute information gain
     * @param dataset
     * @param validRows
     * @param classAttribute
     * @param attribute
     * @return double
     */
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
