package compiler.parser.grammars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import compiler.exception.*;
import compiler.lexer.Token.Type;
import compiler.parser.Parser;

public class Expressions {
    private static Parser p;

    public static void setParser(Parser p) {
        Expressions.p = p;
    }

    /**
     * Program
     *  : StatementList
     *  ;
     */
    public static Node<Node> Program() throws CompileException {
        return new Node<Node>(
            "Program",
            StatementList()
        );
    }

    /**
     * StatementList
     *  : Statement -> Statement Statement
     *  ;
     */
    public static Node<Node> StatementList() throws CompileException {
        List<Node> statements = new ArrayList<Node>();
        for (int i = 0; i < p.getStatementCount(); i++)
            statements.add(Statement());

        return new Node<Node>(
            "StatementList",
            statements
        );
    }

    /**
     * Statement
     *  : BinaryExpression
     *  & SEMI
     *  ;
     */
    public static Node<Node> Statement() throws CompileException {
        Node<Node> out = BinaryExpression();
        p.tryNextToken(Type.NEWLINE);
        return new Node<Node>(
            "Statement",
            out
        );
    }

    /**
     * BinaryExpression
     *  : NumberLiteral
     *  & Operator
     *  & (NumberLiteral | BinaryExpression)
     *  ;
     */
    public static Node<Node> BinaryExpression() throws CompileException {
        List<Node> expression = new ArrayList<Node>(Arrays.asList(
            NumberLiteral(),
            Operator()));
        
        // TODO: make it so the lexer can look more than 1 token in the future
        expression.add(NumberLiteral());

        return new Node<Node>(
            "BinaryExpression",
            expression
        );
    }

    /**
     * Operator
     *  : PLUS
     *  | MINUS
     *  | MUL
     *  | DIV
     *  ;
     */
    public static Node<Type> Operator() throws CompileException {
        Type nextType = p.l.nextType();
        Type out;

        if (nextType == Type.PLUS || nextType == Type.MINUS || nextType == Type.MUL || nextType == Type.DIV) {
            p.tryNextToken(nextType);
            out = nextType;
        } else if (nextType == null)
            throw new EOFException("Operator");
        else
            throw new TokenTypeException(nextType, "Operator");
        
        return new Node<Type>(
            "Operator",
            out
        );
    }

    /**
     * Literal
     *  : IntLiteral
     *  | NumberLiteral
     *  ;
     */
    public static Node<Node> Literal() throws CompileException {
        Type nextType = p.l.nextType();
        Node literal;

        if (nextType == Type.INT || nextType == Type.FLOAT)
            literal = NumberLiteral();
        else if (nextType == Type.STRING)
            literal = StringLiteral();
        else if (nextType == null)
            throw new EOFException("Operator");
        else
            throw new TokenTypeException(nextType, "Literal");

        return new Node<Node>(
            "Literal",
            literal
        );
    }

    /**
     * StringLiteral
     *  : STRING
     *  ;
     */
    public static Node<String> StringLiteral() throws CompileException {
        return new Node<String>(
            "StringLiteral",
            p.tryNextToken(Type.STRING)
        );
    }

    /**
     * NumberLiteral
     *  : IntLiteral
     *  | FloatLiteral
     *  ;
     */
    public static Node<Node> NumberLiteral() throws CompileException {
        Type nextType = p.l.nextType();
        Node literal;

        if (nextType == Type.INT)
            literal = IntLiteral();
        else if (nextType == Type.FLOAT)
            literal = FloatLiteral();
        else if (nextType == null)
            throw new EOFException("Operator");
        else
            throw new TokenTypeException(nextType, "NumberLiteral");
        
        return new Node<Node>(
            "NumberLiteral",
            literal
        );
    }

    /**
     * IntLiteral
     *  : INT
     *  ;
     */
    public static Node<Integer> IntLiteral() throws CompileException {
        return new Node<Integer>(
            "IntLiteral",
            Integer.parseInt(p.tryNextToken(Type.INT))
        );
    }

    /**
     * FloatLiteral
     *  : FLOAT
     *  ;
     */
    public static Node<Float> FloatLiteral() throws CompileException {
        return new Node<Float>(
            "FloatLiteral",
            Float.parseFloat(p.tryNextToken(Type.FLOAT))
        );
    }
    
}
