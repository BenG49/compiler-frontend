package compiler.parser.grammars.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ASTNode<O> extends AST {
    public String name;
    public O operator;
    public List<AST> branches;

    public ASTNode(String name, O operator, AST... branches) {
        this(name, operator, new ArrayList<AST>(Arrays.asList(branches)));
    }
    public ASTNode(String name, O operator, List<AST> branches) {
        this.name = name;
        this.operator = operator;
        this.branches = branches;
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
        sb.append("<");
        sb.append(operator);
        sb.append(">");

        for (AST n : branches)
            sb.append(n.toString(depth+1));

        return sb.toString();
    }
}
