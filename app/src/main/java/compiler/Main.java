package compiler;

import compiler.lexer.Lexer;
import compiler.parser.Parser;

public class Main {
    public static void main(String[] args) {
        Parser p = new Parser();
        try {
            System.out.println(p.parse(String.join("\n"
            , "10"
            , "\"a\"")));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
