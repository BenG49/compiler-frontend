package compiler.parser.grammars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Node of an AST tree, also serves as a grammar
 */
public class ValueNode<E> extends Node {
    public List<E> value;

    public ValueNode(String type, E value) {
        this(type, new ArrayList<E>(Arrays.asList(value)));
    }
    public ValueNode(String type, List<E> value) {
        super(type, new ArrayList<Node>());
        this.value = value;
    }

    @Override
    public String toString() {
        return toString(0);
    }
    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();

        tabs(sb, depth);
        sb.append(type);
        sb.append("{\n");

        for (E e : value) {
            tabs(sb, depth+1);
            sb.append(e+"\n");
        }

        tabs(sb, depth);
        sb.append("}");

        return sb.toString();
    }
}
