package compiler.lexer;

public class IllegalCharacterException extends Exception {
    public IllegalCharacterException(char character, int index) {
        this(Character.toString(character), index);
    }
    public IllegalCharacterException(String character, int index) {
        super("Illegal character '"+character+"' at index "+index);
    }
}
