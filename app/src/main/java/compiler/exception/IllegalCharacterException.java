package compiler.exception;

public class IllegalCharacterException extends CompileException {

    private static final long serialVersionUID = 1L;

    private String string;

    public IllegalCharacterException(String c, int index, int line) {
        this.string = "Illegal character '"+c+"' at line "+line+", index "+index;
    }

    @Override
    public String toString() {
        return string;
    }
    
}
