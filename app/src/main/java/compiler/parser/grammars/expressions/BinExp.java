package compiler.parser.grammars.expressions;

import compiler.exception.parse.ParseException;
import compiler.exception.parse.TokenTypeException;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.AST;
import compiler.parser.grammars.ast.ASTNode;
import compiler.syntax.Type;

public class BinExp {
    /**
     * binaryexpression := term addsuboperator binaryexpression
     *                   | term
     */
    public static AST BinaryExpression(Parser p) throws ParseException {
        AST temp = Term(p);

        Type nextType = p.l.nextType();
        // ADD|SUB binaryexpression
        if (nextType.within(Type.PLUS, Type.MINUS)) {
            p.eat(nextType);
            return new ASTNode<Type>(
                "BinaryExpression", nextType,
                temp, BinaryExpression(p)
            );
        }

        return temp;
    }

    /**
     * term := exp muldivoperator term
     *       | exp
     */
    public static AST Term(Parser p) throws ParseException {
        AST temp = Exp(p);

        Type nextType = p.l.nextType();
        // MUL|DIV term
        if (nextType.within(Type.MUL, Type.DIV)) {
            p.eat(nextType);
            return new ASTNode<Type>(
                "Term", nextType,
                temp, Term(p)
            );
        }

        return temp;
    }

    /**
     * exp := factor EXP exp
     *      | factor
     */
    public static AST Exp(Parser p) throws ParseException {
        AST temp = Factor(p);

        if (p.l.nextType() == Type.EXP) {
            p.eat(Type.EXP);
            return new ASTNode<Type>(
                "Exp", Type.EXP,
                temp, Exp(p)
            );
        }
    
        return temp;
    }

    /**
     * factor :=     ""
     *             | MINUS
     *             | PLUS
     *           variable
     *         | numberliteral
     *         | LPAREN binaryexpression RPAREN
     */
    public static AST Factor(Parser p) throws ParseException {
        final String name = "Factor";

        Type nextType = p.l.nextType();
        Type preceedingSign = null;

        if (nextType.within(Type.MINUS, Type.PLUS)) {
            preceedingSign = nextType;
            p.eat(nextType);
            
            nextType = p.l.nextType();
        }

        // numberliteral
        if (nextType.within(Type.INT, Type.FLOAT)) {
            if (preceedingSign == null)
                return Values.NumberLiteral(p);
            else
                return new ASTNode<Type>(
                    name, preceedingSign,
                    Values.NumberLiteral(p)
                );
        }

        // variable
        if (nextType == Type.VAR)
            if (preceedingSign == null)
                return Values.Variable(p);
            else
                return new ASTNode<Type>(
                    name, preceedingSign,
                    Values.Variable(p)
                );

        // LPAREN binaryexpression RPAREN
        if (nextType == Type.LPAREN) {
            p.eat(Type.LPAREN);
            AST temp = BinaryExpression(p);
            p.eat(Type.RPAREN);

            if (preceedingSign == null)
                return temp;
            else
                return new ASTNode<Type>(
                    name, preceedingSign,
                    temp
                );
        }
        
        throw new TokenTypeException(nextType, "Variable, NumberLiteral, (BinaryExpression)", p.l.getPos());
    }

}
