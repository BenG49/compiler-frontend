package compiler.parser.grammars.expressions;

import compiler.exception.*;
import compiler.parser.Parser;
import compiler.parser.grammars.*;
import compiler.lexer.Token.Type;

public class Literals {
    /**
     * vartypeliteral := VAR_TYPES
     */
    public static ValueNode<Type> VarTypeLiteral(Parser p) throws ParseException {
        Type out = p.l.nextType();
        p.tryNextToken(Type.getVarTypes());

        return new ValueNode<Type>(
            "VarTypeLiteral",
            out
        );
    }

    /**
     * truefalseliteral := TRUE
     *                   | FALSE
     */
    public static ValueNode<Type> TrueFalseLiteral(Parser p) throws ParseException {
        Type out = p.l.nextType();
        p.tryNextToken(Type.TRUE, Type.FALSE);

        return new ValueNode<Type>(
            "TrueFalseLiteral",
            out
        );
    }

    /**
     * stringliteral := STR
     */
    public static ValueNode<String> StringLiteral(Parser p) throws ParseException {
        return new ValueNode<String>(
            "StringLiteral",
            p.tryNextToken(Type.STR)
        );
    }

    /**
     * numberliteral := intliteral
     *                | floatliteral
     */
    public static Node NumberLiteral(Parser p) throws ParseException {
        Type nextType = p.l.nextType();
        Node literal;

        if (nextType == Type.INT)
            literal = IntLiteral(p);
        else if (nextType == Type.FLOAT)
            literal = FloatLiteral(p);
        else if (nextType == null)
            throw new EOFException("Operator");
        else
            throw new TokenTypeException(nextType, "NumberLiteral", p.l.getPos());
        
        return new Node(
            "NumberLiteral",
            literal
        );
    }

    /**
     * intliteral := INT
     */
    public static ValueNode<Integer> IntLiteral(Parser p) throws ParseException {
        return new ValueNode<Integer>(
            "IntLiteral",
            Integer.parseInt(p.tryNextToken(Type.INT))
        );
    }

    /**
     * floatliteral := FLOAT
     */
    public static ValueNode<Float> FloatLiteral(Parser p) throws ParseException {
        return new ValueNode<Float>(
            "FloatLiteral",
            Float.parseFloat(p.tryNextToken(Type.FLOAT))
        );
    }

}
