package compiler.parser.grammars.expressions;

import compiler.exception.parse.ParseException;
import compiler.syntax.Type;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.ASTValue;

public class Operators {
    /**
     * addsuboperator := PLUS
     *                 | MINUS
     */
    public static ASTValue<Type> AddSubOperator(Parser p) throws ParseException {
        Type nextType = p.l.nextType();

        p.eat(Type.PLUS, Type.MINUS);

        return new ASTValue<Type>(
            "AddSubOperator",
            nextType
        );
    }

    /**
     * muldivoperator := PLUS
     *                 | MINUS
     */
    public static ASTValue<Type> MulDivOperator(Parser p) throws ParseException {
        Type nextType = p.l.nextType();

        p.eat(Type.MUL, Type.DIV);

        return new ASTValue<Type>(
            "MulDivOperator",
            nextType
        );
    }
    
}
