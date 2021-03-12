package compiler.parser.grammars.expressions;

import compiler.exception.parse.ParseException;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.AST;
import compiler.parser.grammars.ast.ASTNode;
import compiler.syntax.Type;

public class BoolExp {
    /**
     * boolexpression := boolterm andoroperator boolexpression
     *                 | boolterm
     */
    public static AST BoolExpression(Parser p) throws ParseException {
        AST temp = BoolTerm(p);

        Type nextType = p.l.nextType();
        if (nextType.within(Type.AND, Type.OR)) {
            p.eat(nextType);
            return new ASTNode<Type>(
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
    public static AST BoolTerm(Parser p) throws ParseException {
        AST temp = BoolFactor(p);

        Type nextType = p.l.nextType();
        if (nextType.within(Type.EQUIVALENT, Type.GREATER, Type.LESS, Type.GREATER_EQUAL, Type.LESS_EQUAL)) {
            p.eat(nextType);
            return new ASTNode<Type>(
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
    public static AST BoolFactor(Parser p) throws ParseException {
        final String name = "BoolFactor";
        Type nextType = p.l.nextType();

        // NOT boolfactor
        if (nextType == Type.NOT) {
            p.eat(Type.NOT);
            return new ASTNode<Type>(
                name, nextType,
                BoolFactor(p)
            );
        }

        // LPAREN boolexpression RPAREN
        if (nextType == Type.LPAREN) {
            p.eat(Type.LPAREN);
            AST temp = BoolExpression(p);
            p.eat(Type.RPAREN);
            return temp;
        }

        // truefalseliteral
        if (nextType.within(Type.TRUE, Type.FALSE))
            return Values.TrueFalseLiteral(p);

        // variable
        if (nextType == Type.VAR)
            return Values.Variable(p);

        // binaryexpression
        return BinExp.BinaryExpression(p);
    }

}
