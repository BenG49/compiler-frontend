package compiler.parser.grammars.expressions;

import compiler.exception.CompileException;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.ASTNode;
import compiler.syntax.SymbolTable;
import compiler.syntax.Type;

public class BoolExp {
    /**
     * boolexpression := boolterm andoroperator boolexpression
     *                 | boolterm
     */
    public static ASTNode<?> BoolExpression(Parser p, SymbolTable t) throws CompileException {
        ASTNode<?> temp = BoolTerm(p, t);

        Type nextType = p.l.nextType();
        if (nextType.within(Type.AND, Type.OR)) {
            p.eat(nextType);
            return new ASTNode<ASTNode<?>>(
                "BoolExpression", nextType,
                temp, BoolExpression(p, t)
            );
        }

        return temp;
    }

    /**
     * boolterm := boolfactor compareoperator boolterm
     *           | boolfactor
     */
    public static ASTNode<?> BoolTerm(Parser p, SymbolTable t) throws CompileException {
        ASTNode<?> temp = BoolFactor(p, t);

        Type nextType = p.l.nextType();
        if (nextType.within(Type.EQUIVALENT, Type.GREATER, Type.LESS, Type.GREATER_EQUAL, Type.LESS_EQUAL)) {
            p.eat(nextType);
            return new ASTNode<ASTNode<?>>(
                "BoolTerm", nextType,
                temp, BoolTerm(p, t)
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
    public static ASTNode<?> BoolFactor(Parser p, SymbolTable t) throws CompileException {
        Type nextType = p.l.nextType();

        // NOT boolfactor
        if (nextType == Type.NOT) {
            p.eat(Type.NOT);
            return new ASTNode<ASTNode<?>>(
                "BoolFactor", nextType,
                BoolFactor(p, t)
            );
        }

        // LPAREN boolexpression RPAREN
        if (nextType == Type.LPAREN) {
            p.eat(Type.LPAREN);
            ASTNode<?> temp = BoolExpression(p, t);
            p.eat(Type.RPAREN);
            return temp;
        }

        // truefalseliteral
        if (nextType.within(Type.TRUE, Type.FALSE))
            return Values.TrueFalseLiteral(p);

        // variable
        if (nextType == Type.ID)
            return Values.Variable(p, t);

        // binaryexpression
        return BinExp.BinaryExpression(p, t);
    }

}
