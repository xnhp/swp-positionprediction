package project.software.uni.positionprediction.datatypes_new;

import java.util.ArrayList;

public class Collection<E> extends ArrayList<E> {

    public Collection() { super(); }

    public Collection(Collection e) { super(e); }

    public Collection(E e) {
        Collection<E> c = new Collection<>();
        c.add(e);
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
}
