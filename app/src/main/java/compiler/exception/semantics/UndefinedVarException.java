package compiler.exception.semantics;

public class UndefinedVarException extends SemanticException {
    private static final long serialVersionUID = 1L;

    private final String string;

    public UndefinedVarException (String given) {
        StringBuilder sb = new StringBuilder();
        sb.append("Undefined variable \"");
        sb.append(given);
        sb.append("\"");

        string = sb.toString();
    }
    
    public String toString() {
        return string;
    }

}
