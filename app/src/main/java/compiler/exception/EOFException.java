package compiler.exception;

public class EOFException extends CompileException{

    private static final long serialVersionUID = 1L;

    private String string;
    
    public EOFException(String c) {
        this.string = "Unexpected end of file, expected '"+c+"'";
    }

    @Override
    public String toString() {
        return string;
    }
}
