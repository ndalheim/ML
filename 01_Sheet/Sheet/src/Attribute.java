import java.util.ArrayList;

/**
 * Created by naedd on 22.04.2018.
 */
public class Attribute {

    String name;

    ArrayList<String> values;

    int columnInDataset;

    public Attribute(String name, ArrayList<String> values, int columnInDataset) {
        this.name = name;
        this.values = values;
        this.columnInDataset = columnInDataset;
    }
}
