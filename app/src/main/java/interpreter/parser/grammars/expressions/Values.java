package interpreter.parser.grammars.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import interpreter.exception.parse.*;
import interpreter.exception.semantics.DuplicateIdException;
import interpreter.exception.semantics.UnknownIDException;
import interpreter.lexer.Token;
import interpreter.exception.InterpretException;
import interpreter.parser.Parser;
import interpreter.parser.grammars.ast.*;
import interpreter.semantics.FuncData;
import interpreter.semantics.VarData;
import interpreter.syntax.SymbolTable;
import interpreter.syntax.Type;

public class Values {
    /**
     * vartypeliteral := VAR_TYPES
     */
    public static ASTNode<String> VarTypeLiteral(Parser p) throws InterpretException {
        Type type = p.l.nextType();
        String out = p.eat(Type.getVarTypes()).value;

        return new ASTNode<String>(
            "VarTypeLiteral", type, out
        );
    }

    /**
     * returntypeliteral := (VAR_TYPES | VOID)...
     */
    public static ASTNode<Type> ReturnTypeLiteral(Parser p) throws InterpretException {
        Type out = p.l.nextType();
        List<Type> temp = new ArrayList<Type>(Arrays.asList(Type.getVarTypes()));
        temp.add(Type.VOID);

        p.eat(temp);

        return new ASTNode<Type>(
            "ReturnTypeLiteral", Type.RETURN, out
        );
    }

    /**
     * truefalseliteral := TRUE
     *                   | FALSE
     */
    public static ASTNode<Type> TrueFalseLiteral(Parser p) throws InterpretException {
        Type out = p.l.nextType();
        p.eat(Type.TRUE, Type.FALSE);

        return new ASTNode<Type>(
            "TrueFalseLiteral", out, out
        );
    }

    /**
     * stringliteral := STR
     */
    public static ASTNode<String> StringLiteral(Parser p) throws InterpretException {
        return new ASTNode<String>(
            "StringLiteral", Type.STR, p.eat(Type.STR).value
        );
    }

    /**
     * numberliteral := intliteral
     *                | floatliteral
     */
    public static ASTNode<?> NumberLiteral(Parser p) throws InterpretException {
        Type nextType = p.l.nextType();

        if (nextType == Type.INT)
            return IntLiteral(p);

        if (nextType == Type.FLOAT)
            return FloatLiteral(p);

        if (nextType == null)
            throw new EOFException("Operator");
        else
            throw new TokenTypeException(nextType, "NumberLiteral", p.l.next().index);
    }

    /**
     * intliteral := INT
     */
    public static ASTNode<Integer> IntLiteral(Parser p) throws InterpretException {
        return new ASTNode<Integer>(
            "IntLiteral", Type.INT, Integer.parseInt(p.eat(Type.INT).value)
        );
    }

    /**
     * floatliteral := FLOAT
     */
    public static ASTNode<Float> FloatLiteral(Parser p) throws InterpretException {
        return new ASTNode<Float>(
            "FloatLiteral", Type.FLOAT, Float.parseFloat(p.eat(Type.FLOAT).value)
        );
    }

    /**
     * identifier := ID
     */
    public static ASTNode<String> Variable(Parser p, SymbolTable scopeTable) throws InterpretException {
        return Variable(p, scopeTable, null);
    }
    public static ASTNode<String> Variable(Parser p, SymbolTable scopeTable, Type define) throws InterpretException {
        Token name = p.eat(Type.ID);
        boolean contains = scopeTable.vcontains(name.value);

        if (define == null && !contains)
            throw new UnknownIDException(name.index, name.value);
        
        // special case, functions and vars can have the same name
        if (define != null && contains/*&& p.t.get(name).scope == scope*/)
            throw new DuplicateIdException(name.index, name.value);
        
        if (define != null)
            scopeTable.vput(name.value, new VarData(define));

        return new ASTNode<String>("Identifier", Type.ID, name.value);
    }

    /**
     * function := FUNC
     */
    public static ASTNode<String> Function(Parser p, SymbolTable scopeTable) throws InterpretException {
        Token name = p.eat(Type.ID);
        if (!scopeTable.fcontains(name.value))
            throw new UnknownIDException(name.index, name.value);

        return new ASTNode<String>("Function", Type.ID, name.value);
    }
    public static ASTNode<String> Function(Parser p, SymbolTable scopeTable, Type define, Type... args) throws InterpretException {
        Token name = p.eat(Type.ID);

        if (scopeTable.fcontains(name.value)/*&& p.t.get(name).scope == scope*/)
            throw new DuplicateIdException(name.index, name.value);
        
        scopeTable.fput(name.value, new FuncData(define, args));
        
        return new ASTNode<String>("Function", Type.ID, name.value);
    }
    public static ASTNode<String> Function(Parser p, SymbolTable scopeTable, List<Type> define, List<Type> args) throws InterpretException {
        Token name = p.eat(Type.ID);

        if (scopeTable.fcontains(name.value)/*&& p.t.get(name).scope == scope*/)
            throw new DuplicateIdException(name.index, name.value);
        
        scopeTable.fput(name.value, new FuncData(define, args));
        
        return new ASTNode<String>("Function", Type.ID, name.value);
    }

}
