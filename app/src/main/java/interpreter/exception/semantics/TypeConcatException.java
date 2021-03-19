package interpreter.exception.semantics;

import java.util.HashSet;

import interpreter.exception.InterpretException;
import interpreter.syntax.Type;

public class TypeConcatException extends InterpretException {
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
