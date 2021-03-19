package interpreter.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import interpreter.syntax.Type;

public class Lexer {

    private static final Pattern END;
    private static final Pattern SPACE;
    
    static {
        END = Pattern.compile("\\G\\z");
        SPACE = Pattern.compile("\\G\\s+");
    }

    private final Matcher input;

    private int line;
    private int index;

    private List<Token> tokenCache;

    public Lexer(String input) {
        if (input.endsWith("\n"))
            this.input = END.matcher(input);
        else
            this.input = END.matcher(input+"\n");
        
        tokenCache = new ArrayList<Token>();
        line = 1;
        index = 0;
    }

    public Token next() {
        if (tokenCache.size() > 0)
            return tokenCache.remove(0);
        else
            return nextToken(false);
    }

    public Type nextType() {
        return nextType(1);
    }

    public Type nextType(int lookAheadCount) {
        if (lookAheadCount < 1)
            return null;

        if (tokenCache.size() < lookAheadCount) {
            int max = lookAheadCount-tokenCache.size();
            for (int i = 0; i < max; i++)
                tokenCache.add(nextToken(true));
        }

        return tokenCache.get(lookAheadCount-1).type;
    }

    private Token nextToken(boolean cache) {
        // TODO: empty line with spaces will not add a new line
        // Inc line count
        input.usePattern(Type.NEWLINE.getPattern());
        if (input.find()) {
            int prevIndex = index;
            line++;
            index = 0;
            return new Token(Type.NEWLINE, new int[] {line-1, prevIndex});
        }

        // Skip spaces
        input.usePattern(SPACE);
        if (input.find())
            index += input.group().length();

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
                int prevIndex = index;
                index += group.length();
                return new Token(t, group, new int[] {line, prevIndex});
            }
        }

        // No match
        return null;
    }

    public boolean hasNext() {
        tokenCache.add(nextToken(true));
        return tokenCache.get(tokenCache.size()-1) != null;
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
