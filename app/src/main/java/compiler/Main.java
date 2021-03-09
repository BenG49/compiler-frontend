package compiler;

import compiler.exception.CompileException;
import compiler.lexer.Lexer;
import compiler.parser.Parser;

public class Main {
    private static final String PATH = "/home/bg/code";
    public static void main(String[] args) {
        testLexer();
    }

    public static void testParser() {
        Parser p = new Parser();
        try {
            System.out.println(p.parse(Reader.readFile(PATH)));
        } catch (CompileException e) {
            System.out.println(e);
        }
    }

    public static void testLexer() {
        try {
            Lexer l = new Lexer(Reader.readFile(PATH));
            while (l.hasNext())
                System.out.println(l.next());
        } catch (CompileException e) {
            System.out.println(e);
        }
    }
}
