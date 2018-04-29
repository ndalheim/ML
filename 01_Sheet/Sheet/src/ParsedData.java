import java.util.ArrayList;

/**
 * Util class to save all information of an arff file
 * Created by naedd on 25.04.2018.
 */
public class ParsedData {

    private DataSet dataset;
    private ArrayList<Attribute> attributes;

    /**
     * Constructor for a ParsedData instance
     *
     * @param dataset
     * @param attributes
     */
    public ParsedData(DataSet dataset, ArrayList<Attribute> attributes) {
        this.dataset = dataset;
        this.attributes = attributes;
    }

    /**
     * Getter for DataSet
     *
     * @return
     */
    public DataSet getDataset() {
        return this.dataset;
    }

    /**
     * Getter for Attributes
     *
     * @return
     */
    public ArrayList<Attribute> getAttributes() {
        return this.attributes;
    }
}
