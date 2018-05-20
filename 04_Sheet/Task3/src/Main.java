import data.*;
import io.ArffParser;
import io.ParsedData;
import models.adaboost.AdaBoostDT;
import models.decisiontree.DecisionTreeModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to build the AdaBoost algorithm with the DecisionTree Classifier
 *
 * Created by naedd on 22.04.2018.
 */
public class Main {

    /**
     * Make multiple runs of the DecisionTree Classifier as implemented in the AdaBoost algorithm
     * @param args
     */
    public static void main(String... args) throws Exception {

        System.out.println("Arff file: " +  args[1]);
        ParsedData data = ArffParser.arffFileReader(new File(args[1]));
        DataSet dataSet = data.getDataset();
        ArrayList<Attribute> attributes = data.getAttributes();
        System.out.println("\n Arff-File successfully loaded! \n");

        // Select attribute for class attribute
        int numClassAttribute = Integer.parseInt(args[2]);
        Attribute classAttribute = attributes.get(numClassAttribute);

        if(args[0].equals("folds")){

            buildFolds(args[3], data.getRelName(), attributes, classAttribute,
                    dataSet, Integer.parseInt(args[4]));
            return;
        }

        if(args[0].equals("cv")){

            stratifiedCrossValidation(dataSet, attributes, classAttribute,
                    Integer.parseInt(args[3]), Integer.parseInt(args[4]));
            return;
        }


        System.out.println("No mode selected.");
    }


    public static void buildFolds(String path,
                           String relName,
                           ArrayList<Attribute> attributes,
                           Attribute classAttribute,
                           DataSet dataSet,
                           int folds) throws Exception {

        FoldDataSetsTuple tuple = dataSet.buildFolds(classAttribute, folds);
        for(int i = 0; i < tuple.folds.length; i++){
            ArffParser.saveArffFile(path, relName + "_test_" + i,
                    relName, attributes, tuple.folds[i]);
        }
        for(int i = 0; i < tuple.compFolds.length; i++){
            ArffParser.saveArffFile(path, relName + "_train_" + i,
                    relName, attributes, tuple.compFolds[i]);
        }
    }


    /**
     * Compute the mean and standard deviation for the decision tree
     * with maxDepth using cross validation.
     *
     * @param dataSet Data to use.
     * @param attributes which you want to select
     * @param classAttribute which it is used for decision
     * @param maxDepth of the DecisionTree depth
     * @param folds Number of folds.
     */
    public static void stratifiedCrossValidation(DataSet dataSet,
                                                 ArrayList<Attribute> attributes,
                                                 Attribute classAttribute,
                                                 int maxDepth,
                                                 int folds) throws Exception {

        if(folds < 2){
            System.out.println("Num folds must be at least 2.");
            return;
        }

        double[] accuracies = new double[folds];

        FoldDataSetsTuple tuple = dataSet.buildFolds(classAttribute, folds);

        for (int i = 0; i < folds; i++) {

            System.out.println("Fold: " +  i);
            System.out.println("Starting training");

            DecisionTreeModel model = new DecisionTreeModel();
            model.trainModel(tuple.compFolds[i], attributes, classAttribute, maxDepth);

            System.out.println("Making prediction");
            DataSet predictionSet = new DataSet(tuple.folds[i]);
            model.predict(predictionSet, classAttribute);

            accuracies[i] = tuple.folds[i].computeAccuracy(predictionSet, classAttribute);
            System.out.println("Accuracy : " + accuracies[i]);
            System.out.println();
        }

        double mean = Utils.computeMean(accuracies);
        double stdVar = Utils.computeStdVar(mean, accuracies);

        System.out.println("Accuracy mean : " + mean);
        System.out.println("Accuracy std. var. : " + stdVar);
    }


}
