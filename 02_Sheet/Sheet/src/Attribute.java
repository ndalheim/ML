import java.util.ArrayList;

/**
 * Class to save a attribute of an arff file
 * Created by naedd on 22.04.2018.
 */
public class Attribute {

    private String name;
    private ArrayList<String> values;
    private int columnInDataset;

    /**
     * Constructor
     *
     * @param name            of the attribute
     * @param values          of the attribute (nominal)
     * @param attributeNumber of the arff file
     */
    public Attribute(String name, ArrayList<String> values, int attributeNumber) {
        this.name = name;
        this.values = values;
        this.columnInDataset = attributeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public int getColumnInDataset() {
        return columnInDataset;
    }

    public void setColumnInDataset(int columnInDataset) {
        this.columnInDataset = columnInDataset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attribute attribute = (Attribute) o;

        return name != null ? name.equals(attribute.name) : attribute.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
