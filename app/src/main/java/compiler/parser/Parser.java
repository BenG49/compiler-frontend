package compiler.parser;

import compiler.exception.*;
import compiler.lexer.*;
import compiler.lexer.Token.Type;
import compiler.parser.grammars.*;

public class Parser {
    public String s;
    public Lexer l;

    public Parser() {}

    public Node parse(String s) throws CompileException {
        this.s = s;
        l = new Lexer(s);

        Expressions.setParser(this);
        return Expressions.Program();
    }

    public int getStatementCount() {
        return s.split("\n").length;
    }

    public String tryNextToken(Type type) throws CompileException {
        Token t = l.next();
        if (t == null)
            throw new EOFException(type+"");
        
        if (t.type != type)
            throw new TokenTypeException(t.type, type+"");
        
        return t.value;
    }
}