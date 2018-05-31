package io;

import objects.BagOfWords;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by ken on 28.05.2018.
 */
public class TSVParser {


    public static ArrayList<String> readLines(String filepath) throws IOException {

        BufferedReader bf = new BufferedReader(new FileReader(filepath));

        ArrayList<String> lines = new ArrayList<>();
        String line = null;
        while((line = bf.readLine()) != null){
            lines.add(line);
        }
        return lines;

    }

    public static ArrayList<BagOfWords> buildBags(ArrayList<String> lines) throws IOException {

        ArrayList<BagOfWords> bags = new ArrayList<>();
        for(String line : lines){
            String[] pair = line.split("\t");
            String text = pair[1].substring(1, pair[1].length() - 1);
            BagOfWords bag = new BagOfWords(pair[0], text, true);
            bags.add(bag);
        }
        return bags;
    }

    public static ArrayList<String> filterDistinctLabels(ArrayList<BagOfWords>... bagsList){

        HashSet<String> labels = new HashSet<>();
        for(ArrayList<BagOfWords> bags : bagsList) {
            for (BagOfWords bag : bags) {
                if (!labels.contains(bag.getLabel())) {
                    labels.add(bag.getLabel());
                }
            }
        }
        return new ArrayList<>(labels);
    }


}
