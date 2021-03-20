package compiler.exception.semantics;

import compiler.exception.CompileException;

public class ReturnArgCountException extends CompileException {
    private static final long serialVersionUID = 1L;

    private final String string;

    public ReturnArgCountException(int[] pos, int expected, int given) {
        StringBuilder sb = new StringBuilder();

        sb.append("Too many return arguments given: expected ");
        sb.append(expected);
        sb.append(", given ");
        sb.append(given);
        sb.append(" at line ");
        sb.append(pos[0]);
        sb.append(", index ");
        sb.append(pos[1]);

        string = sb.toString();
    }

    public String toString() {
        return string;
    }

}
