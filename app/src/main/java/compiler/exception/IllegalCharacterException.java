package compiler.exception;

public class IllegalCharacterException extends ParseException {

    private static final long serialVersionUID = 1L;

    private final String string;

    public IllegalCharacterException(String c, int index, int line) {
        this.string = "Illegal character '"+c+"' at line "+line+", index "+index;
    }

    @Override
    public String toString() {
        return string;
    }
    
}
