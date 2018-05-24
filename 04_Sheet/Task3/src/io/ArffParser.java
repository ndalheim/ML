package io;

import data.Attribute;
import data.DataSet;
import data.Utils;

import java.io.*;
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
        String relName = "";

        int rows = 0;
        int columns = 0;

        while ((line = br.readLine()) != null) {

            line = line.trim();

            if (attributeTest) {
                if(line.startsWith("@relation")){
                    relName = line.split(" ")[1];
                }

                if (line.startsWith("@attribute")) {
                    attributes.add(parseAttribute(line, attributeNumber));
                    attributeNumber++;
                }
            } else {
                String[] instance = parseInstance(line);
                if(instance.length != attributes.size()) {
                    System.out.println("Skip illegal line: " + Utils.concatStringArray(instance));
                    continue;
                }
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
        data = new ParsedData(dataset, attributes, relName);
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


    /**
     * Save a given dataset with its ArrayList of attributes as arff file
     * @param path of directory where you want to save the arff file
     * @param filename of the arff file
     * @param relName is the name of the relation in the arff file
     * @param attributes is the list of all arff attributes
     * @param dataSet is the list of all instances/rows
     * @throws IOException
     */
    public static void saveArffFile(String path,
                               String filename,
                               String relName,
                               ArrayList<Attribute> attributes,
                               DataSet dataSet) throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(path + File.separator + filename + ".arff"));
        String header = createArffHeader(relName, attributes);
        String data = createArffData(dataSet);
        bw.write(header);
        bw.write("\n");
        bw.write(data);
        bw.close();
    }


    /**
     * Helper method to parse the header of an arff file
     * @param relName is the name of the relation in the arff file
     * @param attributes is the list of all arff attributes
     * @return a parsed string which contains all arff header information
     */
    public static String createArffHeader(String relName, ArrayList<Attribute> attributes){

        StringBuilder sb = new StringBuilder();
        sb.append("@relation ");
        sb.append(relName);
        sb.append("\n\n");

        for(int i = 0; i < attributes.size(); i++){
            sb.append("@attribute ");
            sb.append(attributes.get(i).getName());
            sb.append(" ");
            sb.append(getAttributeContent(attributes.get(i)));
        }
        return sb.toString();
    }

    /**
     * Helper method to get the attribute values of a single attribute
     * @param attribute which values you want to read out and parse
     * @return a string with all values of the attribute
     */
    public static String getAttributeContent(Attribute attribute){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int j = 0; j < attribute.getValues().size() - 1; j++){
            sb.append(attribute.getValues().get(j));
            sb.append(", ");
        }
        sb.append(attribute.getValues().get(attribute.getValues().size() - 1));
        sb.append("}\n");
        String result = sb.toString();
        if("{real}\n".equals( result)){
            return "real\n";
        }
        return result;
    }

    /**
     * Helper method to parse the data of a arff file to string
     * @param dataSet which you want to parse
     * @return a string with all data of the arff file
     */
    public static String createArffData(DataSet dataSet){
        StringBuilder sb = new StringBuilder();
        sb.append("@data\n");
        for(int i = 0; i < dataSet.getRows(); i++){
            for(int j = 0; j < dataSet.getColumns() - 1; j++){
                sb.append(dataSet.getEntryInDataSet(i, j));
                sb.append(",");
            }
            sb.append(dataSet.getEntryInDataSet(i, dataSet.getColumns() - 1));
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }


}
