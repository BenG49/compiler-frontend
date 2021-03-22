package compiler;

import compiler.codegeneration.ASM;
import compiler.exception.CompileException;
import compiler.lexer.Lexer;
import compiler.parser.Parser;

public class Main {
    public static void main(String... args) {
        try {
            // testLexer(args[0]);
            // testParser(args[0]);
            ASM.test();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Must give a file path argument!");
        }
    }

    public static void testParser(String path) {
        Parser p = new Parser(Reader.readFile(path));
        try {
            System.out.println("Abstract Syntax Tree:\n");
            StringBuilder out = new StringBuilder();
            p.parse().printTree(out, "", "");
            System.out.println(out.toString());
        } catch (CompileException e) {
            System.out.println(e);
            // e.printStackTrace();
        }
    }

    public static void testLexer(String path) {
        Lexer l = new Lexer(Reader.readFile(path));
        while (l.hasNext())
            System.out.println(l.next());
    }
}
