package compiler.semantics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;

import compiler.Empty;
import compiler.exception.semantics.InvalidTypeException;
import compiler.exception.semantics.SemanticException;
import compiler.exception.semantics.TypeConcatException;
import compiler.exception.semantics.UndefinedVarException;
import compiler.parser.grammars.ast.*;
import compiler.syntax.Type;

public class VarSemantics {
    public static void check(ASTNode<?, ?> program) throws SemanticException {
        HashMap<String, Type> variables = new HashMap<String, Type>();
        visit(variables, program);
    }

    @SuppressWarnings("unchecked")
    private static void visit(HashMap<String, Type> variables, ASTNode<?, ?> node) throws SemanticException {
        Iterator<?> itr = node.branchIterator();
        while (itr.hasNext()) {
            Object current = itr.next();
            if (current instanceof ASTNode<?, ?>) {
                ASTNode<?, ?> nodeBranch = (ASTNode<?, ?>)current;
                String s = nodeBranch.name;

                // declaration statement, add to list of variables
                if (s.equals("DeclareStatement")) {
                    Type type = getValueNodeValue((ASTNode<Empty, Type>)nodeBranch.branches.get(0));
                    String var = getValueNodeValue((ASTNode<Empty, String>)nodeBranch.branches.get(1));
                    variables.put(var, type);
                    // single var assignment
                    if (nodeBranch.branches.size() == 3 && !((ASTNode<?, ?>)nodeBranch.branches.get(2)).name.equals("Variable")) {
                        Type assign = getType((ASTNode<?, ?>)nodeBranch.branches.get(2), variables).get(0);
                        if (type != assign)
                            throw new InvalidTypeException(assign, type);
                    // multiple vars
                    } else {
                        Iterator<?> vars = nodeBranch.branchIterator();
                        // type
                        vars.next();
                        // first var
                        vars.next();
                        // assume that they're all variables because that's caught by the parser
                        while (vars.hasNext())
                            variables.put(getValueNodeValue((ASTNode<Empty, String>)vars.next()), type);
                    }
                } else if (s.equals("AssignStatement")) {
                    Type t = getType((ASTNode<?, ?>)nodeBranch.branches.get(1), variables).get(0);
                    String var = getValueNodeValue((ASTNode<Empty, String>)nodeBranch.branches.get(0));

                    if (!variables.keySet().contains(var))
                        throw new UndefinedVarException(var);
                    if (variables.get(var) != t)
                        throw new InvalidTypeException(t, variables.get(var));
                } else
                    visit(variables, nodeBranch);
            }
        }
    }

    // TODO: stop looping if conflict is found or string is found
    private static List<Type> getType(ASTNode<?, ?> node, HashMap<String, Type> variables) throws SemanticException {
        HashSet<Type> foundTypes = new HashSet<Type>();

        if (node.name.equals("TrueFalseLiteral"))
            foundTypes.add(Type.BOOL_ID);
        else if (node.name.equals("IntLiteral"))
            foundTypes.add(Type.INT_ID);
        else if (node.name.equals("FloatLiteral"))
            foundTypes.add(Type.FLOAT_ID);
        else if (node.name.equals("StringLiteral"))
            foundTypes.add(Type.STR_ID);
        else if (node.name.equals("Variable"))
            foundTypes.add(variables.get(node.branches.get(0)));
        else {
            Iterator<?> itr = node.branchIterator();
            while (itr.hasNext()) {
                Object temp = itr.next();

                if (temp instanceof ASTNode<?, ?>) {
                    for (Type t : getType((ASTNode<?, ?>)temp, variables))
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
        } else if (foundTypes.contains(Type.BOOL_ID))
            return new ArrayList<Type>(Arrays.asList(Type.BOOL_ID));

        throw new TypeConcatException(foundTypes);
    }

    private static <T> T getValueNodeValue(ASTNode<Empty, T> node) {
        return node.branches.get(0);
    }

}
