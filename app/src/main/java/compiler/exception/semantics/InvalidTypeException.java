package compiler.exception.semantics;

import compiler.syntax.Type;

public class InvalidTypeException extends SemanticException {
    private static final long serialVersionUID = 1L;

    private final String string;

    public InvalidTypeException (Type given, Type expected) {
        StringBuilder sb = new StringBuilder();
        sb.append("Incorrect type ");
        sb.append(given);
        sb.append(" given, expected ");
        sb.append(expected);

        string = sb.toString();
    }

    public String toString() {
        return string;
    }

}
