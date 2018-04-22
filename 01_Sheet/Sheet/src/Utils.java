/**
 * Created by naedd on 22.04.2018.
 */
public class Utils {

    public static int countOnes(int[] array){
        int count = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] == 1) count++;
        }
        return count;
    }

}
