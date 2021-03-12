package compiler.parser.grammars.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import compiler.exception.parse.*;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.*;
import compiler.syntax.Type;

public class Values {
    /**
     * vartypeliteral := VAR_TYPES
     */
    public static ASTValue<Type> VarTypeLiteral(Parser p) throws ParseException {
        Type out = p.l.nextType();
        p.eat(Type.getVarTypes());

        return new ASTValue<Type>(
            "VarTypeLiteral",
            out
        );
    }

    /**
     * returntypeliteral := VAR_TYPES
     *                    | VOID
     */
    public static ASTValue<Type> ReturnTypeLiteral(Parser p) throws ParseException {
        Type out = p.l.nextType();
        List<Type> temp = new ArrayList<Type>(Arrays.asList(Type.getVarTypes()));
        temp.add(Type.VOID);

        p.eat(temp);

        return new ASTValue<Type>(
            "ReturnTypeLiteral",
            out
        );
    }

    /**
     * truefalseliteral := TRUE
     *                   | FALSE
     */
    public static ASTValue<Type> TrueFalseLiteral(Parser p) throws ParseException {
        Type out = p.l.nextType();
        p.eat(Type.TRUE, Type.FALSE);

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
    public static AST NumberLiteral(Parser p) throws ParseException {
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

    /**
     * variable := VAR
     */
    public static ASTValue<String> Variable(Parser p) throws ParseException {
        return new ASTValue<String>(
            "Variable",
            p.eat(Type.VAR)
        );
    }

}
