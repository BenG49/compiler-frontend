package compiler.parser.grammars.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import compiler.Empty;
import compiler.exception.parse.*;
import compiler.exception.semantics.DuplicateIdException;
import compiler.exception.semantics.UndefinedIdException;
import compiler.exception.CompileException;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.*;
import compiler.syntax.Type;

public class Values {
    /**
     * vartypeliteral := VAR_TYPES
     */
    public static ASTNode<Empty, Type> VarTypeLiteral(Parser p) throws CompileException {
        Type out = p.l.nextType();
        p.eat(Type.getVarTypes());

        return new ASTNode<Empty, Type>(
            "VarTypeLiteral", new Empty(),
            out
        );
    }

    /**
     * returntypeliteral := VAR_TYPES
     *                    | VOID
     */
    public static ASTNode<Empty, Type> ReturnTypeLiteral(Parser p) throws CompileException {
        Type out = p.l.nextType();
        List<Type> temp = new ArrayList<Type>(Arrays.asList(Type.getVarTypes()));
        temp.add(Type.VOID);

        p.eat(temp);

        return new ASTNode<Empty, Type>(
            "ReturnTypeLiteral", new Empty(),
            out
        );
    }

    /**
     * truefalseliteral := TRUE
     *                   | FALSE
     */
    public static ASTNode<Empty, Type> TrueFalseLiteral(Parser p) throws CompileException {
        Type out = p.l.nextType();
        p.eat(Type.TRUE, Type.FALSE);

        return new ASTNode<Empty, Type>(
            "TrueFalseLiteral", new Empty(),
            out
        );
    }

    /**
     * stringliteral := STR
     */
    public static ASTNode<Empty, String> StringLiteral(Parser p) throws CompileException {
        return new ASTNode<Empty, String>(
            "StringLiteral", new Empty(),
            p.eat(Type.STR)
        );
    }

    /**
     * numberliteral := intliteral
     *                | floatliteral
     */
    public static ASTNode<?, ?> NumberLiteral(Parser p) throws CompileException {
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
    public static ASTNode<Empty, Integer> IntLiteral(Parser p) throws CompileException {
        return new ASTNode<Empty, Integer>(
            "IntLiteral", new Empty(),
            Integer.parseInt(p.eat(Type.INT))
        );
    }

    /**
     * floatliteral := FLOAT
     */
    public static ASTNode<Empty, Float> FloatLiteral(Parser p) throws CompileException {
        return new ASTNode<Empty, Float>(
            "FloatLiteral", new Empty(),
            Float.parseFloat(p.eat(Type.FLOAT))
        );
    }

    /**
     * identifier := ID
     */
    public static ASTNode<Empty, String> Identifier(Parser p, boolean define) throws CompileException {
        String name = p.eat(Type.ID);

        if (!define && !p.t.contains(name))
            throw new UndefinedIdException(p.l.getPos(), name);
        
        if (define && p.t.contains(name) /*&& p.t.get(name).scope == scope*/)
            throw new DuplicateIdException(p.l.getPos(), name);

        return new ASTNode<Empty, String>("Identifier", new Empty(), name);
    }

}
