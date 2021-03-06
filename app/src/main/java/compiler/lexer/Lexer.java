package compiler.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import compiler.lexer.Token.Type;

public class Lexer {

    private static final HashMap<String, Type> OPERATORS;
    private static final String DIGITS = "0123456789.";

    static {
        OPERATORS = new HashMap<String, Type>();
        OPERATORS.put("+", Type.PLUS);
        OPERATORS.put("-", Type.MINUS);
        OPERATORS.put("*", Type.MUL);
        OPERATORS.put("/", Type.DIV);
        OPERATORS.put("(", Type.LPAREN);
        OPERATORS.put(")", Type.RPAREN);
    }

    private int index;
    private final String input;

    private StringBuilder numBuffer;
    private int dotCount;

    public Lexer(String input) {
        index = 0;
        this.input = input;

        numBuffer = new StringBuilder();
        dotCount = 0;
    }

    public List<Token> lex() throws Exception {
        // remove all spaces
        String filtered = input.replaceAll(" ", "");
        List<Token> output = new ArrayList<Token>();

        while (index < filtered.length()) {
            String c = Character.toString(filtered.charAt(index));

            // if operator
            if (OPERATORS.keySet().contains(c)) {
                // clear current num buffer
                if (numBuffer.length() > 0)
                    emptyNumBuffer(output);

                // add operator to output
                output.add(new Token(OPERATORS.get(c)));
            // if digit
            } else if (DIGITS.contains(c)) {
                // add to num buffer
                numBuffer.append(c);
                // increment dotcount
                if (c.equals("."))
                    dotCount++;
                // multiple dots
                if (dotCount > 1)
                    throw new IllegalCharacterException(c, index);
            } else
                throw new IllegalCharacterException(c, index);

            // clear num buffer if reached end of input
            if (index == filtered.length()-1 && numBuffer.length() > 0)
                emptyNumBuffer(output);
            
            index++;
        }

        return output;
    }

    private void emptyNumBuffer(List<Token> output) {
        output.add(stringToToken(numBuffer.toString(), dotCount));
        numBuffer.setLength(0);
        dotCount = 0;
    }

    private static Token stringToToken(String in, int dotCount) {
        if (dotCount == 1)
            return new Token(Type.FLOAT, in);

        return new Token(Type.INT, in);
    }
}
