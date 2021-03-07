package compiler.lexer;

import java.util.HashMap;

import compiler.lexer.Token.Type;

public class Lexer {

    /**
     * TODO: make lexer support regex
     */

    private static final HashMap<String, Type> OPERATORS;
    private static final String DIGITS = "0123456789.";
    
    static {
        OPERATORS = new HashMap<String, Type>();
        OPERATORS.put("+", Type.PLUS);
        OPERATORS.put("-", Type.MINUS);
        OPERATORS.put("*", Type.MUL);
        OPERATORS.put("/", Type.DIV);
        OPERATORS.put("(", Type.LP);
        OPERATORS.put(")", Type.RP);
        OPERATORS.put(";", Type.SEMI);
    }

    private int line;
    private int index;
    private final String[] input;

    private int prevIndex;
    private int prevLine;

    public Lexer(String input) {
        this(input.split("\\n"));
    }
    public Lexer(String[] input) {
        index = 0;
        line = 0;
        this.input = input;
    }

    public Token next() throws Exception {
        // End of file
        if (!hasNext())
            return null;

        prevIndex = index;
        prevLine = line;
        String c = get(index);

        // Space
        if (c.equals(" ")) {
            inc();
            checkNextLine();
            return next();
        }

        // Operators
        if (OPERATORS.keySet().contains(c)) {
            inc();
            checkNextLine();
            return new Token(OPERATORS.get(c));
        }
            
        // Number
        if (DIGITS.contains(c)) {
            StringBuilder number = new StringBuilder();
            Type type = Type.INT;

            while (lineHasNext() && DIGITS.contains(get(index))) {
                if (get(index).equals("."))
                    type = Type.FLOAT;
                number.append(get(index));
                inc();
            }

            checkNextLine();
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
            
            checkNextLine();
            return new Token(Type.STRING, string.toString());
        }

        throw new Exception("Illegal character '"+c+"' at index "+index);
    }

    private String get(int index) {
        return Character.toString(input[line].charAt(index));
    }

    private void undo() {
        index = prevIndex;
        line = prevLine;
    }
    
    private void inc() {
        if (lineHasNext())
            index++;
        
        else if (hasNext()) {
            line++;
            index = 0;
        }
    }

    private void checkNextLine() {
        if (!lineHasNext() && hasNext()) {
            line++;
            index = 0;
        }
    }

    public boolean hasNext() {
        return line < input.length;
    }

    public boolean lineHasNext() {
        return index < input[line].length();
    }

    // TODO: cache the token
    public Type nextType() throws Exception {
        Type t =  next().type;
        undo();

        return t;
    }
}
