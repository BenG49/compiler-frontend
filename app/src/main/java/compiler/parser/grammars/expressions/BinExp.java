package compiler.parser.grammars.expressions;

import compiler.exception.parse.TokenTypeException;
import compiler.exception.CompileException;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.ASTNode;
import compiler.syntax.SymbolTable;
import compiler.syntax.Type;

// TODO: add type casting and make it so you can't assign float to int
public class BinExp {
    /**
     * binaryexpression := term addsuboperator binaryexpression
     *                   | term
     */
    public static ASTNode<?> BinaryExpression(Parser p, SymbolTable t) throws CompileException {
        ASTNode<?> temp = Term(p, t);

        Type nextType = p.l.nextType();
        // ADD|SUB binaryexpression
        if (nextType.within(Type.PLUS, Type.MINUS)) {
            p.eat(nextType);
            return new ASTNode<ASTNode<?>>(
                "BinaryExpression", nextType,
                temp, BinaryExpression(p, t)
            );
        }

        return temp;
    }

    /**
     * term := exp muldivoperator term
     *       | exp
     */
    public static ASTNode<?> Term(Parser p, SymbolTable t) throws CompileException {
        ASTNode<?> temp = Exp(p, t);

        Type nextType = p.l.nextType();
        // MUL|DIV term
        if (nextType.within(Type.MUL, Type.DIV)) {
            p.eat(nextType);
            return new ASTNode<ASTNode<?>>(
                "Term", nextType,
                temp, Term(p, t)
            );
        }

        return temp;
    }

    /**
     * exp := factor EXP exp
     *      | factor
     */
    public static ASTNode<?> Exp(Parser p, SymbolTable t) throws CompileException {
        ASTNode<?> temp = Factor(p, t);

        if (p.l.nextType() == Type.EXP) {
            p.eat(Type.EXP);
            return new ASTNode<ASTNode<?>>(
                "Exp", Type.EXP,
                temp, Exp(p, t)
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
    public static ASTNode< ?> Factor(Parser p, SymbolTable t) throws CompileException {
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
                return new ASTNode<ASTNode<?>>(
                    name, preceedingSign,
                    Values.NumberLiteral(p)
                );
        }

        // variable
        if (nextType == Type.ID)
            if (preceedingSign == null)
                return Values.Variable(p, t);
            else
                return new ASTNode<ASTNode<?>>(
                    name, preceedingSign,
                    Values.Variable(p, t)
                );

        // LPAREN binaryexpression RPAREN
        if (nextType == Type.LPAREN) {
            p.eat(Type.LPAREN);
            ASTNode<?> temp = BinaryExpression(p, t);
            p.eat(Type.RPAREN);

            if (preceedingSign == null)
                return temp;
            else
                return new ASTNode<ASTNode<?>>(
                    name, preceedingSign,
                    temp
                );
        }
        
        throw new TokenTypeException(nextType, "Variable, NumberLiteral, (BinaryExpression)", p.l.next().index);
    }

}
