import org.ejml.data.DMatrixIterator;
import org.ejml.simple.SimpleMatrix;

import java.io.*;


public class Main {



    public static void main(String... args) throws IOException {

        SimpleMatrix[] matrices = readTask3(args[0]);

        SimpleMatrix X = matrices[0];
        SimpleMatrix y = matrices[1];

        System.out.println("##################### Input #####################");
        System.out.println("\nX:");
        X.print();
        System.out.println("\ny:");
        y.print();

        System.out.println();
        System.out.println("################## Computation ##################");

        System.out.println("\nX^T:");
        X.transpose().print();

        System.out.println("\nX^T * X:");
        X.transpose().mult(X).print();

        System.out.println("\n(X^T * X)^-1:");
        SimpleMatrix partOne = X.transpose().mult(X).invert();
        partOne.print();

        System.out.println("\nx^T * y:");
        SimpleMatrix partTwo = X.transpose().mult(y);
        partTwo.print();

        System.out.println();
        System.out.println("#################### Result #####################");
        System.out.println("\nw:");
        SimpleMatrix w = partOne.mult(partTwo);
        w.print();

        System.out.print("\nMSE: ");
        double error = computeMeanSquaredError(y, X.mult(w));
        System.out.print(error);

    }

    public static double computeMeanSquaredError(SimpleMatrix groundTruth,
                                                 SimpleMatrix prediction){

        SimpleMatrix diff = groundTruth.minus(prediction);

        DMatrixIterator iter = diff.iterator(true, 0,
                0, diff.numRows() - 1, diff.numCols() - 1);

        double error = 0.0;
        while(iter.hasNext()){
            double value = iter.next();
            error += value*value;
        }
        error /= diff.numRows();
        return error;
    }


    public static SimpleMatrix[] readTask3(String filePath) throws IOException {


        BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));

        int numRows = 19;
        int numColumns = 5;

        double[] X = new double[numRows*numColumns];
        double[] y = new double[numRows];

        String line;
        int row = 0;
        boolean firstLine = true;
        while((line = br.readLine()) != null){

            if(firstLine){
                firstLine = false;
                continue;
            }

            String[] split = line.split(",");
            X[row*numColumns] = 1.0;
            for(int column = 0; column < split.length - 1; column++){
                X[row*numColumns+(column+1)] = Double.parseDouble(split[column]);
            }
            y[row] = Double.parseDouble(split[split.length - 1]);
            row++;
        }
        br.close();
        return new SimpleMatrix[] {new SimpleMatrix(numRows,numColumns, true, X),
            new SimpleMatrix(numRows, 1, true, y)};
    }


}
