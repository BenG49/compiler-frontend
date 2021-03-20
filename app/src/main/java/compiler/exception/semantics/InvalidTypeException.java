package compiler.exception.semantics;

import compiler.exception.CompileException;
import compiler.syntax.Type;

public class InvalidTypeException extends CompileException {
    private static final long serialVersionUID = 1L;

    private final String string;

    public InvalidTypeException (int[] pos, Type given, Type... expected) {
        StringBuilder sb = new StringBuilder();
        sb.append("Incorrect type ");
        sb.append(given);
        sb.append(" given, expected ");
        for (int i = 0; i < expected.length; i++) {
            sb.append(expected[i]);
            if (i+1 < expected.length)
                sb.append(" or ");
        }
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
