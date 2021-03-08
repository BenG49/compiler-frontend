package compiler.lexer;

import java.util.HashMap;

import compiler.exception.*;
import compiler.lexer.Token.Type;

public class Lexer {

    /**
     * TODO: make lexer support regex
     */

    private static final HashMap<String, Type> OPERATORS;
    private static final String DIGITS;
    
    static {
        OPERATORS = new HashMap<String, Type>();
        OPERATORS.put("+", Type.PLUS);
        OPERATORS.put("-", Type.MINUS);
        OPERATORS.put("*", Type.MUL);
        OPERATORS.put("/", Type.DIV);
        OPERATORS.put("(", Type.LP);
        OPERATORS.put(")", Type.RP);
        OPERATORS.put(";", Type.SEMI);

        DIGITS = "0123456789.";
    }

    private int line;
    private int index;
    private final String input;

    private Token nextToken;

    public Lexer(String input) throws CompileException {
        index = 0;
        line = 0;
        // add newline at the end because newlines end statements
        this.input = (input+"\n");

        nextToken = nextToken();
    }

    public Token next() throws CompileException {
        Token out = new Token(nextToken);
        nextToken = nextToken();
        return out;
    }

    private Token nextToken() throws CompileException {
        // End of file
        if (!privateHasNext())
            return null;

        String c = get(index);

        // Newline
        if (c.equals("\n")) {
            index++;
            line++;
            return new Token(Type.NEWLINE);
        }

        // Space
        if (c.equals(" ")) {
            index++;
            return nextToken();
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

            while (privateHasNext() && DIGITS.contains(get(index))) {
                if (get(index).equals("."))
                    type = Type.FLOAT;
                number.append(get(index));
                index++;
            }

            return new Token(type, number.toString());
        }
            
        // String
        if (c.equals("\"")) {
            StringBuilder string = new StringBuilder();
            // pass first quote
            index++;
            string.append("\"");
            
            while (privateHasNext() && !get(index).equals("\""))
                string.append(get(index++));

            if (!privateHasNext() && !get(index-1).equals("\""))
                throw new EOFException("\"");

            // pass second quote (so lexer doesn't start on ending quote)
            index++;
            string.append("\"");
            
            return new Token(Type.STRING, string.toString());
        }

        throw new IllegalCharacterException(c, index, line);
    }

    private String get(int index) {
        return Character.toString(input.charAt(index));
    }

    private boolean privateHasNext() {
        return index < input.length();
    }

    public boolean hasNext() {
        return privateHasNext() || nextToken != null;
    }

    public Type nextType() throws CompileException {
        if (hasNext())
            return nextToken.type;
        else
            return null;
    }

    public int[] getCursor() {
        return new int[] {index, line};
    }
}
