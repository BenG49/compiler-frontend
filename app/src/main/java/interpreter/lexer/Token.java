package interpreter.lexer;

import interpreter.syntax.Type;

public class Token {

    public Type type;
    public String value;
    public int[] index;

    public Token(Token other) {
        this.type = other.type;
        this.value = other.value;
    }

    public Token(Type type, int[] index) { this(type, "", index); }
    public Token(Type type, String value, int[] index) {
        this.type = type;
        this.value = value;
        this.index = index;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (value.length() > 0) {
            sb.append(":");
            sb.append(value);
        }
        sb.append("\t - (");
        sb.append(index[0]);
        sb.append(", ");
        sb.append(index[1]);
        sb.append(")");

        return sb.toString();
    }
}
