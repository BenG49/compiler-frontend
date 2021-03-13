package compiler.parser.grammars.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ASTNode<O, E> {
    public O operator;
    public String name;
    public List<E> branches;

    @SafeVarargs
    public ASTNode(String name, O operator, E... branches) {
        this(name, operator, new ArrayList<E>(Arrays.asList(branches)));
    }
    public ASTNode(String name, O operator, List<E> branches) {
        this.name = name;
        this.operator = operator;
        this.branches = branches;
    }

    // NOTE: ONLY HANDLES VALUE NODES WITH ONE BRANCH
    public void printTree(StringBuilder buffer, String prefix, String branchPrefix) {
        StringBuilder tempBuffer = new StringBuilder();
        for (int i = 0; i < branches.size(); i++) {
            if (branches.get(i) instanceof ASTNode<?, ?>) {
                ASTNode<?, ?> temp = (ASTNode<?, ?>)branches.get(i);
                if (i+1 < branches.size())
                    temp.printTree(tempBuffer, branchPrefix+"├── ", branchPrefix + "│   ");
                else
                    temp.printTree(tempBuffer, branchPrefix+"└── ", branchPrefix + "    ");
            }
        }

        buffer.append(prefix);
        buffer.append(name);

        if (tempBuffer.length() > 0) {
            buffer.append("<");
            buffer.append(operator);
            buffer.append(">\n");
            buffer.append(tempBuffer.toString());
        } else {
            buffer.append(": ");
            buffer.append(branches.get(0));
            buffer.append("\n");
        }
    }

    public Iterator<E> branchIterator() {
        return branches.iterator();
    }
}
