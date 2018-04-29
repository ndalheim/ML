import java.util.ArrayList;

/**
 * Util class to save all information of an arff file
 * Created by naedd on 25.04.2018.
 */
public class Data {

    private Dataset dataset;
    private ArrayList<Attribute> attributes;

    /**
     * Constructor for a Data instance
     * @param dataset
     * @param attributes
     */
    public Data (Dataset dataset, ArrayList<Attribute> attributes) {
        this.dataset = dataset;
        this.attributes = attributes;
    }

    /**
     * Getter for Dataset
     * @return
     */
    public Dataset getDataset() {
        return this.dataset;
    }

    /**
     * Getter for Attributes
     * @return
     */
    public ArrayList<Attribute> getAttributes() {
        return this.attributes;
    }
}
