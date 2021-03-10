package compiler.parser.grammars;

import java.util.ArrayList;
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
     * <program> := <statementlist>
     */
    public static Node Program() throws ParseException {
        return new Node(
            "Program",
            StatementList()
        );
    }

    /**
     * not actually how i'm implementing it, but this is cleaner
     * 
     * <statementlist> := <statement>
     *                  | <statement> <statementlist>
     */
    public static Node StatementList() throws ParseException {
        List<Node> statements = new ArrayList<Node>();
        while (p.l.hasNext()) {
            Node temp = Statement();
            if (temp != null)
                statements.add(temp);
        }

        return new Node(
            "StatementList",
            statements
        );
    }

    /**
     * <statement> := (<binaryexpression>
     *              | <ifexpression>
     *              | "")
     *                "NEWLINE"
     */
    public static Node Statement() throws ParseException {
        Node out;
        Type nextType = p.l.nextType();

        // <ifexpression>
        if (nextType == Type.IF)
            out = IfStatement();
        // <binaryexpression>
        else if (nextType == Type.VAR || nextType == Type.INT || nextType == Type.FLOAT
              || nextType == Type.PLUS || nextType == Type.MINUS)
            out = BinaryExpression();
        else {
            p.tryNextToken(Type.NEWLINE);
            return null;
        }

        p.tryNextToken(Type.NEWLINE);

        return new Node(
            "Statement",
            out
        );
    }

    /**
     * how to do single statment ifs :/
     * <ifexpression> := "IF" "LP" <evalexpression> "RP" (("LB" "RB") | "NEWLINE")
     */
    public static Node IfStatement() throws ParseException {
        List<Node> out = new ArrayList<Node>();

        p.tryNextToken(Type.IF);
        p.tryNextToken(Type.LP);
        out.add(BinaryExpression());
        p.tryNextToken(Type.RP);
        p.tryNextToken(Type.LB);

        // while the type isn't RB
        Type nextType = p.l.nextType();
        while (nextType != Type.RB) {
            Node temp = Statement();
            if (temp != null)
                out.add(temp);
            nextType = p.l.nextType();
        }

        p.tryNextToken(Type.RB);

        return new Node(
            "IfStatement",
            out
        );
    }

    /**
     * https://stackoverflow.com/questions/9934553/extended-backus-naur-form-order-of-operations
     * <binaryexpression> := (<addsuboperator>|"") <termexpression> (<addsuboperator> <termexpression>)*
     */
    public static Node BinaryExpression() throws ParseException {
        List<Node> expression = new ArrayList<Node>();
        Type nextType = p.l.nextType();

        // "+", "-", or ""
        if (nextType == Type.PLUS || nextType == Type.MINUS)
            expression.add(AddSubOperator());
        
        expression.add(TermExpression());

        nextType = p.l.nextType();
        // while next type is plus or minus -> (<addsuboperator> <termexpression>)*
        while (nextType == Type.PLUS || nextType == Type.MINUS) {
            expression.add(AddSubOperator());
            expression.add(TermExpression());

            nextType = p.l.nextType();
        }

        return new Node(
            "BinaryExpression",
            expression
        );
    }

    /**
     * <termexpression> := <factor> (<muldivoperator> <factor>)*
     */
    public static Node TermExpression() throws ParseException {
        List<Node> term = new ArrayList<Node>();

        term.add(Factor());

        Type nextType = p.l.nextType();
        // while next type is mul or div -> (<muldivoperator> <factor>)*
        while (nextType == Type.MUL || nextType == Type.DIV) {
            term.add(MulDivOperator());
            term.add(Factor());

            nextType = p.l.nextType();
        }

        return new Node(
            "TermExpression",
            term
        );
    }

    /**
     * <factor> := <variable> | <numberliteral> | <binaryexpression>
     */
    public static Node Factor() throws ParseException {
        Type nextType = p.l.nextType();
        Node out;

        // <numberliteral>
        if (nextType == Type.INT || nextType == Type.FLOAT)
            out = NumberLiteral();
        // <variable>
        else if (nextType == Type.VAR)
            out = Variable();
        // <binaryexpression>
        // TODO: should probably not have this as an else and throw exception instead
        else
            out = BinaryExpression();
        
        return new Node(
            "Factor",
            out
        );
    }

    /**
     * <variable> := VAR
     */
    public static ValueNode<String> Variable() throws ParseException {
        return new ValueNode<String>(
            "Variable",
            p.tryNextToken(Type.VAR)
        );
    }
    
    /**
     * <addsuboperator> := PLUS
     *                   | MINUS
     */
    public static ValueNode<Type> AddSubOperator() throws ParseException {
        Type nextType = p.l.nextType();

        p.tryNextToken(new Type[] {Type.PLUS, Type.MINUS});

        return new ValueNode<Type>(
            "AddSubOperator",
            nextType
        );
    }

    /**
     * <muldivoperator> := PLUS
     *                   | MINUS
     */
    public static ValueNode<Type> MulDivOperator() throws ParseException {
        Type nextType = p.l.nextType();

        p.tryNextToken(new Type[] {Type.MUL, Type.DIV});

        return new ValueNode<Type>(
            "MulDivOperator",
            nextType
        );
    }

    /**
     * <literal> := <intliteral>
     *            | <numberliteral>
     */
    public static Node Literal() throws ParseException {
        Type nextType = p.l.nextType();
        Node literal;

        if (nextType == Type.INT || nextType == Type.FLOAT)
            literal = NumberLiteral();
        else if (nextType == Type.STR)
            literal = StringLiteral();
        else if (nextType == null)
            throw new EOFException("Operator");
        else
            throw new TokenTypeException(nextType, "Literal", p.l.getPos());

        return new Node(
            "Literal",
            literal
        );
    }

    /**
     * <stringliteral> := STRING
     */
    public static ValueNode<String> StringLiteral() throws ParseException {
        return new ValueNode<String>(
            "StringLiteral",
            p.tryNextToken(Type.STR)
        );
    }

    /**
     * <numberliteral> := <intliteral>
     *                  | <floatliteral>
     */
    public static Node NumberLiteral() throws ParseException {
        Type nextType = p.l.nextType();
        Node literal;

        if (nextType == Type.INT)
            literal = IntLiteral();
        else if (nextType == Type.FLOAT)
            literal = FloatLiteral();
        else if (nextType == null)
            throw new EOFException("Operator");
        else
            throw new TokenTypeException(nextType, "NumberLiteral", p.l.getPos());
        
        return new Node(
            "NumberLiteral",
            literal
        );
    }

    /**
     * <intliteral> := INT
     */
    public static ValueNode<Integer> IntLiteral() throws ParseException {
        return new ValueNode<Integer>(
            "IntLiteral",
            Integer.parseInt(p.tryNextToken(Type.INT))
        );
    }

    /**
     * <floatliteral> := FLOAT
     */
    public static ValueNode<Float> FloatLiteral() throws ParseException {
        return new ValueNode<Float>(
            "FloatLiteral",
            Float.parseFloat(p.tryNextToken(Type.FLOAT))
        );
    }
    
}
