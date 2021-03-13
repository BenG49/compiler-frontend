package compiler.parser;

import java.util.List;

import compiler.exception.parse.*;
import compiler.lexer.*;
import compiler.syntax.Type;
import compiler.parser.grammars.ast.ASTNode;
import compiler.parser.grammars.expressions.Expressions;

public class Parser {
    public String s;
    public Lexer l;

    public Parser(String s) {
        this.s = s;
    }

    public ASTNode<?, ?> parse() throws ParseException, LexException {
        l = new Lexer(s);

        return Expressions.Program(this);
    }

    public String eat(List<Type> type) throws ParseException, LexException {
        Type[] array = new Type[type.size()];
        return eat(type.toArray(array));
    }
    public String eat(Type... type) throws ParseException, LexException {
        Token token = l.next();
        if (token == null)
            throw new EOFException(type+"");
        
        boolean match = false;
        for (Type t:type) {
            if (token.type == t)
                match = true;
        }

        if (!match)
            throw new TokenTypeException(token.type, type, l.getPos());
        
        return token.value;
    }
}