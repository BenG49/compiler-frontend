package compiler;

import compiler.lexer.Lexer;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("1+1");
        try {
            System.out.println(lexer.lex());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
