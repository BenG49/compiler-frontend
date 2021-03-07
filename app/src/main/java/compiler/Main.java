package compiler;

import java.util.HashMap;

import compiler.bnf.Symbols;
import compiler.lexer.Lexer;

public class Main {
    public static void main(String[] args) {
        // Lexer lexer = new Lexer("1+1");
        // try {
        //     System.out.println(lexer.lex());
        // } catch (Exception e) {
        //     System.out.println(e);
        // }
        HashMap<String, String> symbols = new HashMap<String, String>();
        symbols.put("<postal-address>", "<name-part> <street-address> '.' <zip-part>");
        Symbols lang = new Symbols(symbols);
        lang.evalSymbol("<postal-address>");
    }
}
