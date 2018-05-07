package io;

import data.Attribute;
import data.DataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Class to read an Arff-File
 *
 * Created by naedd on 29.04.2018.
 */
public class ArffParser {


    /**
     * Read Arff File and save all information into a dataset object.
     *
     * @param file of the arff-file you want to use for your DecisionTree
     * @return the parsed data from the arff-file
     * @throws Exception
     */
    public static ParsedData arffFileReader(File file) throws Exception {

        ParsedData data;
        String line = null;
        BufferedReader br = new BufferedReader(new FileReader(file));

        ArrayList<Attribute> attributes = new ArrayList<>();
        boolean attributeTest = true;
        int attributeNumber = 0;
        ArrayList<String> datasetList = new ArrayList<>();

        int rows = 0;
        int columns = 0;

        while ((line = br.readLine()) != null) {

            line = line.trim();

            if (attributeTest) {
                if (line.startsWith("@attribute")) {
                    attributes.add(parseAttribute(line, attributeNumber));
                    attributeNumber++;
                }
            } else {
                String[] instance = parseInstance(line);
                for (int i = 0; i < instance.length; i++) {
                    datasetList.add(instance[i]);
                }
                rows++;
            }
            if (line.startsWith("@data")) {
                attributeTest = false;
            }

        }
        columns = attributes.size();

        String[] datasetArray = new String[datasetList.size()];
        for (int i = 0; i < datasetList.size(); i++) {
            datasetArray[i] = datasetList.get(i);
        }

        DataSet dataset = new DataSet(datasetArray, rows, columns);
        data = new ParsedData(dataset, attributes);
        return data;

    }

    /**
     * Parse instance line into String Array and return it.
     *
     * @param line which is buffered from file
     * @return row/instance as String Array
     */
    private static String[] parseInstance(String line) {
        line = line.trim();
        String[] instance = line.split(",");
        return instance;
    }

    /**
     * Parse Arff File Header into an data.Attribute.
     *
     * @param line            which is buffered from file
     * @param attributeNumber is the position-number of the attribute in the arff file
     * @return the arff file attribute as data.Attribute type
     */
    private static Attribute parseAttribute(String line, int attributeNumber) {
        line = line.trim();
        ArrayList<String> values = new ArrayList<>();

        int space1 = line.indexOf(" ");
        int space2 = line.indexOf(" ", space1 + 1);

        String attributename = line.substring(space1 + 1, space2);
        String symbol = line.substring(space2 + 1);

        symbol = symbol.replace("{", "");
        symbol = symbol.replace("}", "");
        symbol = symbol.replace(" ", "");

        String[] tmpValues = symbol.split(",");

        for (int i = 0; i < tmpValues.length; i++) {
            values.add(tmpValues[i]);
        }

        Attribute attribute = new Attribute(attributename, values, attributeNumber);
        return attribute;

    }

}
