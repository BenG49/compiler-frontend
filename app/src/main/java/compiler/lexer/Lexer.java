package compiler.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compiler.lexer.Token.Type;

public class Lexer {

    private static final HashMap<String, Type> OPERATORS;
    private static final String DIGITS = "0123456789.f";

    static {
        OPERATORS = new HashMap<String, Type>();
        OPERATORS.put("+", Type.PLUS);
        OPERATORS.put("-", Type.MINUS);
        OPERATORS.put("*", Type.MUL);
        OPERATORS.put("/", Type.DIV);
        OPERATORS.put("(", Type.LPAREN);
        OPERATORS.put(")", Type.RPAREN);
    }

    public static List<Token> lex(String input) {
        // remove all spaces
        String filtered = input.replaceAll(" ", "");
        List<Token> output = new ArrayList<Token>();

        // num buffer
        StringBuilder currentElement = new StringBuilder();
        int dotCount = 0;

        for (int i = 0; i < filtered.length(); i++) {
            String c = Character.toString(filtered.charAt(i));

            // if operator
            if (OPERATORS.keySet().contains(c)) {
                // clear current num buffer
                if (currentElement.length() > 0) {
                    output.add(stringToToken(currentElement.toString(), dotCount));
                    currentElement.setLength(0);
                    dotCount = 0;
                }

                // add operator to output
                output.add(new Token(OPERATORS.get(c)));
            // if digit
            } else if (DIGITS.contains(c)) {
                // add to num buffer
                currentElement.append(c);
                // increment dotcount
                if (c.equals("."))
                    dotCount++;
            }

            // clear num buffer if reached end of input
            if (i == filtered.length()-1 && currentElement.length() > 0)
                output.add(stringToToken(currentElement.toString(), dotCount));
        }

        return output;
    }

    private static Token stringToToken(String in, int dotCount) {
        if (dotCount > 1)
            return new Token(Type.ERROR, in);

        if (in.contains("f") || dotCount == 1)
            return new Token(Type.FLOAT, in);

        return new Token(Type.INT, in);
    }
}
