package compiler;

import compiler.parser.Parser;

public class Main {
    public static void main(String[] args) {
        Parser p = new Parser();
        try {
            System.out.println(p.parse(String.join("\n"
            , "\"a\";"
            , "10;")));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
