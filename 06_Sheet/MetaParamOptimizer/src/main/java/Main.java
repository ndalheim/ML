import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.util.Random;


public class Main {

    public static final Integer SKIP = 1;

    public static void main(String... args) throws Exception {

        if ("J48Weka".equals(args[0])) {

            Instances[] instancesList = new Instances[(args.length - SKIP) / 2];
            for (int i = 0; i < args.length - SKIP; i += 2) {
                Instances dataSet = new Instances(new FileReader(new File(args[i + SKIP])));
                dataSet.setClassIndex(Integer.parseInt(args[i + SKIP + 1]));
                instancesList[i / 2] = dataSet;
            }

            tuneConfParamJ48Weka(instancesList);
        } else if ("paramSelectJ48Weka".equals(args[0])) {

            Instances dataSet = new Instances(new FileReader(new File(args[1])));
            dataSet.setClassIndex(Integer.parseInt(args[2]));
            cvParameterSelection(dataSet, args[3], args[4]);

        } else if ("optimalJ48".equals(args[0])) {

            Instances dataSet = new Instances(new FileReader(new File(args[1])));
            dataSet.setClassIndex(Integer.parseInt(args[2]));
            cvpsPrediction(dataSet, args[3]);
        }

    }

    public static String[] buildRealRange(String range) {

        String[] split = range.split(":");
        double value = Double.parseDouble(split[0]);
        double endValue = Double.parseDouble(split[2]);
        double stepSize = Double.parseDouble(split[1]);

        if (endValue < value) return new String[]{};

        int steps = (int) ((endValue - value) / stepSize);
        String[] results = new String[steps + 1];

        for (int i = 0; i < steps + 1; i++) {
            results[i] = String.valueOf(value);
            value += stepSize;
        }
        return results;
    }

    public static String[] buildNominalRange(String range) {

        return range.substring(1, range.length() - 1).split(",");
    }

    public static void cvpsPrediction(Instances instances,
                                      String range) throws Exception {

        J48 j48 = cvParameterSelection(instances, "-C", range);

        Evaluation eval = new Evaluation(instances);
        double[] predictions = eval.evaluateModel(j48, instances);

        System.out.println("Predictions on complete dataset :");
        for (int i = 0; i < predictions.length; i++) {
            System.out.print(predictions[i] + ", ");
        }
        System.out.println();
        System.out.println("Accuracy on complete dataset : " + eval.pctCorrect());
    }

    public static J48 cvParameterSelection(Instances instances,
                                           String option,
                                           String range) throws Exception {

        String[] rangeElements = null;
        if ("{".equals(range.charAt(0))) {
            rangeElements = buildNominalRange(range);
        } else {
            rangeElements = buildRealRange(range);
        }

        Random random = new Random();
        Instances randData = new Instances(instances);
        randData.randomize(random);
        int folds = 10;

        J48 maxJ48 = null;
        double maxAccuracy = Double.MIN_VALUE;
        int rangeElementsMaxIndex = -1;

        for (int j = 0; j < rangeElements.length; j++) {

            double accuracy = 0.0;
            double currMaxJ48Accuracy = Double.MIN_VALUE;
            J48 currMaxJ48 = null;
            for (int i = 0; i < folds; i++) {
                Instances train = randData.trainCV(folds, i);
                Instances test = randData.testCV(folds, i);

                J48 j48 = new J48();
                j48.setOptions(new String[]{option, " " + rangeElements[j]});
                j48.buildClassifier(train);

                Evaluation eval = new Evaluation(randData);
                eval.evaluateModel(j48, test);

                if (currMaxJ48Accuracy < eval.pctCorrect()) {
                    currMaxJ48Accuracy = eval.pctCorrect();
                    currMaxJ48 = j48;
                }

                accuracy += eval.pctCorrect();
            }
            accuracy /= (double) folds;

            if (accuracy > maxAccuracy) {
                maxAccuracy = accuracy;
                rangeElementsMaxIndex = j;
                maxJ48 = currMaxJ48;
            }
        }

        System.out.println("Results:");
        System.out.println("Option : " + option);
        System.out.println("Best value : " + rangeElements[rangeElementsMaxIndex]);
        System.out.println("Best accuracy : " + maxAccuracy);

        return maxJ48;
    }


    public static void tuneConfParamJ48Weka(Instances[] instancesList) throws Exception {

        int folds = 10;
        Random random = new Random();

        double[][] accuracies = new double[instancesList.length][folds];
        for (int p = 0; p < instancesList.length; p++) {

            System.out.println("Computing dataset : " + p);

            Instances randData = new Instances(instancesList[p]);
            randData.randomize(random);

            for (int j = 1; j < 11; j++) {
                for (int i = 0; i < folds; i++) {
                    long time = System.currentTimeMillis();
                    System.out.print("Starting fold : " + i + "\t");

                    Instances train = randData.trainCV(folds, i);
                    Instances test = randData.testCV(folds, i);

                    J48 j48 = new J48();
                    j48.setOptions(new String[]{"-C", "" + 0.05 * j});
                    j48.buildClassifier(train);

                    Evaluation eval = new Evaluation(randData);
                    eval.evaluateModel(j48, test);

                    accuracies[p][j - 1] += eval.pctCorrect();

                    System.out.print("Finished fold\t");
                    System.out.print(((double) (System.currentTimeMillis() - time) / 1000.0) + "\n");
                }
                accuracies[p][j - 1] /= (double) folds;
            }

            /*
            for (int i = 0; i < accuracies.length; i++) {
                System.out.print(accuracies[i]);
                System.out.print(", ");
            }
            */

        }

        double[] x = new double[10];
        for (int i = 0; i < 10; i++) {
            x[i] = 0.05 * (i + 1);
        }

        DefaultXYDataset data = new DefaultXYDataset();
        for(int p = 0; p < accuracies.length; p++) {
            data.addSeries(instancesList[p].relationName(), new double[][]{x, accuracies[p]});
        }

        /**
         * Code can be seen in factory. Look at outcommented factory call below.
         */
        NumberAxis xAxis = new NumberAxis("-C Option");
        xAxis.setAutoRangeIncludesZero(true);
        xAxis.setAutoRange(true);
        NumberAxis yAxis = new NumberAxis("Accuracy in %");
        yAxis.setAutoRangeIncludesZero(false);
        yAxis.setAutoRange(true);
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        XYPlot plot = new XYPlot(data, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        JFreeChart xyChart = new JFreeChart("Accuracy plots",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        (new StandardChartTheme("JFree")).apply(xyChart);

        /*
        // Less customizing possible but fast and elegant.
        JFreeChart xyChart = ChartFactory.createXYLineChart("plot",
                "-C Option", "Accuracy in %",
                data, PlotOrientation.VERTICAL, true, false, false);
        */

        ApplicationFrame af = new ApplicationFrame("Accuracy plots");
        af.setContentPane(new ChartPanel(xyChart));
        af.setSize(500, 500);
        RefineryUtilities.centerFrameOnScreen(af);
        af.setVisible(true);
    }

}