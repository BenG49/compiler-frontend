package compiler.lexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compiler.exception.parse.ParseException;
import compiler.syntax.Type;

public class Lexer {

    private static final Pattern END;
    private static final Pattern SPACE;
    
    static {
        END = Pattern.compile("\\G\\z");
        SPACE = Pattern.compile("\\G\\s+");
    }

    private final Matcher input;

    private Token nextToken;
    // TODO: make it so that line count is incremented in multiline comments, strings, etc
    private int line;
    private int index;

    public Lexer(String input) throws ParseException {
        if (input.endsWith("\n"))
            this.input = END.matcher(input);
        else
            this.input = END.matcher(input+"\n");
        
        nextToken = nextToken();
    }

    public Token next() throws ParseException {
        Token out = new Token(nextToken);
        nextToken = nextToken();
        return out;
    }

    public Type nextType() {
        return nextToken.type;
    }

    private Token nextToken() throws ParseException {
        // Inc line count
        input.usePattern(Type.NEWLINE.getPattern());
        if (input.find()) {
            line++;
            index = 0;
            return new Token(Type.NEWLINE);
        }

        // Skip spaces
        input.usePattern(SPACE);
        if (input.find())
            index++;

        // Comments
        input.usePattern(Type.LINECOMMENT.getPattern());
        input.find();
        input.usePattern(Type.BLOCKCOMMENT.getPattern());
        input.find();
        
        for (Type t : Type.getAllOf()) {
            input.usePattern(t.getPattern());
            if (input.find()) {
                String group = input.group();
                index += group.length();
                return new Token(t, group);
            }
        }

        // No match
        return null;
    }

    public boolean hasNext() {
        return nextToken != null;
    }

    public int[] getPos() {
        return new int[] {line+1, index};
    }
}
