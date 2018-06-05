package models;

import objects.BagOfWords;


import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ken on 28.05.2018.
 */
public class NaiveBayes implements Serializable{


    private ArrayList<String> labels;
    private HashMap<String, Double> labelProbabilities;
    private BagOfWords vocabulary;
    private HashMap<String, HashMap<String, Double>> wordProbabilities;


    public void trainModel(ArrayList<String> labels,
                           ArrayList<BagOfWords> bags,
                           BagOfWords vocabulary){

        this.labels = labels;
        this.vocabulary = vocabulary;
        labelProbabilities = new HashMap<>();
        wordProbabilities = new HashMap<>();

        HashMap<String, BagOfWords> docs = new HashMap<>();
        addToDocs(bags, docs);
        computeLabelProbabilities(vocabulary, docs);

        for(Map.Entry<String, BagOfWords> doc : docs.entrySet()){
            computeWordProbsForDoc(vocabulary, doc);
        }
    }

    private void computeWordProbsForDoc(BagOfWords vocabulary, Map.Entry<String, BagOfWords> doc) {
        HashMap<String, Double> wordProbs = new HashMap<>();
        for(Map.Entry<String, Integer> word : vocabulary.entrySet()){
            wordProbs.put(word.getKey(),
                    (doc.getValue().safeGet(word.getKey()) + 1.0) / (doc.getValue().countWords() + vocabulary.size()));
        }
        wordProbabilities.put(doc.getKey(), wordProbs);
    }

    private void computeLabelProbabilities(BagOfWords vocabulary, HashMap<String, BagOfWords> docs) {
        for(Map.Entry<String, BagOfWords> entry : docs.entrySet()){
            labelProbabilities.put(entry.getKey(),
                    (entry.getValue().getUnions() + 1.0) / (vocabulary.getUnions() + 1.0));
        }
    }

    private void addToDocs(ArrayList<BagOfWords> bags, HashMap<String, BagOfWords> docs) {
        for(BagOfWords bag : bags){
            if(docs.containsKey(bag.getLabel())){
                docs.get(bag.getLabel()).union(bag);
            }else{
                docs.put(bag.getLabel(), new BagOfWords(bag));
            }
        }
    }


    public void predict(ArrayList<BagOfWords> bags){

        for(BagOfWords bag : bags){
            BigDecimal maxProb = new BigDecimal(0);
            String maxLabel = null;
            for(String label : labels){
                HashMap<String, Double> labelWordProbs = wordProbabilities.get(label);
                // Probability tells how probable it is to classify this bag as label
                // Initialize probability with P(v_j)
                BigDecimal probability = new BigDecimal(labelProbabilities.get(label));
                for(Map.Entry<String, Integer> word : bag.entrySet()){
                    BigDecimal tmp = new BigDecimal(labelWordProbs.get(word.getKey()));
                    probability = probability.multiply(tmp);
                }
                if(maxProb.compareTo(probability) < 0){
                    maxProb = probability;
                    maxLabel = label;
                }
            }
            bag.setLabel(maxLabel);
        }
    }

    public void saveModel(String fileName) throws IOException {

        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(new File("." + File.separator + fileName)));
        oos.writeObject(this);
        oos.close();
    }

    public static NaiveBayes readModel(String fileName) throws IOException, ClassNotFoundException {
        return readModel(fileName, false);
    }

    public static NaiveBayes readModelFromJar(String fileName) throws IOException, ClassNotFoundException {
        return readModel(fileName, true);
    }

    private static NaiveBayes readModel(String fileName,
                                       boolean fromJar)
            throws IOException, ClassNotFoundException {

        ObjectInputStream ois = null;
        if(fromJar){
            InputStream is = NaiveBayes.class.getClassLoader().getResourceAsStream(fileName);
            ois = new ObjectInputStream(is);
        } else {
            ois = new ObjectInputStream(
                    new FileInputStream(new File("." + File.separator + fileName)));
        }
        NaiveBayes nb = (NaiveBayes) ois.readObject();
        ois.close();
        return nb;
    }


    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public HashMap<String, Double> getLabelProbabilities() {
        return labelProbabilities;
    }

    public void setLabelProbabilities(HashMap<String, Double> labelProbabilities) {
        this.labelProbabilities = labelProbabilities;
    }

    public BagOfWords getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(BagOfWords vocabulary) {
        this.vocabulary = vocabulary;
    }

    public HashMap<String, HashMap<String, Double>> getWordProbabilities() {
        return wordProbabilities;
    }

    public void setWordProbabilities(HashMap<String, HashMap<String, Double>> wordProbabilities) {
        this.wordProbabilities = wordProbabilities;
    }
}
