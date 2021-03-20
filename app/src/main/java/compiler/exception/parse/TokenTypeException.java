package compiler.exception.parse;

import compiler.exception.CompileException;
import compiler.syntax.Type;

public class TokenTypeException extends CompileException {

    private static final long serialVersionUID = 1L;

    private final String string;

    public TokenTypeException(Type given, Object expected, int[] pos) {
        this(given, new Object[] {expected}, pos);
    }
    public TokenTypeException(Type given, Object[] expected, int[] pos) {
        StringBuilder sb = new StringBuilder();
        sb.append("Incorrect token type ");
        sb.append(given);
        sb.append(" given, expected ");
        for (int i = 0; i < expected.length; i++) {
            sb.append(expected[i]);
            if (i+1 != expected.length)
                sb.append(" or ");
        }
        sb.append(" at line ");
        sb.append(pos[0]);
        sb.append(", index ");
        sb.append(pos[1]);

        this.string = sb.toString();
    }

    @Override
    public String toString() {
        return string;
    }
}
