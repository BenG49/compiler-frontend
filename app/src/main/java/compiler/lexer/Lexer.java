package compiler.lexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Lexer(String input) {
        if (input.endsWith("\n"))
            this.input = END.matcher(input);
        else
            this.input = END.matcher(input+"\n");
        
        nextToken = nextToken();
    }

    public Token next() {
        Token out = new Token(nextToken);
        nextToken = nextToken();
        return out;
    }

    public Type nextType() {
        return nextToken.type;
    }

    private Token nextToken() {
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
        if (input.find())
            line++;
        input.usePattern(Type.BLOCKCOMMENT.getPattern());
        if (input.find())
            line += newLineCount(input.group());
        
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
    
    private int newLineCount(String comment) {
        int out = 0;

        for (int i = 0; i < comment.length(); i++) {
            if (comment.charAt(i) == '\n')
                out++;
        }

        return out;
    }
}
