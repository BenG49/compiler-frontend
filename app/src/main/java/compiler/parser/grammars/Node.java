package compiler.parser.grammars;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node<E> {
    public String type;
    public List<E> value;

    public Node(String type, E value) {
        this(type, new ArrayList<E>(Arrays.asList((E[])Array.newInstance(value.getClass(), 1))));
    }
    public Node(String type, List<E> value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return type+"{\n"+value+"\n}";
    }
}
