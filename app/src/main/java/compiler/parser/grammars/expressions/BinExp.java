package compiler.parser.grammars.expressions;

import compiler.exception.parse.TokenTypeException;
import compiler.exception.semantics.InvalidTypeException;
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
        boolean preceedingMinus = false;

        if (nextType == Type.MINUS) {
            preceedingMinus = true;
            p.eat(Type.MINUS);
            
            nextType = p.l.nextType();
        }

        // numberliteral
        if (nextType.within(Type.INT, Type.FLOAT)) {
            if (preceedingMinus)
                return new ASTNode<ASTNode<?>>(
                    name, Type.MINUS,
                    Values.NumberLiteral(p)
                );
            else
                return Values.NumberLiteral(p);
        }

        // variable/function
        if (nextType == Type.ID) {
            ASTNode<?> temp;
            Type assignType;
            if (p.l.nextType(2) == Type.LPAREN) {
                temp = Expressions.FunctionCall(p, t);
                assignType = temp.operator;
            } else {
                temp = Values.Variable(p, t);
                assignType = t.vget((String)temp.branches.get(0)).type;
            }

            if (!assignType.within(Type.INT_ID, Type.FLOAT_ID))
                throw new InvalidTypeException(p.l.next().index, assignType, Type.INT, Type.FLOAT);

            if (preceedingMinus)
                return new ASTNode<ASTNode<?>>(
                    name, Type.MINUS, temp
                );
            else
                return temp;
        }

        // LPAREN binaryexpression RPAREN
        if (nextType == Type.LPAREN) {
            p.eat(Type.LPAREN);
            ASTNode<?> temp = BinaryExpression(p, t);
            p.eat(Type.RPAREN);

            if (preceedingMinus)
                return new ASTNode<ASTNode<?>>(
                    name, Type.MINUS, temp
                );
            else
                return temp;
        }
        
        throw new TokenTypeException(nextType, "Variable, NumberLiteral, (BinaryExpression)", p.l.next().index);
    }

}
