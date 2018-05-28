package utils;

import interfaces.DeepCloneable;
import objects.BagOfWords;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by ken on 28.05.2018.
 */
public class Utils {


    public static BagOfWords buildVocabulary(ArrayList<BagOfWords>... bags){

        LinkedList<BagOfWords> linkedBags = new LinkedList<>();
        for(int i = 0; i < bags.length; i++){
            linkedBags.addAll(bags[i]);
        }
        Iterator<BagOfWords> iterator = linkedBags.iterator();

        if(!iterator.hasNext()) return null;
        BagOfWords vocabulary = new BagOfWords(iterator.next());

        while (iterator.hasNext()){
            vocabulary.union(iterator.next());
        }
        return vocabulary;
    }


    public static <T extends DeepCloneable<T>> ArrayList<T> deepCloneCollection(Collection<T> list){
        ArrayList<T> copy = new ArrayList<T>();
        for( T element : list){
            copy.add(element.deepClone());
        }
        return copy;
    }


}
