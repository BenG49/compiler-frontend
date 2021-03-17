package compiler.syntax;

import java.util.EnumSet;
import java.util.regex.Pattern;

public enum Type {
    // https://regexr.com

    BLOCKCOMMENT(p("\\/\\*(\\*(?!\\/)|[^*])*\\*\\/(\r\n|\r|\n)*")),
    LINECOMMENT(p("\\/\\/.*+(\r\n|\r|\n)")),

    FLOAT(  p("\\d+\\.\\d+")),
    INT(    p("\\d+")),
    // https://stackoverflow.com/questions/171480/regex-grabbing-values-between-quotation-marks
    STR(    p("\"(.*?)\"")),

    EQUIVALENT( p("(==)")),
    LESS_EQUAL( p("(<=)")),
    GREATER_EQUAL(  p("(>=)")),
    LESS(   p("<")),
    GREATER(p(">")),
    // if (a =| 1, 3, 5) -> if a is equal to 1, 3, or 5
    OR(     p("(\\|\\|)")),
    AND(    p("(\\&\\&)")),
    NOT(    p("!")),

    EXP(    p("(\\*\\*)")),
    EQUAL(  p("=")),
    MUL(    p("\\*")),
    DIV(    p("/")),
    PLUS(   p("\\+")),
    MINUS(  p("\\-")),

    LPAREN( p("\\(")),
    RPAREN( p("\\)")),
    LB(     p("\\{")),
    RB(     p("\\}")),
    COMMA(  p(",")),
    
    // reserved words
    INT_ID( p("(int)")),
    FLOAT_ID(p("(float)")),
    STR_ID( p("(str)")),
    BOOL_ID(p("(bool)")),
    IF(     p("(if)")),
    ELSE(   p("(else)")),
    TRUE(   p("(true)")),
    FALSE(  p("(false)")),
    WHILE(  p("(while)")),
    FOR(    p("(for)")),
    VOID(   p("(void)")),
    RETURN( p("(return)")),

    NEWLINE(p("(\r\n|\r|\n)")),

    // TODO: this is horrible, use lookahead instead
    ID(    p("[A-z]+[A-z\\d]*"));

    private static final EnumSet<Type> allOf;

    static {
        allOf = EnumSet.allOf(Type.class);
    }

    private Pattern regex;

    Type(Pattern regex) {
        this.regex = regex;
    }

    public Pattern getPattern() {
        return regex;
    }

    public static EnumSet<Type> getAllOf() {
        return allOf;
    }

    public static Type[] getVarTypes() {
        return new Type[] { INT_ID, FLOAT_ID, STR_ID, BOOL_ID };
    }

    public boolean within(Type... types) {
        for (Type t:types) {
            if (this == t)
                return true;
        }
        
        return false;
    }

    private static Pattern p(String s) {
        return Pattern.compile("\\G"+s);
    }
}
