package compiler.lexer;

import java.util.HashMap;

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

    public Lexer(String input) {
        index = 0;
        this.input = input;
    }

    public Token next() throws Exception {
        if (!hasNext())
            return null;

        String c = get(index);

        // Space
        if (c.equals(" ")) {
            index++;
            return next();
        }

        // Operators
        if (OPERATORS.keySet().contains(c)) {
            index++;
            return new Token(OPERATORS.get(c));
        }
            
        // Number
        if (DIGITS.contains(c)) {
            StringBuilder number = new StringBuilder();
            Type type = Type.INT;

            while (hasNext() && DIGITS.contains(get(index))) {
                if (get(index).equals("."))
                    type = Type.FLOAT;
                number.append(get(index++));
            }

            return new Token(type, number.toString());
        }
            
        // String
        if (c.equals("\"")) {
            StringBuilder string = new StringBuilder();
            // pass first quote
            index++;
            string.append("\"");
            
            while (hasNext() && !get(index).equals("\""))
                string.append(get(index++));

            if (!hasNext() && !get(index-1).equals("\""))
                throw new Exception("Unexpected end of file, expected '\"'");

            // pass second quote (so lexer doesn't start on ending quote)
            index++;
            string.append("\"");
            
            return new Token(Type.STRING, string.toString());
        }

        throw new Exception("Illegal character '"+c+"' at index "+index);
    }

    private String get(int index) {
        return Character.toString(input.charAt(index));
    }

    public boolean hasNext() {
        return index < input.length();
    }
}
