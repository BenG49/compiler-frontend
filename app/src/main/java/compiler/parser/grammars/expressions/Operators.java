package compiler.parser.grammars.expressions;

import java.util.ArrayList;
import java.util.List;

import compiler.exception.ParseException;
import compiler.lexer.Token.Type;
import compiler.parser.Parser;
import compiler.parser.grammars.ValueNode;

public class Operators {
    /**
     * addsuboperator := PLUS
     *                 | MINUS
     */
    public static ValueNode<Type> AddSubOperator(Parser p) throws ParseException {
        Type nextType = p.l.nextType();

        p.tryNextToken(Type.PLUS, Type.MINUS);

        return new ValueNode<Type>(
            "AddSubOperator",
            nextType
        );
    }

    /**
     * muldivoperator := PLUS
     *                 | MINUS
     */
    public static ValueNode<Type> MulDivOperator(Parser p) throws ParseException {
        Type nextType = p.l.nextType();

        p.tryNextToken(Type.MUL, Type.DIV);

        return new ValueNode<Type>(
            "MulDivOperator",
            nextType
        );
    }

    /**
     * compareoperator := EQUIVALENT
     *                  | LESS
     *                      | LESS EQUALS
     *                  | GREATER
     *                      | GREATER EQUALS
     */
    public static ValueNode<List<Type>> CompareOperator(Parser p) throws ParseException {
        List<Type> out = new ArrayList<Type>();
        Type nextType = p.l.nextType();

        p.tryNextToken(Type.EQUIVALENT, Type.LESS, Type.GREATER);
        out.add(nextType);

        nextType = p.l.nextType();
        if (nextType == Type.EQUALS) {
            p.tryNextToken(Type.EQUALS);
            out.add(nextType);
        }

        return new ValueNode<List<Type>>(
            "CompareOperator",
            out
        );
    }

    /**
     * andoroperator := AND
     *                | OR
     */
    public static ValueNode<Type> AndOrOperator(Parser p) throws ParseException {
        Type nextType = p.l.nextType();

        p.tryNextToken(Type.AND, Type.OR);

        return new ValueNode<Type>(
            "MulDivOperator",
            nextType
        );
    }
    
}
