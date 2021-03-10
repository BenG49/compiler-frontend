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
     * program := statementlist
     */
    public static Node Program() throws ParseException {
        return new Node(
            "Program",
            StatementList()
        );
    }

    /**
     * statementlist := statement...
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
     * statement :=     binaryexpression
     *                | ifexpression
     *                | declareexpression
     *                | assignexpression
     *                | ""
     *              NEWLINE
     */
    public static Node Statement() throws ParseException {
        Node out;
        Type nextType = p.l.nextType();

        // ifexpression
        if (nextType == Type.IF)
            out = IfExpression();
        // binaryexpression
        else if (nextType.within(Type.INT, Type.FLOAT, Type.PLUS, Type.MINUS))
            out = BinaryExpression();
        // declareexpression
        else if (nextType.within(Type.getVarTypes()))
            out = DeclareExpression();
        // assignexpression
        else if (nextType == Type.VAR)
            out = AssignExpression();
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
     * declareexpression := vartypeliteral variable
     *                          ""
     *                        | EQUALS 
     *                              binaryexpression
     *                            | stringliteral
     */
    public static Node DeclareExpression() throws ParseException {
        List<Node> out = new ArrayList<Node>();
        // <vartypeliteral>
        out.add(VarTypeLiteral());
        // <variable>
        out.add(Variable());

        // EQUALS
        p.tryNextToken(Type.EQUALS);
        
        Type nextType = p.l.nextType();
        // <stringliteral>
        if (nextType == Type.STR)
            out.add(StringLiteral());
        // <binaryexpression>
        else
            out.add(BinaryExpression());
        
        
        return new Node(
            "InitExpression",
            out
        );
    }

    /**
     * assignexpression := variable 
     *                         addsuboperator
     *                       | muldivoperator
     *                       | ""
     *                     EQUALS 
     *                         binaryexpression
     *                       | stringliteral
     */
    public static Node AssignExpression() throws ParseException {
        List<Node> out = new ArrayList<Node>();
        // variable
        out.add(Variable());

        Type nextType = p.l.nextType();
        // addsuboperator
        if (nextType.within(Type.PLUS, Type.MINUS))
            out.add(AddSubOperator());
        // muldivoperator
        else if (nextType.within(Type.MUL, Type.DIV))
            out.add(MulDivOperator());
        
        // EQUALS
        p.tryNextToken(Type.EQUALS);

        nextType = p.l.nextType();
        // stringliteral
        if (nextType == Type.STR)
            out.add(StringLiteral());
        // binaryexpression
        else
            out.add(BinaryExpression());
        
        return new Node(
            "AssignExpression",
            out
        );
    }

    /**
     * ifexpression := IF LP boolexpression RP LB statement... RB
     */
    public static Node IfExpression() throws ParseException {
        List<Node> out = new ArrayList<Node>();

        p.tryNextToken(Type.IF);
        p.tryNextToken(Type.LP);
        out.add(BoolExpression());
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
     * boolexpression := boolterm andoroperator boolterm
     *                 | boolterm
     */
    public static Node BoolExpression() throws ParseException {
        List<Node> expression = new ArrayList<Node>();
        expression.add(BoolTerm());

        Type nextType = p.l.nextType();
        if (nextType.within(Type.AND, Type.OR)) {
            expression.add(AndOrOperator());
            expression.add(BoolTerm());
        }

        return new Node(
            "BoolExpression",
            expression
        );
    }

    /**
     * boolterm := boolfactor compareoperator boolfactor
     *           | boolfactor
     */
    public static Node BoolTerm() throws ParseException {
        List<Node> expression = new ArrayList<Node>();
        expression.add(BoolFactor());

        Type nextType = p.l.nextType();
        if (nextType.within(Type.EQUIVALENT, Type.GREATER, Type.LESS)) {
            expression.add(CompareOperator());
            expression.add(BoolFactor());
        }

        return new Node(
            "BoolTerm",
            expression
        );
    }

    /**
     * boolfactor := NOT boolfactor
     *             | LP boolexpression RP
     *             | binaryexpression
     *             | truefalseliteral
     *             | VAR
     */
    public static Node BoolFactor() throws ParseException {
        Type nextType = p.l.nextType();
        Node out;

        // NOT boolfactor
        if (nextType == Type.NOT) {
            p.tryNextToken(Type.NOT);
            out = BoolFactor();
        // LP boolexpression RP
        } else if (nextType == Type.LP) {
            p.tryNextToken(Type.LP);
            out = BoolExpression();
            p.tryNextToken(Type.RP);
        // truefalseliteral
        } else if (nextType.within(Type.TRUE, Type.FALSE))
            out = TrueFalseLiteral();
        // VAR
        else if (nextType == Type.VAR)
            out = Variable();
        // binaryexpression
        else
            out = BinaryExpression();
        
        return new Node(
            "BoolFactor",
            out
        );
    }

    /**
     * binaryexpression :=     addsuboperator
     *                       | ""
     *                             term addsuboperator binaryexpression
     *                           | term
     */
    public static Node BinaryExpression() throws ParseException {
        List<Node> expression = new ArrayList<Node>();
        Type nextType = p.l.nextType();

        // PLUS | MINUS
        if (nextType.within(Type.PLUS, Type.MINUS))
            expression.add(AddSubOperator());
        
        expression.add(Term());

        nextType = p.l.nextType();
        // addsuboperator binaryexpression
        if (nextType.within(Type.PLUS, Type.MINUS)) {
            expression.add(AddSubOperator());
            expression.add(BinaryExpression());
        }

        return new Node(
            "BinaryExpression",
            expression
        );
    }

    /**
     * term := factor muldivoperator term
     *       | factor
     */
    public static Node Term() throws ParseException {
        List<Node> term = new ArrayList<Node>();

        term.add(Factor());

        Type nextType = p.l.nextType();
        // muldivoperator term
        if (nextType.within(Type.MUL, Type.DIV)) {
            term.add(MulDivOperator());
            term.add(Term());
        }

        return new Node(
            "Term",
            term
        );
    }

    /**
     * factor := variable
     *         | numberliteral
     *         | LP binaryexpression RP
     */
    public static Node Factor() throws ParseException {
        Type nextType = p.l.nextType();
        Node out;

        // numberliteral
        if (nextType.within(Type.INT, Type.FLOAT))
            out = NumberLiteral();
        // variable
        else if (nextType == Type.VAR)
            out = Variable();
        // LP binaryexpression RP
        else if (nextType == Type.LP) {
            p.tryNextToken(Type.LP);
            out = BinaryExpression();
            p.tryNextToken(Type.RP);
        } else
            throw new TokenTypeException(nextType, "VAR, NumberLiteral, (LP BinaryExpression RP)", p.l.getPos());
        
        return new Node(
            "Factor",
            out
        );
    }

    /**
     * variable := VAR
     *           | TRUE
     *           | FALSE
     */
    public static ValueNode<String> Variable() throws ParseException {
        return new ValueNode<String>(
            "Variable",
            p.tryNextToken(Type.VAR, Type.TRUE, Type.FALSE)
        );
    }
    
    /**
     * addsuboperator := PLUS
     *                 | MINUS
     */
    public static ValueNode<Type> AddSubOperator() throws ParseException {
        Type nextType = p.l.nextType();

        p.tryNextToken(Type.PLUS, Type.MINUS);

        return new ValueNode<Type>(
            "AddSubOperator",
            nextType
        );
    }

    /**
     * muldivoperator := PLUS
     *                 | MINUS
     */
    public static ValueNode<Type> MulDivOperator() throws ParseException {
        Type nextType = p.l.nextType();

        p.tryNextToken(Type.MUL, Type.DIV);

        return new ValueNode<Type>(
            "MulDivOperator",
            nextType
        );
    }

    /**
     * compareoperator := EQUIVALENT
     *                  | LESS
     *                      | LESS EQUALS
     *                  | GREATER
     *                      | GREATER EQUALS
     */
    public static ValueNode<List<Type>> CompareOperator() throws ParseException {
        List<Type> out = new ArrayList<Type>();
        Type nextType = p.l.nextType();

        p.tryNextToken(Type.EQUIVALENT, Type.LESS, Type.GREATER);
        out.add(nextType);

        nextType = p.l.nextType();
        if (nextType == Type.EQUALS) {
            p.tryNextToken(Type.EQUALS);
            out.add(nextType);
        }

        return new ValueNode<List<Type>>(
            "CompareOperator",
            out
        );
    }

    /**
     * andoroperator := AND
     *                | OR
     */
    public static ValueNode<Type> AndOrOperator() throws ParseException {
        Type nextType = p.l.nextType();

        p.tryNextToken(Type.AND, Type.OR);

        return new ValueNode<Type>(
            "MulDivOperator",
            nextType
        );
    }

    /**
     * vartypeliteral := VAR_TYPES
     */
    public static ValueNode<Type> VarTypeLiteral() throws ParseException {
        Type out = p.l.nextType();
        p.tryNextToken(Type.getVarTypes());

        return new ValueNode<Type>(
            "VarTypeLiteral",
            out
        );
    }

    /**
     * truefalseliteral := TRUE
     *                   | FALSE
     */
    public static ValueNode<Type> TrueFalseLiteral() throws ParseException {
        Type out = p.l.nextType();
        p.tryNextToken(Type.TRUE, Type.FALSE);

        return new ValueNode<Type>(
            "TrueFalseLiteral",
            out
        );
    }

    /**
     * stringliteral := STR
     */
    public static ValueNode<String> StringLiteral() throws ParseException {
        return new ValueNode<String>(
            "StringLiteral",
            p.tryNextToken(Type.STR)
        );
    }

    /**
     * numberliteral := intliteral
     *                | floatliteral
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
     * intliteral := INT
     */
    public static ValueNode<Integer> IntLiteral() throws ParseException {
        return new ValueNode<Integer>(
            "IntLiteral",
            Integer.parseInt(p.tryNextToken(Type.INT))
        );
    }

    /**
     * floatliteral := FLOAT
     */
    public static ValueNode<Float> FloatLiteral() throws ParseException {
        return new ValueNode<Float>(
            "FloatLiteral",
            Float.parseFloat(p.tryNextToken(Type.FLOAT))
        );
    }
    
}
