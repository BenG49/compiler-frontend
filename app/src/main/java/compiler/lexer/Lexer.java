package compiler.lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private int line;
    private int index;

    private List<Token> tokenCache;
    private List<List<Integer>> posCache;

    public Lexer(String input) {
        if (input.endsWith("\n"))
            this.input = END.matcher(input);
        else
            this.input = END.matcher(input+"\n");
        
        tokenCache = new ArrayList<Token>();
        posCache = new ArrayList<List<Integer>>();
        line = 1;
        index = 0;
    }

    public Token next() {
        if (tokenCache.size() > 0) {
            Token out = tokenCache.get(0);
            line = posCache.get(0).get(0);
            index = posCache.get(0).get(1);

            tokenCache.remove(0);
            posCache.remove(0);
            return out;
        } else
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
        // Inc line count
        input.usePattern(Type.NEWLINE.getPattern());
        if (input.find()) {
            index(line+1, 0, cache);
            return new Token(Type.NEWLINE);
        }

        // Skip spaces
        input.usePattern(SPACE);
        if (input.find())
            index(line, index+1, cache);

        // Comments
        input.usePattern(Type.LINECOMMENT.getPattern());
        if (input.find())
            index(line+1, index, cache);

        input.usePattern(Type.BLOCKCOMMENT.getPattern());
        if (input.find())
            index(line+newLineCount(input.group()), index, cache);
        
        for (Type t : Type.getAllOf()) {
            input.usePattern(t.getPattern());
            if (input.find()) {
                String group = input.group();
                index(line, index+group.length(), cache);
                return new Token(t, group);
            }
        }

        // No match
        return null;
    }

    public boolean hasNext() {
        tokenCache.add(nextToken(true));
        return tokenCache.get(tokenCache.size()-1) != null;
    }

    public int[] getPos() {
        return new int[] {line, index};
    }
    
    private int newLineCount(String comment) {
        int out = 0;

        for (int i = 0; i < comment.length(); i++) {
            if (comment.charAt(i) == '\n')
                out++;
        }

        return out;
    }
    
    private void index(int line, int index, boolean cache) {
        if (cache)
            posCache.add(new ArrayList<Integer>(Arrays.asList(line, index)));
        else {
            this.line = line;
            this.index = index;
        }
    }

}
