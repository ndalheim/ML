import com.sun.org.apache.xpath.internal.SourceTree;
import io.TSVParser;
import models.NaiveBayes;
import objects.BagOfWords;
import utils.Calculator;
import utils.TrainTestHelper;
import utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ken on 28.05.2018.
 */
public class Main {


    public static final String MODEL_NAME = "naiveBayesModel";

    public static void main(String... args) throws IOException, ClassNotFoundException {

        if(args.length < 1) {
            System.out.println("No args specified.");
        }

        long timeVar = 0;


        if("developer".equals(args[0])) {

            System.out.print("Start reading...");
            timeVar = System.currentTimeMillis();
            ArrayList<String> plainFileContent = TSVParser.readLines(args[1]);
            timeVar = System.currentTimeMillis() - timeVar;
            System.out.println("\t " + (double)timeVar / 1000 + "s");

            System.out.print("Splitting train and test...");
            timeVar = System.currentTimeMillis();
            ArrayList<String>[] trainTest = TrainTestHelper
                    .splitTrainTest(plainFileContent, 2.0 / 3.0);
            timeVar = System.currentTimeMillis() - timeVar;
            System.out.println("\t " + (double)timeVar / 1000 + "s");

            System.out.print("Building bags...");
            timeVar = System.currentTimeMillis();
            ArrayList<BagOfWords> trainBags = TSVParser.buildBags(trainTest[0]);
            ArrayList<BagOfWords> testBags = TSVParser.buildBags(trainTest[1]);
            timeVar = System.currentTimeMillis() - timeVar;
            System.out.println("\t " + (double)timeVar / 1000 + "s");

            System.out.print("Building vocabulary...");
            timeVar = System.currentTimeMillis();
            BagOfWords vocabulary = Utils.buildVocabulary(trainBags, testBags);
            timeVar = System.currentTimeMillis() - timeVar;
            System.out.println("\t " + (double)timeVar / 1000 + "s");

            System.out.print("Building most frequent words... " );
            timeVar = System.currentTimeMillis();
            BagOfWords frequentWords = new BagOfWords(vocabulary);
            frequentWords.keepFrequentWords(0.011);
            timeVar = System.currentTimeMillis() - timeVar;
            System.out.println("\t " + (double)timeVar / 1000 + "s");

            System.out.print("Removing most frequent words form bags...");
            timeVar = System.currentTimeMillis();
            TrainTestHelper.removeBagOfWords(frequentWords, trainBags, testBags);
            vocabulary.remove(frequentWords);
            timeVar = System.currentTimeMillis() - timeVar;
            System.out.println("\t " + (double)timeVar / 1000 + "s");


            System.out.print("Naive bayes preparations...");
            timeVar = System.currentTimeMillis();
            ArrayList<String> labels = TSVParser.filterDistinctLabels(trainBags, testBags);
            ArrayList<BagOfWords> predictionBags = Utils.deepCloneCollection(testBags);
            timeVar = System.currentTimeMillis() - timeVar;
            System.out.println("\t " + (double)timeVar / 1000 + "s");

            System.out.print("Begin training...");
            timeVar = System.currentTimeMillis();
            NaiveBayes nb = new NaiveBayes();
            nb.trainModel(labels, trainBags, vocabulary);
            timeVar = System.currentTimeMillis() - timeVar;
            System.out.println("\t " + (double)timeVar / 1000 + "s");

            System.out.print("Begin predicting...");
            timeVar = System.currentTimeMillis();
            nb.predict(predictionBags);
            timeVar = System.currentTimeMillis() - timeVar;
            System.out.println("\t " + (double)timeVar / 1000 + "s");

            System.out.println("Saving model...");
            nb.saveModel(MODEL_NAME);

            System.out.println("Compute statistics....");
            Calculator.computeStatistics(predictionBags, testBags, labels);

        } else {

            System.out.println("Loading model...");
            NaiveBayes nb = NaiveBayes.readModelFromJar(MODEL_NAME);

            System.out.println("Start reading...");
            ArrayList<String> plainFileContent = TSVParser.readLines(args[0]);

            System.out.println("Building bags...");
            ArrayList<BagOfWords> bags = TSVParser.buildBags(plainFileContent);

            System.out.println("Predicting...");
            TrainTestHelper.fitToVocabulary(nb.getVocabulary(), bags);
            nb.predict(bags);

            System.out.println("Saving predictions...");
            Utils.savePredictions(args[1], bags);
        }



    }


}
