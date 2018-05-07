package data;

/**
 * Class for utility methods
 * Created by naedd on 22.04.2018.
 */
public class Utils {

    /**
     * Helpmethod to count all validRows (instances) which are represented by +1
     * and not valid rows as -1
     *
     * @param array which you want to scan
     * @return number of valid rows
     */
    public static int countOnes(int[] array) {
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) count++;
        }
        return count;
    }

    /**
     * Helpmethod to get the maximal double of an array
     *
     * @param array which you want to scan
     * @return the maximal number of the array
     */
    public static int argMax(double[] array){
        double max = -1 * Double.MIN_VALUE;
        int maxIndex = -1;
        for(int i = 0; i < array.length; i++){
            if(max < array[i]){
                max = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /**
     * Helpmethod to get the index of the first valid row which is represented by a one
     *
     * @param array which you want to scan
     * @return the first valid row
     */
    public static int getIndexOfFirstOne(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Helpmethod to compute the mean value of a given array.
     *
     * @param array which you want to scan for computation
     * @return the mean of the array values
     */
    public static double computeMean(double[] array){
        double mean = 0.0;
        for(int i = 0; i < array.length; i++){
            mean += array[i];
        }
        return mean / (double) array.length;
    }

    /**
     * Helpmethod to compute the standard deviation of a given array
     *
     * @param mean of the given array
     * @param array which you want to scan for computation
     * @return the standard deviation of the given array
     */
    public static double computeStdVar(double mean, double[] array){
        double stdVar = 0.0;
        for(int i = 0; i < array.length; i++){
            stdVar += Math.pow( mean - array[i], 2.0);
        }
        return stdVar / (double) array.length;
    }

}
