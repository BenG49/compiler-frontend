package compiler.exception;

import compiler.lexer.Token.Type;

public class TokenTypeException extends CompileException {

    private static final long serialVersionUID = 1L;

    private String string;

    public TokenTypeException(Type given, String expected, int[] pos) {
        StringBuilder sb = new StringBuilder();
        sb.append("Incorrect token type ");
        sb.append(given);
        sb.append(" given, expected ");
        sb.append(expected);
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
