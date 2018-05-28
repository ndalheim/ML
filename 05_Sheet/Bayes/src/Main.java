import io.TSVParser;
import models.NaiveBayes;
import objects.BagOfWords;
import utils.Calculator;
import utils.TrainTestHelper;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ken on 28.05.2018.
 */
public class Main {


    public static void main(String... args) throws IOException {

        if(args.length < 1) {
            System.out.println("No args specified.");
        }

        if("developer".equals(args[0])) {

            ArrayList<String> plainFileContent = TSVParser.readLines(args[1]);

            ArrayList<String>[] trainTest = TrainTestHelper
                    .splitTrainTest(plainFileContent, 2.0 / 3.0);

            ArrayList<BagOfWords> trainBags = TSVParser.buildBags(trainTest[0]);
            ArrayList<BagOfWords> testBags = TSVParser.buildBags(trainTest[1]);
            BagOfWords vocabulary = Utils.buildVocabulary(trainBags, testBags);

            ArrayList<String> labels = TSVParser.filterDistinctLabels(trainBags, testBags);

            ArrayList<BagOfWords> predictionBags = Utils.deepCloneCollection(testBags);


            NaiveBayes nb = new NaiveBayes();
            nb.trainModel(labels, trainBags, vocabulary);
            nb.predict(predictionBags);

            Calculator.computeStatistics(predictionBags, testBags, labels);
        }

    }


}
