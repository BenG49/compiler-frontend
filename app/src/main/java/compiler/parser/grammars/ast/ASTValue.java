package compiler.parser.grammars.ast;

/**
 * Base node of an AST tree
 */
public class ASTValue<E> extends AST {
    private String name;
    public E value;

    public ASTValue(String name, E value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return toString(0);
    }
    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        tabs(sb, depth);
        if (depth > 0)
            sb.append("--");
        sb.append(name);
        sb.append(": ");
        sb.append(value);

        return sb.toString();
    }
}
