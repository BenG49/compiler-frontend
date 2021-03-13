package compiler.exception.semantics;

import java.util.HashSet;

import compiler.syntax.Type;

public class TypeConcatException extends SemanticException {
    private static final long serialVersionUID = 1L;

    private final String string;

    public TypeConcatException(HashSet<Type> types) {
        StringBuilder sb = new StringBuilder();
        sb.append("Incorrect types ");
        sb.append(types);
        sb.append(" were attempted to be concatenated.");

        this.string = sb.toString();
    }

    public String toString() {
        return string;
    }
    
}
