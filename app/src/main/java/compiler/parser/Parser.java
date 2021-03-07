package compiler.parser;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.lexer.Token.Type;
import compiler.parser.grammars.Expressions;
import compiler.parser.grammars.Expressions.Program;

public class Parser {
    public String[] s;
    public Lexer l;

    public Parser() {}

    public Program parse(String s) throws Exception {
        return parse(s.split("\\n"));
    }
    public Program parse(String[] s) throws Exception {
        this.s = s;
        l = new Lexer(s);

        Expressions.setParser(this);
        return new Expressions.Program();
    }

    public String eat(Type type) throws Exception {
        Token t = l.next();
        if (t == null)
            throw new Exception("Unexpected end of input, expected: "+type);
        
        if (t.type != type)
            throw new Exception("Incorrect token type "+t.type+" given, expected: "+type);
        
        return t.value;
    }
}

abstract class Exp {
    public abstract String toString();

    public Exp() {}
}