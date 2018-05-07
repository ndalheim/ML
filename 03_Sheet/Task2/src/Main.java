import data.Attribute;
import data.DataSet;
import data.Utils;
import data.WeightedDataSet;
import io.ArffParser;
import io.ParsedData;
import models.adaboost.AdaBoostDT;

import java.io.File;
import java.util.ArrayList;

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

            multipleRun(trainingDataSet.transformToWeightedDataSet(),
                    attributes,
                    classAttribute,
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]),
                    Integer.parseInt(args[4]));



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
     * @param boostIterations Maximal number of used models inside of boost
     * @param iterations how often you want to run the model
     */
    public static void multipleRun(WeightedDataSet trainingDataSet,
                                    ArrayList<Attribute> attributes,
                                    Attribute classAttribute,
                                    int boostIterations,
                                    int iterations,
                                    int maxDepth) {

        double[] accuracies = new double[iterations];

        for (int i = 0; i < iterations; i++) {
            WeightedDataSet iterationTrainingSet = new WeightedDataSet(trainingDataSet);

            System.out.println("Round: " +  i);
            System.out.println("Splitting data");
            WeightedDataSet testDataSet = iterationTrainingSet
                    .extractTestDataSet(0.33).transformToWeightedDataSet();
            WeightedDataSet predictionSet = new WeightedDataSet(testDataSet);

            System.out.println("Starting training");

            AdaBoostDT model = new AdaBoostDT(iterationTrainingSet,
                    attributes, classAttribute);
            model.modelGeneration(boostIterations, maxDepth);

            System.out.println("Making prediction");
            model.predict(predictionSet);

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
