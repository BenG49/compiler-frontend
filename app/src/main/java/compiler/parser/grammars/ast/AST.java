package compiler.parser.grammars.ast;

public abstract class AST {

    public abstract String toString();
    public abstract String toString(int depth);

    // https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram
    public abstract void printTree(StringBuilder buffer, String prefix, String branchPrefix);

    protected void tabs(StringBuilder sb, int depth) {
        final String tab = ":  ";
        for (int i = 0; i < depth; i++)
            sb.append(tab);
    }
}
