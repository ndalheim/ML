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

            System.out.println("Splitting data");
            DataSet testDataSet = trainingDataSet.extractTestDataset(0.33);
            DataSet predictionSet = new DataSet(testDataSet);

            System.out.println("Starting training");

            DecisionTreeModel model = new DecisionTreeModel();
            model.trainModel(trainingDataSet, attributes, classAttribute);
            model.printModel();

            System.out.println("Making prediction");
            model.predict(predictionSet, classAttribute);

            System.out.println("Accuracy : ");
            System.out.println(testDataSet.computeAccuracy(predictionSet, classAttribute));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
