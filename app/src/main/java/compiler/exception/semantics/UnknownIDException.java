package compiler.exception.semantics;

import compiler.exception.CompileException;

public class UnknownIDException extends CompileException {
    private static final long serialVersionUID = 1L;

    private final String string;

    public UnknownIDException (int[] pos, String given) {
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown identifier \"");
        sb.append(given);
        sb.append("\" at line ");
        sb.append(pos[0]);
        sb.append(", index ");
        sb.append(pos[1]);
        sb.append(", either out of scope or undefined");

        string = sb.toString();
    }
    
    public String toString() {
        return string;
    }

}
