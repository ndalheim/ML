package data;

import java.util.ArrayList;

/**
 * Class to save a attribute of an arff file
 *
 * Created by naedd on 22.04.2018.
 */
public class Attribute {

    private String name;
    private ArrayList<String> values;
    private int columnInDataset;

    /**
     * Constructor of an attribute object
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

    /**
     * Getter of the attribute name
     *
     * @return the name of the attribute
     */
    public String getName() {
        return name;
    }

    /**
     * Set the attribute name
     * @param name of the attribute
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of the attribute values
     *
     * @return the values of the attribute as ArrayList<String>
     */
    public ArrayList<String> getValues() {
        return values;
    }

    /**
     * Setter of the attribute values
     *
     * @param values of the attribute
     */
    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    /**
     * Getter of the attribute column in the arff file dataset
     *
     * @return the column number of the attribute in the arff file dataset
     */
    public int getColumnInDataset() {
        return columnInDataset;
    }

    /**
     * Setter of the attribute column in the arff file dataset
     * @param columnInDataset the column number of the attribute in the arff file dataset
     */
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
