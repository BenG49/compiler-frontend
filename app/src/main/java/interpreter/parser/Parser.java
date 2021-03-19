package interpreter.parser;

import java.util.ArrayList;
import java.util.List;

import interpreter.exception.InterpretException;
import interpreter.exception.parse.*;
import interpreter.lexer.*;
import interpreter.syntax.SymbolTable;
import interpreter.syntax.Type;
import interpreter.parser.grammars.ast.ASTNode;
import interpreter.parser.grammars.expressions.Expressions;

public class Parser {
    public String s;
    public Lexer l;

    public Parser(String s) {
        this.s = s;
    }

    public ASTNode<?> parse() throws InterpretException {
        l = new Lexer(s);

        return Expressions.Program(this, new SymbolTable());
    }

    public List<Token> eatMultiple(Type... type) throws InterpretException {
        List<Token> out = new ArrayList<Token>();

        for (Type t:type)
            out.add(eat(t));
        
        return out;
    }

    public Token eat(List<Type> type) throws InterpretException {
        Type[] array = new Type[type.size()];
        return eat(type.toArray(array));
    }
    public Token eat(Type... type) throws InterpretException {
        Token token = l.next();
        if (token == null)
            throw new EOFException(type+"");
        
        boolean match = false;
        for (Type t:type) {
            if (token.type == t)
                match = true;
        }

        if (!match)
            throw new TokenTypeException(token.type, type, token.index);
        
        return token;
    }

}