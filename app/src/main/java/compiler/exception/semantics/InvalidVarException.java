package compiler.exception.semantics;

public class InvalidVarException extends SemanticException {
    private static final long serialVersionUID = 1L;

    private final String string;

    public InvalidVarException(String var) {
        StringBuilder sb = new StringBuilder();
        sb.append("Incorrect type given or undefined variable ");
        sb.append(var);

        string = sb.toString();
    }

    public String toString() {
        return string;
    }
    
}
