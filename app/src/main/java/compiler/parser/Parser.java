package compiler.parser;

import java.util.List;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.lexer.Token.Type;

public class Parser {
    private String s;
    private Lexer l;

    public Parser() {}

    public Exp parse(String s) throws Exception {
        this.s = s;
        l = new Lexer(s);

        return Program();
    }

    /**
     * Program
     *  : Literal
     *  ;
     */
    private Exp Program() throws Exception {
        return new Exp() {
            public String type = "Program";
            public Exp body = IntLiteral();
            @Override public String toString() {
                return "type: "+type+"\nbody: {\n"+body.toString()+"\n}";
            }
        };
    }

    /**
     * Literal
     *  : IntLiteral
     *  | StringLiteral
     *  ;
     */

    /**
     * IntLiteral
     *  : INT
     *  ;
     */
    private Exp IntLiteral() throws Exception {
        return new Exp() {
            public String type = "NumericLiteral";
            public int value = Integer.parseInt(eat(Type.INT));
            @Override public String toString() {
                return "\ttype: "+type+"\n\tvalue: "+value; }
        };
    }

    private String eat(Type type) throws Exception {
        Token t = l.next();
        if (t == null)
            throw new Exception("Unexpected end of input, expected: "+type);
        
        if (t.type != type)
            throw new Exception("Incorrect token "+t.type+" given, expected: "+type);
        
        return t.value;
    }
}

abstract class Exp {
    public abstract String toString();
}