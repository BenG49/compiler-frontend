package compiler.parser;

import compiler.exception.parse.*;
import compiler.lexer.*;
import compiler.syntax.Type;
import compiler.parser.grammars.ast.AST;
import compiler.parser.grammars.expressions.Expressions;

public class Parser {
    public String s;
    public Lexer l;

    public Parser(String s) {
        this.s = s;
    }

    public AST parse() throws ParseException {
        l = new Lexer(s);

        return Expressions.Program(this);
    }

    public String eat(Type type) throws ParseException {
        return tryNextToken(new Type[] {type});
    }
    public String tryNextToken(Type... type) throws ParseException {
        Token token = l.next();
        if (token == null)
            throw new EOFException(type+"");
        
        boolean match = false;
        for (Type t:type) {
            if (token.type == t)
                match = true;
        }

        if (!match)
            throw new TokenTypeException(token.type, type, l.getPos());
        
        return token.value;
    }
}