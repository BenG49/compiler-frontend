package compiler.exception.parse;

import compiler.exception.CompileException;

public class EOFException extends CompileException {

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
