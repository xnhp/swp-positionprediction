package project.software.uni.positionprediction.datatypes;

import java.util.HashMap;


public class Collections<K, T> extends HashMap<K, Collection<? extends T>>{

    public Collections(){
        super();
    }

    public Collections(K key, T element){
        super();
        put(key, new Collection(element));
    }

    public Collections(K key, Collection<? extends T> collection){
        super();
        put(key, collection);
    }

    // todo
    protected void add(K key, T element) {}

    // todo
    protected void add(K key, Collection<? extends T> collection) {}

}
