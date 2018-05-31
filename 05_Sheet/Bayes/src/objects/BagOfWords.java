package objects;

import com.sun.deploy.util.OrderedHashSet;
import interfaces.DeepCloneable;

import java.util.*;

/**
 * Created by ken on 28.05.2018.
 */
public class BagOfWords extends HashMap<String, Integer>
        implements DeepCloneable<BagOfWords> {


    private String label;

    private Integer unions;

    private static final int SHINGLE_SIZE = 5;
    private static final int SHINGLE_STRIDE = 1;

    public BagOfWords(BagOfWords bag) {
        this.label = bag.label;
        this.unions = bag.unions;
        this.putAll(bag);
    }

    public BagOfWords(String label, String text) {
        this.unions = 0;
        this.label = label;
        buildBagOfWords(text);
    }


    public BagOfWords(String label, String text, boolean useShingles) {
        this.unions = 0;
        this.label = label;
        if(useShingles){
            buildBagOfWordsUsingShingles(text);
        }
        buildBagOfWords(text);
    }

    @Override
    public BagOfWords deepClone() {
        return new BagOfWords(this);
    }

    public void union(BagOfWords bag) {

        for (Entry<String, Integer> entry : bag.entrySet()) {
            if (containsKey(entry.getKey())) {
                put(entry.getKey(), get(entry.getKey()) + entry.getValue());
            } else {
                put(entry.getKey(), entry.getValue());
            }
        }
        unions++;
    }

    public int safeGet(String key) {
        Integer value = get(key);
        return (value == null) ? 0 : value;
    }

    public HashMap<String, Integer> createIndexing() {

        HashMap<String, Integer> indexing = new HashMap<>();
        int counter = 0;
        for (Entry<String, Integer> entry : entrySet()) {
            indexing.put(entry.getKey(), counter);
            counter++;
        }
        return indexing;
    }

    private void buildBagOfWords(String text) {

        // Words to build the bag of words from.
        String[] words = text.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (this.containsKey(words[i])) {
                put(words[i], get(words[i]) + 1);
            } else {
                put(words[i], 1);
            }
        }
    }

    private void buildBagOfWordsUsingShingles(String text) {

        for (int i = 0; i + SHINGLE_SIZE <= text.length(); i += SHINGLE_STRIDE) {
            String word = text.substring(i, i + SHINGLE_SIZE);
            if (this.containsKey(word)) {
                put(word, get(word) + 1);
            } else {
                put(word, 1);
            }
        }
    }

    public int countWords() {
        int count = 0;
        for (Entry<String, Integer> entry : entrySet()) {
            count += entry.getValue();
        }
        return count;
    }

    /**
     *
     * @param percentage
     */
    public void keepFrequentWords(double percentage) {
        int count = this.size();
        int border = (int) (count * (1.0 - percentage));

        ArrayList<Map.Entry<String, Integer>> sortedList = new ArrayList<>();
        sortedList.addAll(this.entrySet());
        sortedList.sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()));

        for(Map.Entry<String, Integer> entry : sortedList) {
            if(border == 0){
                break;
            }
            remove(entry.getKey());
            border --;
        }

    }

    public void remove(BagOfWords bag){
        for(Map.Entry<String, Integer> entry : bag.entrySet()){
            remove(entry.getKey());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Integer> entry : entrySet()) {
            sb.append(entry.getKey());
            sb.append(" ");
        }
        return sb.toString();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getUnions() {
        return unions;
    }

    public void setUnions(Integer unions) {
        this.unions = unions;
    }
}
