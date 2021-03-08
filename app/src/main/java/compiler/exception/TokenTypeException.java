package compiler.exception;

import compiler.lexer.Token.Type;

public class TokenTypeException extends CompileException {

    private static final long serialVersionUID = 1L;

    private String string;

    public TokenTypeException(Type given, String expected) {
        this.string = "Incorrect token type "+given+" given, expected "+expected;
    }

    @Override
    public String toString() {
        return string;
    }
}
