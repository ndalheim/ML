package utils;

import objects.BagOfWords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ken on 28.05.2018.
 */
public class Calculator {


    public static void computeStatistics(ArrayList<BagOfWords> predictionSet,
                                         ArrayList<BagOfWords> testSet,
                                         ArrayList<String> labels){

        HashMap<String, Tuple<Integer>> labelClassified = new HashMap<>();
        for(String label : labels){
            labelClassified.put(label, new Tuple<Integer>(0,0, 0));
        }

        int size = Math.min(predictionSet.size(), testSet.size());
        double accuracy = 0.0;
        for(int i = 0; i < size; i++){
            String predictionLabel = predictionSet.get(i).getLabel();
            String correctLabel = testSet.get(i).getLabel();
            Tuple<Integer> statisticTuple = labelClassified.get(predictionLabel);
            Tuple<Integer> statisticTuple2 = labelClassified.get(correctLabel);
            if(predictionLabel.equals(correctLabel)){
                statisticTuple.y++;
                accuracy++;
            }
            statisticTuple.x++;
            statisticTuple2.z++;
        }
        accuracy /= size;

        System.out.println("Accuracy : " + accuracy);
        System.out.println();
        System.out.println("\tGT / TP / CP / IP");
        for(Map.Entry<String, Tuple<Integer>> label : labelClassified.entrySet()){
            System.out.println(label.getKey() + " : "
                    + label.getValue().z + " / "
                    + label.getValue().x + " / "
                    + label.getValue().y + " / "
                    + (label.getValue().x - label.getValue().y));
        }

    }



}
