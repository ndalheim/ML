import java.io.File;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

/**
 * Created by naedd on 22.04.2018.
 */
public class Main {

    public static void main(String... args) {

        System.out.println("File path: " + args[0]);

        try {
            ParsedData data = ArffParser.arffFileReader(new File(args[0]));
            DataSet trainingDataSet = data.getDataset();
            ArrayList<Attribute> attributes = data.getAttributes();
            System.out.println("\n Arff-File successfully loaded! \n");

            // Select attribute for class attribute
            int numClassAttribute = Integer.parseInt(args[1]);
            Attribute classAttribute = attributes.get(numClassAttribute);

            double accuracy = multipleRun(trainingDataSet,
                    attributes, classAttribute, 10);

            System.out.println("Average accuracy : " + accuracy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double multipleRun(DataSet trainingDataSet,
                              ArrayList<Attribute> attributes,
                              Attribute classAttribute,
                              int iterations) {

        double avgAccuracy = 0.0;

        for (int i = 0; i < iterations; i++) {
            System.out.println("Round: " +  i);
            System.out.println("Splitting data");
            DataSet testDataSet = trainingDataSet.extractTestDataset(0.33);
            DataSet predictionSet = new DataSet(testDataSet);

            System.out.println("Starting training");

            DecisionTreeModel model = new DecisionTreeModel();
            model.trainModel(trainingDataSet, attributes, classAttribute);
            model.printModel();

            System.out.println("Making prediction");
            model.predict(predictionSet, classAttribute);

            double accuracy = testDataSet.computeAccuracy(predictionSet, classAttribute);
            avgAccuracy += accuracy;
            System.out.println("Accuracy : " + accuracy);
            System.out.println();
        }

        return avgAccuracy / (double) iterations;
    }


}
