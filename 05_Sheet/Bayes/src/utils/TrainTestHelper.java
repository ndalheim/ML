package utils;

import objects.BagOfWords;

import java.util.ArrayList;

/**
 * Created by ken on 28.05.2018.
 */
public class TrainTestHelper {



    public static <T> ArrayList<T>[] splitTrainTest(ArrayList<T> list,
                                                    double trainPercentage){

        int trainSize = (int) ((double) list.size() * trainPercentage);
        ArrayList<T>[] trainTest = new ArrayList[2];
        trainTest[0] = new ArrayList<T>();
        trainTest[1] = new ArrayList<T>();
        int counter = 0;
        for(T element : list){
            if(counter < trainSize){
                trainTest[0].add(element);
            } else {
                trainTest[1].add(element);
            }
            counter++;
        }
        return trainTest;
    }

    public static void removeBagOfWords(BagOfWords toRemove, ArrayList<BagOfWords>... bagsList){

        for(ArrayList<BagOfWords> bags : bagsList){
            for(BagOfWords bag : bags){
                bag.remove(toRemove);
            }
        }
    }


}
