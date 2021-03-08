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
    // TODO: maybe add indentations

    /**
     * Program
     *  : StatementList
     *  ;
     */
    public static class Program extends Exp {
        public String type = "Program";
        public Exp body;
        public Program() throws Exception {
            body = new Expressions.StatementList(); }
        @Override public String toString() {
            return type+" {\n"+body+"\n}"; }
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
            sb.append(type);
            for (int i = 0; i < body.size(); i++) {
                sb.append("{\n");
                sb.append(body.get(i));
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
    public static class Statement extends Exp {
        public String type = "Statement";
        public Exp body;
        public Statement() throws Exception {
            body = new Expressions.Literal(); }
        @Override public String toString() {
            return type+" {\n"+body+"\n}"; }
    }

    /**
     * Literal
     *  : IntLiteral
     *  | StringLiteral
     *  | FloatLiteral
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
            else if (nextType == Type.FLOAT)
                body = new Expressions.FloatLiteral();
            else
                throw new Exception("Incorrect token type "+nextType+" given, expected Literal");
        }
        @Override public String toString() {
            return type+" {\n"+body+"\n}"; }
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
            value = p.tryNextToken(Type.STRING); }
        @Override public String toString() {
            return type+", "+value; }
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
            value = Integer.parseInt(p.tryNextToken(Type.INT)); }
        @Override public String toString() {
            return type+", "+value; }
    }

    /**
     * FloatLiteral
     *  : FLOAT
     *  ;
     */
    public static class FloatLiteral extends Exp {
        public String type = "FloatLiteral";
        public float value;
        public FloatLiteral() throws Exception {
            value = Float.parseFloat(p.tryNextToken(Type.FLOAT)); }
        @Override public String toString() {
            return type+", "+value; }
    }
    
}
