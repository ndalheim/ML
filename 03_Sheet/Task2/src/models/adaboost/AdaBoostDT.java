package models.adaboost;

import data.Attribute;
import data.Utils;
import data.WeightedDataSet;
import models.decisiontree.DecisionTreeModel;

import java.util.ArrayList;

/**
 * Class of own version of AdaBoost with our DecisionTree implementation
 * Created by ken on 06.05.2018.
 */
public class AdaBoostDT {


    private DecisionTreeModel[] models;
    private double[] modelErrors;
    private int numModels;

    private WeightedDataSet weightedDataSet;
    private ArrayList<Attribute> attributes;
    private Attribute classAttribute;

    /**
     * Constructor of an AdaBoostDT
     * @param trainingSet
     * @param attributes
     * @param classAttribute
     */
    public AdaBoostDT(WeightedDataSet trainingSet,
                      ArrayList<Attribute> attributes,
                      Attribute classAttribute) {

        this.weightedDataSet = trainingSet;
        this.attributes = attributes;
        this.classAttribute = classAttribute;
    }

    /**
     * Generate the AdaBoostDT Model
     * @param maxIterations the number of the maximum iterations
     * @param maxDepth the number of the maximum tree depth
     */
    public void modelGeneration(int maxIterations, int maxDepth){

        models = new DecisionTreeModel[maxIterations];
        modelErrors = new double[maxIterations];

        weightedDataSet.initializeWeightsEqually();

        for(int i = 0; i < maxIterations; i++){

            WeightedDataSet sample = weightedDataSet.sample();
            WeightedDataSet prediction = new WeightedDataSet(weightedDataSet);

            models[i] = new DecisionTreeModel();
            models[i].trainModel(sample, attributes, classAttribute, maxDepth);
            models[i].predict(prediction, classAttribute);
            numModels++;

            modelErrors[i] = sample.computeModelError(prediction,
                    classAttribute);

            if(modelErrors[i] == 0 || modelErrors[i] >= 0.5){
                if(numModels > 1) {
                    models[i] = null;
                    modelErrors[i] = 0.0;
                    numModels--;
                }
                break;
            }

            weightedDataSet.updateWeights(prediction, classAttribute,
                    modelErrors[i]);
        }
    }

    /**
     * Make a prediction of the given WeightedDataSet
     * @param predictionDataSet
     */
    public void predict(WeightedDataSet predictionDataSet){

        predictionDataSet.initializeWeightsWithZero();

        PredictionHelper[] predictionHelpers = new PredictionHelper[predictionDataSet.getRows()];
        for(int i = 0; i < predictionDataSet.getRows(); i++){
            predictionHelpers[i] = new PredictionHelper(classAttribute);
        }

        for(int i = 0; i < numModels; i++){

            models[i].predict(predictionDataSet, classAttribute);

            for(int j = 0;  j < predictionDataSet.getRows(); j++){
                predictionHelpers[j].updateWeight(
                        predictionDataSet.getEntryInDataSet(classAttribute, j),
                        modelErrors[i]);
            }
        }
        for(int i = 0; i < predictionHelpers.length; i++){
            predictionHelpers[i].predict(predictionDataSet, i);
        }
    }


    public DecisionTreeModel[] getModels() {
        return models;
    }

    public void setModels(DecisionTreeModel[] models) {
        this.models = models;
    }

    public WeightedDataSet getWeightedDataSet() {
        return weightedDataSet;
    }

    public void setWeightedDataSet(WeightedDataSet weightedDataSet) {
        this.weightedDataSet = weightedDataSet;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Attribute getClassAttribute() {
        return classAttribute;
    }

    public void setClassAttribute(Attribute classAttribute) {
        this.classAttribute = classAttribute;
    }

    public double[] getModelErrors() {
        return modelErrors;
    }

    public void setModelErrors(double[] modelErrors) {
        this.modelErrors = modelErrors;
    }

    private int getNumModels() {
        return numModels;
    }

    private void setNumModels(int numModels) {
        this.numModels = numModels;
    }


    /**
     * Helper class for Model AdaBoostDT precictions
     */
    private class PredictionHelper {

        private Attribute classAttribute;
        private double[] weights;

        /**
         * Constructor
         * @param classAttribute of the arff file
         */
        public PredictionHelper(Attribute classAttribute) {
            this.classAttribute = classAttribute;
            this.weights = new double[classAttribute.getValues().size()];
        }

        /**
         * Update the new generadet instance weights
         * @param value of the instance
         * @param modelError which is new computed
         */
        public void updateWeight(String value, double modelError){

            int valueIndex = classAttribute.getValues().indexOf(value);
            weights[valueIndex] += - Math.log( modelError / (1 - modelError));
        }

        /**
         * Set the new predicted entry into the predictionSet
         * @param predictionSet which you want to use for prediction
         * @param row specific instance number
         */
        public void predict(WeightedDataSet predictionSet, int row){
            int maxIndex = Utils.argMax(weights);
            predictionSet.setEntryInDataSet(classAttribute,
                    row, classAttribute.getValues().get(maxIndex),
                    weights[maxIndex]);
        }
    }

}
