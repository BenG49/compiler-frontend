package compiler.semantics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;

import compiler.exception.semantics.InvalidVarException;
import compiler.exception.semantics.SemanticException;
import compiler.exception.semantics.TypeConcatException;
import compiler.parser.grammars.ast.*;
import compiler.syntax.Type;

public class VarSemantics {
    public static void check(ASTNode<?, ?> program) throws SemanticException {
        List<Symbol> variables = new ArrayList<Symbol>();
        visit(variables, program);
    }

    private static void visit(List<Symbol> variables, ASTNode<?, ?> node) throws SemanticException {
        Iterator<?> itr = node.branchIterator();
        while (itr.hasNext()) {
            Object temp = itr.next();
            if (temp instanceof ASTNode<?, ?>) {
                ASTNode<?, ?> branch = (ASTNode<?, ?>)temp;
                String s = branch.name;

                // declaration statement, add to list of variables
                if (s.equals("DeclareStatement")) {
                    Type type = (Type)branch.branches.get(0);
                    Iterator<?> vars = branch.branchIterator();
                    while (vars.hasNext()) {
                        variables.add(new Symbol((String)vars.next(), type));
                    }
                } else if (s.equals("AssignStatement")) {
                    // can't tell if it's a type or undefined error
                    if (!variables.contains(new Symbol((String)branch.branches.get(0),
                            getType((ASTNode<?, ?>)branch.branches.get(1)).get(0)))) {
                        throw new InvalidVarException((String)branch.branches.get(0));
                    }
                } else
                    visit(variables, branch);
            }
        }
    }

    private static List<Type> getType(ASTNode<?, ?> node) throws SemanticException {
        HashSet<Type> foundTypes = new HashSet<Type>();
        Iterator<?> itr = node.branchIterator();
        while (itr.hasNext()) {
            Object temp = itr.next();

            if (temp instanceof ASTNode<?, ?>) {
                ASTNode<?, ?> branch = (ASTNode<?, ?>)temp;
                if (branch.name.equals("TrueFalseLiteral"))
                    foundTypes.add(Type.BOOL_ID);
                else if (branch.name.equals("IntLiteral"))
                    foundTypes.add(Type.INT_ID);
                else if (branch.name.equals("FloatLiteral"))
                    foundTypes.add(Type.FLOAT_ID);
                else if (branch.name.equals("StringLiteral"))
                    foundTypes.add(Type.STR_ID);
                else {
                    for (Type t:getType((ASTNode<?, ?>)temp))
                        foundTypes.add(t);
                }
            }
        }

        /**
         * type hierarchy:
         * int, float, bool, string
         */
        if (foundTypes.contains(Type.STR_ID))
            return new ArrayList<Type>(Arrays.asList(Type.STR_ID));
        
        // bool+float | bool+int invalid
        if (!foundTypes.contains(Type.BOOL_ID)) {
            if (foundTypes.contains(Type.FLOAT_ID))
                return new ArrayList<Type>(Arrays.asList(Type.FLOAT_ID));
            
            if (foundTypes.contains(Type.INT_ID))
                return new ArrayList<Type>(Arrays.asList(Type.INT_ID));
        } else
            if (foundTypes.contains(Type.BOOL_ID))

        throw new TypeConcatException(foundTypes);
        return new ArrayList<Type>();
    }

}
