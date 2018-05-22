import data.*;
import io.ArffParser;
import io.ParsedData;
import models.decisiontree.DecisionTreeModel;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

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

        if(args[0].equals("folds") || args[0].equals("cv")) {

            System.out.println("Arff file: " +  args[1]);
            ParsedData data = ArffParser.arffFileReader(new File(args[1]));
            DataSet dataSet = data.getDataset();
            ArrayList<Attribute> attributes = data.getAttributes();
            System.out.println("\n Arff-File successfully loaded! \n");

            // Select attribute for class attribute
            int numClassAttribute = Integer.parseInt(args[2]);
            Attribute classAttribute = attributes.get(numClassAttribute);

            if (args[0].equals("folds")) {

                buildFolds(args[3], data.getRelName(), attributes, classAttribute,
                        dataSet, Integer.parseInt(args[4]));
                return;
            }

            if (args[0].equals("cv")) {

                stratifiedCrossValidation(dataSet, attributes, classAttribute,
                        Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                return;
            }
        }

        if(args[0].equals("weka")){

            runRfAndJ48(args[1], args[2], args[3], Integer.parseInt(args[4]),
                    Integer.parseInt(args[5]));
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

    public static void runRfAndJ48(String directory,
                            String relName,
                            String pathToCompleteData,
                            int folds,
                            int classIndex) throws Exception {

        Instances data = new Instances(new BufferedReader(new FileReader(pathToCompleteData)));
        data.setClassIndex(classIndex);
        Instances[] train = new Instances[folds];
        Instances[] test = new Instances[folds];

        for(int i = 0; i < folds; i++) {
            BufferedReader trainReader = new BufferedReader(
                    new FileReader(directory + File.separator + relName + "_train_" + i + ".arff"));
            BufferedReader testReader = new BufferedReader(
                    new FileReader(directory + File.separator + relName + "_test_" + i + ".arff"));
            train[i] = new Instances(trainReader);
            test[i] = new Instances(testReader);
            train[i].setClassIndex(classIndex);
            test[i].setClassIndex(classIndex);
            trainReader.close();
            testReader.close();
        }

        double rf_accuracy = 0;
        double j48_accuracy = 0;
        double rf_incorrect = 0;
        double j48_incorrect = 0;
        ArrayList<Prediction> rf_pred = null;
        ArrayList<Prediction> j48_pred = null;
        for (int i = 0; i < folds; i++) {
            RandomForest rf = new RandomForest();
            rf.buildClassifier(train[i]);

            Evaluation eval = new Evaluation(data);
            eval.evaluateModel(rf, test[i]);
            rf_accuracy += eval.pctCorrect();
            rf_incorrect += eval.incorrect();
            rf_pred = eval.predictions();

            J48 j = new J48();
            j.buildClassifier(train[i]);

            Evaluation eval2 = new Evaluation(data);
            eval2.evaluateModel(j, test[i]);
            j48_accuracy += eval2.pctCorrect();
            j48_incorrect += eval2.incorrect();
            j48_pred = eval2.predictions();
        }

        rf_accuracy /= folds;
        j48_accuracy /= folds;
        rf_incorrect /= folds;
        j48_incorrect /= folds;
        System.out.println("RF Average accuracy : " + rf_accuracy);
        System.out.println("J48 Average accuracy : " + j48_accuracy);
        System.out.println("RF incorrect : " + rf_incorrect);
        System.out.println("J48 incorrect : " + j48_incorrect);

        int[] ns = numbers(rf_pred, j48_pred);


        System.out.println("\n");
        System.out.println("McNemar:");
        System.out.println("--------------------");
        System.out.println(ns[0] + "\t|\t" + (double) (ns[1] + ns[2]) / 2.0);
        System.out.println("--------------------");
        System.out.println((double) (ns[1] + ns[2]) / 2.0 + "\t|\t" + ns[3]);
        System.out.println("---------------------");

    }

    private static int[] numbers(ArrayList<Prediction> fp, ArrayList<Prediction> sc){

        int[] ns = new int[4];
        for(int i = 0; i < fp.size(); i++){

            boolean fR = fp.get(i).actual() == fp.get(i).predicted() ? true : false;
            boolean sR = sc.get(i).actual() == sc.get(i).predicted() ? true : false;

            if(!fR && !sR) ns[0]++;
            if(!fR &&  sR) ns[1]++;
            if(fR &&  !sR) ns[2]++;
            if(fR &&  sR) ns[3]++;
        }
        return ns;
    }


}
