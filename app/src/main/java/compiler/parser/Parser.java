package compiler.parser;

import compiler.exception.*;
import compiler.lexer.*;
import compiler.lexer.Token.Type;
import compiler.parser.grammars.*;

public class Parser {
    public String s;
    public Lexer l;

    public Parser(String s) {
        this.s = s;
    }

    public Node parse() throws ParseException {
        l = new Lexer(s);

        return Expressions.Program(this);
    }

    public int getStatementCount() {
        return s.split("\n").length;
    }

    public String tryNextToken(Type type) throws ParseException {
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