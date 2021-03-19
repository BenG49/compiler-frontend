package compiler.parser.grammars.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import compiler.syntax.Type;

public class ASTNode<E> {
    public Type operator;
    public String name;
    public List<E> branches;

    @SafeVarargs
    public ASTNode(String name, Type operator, E... branches) {
        this(name, operator, new ArrayList<E>(Arrays.asList(branches)));
    }
    public ASTNode(String name, Type operator, List<E> branches) {
        this.operator = operator;
        this.name = name;
        this.branches = branches;
    }

    public void printTree(StringBuilder buffer, String prefix, String branchPrefix) {
        boolean valueNode = false;

        if (operator.within(Type.getLiterals()))
            valueNode = true;

        buffer.append(prefix);
        buffer.append(name);

        if (valueNode) {
            buffer.append(": ");
            buffer.append(branches.get(0));
            buffer.append("");
        } else if (operator != Type.BLANK) {
            buffer.append("<");
            buffer.append(operator);
            buffer.append(">");
        }

        buffer.append("\n");

        for (int i = 0; i < branches.size(); i++) {
            if (branches.get(i) instanceof ASTNode<?>) {
                ASTNode<?> branch = (ASTNode<?>)branches.get(i);

                if (i+1 < branches.size())
                    branch.printTree(buffer, branchPrefix+"├── ", branchPrefix + "│   ");
                else
                    branch.printTree(buffer, branchPrefix+"└── ", branchPrefix + "    ");
            }
        }

    }

    public Iterator<E> branchIterator() {
        return branches.iterator();
    }
}
