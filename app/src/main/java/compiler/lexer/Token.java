package compiler.lexer;

public class Token {
    public enum Type{
        INT, FLOAT, STRING,
        PLUS, MINUS, MUL, DIV,
        LPAREN, RPAREN
    };

    public Type type;
    public String value;

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
