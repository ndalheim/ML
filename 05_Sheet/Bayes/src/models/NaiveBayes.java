package models;

import objects.BagOfWords;
import utils.Utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ken on 28.05.2018.
 */
public class NaiveBayes {


    private ArrayList<String> labels;
    private HashMap<String, Double> labelProbabilities;

    private ArrayList<BagOfWords> bagsOfWords;
    private HashMap<String, HashMap<String, Double>> wordProbabilities;


    public void trainModel(ArrayList<String> labels,
                           ArrayList<BagOfWords> bags,
                           BagOfWords vocabulary){

        this.labels = labels;
        this.bagsOfWords = bags;
        labelProbabilities = new HashMap<>();
        wordProbabilities = new HashMap<>();

        HashMap<String, BagOfWords> docs = new HashMap<>();
        for(BagOfWords bag : bags){
            if(docs.containsKey(bag.getLabel())){
                docs.get(bag.getLabel()).union(bag);
            }else{
                docs.put(bag.getLabel(), new BagOfWords(bag));
            }
        }
        for(Map.Entry<String, BagOfWords> entry : docs.entrySet()){
            labelProbabilities.put(entry.getKey(),
                    (entry.getValue().getUnions() + 1.0) / (vocabulary.getUnions() + 1.0));
        }
        for(Map.Entry<String, BagOfWords> doc : docs.entrySet()){
            HashMap<String, Double> wordProbs = new HashMap<>();
            for(Map.Entry<String, Integer> word : vocabulary.entrySet()){
                wordProbs.put(word.getKey(),
                        (doc.getValue().safeGet(word.getKey()) + 1.0) / (doc.getValue().countWords() + vocabulary.size()));
            }
            wordProbabilities.put(doc.getKey(), wordProbs);
        }
    }


    public void predict(ArrayList<BagOfWords> bags){

        for(BagOfWords bag : bags){
            double maxProb = Double.NEGATIVE_INFINITY;
            String maxLabel = null;
            for(String label : labels){
                HashMap<String, Double> labelWordProbs = wordProbabilities.get(label);
                // Probability tells how probable it is to classify this bag as label
                // Initialize probability with P(v_j)
                double probability = labelProbabilities.get(label);
                for(Map.Entry<String, Integer> word : bag.entrySet()){
                    Double tmp = labelWordProbs.get(word.getKey());
                    probability *= tmp;
                }
                if(maxProb < probability){
                    maxProb = probability;
                    maxLabel = label;
                }
            }
            bag.setLabel(maxLabel);
        }
    }


}
