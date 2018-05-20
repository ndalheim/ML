package io;

import data.Attribute;
import data.DataSet;

import java.util.ArrayList;

/**
 * Util class to save all information of an arff file
 * Created by naedd on 25.04.2018.
 */
public class ParsedData {

    private DataSet dataset;
    private ArrayList<Attribute> attributes;
    private String relName;

    /**
     * Constructor for a io.ParsedData instance
     *
     * @param dataset
     * @param attributes
     */
    public ParsedData(DataSet dataset, ArrayList<Attribute> attributes, String relName) {
        this.dataset = dataset;
        this.attributes = attributes;
        this.relName = relName;
    }

    /**
     * Getter for data.DataSet
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



    public String getRelName() {
        return relName;
    }

}
