package objects;

import interfaces.DeepCloneable;

import java.util.HashMap;

/**
 * Created by ken on 28.05.2018.
 */
public class BagOfWords extends HashMap<String, Integer>
    implements DeepCloneable<BagOfWords>{


    private String label;

    private Integer unions;

    public BagOfWords(BagOfWords bag){
        this.label = bag.label;
        this.unions = bag.unions;
        this.putAll(bag);
    }


    public BagOfWords(String label, String text) {
        this.unions = 0;
        this.label = label;
        buildBagOfWords(text);
    }

    @Override
    public BagOfWords deepClone() {
        return new BagOfWords(this);
    }

    public void union(BagOfWords bag){

        for(Entry<String, Integer> entry : bag.entrySet()){
            if(containsKey(entry.getKey())){
                put(entry.getKey(), get(entry.getKey()) + entry.getValue());
            }else{
                put(entry.getKey(), entry.getValue());
            }
        }
        unions++;
    }

    public int safeGet(String key){
        Integer value = get(key);
        return (value == null) ? 0 : value;
    }

    public HashMap<String, Integer> createIndexing(){

        HashMap<String, Integer> indexing = new HashMap<>();
        int counter = 0;
        for(Entry<String, Integer> entry : entrySet()){
            indexing.put(entry.getKey(), counter);
            counter++;
        }
        return indexing;
    }

    private void buildBagOfWords(String text){

        // Words to build the bag of words from.
        String[] words = text.split(" ");
        for(int i = 0; i < words.length; i++){
            if(this.containsKey(words[i])){
                put(words[i], get(words[i]) + 1);
            } else {
                put(words[i], 1);
            }
        }
    }


    public int countWords(){
        int count = 0;
        for(Entry<String, Integer> entry : entrySet()){
            count += entry.getValue();
        }
        return count;
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
