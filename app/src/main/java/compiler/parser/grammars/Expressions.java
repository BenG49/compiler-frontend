package compiler.parser.grammars;

import java.util.ArrayList;
import java.util.List;

import compiler.lexer.Token.Type;
import compiler.parser.Parser;

public class Expressions {
    private static Parser p;

    public static void setParser(Parser p) {
        Expressions.p = p;
    }

    /**
     * Program
     *  : StatementList
     *  ;
     */
    public static class Program {
        public String type = "Program";
        public Exp body;
        public Program() throws Exception {
            body = new Expressions.StatementList(); }
        @Override public String toString() {
            return "type: "+type+"\nbody: {\n"+body+"\n}"; }
    }

    /**
     * StatementList
     *  : Statement
     *  | Statement
     *  ...
     *  ;
     */
    public static class StatementList extends Exp {
        public String type = "StatementList";
        public List<Statement> body;
        public StatementList() throws Exception {
            body = new ArrayList<Statement>();
            for (int i = 0; i < p.s.length; i++)
                body.add(new Expressions.Statement());
        }
        @Override public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("type: ");
            sb.append(type);
            sb.append("\nbody");
            for (Statement s : body) {
                sb.append("{\n");
                sb.append(s);
                sb.append("\n}");
            }

            return sb.toString();
        }
    }

    /**
     * Statement
     *  : Literal
     *  ;
     */
    public static class Statement {
        public String type = "Statement";
        public Exp body;
        public String semi;
        public Statement() throws Exception {
            body = new Expressions.Literal();
            semi = p.eat(Type.SEMI); }
        @Override public String toString() {
            return "type: "+type+"\nbody: {\n"+body+"\n}"; }
    }

    /**
     * Literal
     *  : IntLiteral
     *  | StringLiteral
     *  ;
     */
    public static class Literal extends Exp {
        public String type = "Literal";
        public Exp body;
        public Literal() throws Exception {
            Type nextType = p.l.nextType();
            if (nextType == Type.INT)
                body = new Expressions.IntLiteral();
            else if (nextType == Type.STRING)
                body = new Expressions.StringLiteral();
            else
                throw new Exception("Incorrect token type "+nextType+" given, expected Literal");
        }
        @Override public String toString() {
            return "type: "+type+"\nbody: {\n"+body+"\n}"; }
    }

    /**
     * StringLiteral
     *  : STRING
     *  ;
     */
    public static class StringLiteral extends Exp {
        public String type = "StringLiteral";
        public String value;
        public StringLiteral() throws Exception {
            value = p.eat(Type.STRING); }
        @Override public String toString() {
            return "\ttype: "+type+"\nvalue: "+value; }
    }

    /**
     * IntLiteral
     *  : INT
     *  ;
     */
    public static class IntLiteral extends Exp {
        public String type = "IntLiteral";
        public int value;
        public IntLiteral() throws Exception {
            value = Integer.parseInt(p.eat(Type.INT)); }
        @Override public String toString() {
            return "\ttype: "+type+"\nvalue: "+value; }
    }
}
