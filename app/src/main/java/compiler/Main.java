package compiler;

import compiler.exception.parse.ParseException;
import compiler.lexer.Lexer;
import compiler.parser.Parser;

public class Main {
    private static final String PATH = "/home/bg/code";
    public static void main(String[] args) {
        // testLexer();
        testParser();
    }

    public static void testParser() {
        Parser p = new Parser(Reader.readFile(PATH));
        try {
            System.out.println("Abstract Syntax Tree:\n");
            System.out.println(p.parse());
        } catch (ParseException e) {
            System.out.println(e);
        }
    }

    public static void testLexer() {
        try {
            Lexer l = new Lexer(Reader.readFile(PATH));
            while (l.hasNext())
                System.out.println(l.next());
        } catch (ParseException e) {
            System.out.println(e);
        }
    }
}
