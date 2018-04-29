import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by naedd on 29.04.2018.
 */
public class ArffParser {


    /**
     * Read Arff File and save all information into a dataset object.
     *
     * @param file
     * @return dataset
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
     * @param line
     * @return String[]
     */
    private static String[] parseInstance(String line) {
        line = line.trim();
        String[] instance = line.split(",");
        return instance;
    }

    /**
     * Parse Arff File Header into an Attribute.
     *
     * @param line
     * @param attributeNumber
     * @return Attribute
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
