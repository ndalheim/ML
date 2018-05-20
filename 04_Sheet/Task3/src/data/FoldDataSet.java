package data;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ken on 20.05.2018.
 */
public class FoldDataSet extends DataSet {

    protected DataSet sourceDataSet;
    protected int[] sourceRows;

    public FoldDataSet(FoldDataSet dataSet) {
        super(dataSet);
        this.sourceRows = dataSet.getSourceRows();
        this.sourceDataSet = dataSet.getSourceDataSet();
    }

    public FoldDataSet(DataSet sourceDataSet,
                       int[] sourceRows) {
        super(sourceDataSet, sourceRows);
        this.sourceDataSet = sourceDataSet;
        this.sourceRows = sourceRows;
    }


    public FoldDataSet buildComplement(){

        ArrayList<Integer> complement = new ArrayList<>();
        for(int i = 0; i < sourceDataSet.getRows(); i++){
            if(!Utils.contains(sourceRows, i)){
                complement.add(i);
            }
        }
        return new FoldDataSet(sourceDataSet, Utils.toIntArray(complement));
    }



    public DataSet getSourceDataSet() {
        return sourceDataSet;
    }

    public void setSourceDataSet(DataSet sourceDataSet) {
        this.sourceDataSet = sourceDataSet;
    }

    public int[] getSourceRows() {
        return sourceRows;
    }

    public void setSourceRows(int[] sourceRows) {
        this.sourceRows = sourceRows;
    }
}
