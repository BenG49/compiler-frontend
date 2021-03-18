package compiler.parser;

import java.util.ArrayList;
import java.util.List;

import compiler.exception.CompileException;
import compiler.exception.parse.*;
import compiler.lexer.*;
import compiler.syntax.SymbolTable;
import compiler.syntax.Type;
import compiler.parser.grammars.ast.ASTNode;
import compiler.parser.grammars.expressions.Expressions;

public class Parser {
    public String s;
    public Lexer l;

    public Parser(String s) {
        this.s = s;
    }

    public ASTNode<?, ?> parse() throws CompileException {
        l = new Lexer(s);

        return Expressions.Program(this, new SymbolTable());
    }

    public List<String> eatMultiple(Type... type) throws CompileException {
        List<String> out = new ArrayList<String>();

        for (Type t:type)
            out.add(eat(t));
        
        return out;
    }

    public String eat(List<Type> type) throws CompileException {
        Type[] array = new Type[type.size()];
        return eat(type.toArray(array));
    }
    public String eat(Type... type) throws CompileException {
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