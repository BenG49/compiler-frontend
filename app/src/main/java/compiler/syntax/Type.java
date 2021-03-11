package compiler.syntax;

import java.util.EnumSet;
import java.util.regex.Pattern;

public enum Type {
    // https://regexr.com

    BLOCKCOMMENT(   "\\G\\/\\*(\\*(?!\\/)|[^*])*\\*\\/"),
    // TODO: does not remove newline character
    LINECOMMENT(    "\\G\\/\\/.*+\n"),

    INT(    "\\G\\d+"),
    FLOAT(  "\\G\\d+[.]\\d+"),
    // https://stackoverflow.com/questions/171480/regex-grabbing-values-between-quotation-marks
    STR(    "\\G\"(.*?)\""),

    EQUIVALENT( "\\G(==)"),
    LESS_EQUAL( "\\G(<=)"),
    GREATER_EQUAL(  "\\G(>=)"),
    LESS(   "\\G<"),
    GREATER("\\G>"),
    // TODO: add equals set
    // if (a =| 1, 3, 5) -> if a is equal to 1, 3, or 5
    OR(     "\\G(\\|\\|)"),
    AND(    "\\G(\\&\\&)"),
    NOT(    "\\G!"),

    PLUS(   "\\G\\+"),
    MINUS(  "\\G\\-"),
    MUL(    "\\G\\*"),
    DIV(    "\\G/"),
    EQUAL(  "\\G="),
    // TODO: add +=,  -=, ++, --, *=, **, etc

    LPAREN( "\\G\\("),
    RPAREN( "\\G\\)"),
    LB(     "\\G\\{"),
    RB(     "\\G\\}"),
    
    // reserved words
    INT_ID( "\\G(int)"),
    FLOAT_ID("\\G(float)"),
    STR_ID( "\\G(str)"),
    IF(     "\\G(if)"),
    ELSE(   "\\G(else)"),
    TRUE(   "\\G(true)"),
    FALSE(  "\\G(false)"),
    WHILE(  "\\G(while)"),
    FOR(    "\\G(for)"),

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

    public static Type[] getVarTypes() {
        return new Type[] { INT_ID, FLOAT_ID, STR_ID };
    }

    public boolean within(Type... types) {
        for (Type t:types) {
            if (this == t)
                return true;
        }
        
        return false;
    }
}
