package compiler.parser.grammars.expressions;

import compiler.syntax.Type;
import compiler.exception.CompileException;
import compiler.parser.Parser;

public class Operators {
    /**
     * assignoperator := PLUS
     *                       PLUS
     *                     | EQUALS
     *                 | MINUS
     *                       MINUS
     *                     | EQUALS
     *                 | MUL EQUALS
     *                 | DIV EQUALS
     */
    public static Type[] AssignOperator(Parser p) throws CompileException {
        Type firstType = p.l.nextType();

        p.eat(Type.PLUS, Type.MINUS, Type.MUL, Type.DIV);

        Type secondType = p.l.nextType();

        if (firstType.within(Type.DIV, Type.MUL))
            p.eat(Type.EQUAL);
        else 
            p.eat(firstType, Type.EQUAL);
        
        return new Type[] {firstType, secondType};
    }
    
}
