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

    public static int getIndexOfFirstOne(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) {
                return i;
            }
        }
        return -1;
    }

}
