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

        Expressions.setParser(this);
        return Expressions.Program();
    }

    public int getStatementCount() {
        return s.split("\n").length;
    }

    public String tryNextToken(Type type) throws ParseException {
        Token t = l.next();
        if (t == null)
            throw new EOFException(type+"");
        
        if (t.type != type)
            throw new TokenTypeException(t.type, type+"", l.getPos());
        
        return t.value;
    }
}