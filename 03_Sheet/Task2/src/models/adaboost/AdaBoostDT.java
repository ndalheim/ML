package models.adaboost;

import data.Attribute;
import data.Utils;
import data.WeightedDataSet;
import models.decisiontree.DecisionTreeModel;

import java.util.ArrayList;

/**
 * Created by ken on 06.05.2018.
 */
public class AdaBoostDT {


    private DecisionTreeModel[] models;
    private double[] modelErrors;
    private int numModels;

    private WeightedDataSet weightedDataSet;
    private ArrayList<Attribute> attributes;
    private Attribute classAttribute;

    public AdaBoostDT(WeightedDataSet trainingSet,
                      ArrayList<Attribute> attributes,
                      Attribute classAttribute) {

        this.weightedDataSet = trainingSet;
        this.attributes = attributes;
        this.classAttribute = classAttribute;
    }


    public void modelGeneration(int maxIterations){

        setModels(new DecisionTreeModel[maxIterations]);
        setModelErrors(new double[maxIterations]);

        getWeightedDataSet().initializeWeightsEqually();

        WeightedDataSet dataSetToUse = getWeightedDataSet();
        for(int i = 0; i < maxIterations; i++){

            WeightedDataSet sample = dataSetToUse.sample();
            WeightedDataSet samplePred = new WeightedDataSet(sample);

            getModels()[i] = new DecisionTreeModel();
            getModels()[i].trainModel(sample, getAttributes(), getClassAttribute(), 5);
            getModels()[i].predict(samplePred, getClassAttribute());

            getModelErrors()[i] = sample.computeModelError(samplePred,
                    getClassAttribute());

            if(getModelErrors()[i] == 0 || getModelErrors()[i] >= 0.5){
                getModels()[i] = null;
                getModelErrors()[i] = 0.0;
                break;
            }
            sample.updateWeights(samplePred, getClassAttribute(),
                    getModelErrors()[i]);
            
            dataSetToUse = sample;
            setNumModels(getNumModels() + 1);
        }
    }

    public void predict(WeightedDataSet predictionDataSet){

        PredictionHelper[] predictionHelpers = new PredictionHelper[predictionDataSet.getRows()];
        for(int i = 0; i < predictionDataSet.getRows(); i++){
            predictionHelpers[i] = new PredictionHelper(getClassAttribute());
        }

        for(int i = 0; i < getNumModels(); i++){

            getModels()[i].predict(predictionDataSet, getClassAttribute());

            for(int j = 0;  j < predictionDataSet.getRows(); j++){
                predictionHelpers[j].updateWeight(
                        predictionDataSet.getEntryInDataSet(getClassAttribute(), j),
                        getModelErrors()[i]);
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

    private class PredictionHelper {

        private Attribute classAttribute;
        private double[] weights;

        public PredictionHelper(Attribute classAttribute) {
            this.classAttribute = classAttribute;
            this.weights = new double[classAttribute.getValues().size()];
        }

        public void updateWeight(String value, double modelError){

            int valueIndex = getClassAttribute().getValues().indexOf(value);
            getWeights()[valueIndex] += - Math.log( modelError / (1 - modelError));
        }

        public void predict(WeightedDataSet predictionSet, int row){
            int maxIndex = Utils.argMax(getWeights());
            predictionSet.setEntryInDataSet(getClassAttribute(),
                    row, getClassAttribute().getValues().get(maxIndex),
                    getWeights()[maxIndex]);
        }

        public Attribute getClassAttribute() {
            return classAttribute;
        }

        public void setClassAttribute(Attribute classAttribute) {
            this.classAttribute = classAttribute;
        }

        public double[] getWeights() {
            return weights;
        }

        public void setWeights(double[] weights) {
            this.weights = weights;
        }
    }

}
