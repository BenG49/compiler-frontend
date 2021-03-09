package compiler.lexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compiler.exception.CompileException;
import compiler.lexer.Token.Type;

public class Lexer {

    private static final Pattern END;
    private static final Pattern SPACE;
    
    static {
        END = Pattern.compile("\\G\\z");
        SPACE = Pattern.compile("\\G\\s+");
    }

    private final Matcher input;

    private Token nextToken;

    public Lexer(String input) throws CompileException {
        if (input.endsWith("\n"))
            this.input = END.matcher(input);
        else
            this.input = END.matcher(input+"\n");
        
        nextToken = nextToken();
    }

    public Token next() throws CompileException {
        Token out = new Token(nextToken);
        nextToken = nextToken();
        return out;
    }

    public Type nextType() {
        return nextToken.type;
    }

    private Token nextToken() throws CompileException {
        // Comments
        input.usePattern(Type.BLOCKCOMMENT.getPattern());
        input.find();
        input.usePattern(Type.LINECOMMENT.getPattern());
        input.find();

        // Inc line count
        input.usePattern(Type.NEWLINE.getPattern());
        if (input.find())
            return new Token(Type.NEWLINE);

        // Skip spaces
        input.usePattern(SPACE);
        input.find();
        
        for (Type t : Type.getAllOf()) {
            input.usePattern(t.getPattern());
            if (input.find())
                return new Token(t, input.group());
        }

        // No match
        return null;
    }

    public boolean hasNext() {
        return nextToken != null;
    }
}
