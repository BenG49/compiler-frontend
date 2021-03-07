package compiler.parser;

import compiler.lexer.Lexer;
import compiler.lexer.Token;
import compiler.lexer.Token.Type;

public class Parser {
    private String[] s;
    private Lexer l;

    public Parser() {}

    public Exp parse(String s) throws Exception {
        return parse(s.split("\\n"));
    }
    public Exp parse(String[] s) throws Exception {
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
            public Exp body = Literal();
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
    private Exp Literal() throws Exception {
        Type nextType = l.nextType();
        if (nextType == Type.INT)
            return IntLiteral();
        else if (nextType == Type.STRING)
            return StringLiteral();
        else
            throw new Exception("Incorrect token type "+nextType+" given, expected Literal");
    }

    /**
     * 
     */
    private Exp StringLiteral() throws Exception {
        return new Exp() {
            public String type = "StringLiteral";
            public String value = eat(Type.STRING);
            @Override public String toString() {
                return "\ttype: "+type+"\n\tvalue: "+value; }
        };
    }

    /**
     * IntLiteral
     *  : INT
     *  ;
     */
    private Exp IntLiteral() throws Exception {
        return new Exp() {
            public String type = "IntLiteral";
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
            throw new Exception("Incorrect token type "+t.type+" given, expected: "+type);
        
        return t.value;
    }
}

abstract class Exp {
    public abstract String toString();
}