package compiler.parser.grammars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Node of an AST tree, also serves as a grammar
 */
public class Node<E> {
    public String type;
    public List<E> value;

    public Node(String type, E value) {
        this(type, new ArrayList<E>(Arrays.asList(value)));
    }
    public Node(String type, List<E> value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return toString(0);
    }
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();

        tabs(sb, depth);
        sb.append(type);
        sb.append("{\n");

        for (E e : value) {

            if (e instanceof Node)
                sb.append(((Node)e).toString(depth+1)+"\n");
            else {
                tabs(sb, depth+1);
                sb.append(e+"\n");
            }
        }

        tabs(sb, depth);
        sb.append("}");

        return sb.toString();
    }

    private void tabs(StringBuilder sb, int depth) {
        final String tab = "  ";
        for (int i = 0; i < depth; i++)
            sb.append(tab);
    }
}
