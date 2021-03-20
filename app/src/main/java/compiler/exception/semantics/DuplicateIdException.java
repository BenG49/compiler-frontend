package compiler.exception.semantics;

import compiler.exception.CompileException;

public class DuplicateIdException extends CompileException {
    private static final long serialVersionUID = 1L;

    final String string;

    public DuplicateIdException(int[] pos, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("Duplicate identifier \"");
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
