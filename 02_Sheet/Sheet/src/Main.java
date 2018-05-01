import java.io.File;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

/**
 * Class to build a DecisionTree on a given Arff-File
 *
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

            multipleRun(trainingDataSet,
                    attributes,
                    classAttribute,
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]));



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Compute the accuracy mean and standard deviation for the decision tree
     * with maxDepth and n iterations
     *
     * @param trainingDataSet of the arff dataset
     * @param attributes which you want to select
     * @param classAttribute which it is used for decision
     * @param maxDepth which the decision max. should have
     * @param iterations how often you want to run the model
     */
    public static void multipleRun(DataSet trainingDataSet,
                              ArrayList<Attribute> attributes,
                              Attribute classAttribute,
                              int maxDepth,
                              int iterations) {

        double[] accuracies = new double[iterations];

        for (int i = 0; i < iterations; i++) {
            DataSet iterationTrainingSet = new DataSet(trainingDataSet);

            System.out.println("Round: " +  i);
            System.out.println("Splitting data");
            DataSet testDataSet = iterationTrainingSet.extractTestDataset(0.33);
            DataSet predictionSet = new DataSet(testDataSet);

            System.out.println("Starting training");

            DecisionTreeModel model = new DecisionTreeModel();
            if(maxDepth <= 0){
                model.trainModel(iterationTrainingSet, attributes,
                        classAttribute);
            }else {
                model.trainModel(iterationTrainingSet, attributes,
                        classAttribute, maxDepth);
            }
            if(i == 0) {
                model.printModel();
            }

            System.out.println("Making prediction");
            model.predict(predictionSet, classAttribute);

            accuracies[i] = testDataSet.computeAccuracy(predictionSet, classAttribute);
            System.out.println("Accuracy : " + accuracies[i]);
            System.out.println();
        }

        double mean = Utils.computeMean(accuracies);
        double stdVar = Utils.computeStdVar(mean, accuracies);

        System.out.println("Accuracy mean : " + mean);
        System.out.println("Accuracy std. var. : " + stdVar);
    }


}
