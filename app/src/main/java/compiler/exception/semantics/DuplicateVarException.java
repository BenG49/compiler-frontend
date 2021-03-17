package compiler.exception.semantics;

import compiler.exception.CompileException;

public class DuplicateVarException extends CompileException {
    private static final long serialVersionUID = 1L;

    final String string;

    public DuplicateVarException(int[] pos, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("Duplicate variable \"");
        sb.append(name);
        sb.append("\" defined at line ");
        sb.append(pos[0]);
        sb.append(", index ");
        sb.append(pos[1]);

        string = sb.toString();
    }

    public String toString() {
        return string;
    }
    
}
