package compiler.parser.grammars.expressions;

import compiler.exception.*;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.*;
import compiler.lexer.Token.Type;

public class Literals {
    /**
     * vartypeliteral := VAR_TYPES
     */
    public static ASTValue<Type> VarTypeLiteral(Parser p) throws ParseException {
        Type out = p.l.nextType();
        p.tryNextToken(Type.getVarTypes());

        return new ASTValue<Type>(
            "VarTypeLiteral",
            out
        );
    }

    /**
     * truefalseliteral := TRUE
     *                   | FALSE
     */
    public static ASTValue<Type> TrueFalseLiteral(Parser p) throws ParseException {
        Type out = p.l.nextType();
        p.tryNextToken(Type.TRUE, Type.FALSE);

        return new ASTValue<Type>(
            "TrueFalseLiteral",
            out
        );
    }

    /**
     * stringliteral := STR
     */
    public static ASTValue<String> StringLiteral(Parser p) throws ParseException {
        return new ASTValue<String>(
            "StringLiteral",
            p.eat(Type.STR)
        );
    }

    /**
     * numberliteral := intliteral
     *                | floatliteral
     */
    public static ASTValue NumberLiteral(Parser p) throws ParseException {
        Type nextType = p.l.nextType();

        if (nextType == Type.INT)
            return IntLiteral(p);

        if (nextType == Type.FLOAT)
            return FloatLiteral(p);

        if (nextType == null)
            throw new EOFException("Operator");
        else
            throw new TokenTypeException(nextType, "NumberLiteral", p.l.getPos());
    }

    /**
     * intliteral := INT
     */
    public static ASTValue<Integer> IntLiteral(Parser p) throws ParseException {
        return new ASTValue<Integer>(
            "IntLiteral",
            Integer.parseInt(p.eat(Type.INT))
        );
    }

    /**
     * floatliteral := FLOAT
     */
    public static ASTValue<Float> FloatLiteral(Parser p) throws ParseException {
        return new ASTValue<Float>(
            "FloatLiteral",
            Float.parseFloat(p.eat(Type.FLOAT))
        );
    }

}
