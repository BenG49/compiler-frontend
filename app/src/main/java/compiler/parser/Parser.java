package compiler.parser;

import compiler.lexer.*;
import compiler.lexer.Token.Type;
import compiler.parser.grammars.*;

public class Parser {
    public String[] s;
    public Lexer l;

    public Parser() {}

    public Node parse(String s) throws Exception {
        return parse(s.split("\\n"));
    }
    public Node parse(String[] s) throws Exception {
        this.s = s;
        l = new Lexer(s);

        Expressions.setParser(this);
        return Expressions.Program();
    }

    public String tryNextToken(Type type) throws Exception {
        Token t = l.next();
        if (t == null)
            throw new Exception("Unexpected end of input, expected: "+type);
        
        if (t.type != type)
            throw new Exception("Incorrect token type "+t.type+" given, expected: "+type);
        
        return t.value;
    }
}