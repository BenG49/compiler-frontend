package compiler.parser.grammars.expressions;

import java.util.ArrayList;
import java.util.List;

import compiler.exception.*;
import compiler.lexer.Token.Type;
import compiler.parser.Parser;
import compiler.parser.grammars.Node;
import compiler.parser.grammars.ValueNode;

public class Expressions {
    /**
     * program := statementlist
     */
    public static Node Program(Parser p) throws ParseException {
        return new Node(
            "Program",
            StatementList(p)
        );
    }

    /**
     * statementlist := statement...
     */
    public static Node StatementList(Parser p) throws ParseException {
        List<Node> statements = new ArrayList<Node>();
        while (p.l.hasNext()) {
            Node temp = Statement(p);
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
    public static Node Statement(Parser p) throws ParseException {
        Node out;
        Type nextType = p.l.nextType();

        // ifexpression
        if (nextType == Type.IF)
            out = IfExpression(p);
        // binaryexpression
        else if (nextType.within(Type.INT, Type.FLOAT, Type.PLUS, Type.MINUS))
            out = BinaryExpression(p);
        // declareexpression
        else if (nextType.within(Type.getVarTypes()))
            out = DeclareExpression(p);
        // assignexpression
        else if (nextType == Type.VAR)
            out = AssignExpression(p);
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
     * evalblockstatement := BLOCK_TYPE LP boolexpression RP LB statement... RB
     */
    public static Node EvalBlockStatement(Parser p, Type blockType) throws ParseException {
        List<Node> out = new ArrayList<Node>();

        p.tryNextToken(blockType);
        p.tryNextToken(Type.LP);
        out.add(BoolExpression(p));
        p.tryNextToken(Type.RP);
        p.tryNextToken(Type.LB);

        // while the type isn't RB
        Type nextType = p.l.nextType();
        while (nextType != Type.RB) {
            Node temp = Statement(p);
            if (temp != null)
                out.add(temp);
            nextType = p.l.nextType();
        }

        p.tryNextToken(Type.RB);

        return new Node(
            "EvalBlockStatement: "+blockType,
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
    public static Node DeclareExpression(Parser p) throws ParseException {
        List<Node> out = new ArrayList<Node>();
        // <vartypeliteral>
        out.add(Literals.VarTypeLiteral(p));
        // <variable>
        out.add(Variable(p));

        // EQUALS
        p.tryNextToken(Type.EQUALS);
        
        Type nextType = p.l.nextType();
        // <stringliteral>
        if (nextType == Type.STR)
            out.add(Literals.StringLiteral(p));
        // <binaryexpression>
        else
            out.add(BinaryExpression(p));
        
        
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
    public static Node AssignExpression(Parser p) throws ParseException {
        List<Node> out = new ArrayList<Node>();
        // variable
        out.add(Variable(p));

        Type nextType = p.l.nextType();
        // addsuboperator
        if (nextType.within(Type.PLUS, Type.MINUS))
            out.add(AddSubOperator(p));
        // muldivoperator
        else if (nextType.within(Type.MUL, Type.DIV))
            out.add(MulDivOperator(p));
        
        // EQUALS
        p.tryNextToken(Type.EQUALS);

        nextType = p.l.nextType();
        // stringliteral
        if (nextType == Type.STR)
            out.add(Literals.StringLiteral(p));
        // binaryexpression
        else
            out.add(BinaryExpression(p));
        
        return new Node(
            "AssignExpression",
            out
        );
    }

    /**
     * ifexpression := IF LP boolexpression RP LB statement... RB
     */
    public static Node IfExpression(Parser p) throws ParseException {
        return EvalBlockStatement(p, Type.IF);
    }

    /**
     * boolexpression := boolterm andoroperator boolterm
     *                 | boolterm
     */
    public static Node BoolExpression(Parser p) throws ParseException {
        List<Node> expression = new ArrayList<Node>();
        expression.add(BoolTerm(p));

        Type nextType = p.l.nextType();
        if (nextType.within(Type.AND, Type.OR)) {
            expression.add(AndOrOperator(p));
            expression.add(BoolTerm(p));
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
    public static Node BoolTerm(Parser p) throws ParseException {
        List<Node> expression = new ArrayList<Node>();
        expression.add(BoolFactor(p));

        Type nextType = p.l.nextType();
        if (nextType.within(Type.EQUIVALENT, Type.GREATER, Type.LESS)) {
            expression.add(CompareOperator(p));
            expression.add(BoolFactor(p));
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
    public static Node BoolFactor(Parser p) throws ParseException {
        Type nextType = p.l.nextType();
        Node out;

        // NOT boolfactor
        if (nextType == Type.NOT) {
            p.tryNextToken(Type.NOT);
            out = BoolFactor(p);
        // LP boolexpression RP
        } else if (nextType == Type.LP) {
            p.tryNextToken(Type.LP);
            out = BoolExpression(p);
            p.tryNextToken(Type.RP);
        // truefalseliteral
        } else if (nextType.within(Type.TRUE, Type.FALSE))
            out = Literals.TrueFalseLiteral(p);
        // VAR
        else if (nextType == Type.VAR)
            out = Variable(p);
        // binaryexpression
        else
            out = BinaryExpression(p);
        
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
    public static Node BinaryExpression(Parser p) throws ParseException {
        List<Node> expression = new ArrayList<Node>();
        Type nextType = p.l.nextType();

        // PLUS | MINUS
        if (nextType.within(Type.PLUS, Type.MINUS))
            expression.add(AddSubOperator(p));
        
        expression.add(Term(p));

        nextType = p.l.nextType();
        // addsuboperator binaryexpression
        if (nextType.within(Type.PLUS, Type.MINUS)) {
            expression.add(AddSubOperator(p));
            expression.add(BinaryExpression(p));
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
    public static Node Term(Parser p) throws ParseException {
        List<Node> term = new ArrayList<Node>();

        term.add(Factor(p));

        Type nextType = p.l.nextType();
        // muldivoperator term
        if (nextType.within(Type.MUL, Type.DIV)) {
            term.add(MulDivOperator(p));
            term.add(Term(p));
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
    public static Node Factor(Parser p) throws ParseException {
        Type nextType = p.l.nextType();
        Node out;

        // numberliteral
        if (nextType.within(Type.INT, Type.FLOAT))
            out = Literals.NumberLiteral(p);
        // variable
        else if (nextType == Type.VAR)
            out = Variable(p);
        // LP binaryexpression RP
        else if (nextType == Type.LP) {
            p.tryNextToken(Type.LP);
            out = BinaryExpression(p);
            p.tryNextToken(Type.RP);
        } else
            throw new TokenTypeException(nextType, "Variable, NumberLiteral, (BinaryExpression)", p.l.getPos());
        
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
    public static ValueNode<String> Variable(Parser p) throws ParseException {
        return new ValueNode<String>(
            "Variable",
            p.tryNextToken(Type.VAR, Type.TRUE, Type.FALSE)
        );
    }
    
    /**
     * addsuboperator := PLUS
     *                 | MINUS
     */
    public static ValueNode<Type> AddSubOperator(Parser p) throws ParseException {
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
    public static ValueNode<Type> MulDivOperator(Parser p) throws ParseException {
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
    public static ValueNode<List<Type>> CompareOperator(Parser p) throws ParseException {
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
    public static ValueNode<Type> AndOrOperator(Parser p) throws ParseException {
        Type nextType = p.l.nextType();

        p.tryNextToken(Type.AND, Type.OR);

        return new ValueNode<Type>(
            "MulDivOperator",
            nextType
        );
    }
    
}
