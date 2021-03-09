package compiler.parser.grammars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {
    public String type;
    public List<Node> value;

    public Node(String type, Node value) {
        this(type, new ArrayList<Node>(Arrays.asList(value)));
    }
    public Node(String type, List<Node> value) {
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

        for (Node n : value)
            sb.append(n.toString(depth+1)+"\n");

        tabs(sb, depth);
        sb.append("}");

        return sb.toString();
    }

    protected void tabs(StringBuilder sb, int depth) {
        final String tab = "|  ";
        for (int i = 0; i < depth; i++)
            sb.append(tab);
    }
}
