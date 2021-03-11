package compiler.lexer;

import compiler.syntax.Type;

public class Token {

    public Type type;
    public String value;

    public Token(Token other) {
        this.type = other.type;
        this.value = other.value;
    }

    public Token(Type type) { this(type, ""); }
    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (value.length() > 0) {
            sb.append(":");
            sb.append(value);
        }

        return sb.toString();
    }
}
