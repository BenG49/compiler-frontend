package compiler.parser.grammars.ast;

public abstract class AST {

    public abstract String toString();
    public abstract String toString(int depth);

    protected void tabs(StringBuilder sb, int depth) {
        final String tab = ":  ";
        for (int i = 0; i < depth; i++)
            sb.append(tab);
    }
}
