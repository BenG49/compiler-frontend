package compiler.lexer;

import java.util.EnumSet;
import java.util.regex.Pattern;

public class Token {
    public enum Type {
        // https://regexr.com

        BLOCKCOMMENT(   "\\G\\/\\*(\\*(?!\\/)|[^*])*\\*\\/"),
        // TODO: does not remove newline character
        LINECOMMENT(    "\\G\\/\\/.*+\n"),

        INT(    "\\G\\d+"),
        FLOAT(  "\\G\\d+[.]\\d+"),
        // https://stackoverflow.com/questions/171480/regex-grabbing-values-between-quotation-marks
        STR( "\\G\"(.*?)\""),
        PLUS(   "\\G\\+"),
        MINUS(  "\\G\\-"),
        MUL(    "\\G\\*"),
        DIV(    "\\G/"),
        EQUALS( "\\G="),
        LP(     "\\G\\("),
        RP(     "\\G\\)"),
        LB(     "\\G\\{"),
        RB(     "\\G\\}"),
        
        // reserved words
        INT_ID("\\G(int)"),
        FLOAT_ID("\\G(float)"),
        STRING_ID("\\G(str)"),
        IF("\\G(if)"),
        ELSE("\\G(if)"),

        NEWLINE("\\G(\r\n|\r|\n)"),

        VAR("\\G[A-z]+[A-z\\d]*");

        private static final EnumSet<Type> allOf;

        static {
            allOf = EnumSet.allOf(Type.class);
        }

        private String regex;

        Type(String regex) {
            this.regex = regex;
        }

        public Pattern getPattern() {
            return Pattern.compile(regex);
        }

        public static EnumSet<Type> getAllOf() {
            return allOf;
        }
    };

    public Type type;
    public String value;

    public Token(Token other) {
        this.type = other.type;
        this.value = other.value;
    }

    public Token(Type type) { this(type, ""); }
    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (value.length() > 0) {
            sb.append(":");
            sb.append(value);
        }

        return sb.toString();
    }
}
