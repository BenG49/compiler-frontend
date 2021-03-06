package compiler;

import compiler.lexer.Lexer;

public class Main {
    public static void main(String[] args) {
        System.out.println(Lexer.lex("100.5402/10.1*30+1f-1as"));
    }
}
