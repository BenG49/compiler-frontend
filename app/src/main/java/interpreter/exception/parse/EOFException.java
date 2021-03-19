package interpreter.exception.parse;

import interpreter.exception.InterpretException;

public class EOFException extends InterpretException {

    private static final long serialVersionUID = 1L;

    private final String string;
    
    public EOFException(String c) {
        this.string = "Unexpected end of file, expected '"+c+"'";
    }

    @Override
    public String toString() {
        return string;
    }
}
