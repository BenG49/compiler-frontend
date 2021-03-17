package compiler.exception.semantics;

import compiler.exception.CompileException;

public class UndefinedVarException extends CompileException {
    private static final long serialVersionUID = 1L;

    private final String string;

    public UndefinedVarException (int[] pos, String given) {
        StringBuilder sb = new StringBuilder();
        sb.append("Undefined variable \"");
        sb.append(given);
        sb.append("\" at line");
        sb.append(pos[0]);
        sb.append(", index ");
        sb.append(pos[1]);

        string = sb.toString();
    }
    
    public String toString() {
        return string;
    }

}