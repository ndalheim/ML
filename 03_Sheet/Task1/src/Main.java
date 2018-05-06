import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

/**
 * Created by ken on 06.05.2018.
 */
public class Main {


    public static void main(String... args) throws Exception {


        BufferedReader reader = new BufferedReader(
                new FileReader(args[0]));

        Instances data = new Instances(reader);
        data.setClassIndex(Integer.valueOf(args[1]));

        reader.close();

        Instances[] trainTest = splitTrainTest(data, 0.66);


        double accuracy = 0;
        int upperBound = Integer.valueOf(args[3]);
        for (int i = 0; i < upperBound; i++) {
            RandomForest rf = new RandomForest();
            rf.buildClassifier(trainTest[0]);
            rf.setNumIterations(Integer.valueOf(args[2]));

            Evaluation eval = new Evaluation(data);
            eval.evaluateModel(rf, trainTest[1]);
            accuracy += eval.pctCorrect();
        }
        accuracy /= upperBound;
        System.out.println("Bagging iteration : " + args[2]);
        System.out.println("Num RF iterations : " + args[3]);
        System.out.println("Average accuracy : " + accuracy);
    }


    private static Instances[] splitTrainTest(Instances data,
                                              double splitPercentage) {

        Random random = new Random();
        Instances train = new Instances(data);
        train.randomize(random);
        Instances test = new Instances(train);

        int border = (int) Math.round(splitPercentage * train.size());
        for (int i = data.size() - 1; i >= 0; i--) {
            if (i < border) {
                test.remove(i);
            } else {
                train.remove(i);
            }
        }
        return new Instances[]{train, test};
    }

}
