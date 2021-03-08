package compiler.parser.grammars;

import java.util.ArrayList;
import java.util.List;

import compiler.lexer.Token.Type;
import compiler.parser.Parser;

public class Expressions {
    private static Parser p;

    public static void setParser(Parser p) {
        Expressions.p = p;
    }
    // TODO: maybe add indentations

    /**
     * Program
     *  : StatementList
     *  ;
     */
    public static Node<Node> Program() throws Exception {
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
    public static Node<Node> StatementList() throws Exception {
        List<Node> statements = new ArrayList<Node>();
        for (int i = 0; i < p.s.length; i++)
            statements.add(Statement());

        return new Node<Node>(
            "StatementList",
            statements
        );
    }

    /**
     * Statement
     *  : Literal
     *  ;
     */
    public static Node<Node> Statement() throws Exception {
        return new Node<Node>(
            "Statement",
            Literal()
        );
    }

    /**
     * Literal
     *  : IntLiteral
     *  | StringLiteral
     *  | FloatLiteral
     *  ;
     */
    public static Node<Node> Literal() throws Exception {
        Type nextType = p.l.nextType();
        Node literal;

        if (nextType == Type.INT)
            literal = IntLiteral();
        if (nextType == Type.FLOAT)
            literal = FloatLiteral();
        if (nextType == Type.STRING)
            literal = StringLiteral();
        else
            throw new Exception("Incorrect token type "+nextType+" given, expected Literal");

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
    public static Node<String> StringLiteral() throws Exception {
        return new Node<String>(
            "StringLiteral",
            p.tryNextToken(Type.STRING)
        );
    }

    /**
     * IntLiteral
     *  : INT
     *  ;
     */
    public static Node<Integer> IntLiteral() throws Exception {
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
    public static Node<Float> FloatLiteral() throws Exception {
        return new Node<Float>(
            "FloatLiteral",
            Float.parseFloat(p.tryNextToken(Type.FLOAT))
        );
    }
    
}
