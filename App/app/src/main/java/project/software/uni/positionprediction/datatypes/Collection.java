package project.software.uni.positionprediction.datatypes;

import java.util.ArrayList;
import java.util.Collections;

public class Collection<E> extends ArrayList<E> {

    public Collection() { super(); }

    public Collection(Collection c) { super(c); }

    public Collection(E e) {
        //Collection<E> c = new Collection<>();
        this.add(e);
    }

    public Collection<E> addElement(E e){
        Collection<E> result = this;
        result.add(e);
        return result;
    }

    public Collection<E> addElements(Collection<E> e){
        Collection<E> result = this;
        result.addAll(e);
        return result;
    }

    public Collection<E> reverse(){
        Collection<E> result = this;
        Collections.reverse(this);
        return result;
    }

    public static Collection add(Collection a, Collection b){
        Collection result = a;
        result.addAll(b);
        return result;
    }
}
