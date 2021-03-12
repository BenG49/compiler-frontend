package compiler.syntax;

import java.util.EnumSet;
import java.util.regex.Pattern;

public enum Type {
    // https://regexr.com

    BLOCKCOMMENT(p("\\G\\/\\*(\\*(?!\\/)|[^*])*\\*\\/")),
    LINECOMMENT(p("\\G\\/\\/.*+\n")),

    FLOAT(  p("\\G\\d+\\.\\d+")),
    INT(    p("\\G\\d+")),
    // https://stackoverflow.com/questions/171480/regex-grabbing-values-between-quotation-marks
    STR(    p("\\G\"(.*?)\"")),

    EQUIVALENT( p("\\G(==)")),
    LESS_EQUAL( p("\\G(<=)")),
    GREATER_EQUAL(  p("\\G(>=)")),
    LESS(   p("\\G<")),
    GREATER(p("\\G>")),
    // if (a =| 1, 3, 5) -> if a is equal to 1, 3, or 5
    OR(     p("\\G(\\|\\|)")),
    AND(    p("\\G(\\&\\&)")),
    NOT(    p("\\G!")),

    EXP(    p("\\G(\\*\\*)")),
    EQUAL(  p("\\G=")),
    MUL(    p("\\G\\*")),
    DIV(    p("\\G/")),
    PLUS(   p("\\G\\+")),
    MINUS(  p("\\G\\-")),

    LPAREN( p("\\G\\(")),
    RPAREN( p("\\G\\)")),
    LB(     p("\\G\\{")),
    RB(     p("\\G\\}")),
    COMMA(  p("\\G,")),
    
    // reserved words
    INT_ID( p("\\G(int)")),
    FLOAT_ID(p("\\G(float)")),
    STR_ID( p("\\G(str)")),
    BOOL_ID(p("\\G(bool)")),
    IF(     p("\\G(if)")),
    ELSE(   p("\\G(else)")),
    TRUE(   p("\\G(true)")),
    FALSE(  p("\\G(false)")),
    WHILE(  p("\\G(while)")),
    FOR(    p("\\G(for)")),
    VOID(   p("\\G(void)")),
    RETURN( p("\\G(return)")),

    NEWLINE(p("\\G(\r\n|\r|\n)")),

    FUNC(   p("\\G[A-z]+[A-z\\d]*\\s*\\(")),
    VAR(    p("\\G[A-z]+[A-z\\d]*"));

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
        return Pattern.compile(s);
    }
}
