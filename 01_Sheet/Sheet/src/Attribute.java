import java.util.ArrayList;

/**
 * Class to save a attribute of an arff file
 * Created by naedd on 22.04.2018.
 */
public class Attribute {

    String name;
    ArrayList<String> values;
    int columnInDataset;

    /**
     * Constructor
     * @param name of the attribute
     * @param values of the attribute (nominal)
     * @param attributeNumber of the arff file
     */
    public Attribute(String name, ArrayList<String> values, int attributeNumber) {
        this.name = name;
        this.values = values;
        this.columnInDataset = attributeNumber;
    }
}
