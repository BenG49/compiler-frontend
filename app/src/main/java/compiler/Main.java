package compiler;

import compiler.exception.parse.LexException;
import compiler.exception.parse.ParseException;
import compiler.exception.semantics.SemanticException;
import compiler.lexer.Lexer;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.ASTNode;
import compiler.semantics.VarSemantics;

// TODO: improve lexer position, make it so that semantics can return position
public class Main {
    public static void main(String... args) {
        try {
            // testLexer(args[0]);
            // testParser(args[0]);
            testSemantic(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Must give a file path argument!");
        }
    }

    public static void testSemantic(String path) {
        Parser p = new Parser(Reader.readFile(path));
        try {
            ASTNode<?, ?> program = p.parse();
            VarSemantics.check(program);
            
            System.out.println("Abstract Syntax Tree:\n");
            StringBuilder out = new StringBuilder();
            program.printTree(out, "", "");
            System.out.println(out.toString());
        } catch (LexException | ParseException | SemanticException e) {
            System.out.println(e);
        }
    }

    public static void testParser(String path) {
        Parser p = new Parser(Reader.readFile(path));
        try {
            System.out.println("Abstract Syntax Tree:\n");
            StringBuilder out = new StringBuilder();
            p.parse().printTree(out, "", "");
            System.out.println(out.toString());
        } catch (LexException | ParseException e) {
            System.out.println(e);
        }
    }

    public static void testLexer(String path) {
        try {
            Lexer l = new Lexer(Reader.readFile(path));
            while (l.hasNext())
                System.out.println(l.next());
        } catch (LexException e) {
            System.out.println(e);
        }
    }
}
