package compiler;

import compiler.exception.CompileException;
import compiler.lexer.Lexer;
import compiler.parser.Parser;

public class Main {
    public static void main(String[] args) {
        Parser p = new Parser();
        try {
            System.out.println(p.parse(String.join("\n"
                , "10+10"
            )));
        } catch (CompileException e) {
            System.out.println(e);
        }
    }
}
