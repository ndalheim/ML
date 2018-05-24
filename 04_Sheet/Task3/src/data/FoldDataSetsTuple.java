package data;

/**
 * Helper class to represent dataset tuple folds which
 * contain the testset/testfolds and the trainingset/complement folds
 * Created by alexa on 20.05.2018.
 */
public class FoldDataSetsTuple {

    public FoldDataSet[] folds;
    public FoldDataSet[] compFolds;
    public int numFolds;

}
