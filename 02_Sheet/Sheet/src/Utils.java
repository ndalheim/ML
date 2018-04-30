/**
 * Class for utility methods
 * Created by naedd on 22.04.2018.
 */
public class Utils {

    /**
     * Count all validRows (instances) with ones
     *
     * @param array
     * @return number of valid rows
     */
    public static int countOnes(int[] array) {
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) count++;
        }
        return count;
    }

    public static int argMax(double[] array){
        double max = Double.MIN_VALUE;
        int maxIndex = -1;
        for(int i = 0; i < array.length; i++){
            if(max < array[i]){
                max = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static int getIndexOfFirstOne(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) {
                return i;
            }
        }
        return -1;
    }

    public static double computeMean(double[] array){
        double mean = 0.0;
        for(int i = 0; i < array.length; i++){
            mean += array[i];
        }
        return mean / (double) array.length;
    }

    public static double computeStdVar(double mean, double[] array){
        double stdVar = 0.0;
        for(int i = 0; i < array.length; i++){
            stdVar += Math.pow( mean - array[i], 2.0);
        }
        return stdVar / (double) array.length;
    }

}
