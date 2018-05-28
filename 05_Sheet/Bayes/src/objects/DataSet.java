package objects;

import java.util.ArrayList;

/**
 * Created by ken on 28.05.2018.
 */
public class DataSet {

    ArrayList<BagOfWords> bags;
    ArrayList<String> labels;

    public DataSet(ArrayList<BagOfWords> bags, ArrayList<String> labels) {
        this.bags = bags;
        this.labels = labels;
    }

    public DataSet(DataSet dataSet){
        labels = new ArrayList<>(dataSet.getLabels());
        bags = new ArrayList<>(dataSet.getBags());
    }

    public ArrayList<BagOfWords> getBags() {
        return bags;
    }

    public void setBags(ArrayList<BagOfWords> bags) {
        this.bags = bags;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }
}
