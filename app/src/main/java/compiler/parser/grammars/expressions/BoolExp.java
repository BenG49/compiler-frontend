package compiler.parser.grammars.expressions;

import compiler.exception.CompileException;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.ASTNode;
import compiler.syntax.Type;

public class BoolExp {
    /**
     * boolexpression := boolterm andoroperator boolexpression
     *                 | boolterm
     */
    public static ASTNode<?, ?> BoolExpression(Parser p) throws CompileException {
        ASTNode<?, ?> temp = BoolTerm(p);

        Type nextType = p.l.nextType();
        if (nextType.within(Type.AND, Type.OR)) {
            p.eat(nextType);
            return new ASTNode<Type, ASTNode<?, ?>>(
                "BoolExpression", nextType,
                temp, BoolExpression(p)
            );
        }

        return temp;
    }

    /**
     * boolterm := boolfactor compareoperator boolterm
     *           | boolfactor
     */
    public static ASTNode<?, ?> BoolTerm(Parser p) throws CompileException {
        ASTNode<?, ?> temp = BoolFactor(p);

        Type nextType = p.l.nextType();
        if (nextType.within(Type.EQUIVALENT, Type.GREATER, Type.LESS, Type.GREATER_EQUAL, Type.LESS_EQUAL)) {
            p.eat(nextType);
            return new ASTNode<Type, ASTNode<?, ?>>(
                "BoolTerm", nextType,
                temp, BoolTerm(p)
            );
        }

        return temp;
    }

    /**
     * boolfactor := NOT boolfactor
     *             | LPAREN boolexpression RPAREN
     *             | binaryexpression
     *             | truefalseliteral
     *             | variable
     */
    public static ASTNode<?, ?> BoolFactor(Parser p) throws CompileException {
        final String name = "BoolFactor";
        Type nextType = p.l.nextType();

        // NOT boolfactor
        if (nextType == Type.NOT) {
            p.eat(Type.NOT);
            return new ASTNode<Type, ASTNode<?, ?>>(
                name, nextType,
                BoolFactor(p)
            );
        }

        // LPAREN boolexpression RPAREN
        if (nextType == Type.LPAREN) {
            p.eat(Type.LPAREN);
            ASTNode<?, ?> temp = BoolExpression(p);
            p.eat(Type.RPAREN);
            return temp;
        }

        // truefalseliteral
        if (nextType.within(Type.TRUE, Type.FALSE))
            return Values.TrueFalseLiteral(p);

        // variable
        if (nextType == Type.VAR)
            return Values.Variable(p, false);

        // binaryexpression
        return BinExp.BinaryExpression(p);
    }

}