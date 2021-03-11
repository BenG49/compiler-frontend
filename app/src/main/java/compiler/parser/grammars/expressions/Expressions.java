package compiler.parser.grammars.expressions;

import java.util.ArrayList;
import java.util.List;

import compiler.exception.parse.*;
import compiler.syntax.Type;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.*;

public class Expressions {
    /**
     * program := statementlist
     */
    public static ASTNode<String> Program(Parser p) throws ParseException {
        return new ASTNode<String>(
            "Program", "",
            StatementList(p)
        );
    }

    /**
     * statementlist := statement...
     */
    public static ASTNode<String> StatementList(Parser p) throws ParseException {
        List<AST> statements = new ArrayList<AST>();
        while (p.l.hasNext()) {
            AST temp = Statement(p);
            if (temp != null)
                statements.add(temp);
        }

        return new ASTNode<String>(
            "StatementList", "",
            statements
        );
    }

    /**
     * statement :=     ifexpression
     *                | declareexpression
     *                | assignexpression
     *                | whileexpression
     *                | ""
     *              NEWLINE
     */
    public static AST Statement(Parser p) throws ParseException {
        AST out;
        Type nextType = p.l.nextType();

        // ifexpression
        if (nextType == Type.IF)
            out = IfExpression(p);
        // declareexpression
        else if (nextType.within(Type.getVarTypes()))
            out = DeclareExpression(p);
        // assignexpression
        else if (nextType == Type.VAR)
            out = AssignExpression(p);
        else if (nextType == Type.WHILE)
            out = WhileExpression(p);
        else {
            p.eat(Type.NEWLINE);
            return null;
        }

        p.eat(Type.NEWLINE);

        return out;
    }

    /**
     * blockstatementbody := statement... RB
     */
    public static ASTNode<String> BlockStatementBody(Parser p) throws ParseException {
        List<AST> statements = new ArrayList<AST>();
        Type nextType = p.l.nextType();
        while (nextType != Type.RB) {
            AST temp = Statement(p);

            if (temp != null)
                statements.add(temp);

            nextType = p.l.nextType();
        }

        return new ASTNode<String>(
            "BlockStatementBody", "",
            statements
        );
    }

    /**
     * whileexpression := WHILE LP boolexpression RP LB blockstatementbody RB
     */
    public static ASTNode<Type> WhileExpression(Parser p) throws ParseException {
        List<AST> out = new ArrayList<AST>();

        p.eat(Type.WHILE);
        p.eat(Type.LPAREN);
        out.add(BoolExpression(p));
        p.eat(Type.RPAREN);
        p.eat(Type.LB);
        out.add(BlockStatementBody(p));
        p.eat(Type.RB);

        return new ASTNode<Type>(
            "WhileExpression", Type.WHILE,
            out
        );
    }

    /**
     * forexpression := FOR LP boolexpression RP LB blockstatementbody RB
     * for (int i = 0, i > var, i++)
     * for (int i in iterable)
     */

    /**
     * declareexpression := vartypeliteral variable
     *                          ""
     *                        | EQUALS 
     *                              binaryexpression
     *                            | stringliteral
     */
    public static ASTNode<Type> DeclareExpression(Parser p) throws ParseException {
        List<AST> out = new ArrayList<AST>();
        // <vartypeliteral>
        out.add(Literals.VarTypeLiteral(p));
        // <variable>
        out.add(Variable(p));

        // EQUALS
        p.eat(Type.EQUAL);
        
        Type nextType = p.l.nextType();
        // <stringliteral>
        if (nextType == Type.STR)
            out.add(Literals.StringLiteral(p));
        // <binaryexpression>
        else
            out.add(BinaryExpression(p));
        
        
        return new ASTNode<Type>(
            "InitExpression", Type.EQUAL,
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
    public static ASTNode<Type> AssignExpression(Parser p) throws ParseException {
        List<AST> out = new ArrayList<AST>();
        // variable
        out.add(Variable(p));

        Type nextType = p.l.nextType();
        // addsuboperator
        if (nextType.within(Type.PLUS, Type.MINUS))
            out.add(Operators.AddSubOperator(p));
        // muldivoperator
        else if (nextType.within(Type.MUL, Type.DIV))
            out.add(Operators.MulDivOperator(p));
        
        // EQUALS
        p.eat(Type.EQUAL);

        nextType = p.l.nextType();
        // stringliteral
        if (nextType == Type.STR)
            out.add(Literals.StringLiteral(p));
        // binaryexpression
        else
            out.add(BinaryExpression(p));
        
        return new ASTNode<Type>(
            "AssignExpression", Type.EQUAL,
            out
        );
    }

    /**
     * ifexpression := IF LP boolexpression RP LB blockstatementbody RB
     *                     ""
     *                   | ELSE LB blockstatementlist RB
     *                   | ELSE evalblockstatement<IF>
     */
    public static ASTNode<Type> IfExpression(Parser p) throws ParseException {
        final String name = "IfExpression";
        List<AST> out = new ArrayList<AST>();

        p.eat(Type.IF);
        p.eat(Type.LPAREN);
        out.add(BoolExpression(p));
        p.eat(Type.RPAREN);

        p.eat(Type.LB);
        out.add(BlockStatementBody(p));
        p.eat(Type.RB);
        
        Type nextType = p.l.nextType();
        if (nextType == Type.ELSE) {
            p.eat(nextType);

            nextType = p.l.nextType();
            if (nextType == Type.IF)
                out.add(IfExpression(p));
            else {
                p.eat(Type.LB);
                out.add(BlockStatementBody(p));
                p.eat(Type.RB);
            }
        }
        
        return new ASTNode<Type>(
            name, Type.IF,
            out
        );
    }

    /**
     * boolexpression := boolterm andoroperator boolexpression
     *                 | boolterm
     */
    public static AST BoolExpression(Parser p) throws ParseException {
        AST temp = BoolTerm(p);

        Type nextType = p.l.nextType();
        if (nextType.within(Type.AND, Type.OR)) {
            p.eat(nextType);
            return new ASTNode<Type>(
                "BoolExpression", nextType,
                temp, BoolExpression(p)
            );
        }

        return temp;
    }

    /**
     * boolterm := boolfactor compareoperator boolterm
     *           | boolfactor
     */
    public static AST BoolTerm(Parser p) throws ParseException {
        AST temp = BoolFactor(p);

        Type nextType = p.l.nextType();
        if (nextType.within(Type.EQUIVALENT, Type.GREATER, Type.LESS, Type.GREATER_EQUAL, Type.LESS_EQUAL)) {
            p.eat(nextType);
            return new ASTNode<Type>(
                "BoolTerm", nextType,
                temp, BoolTerm(p)
            );
        }

        return temp;
    }

    /**
     * boolfactor := NOT boolfactor
     *             | LP boolexpression RP
     *             | binaryexpression
     *             | truefalseliteral
     *             | VAR
     */
    public static AST BoolFactor(Parser p) throws ParseException {
        final String name = "BoolFactor";
        Type nextType = p.l.nextType();

        // NOT boolfactor
        if (nextType == Type.NOT) {
            p.eat(Type.NOT);
            return new ASTNode<Type>(
                name, nextType,
                BoolFactor(p)
            );
        }

        // LP boolexpression RP
        if (nextType == Type.LPAREN) {
            p.eat(Type.LPAREN);
            AST temp = BoolExpression(p);
            p.eat(Type.RPAREN);
            return temp;
        }

        // truefalseliteral
        if (nextType.within(Type.TRUE, Type.FALSE))
            return Literals.TrueFalseLiteral(p);

        // VAR
        if (nextType == Type.VAR)
            return Variable(p);

        // binaryexpression
        return BinaryExpression(p);
    }

    /**
     * binaryexpression := term addsuboperator binaryexpression
     *                   | term
     */
    public static AST BinaryExpression(Parser p) throws ParseException {
        AST temp = Term(p);

        Type nextType = p.l.nextType();
        // ADD|SUB binaryexpression
        if (nextType.within(Type.PLUS, Type.MINUS)) {
            p.eat(nextType);
            return new ASTNode<Type>(
                "BinaryExpression", nextType,
                temp, BinaryExpression(p)
            );
        }

        return temp;
    }

    /**
     * term := factor muldivoperator term
     *       | factor
     */
    public static AST Term(Parser p) throws ParseException {
        AST temp = Factor(p);

        Type nextType = p.l.nextType();
        // MUL|DIV term
        if (nextType.within(Type.MUL, Type.DIV)) {
            p.eat(nextType);
            return new ASTNode<Type>(
                "Term", nextType,
                temp, Term(p)
            );
        }

        return temp;
    }

    /**
     * factor :=     ""
     *             | MINUS
     *             | PLUS
     *           variable
     *         | numberliteral
     *         | LP binaryexpression RP
     */
    public static AST Factor(Parser p) throws ParseException {
        final String name = "Factor";

        Type nextType = p.l.nextType();
        Type preceedingSign = null;

        if (nextType.within(Type.MINUS, Type.PLUS)) {
            preceedingSign = nextType;
            p.eat(nextType);
            
            nextType = p.l.nextType();
        }

        // numberliteral
        if (nextType.within(Type.INT, Type.FLOAT)) {
            if (preceedingSign == null)
                return Literals.NumberLiteral(p);
            else
                return new ASTNode<Type>(
                    name, preceedingSign,
                    Literals.NumberLiteral(p)
                );
        }

        // variable
        if (nextType == Type.VAR)
            if (preceedingSign == null)
                return Variable(p);
            else
                return new ASTNode<Type>(
                    name, preceedingSign,
                    Variable(p)
                );

        // LP binaryexpression RP
        if (nextType == Type.LPAREN) {
            p.eat(Type.LPAREN);
            AST temp = BinaryExpression(p);
            p.eat(Type.RPAREN);

            if (preceedingSign == null)
                return temp;
            else
                return new ASTNode<Type>(
                    name, preceedingSign,
                    temp
                );
        }
        
        throw new TokenTypeException(nextType, "Variable, NumberLiteral, (BinaryExpression)", p.l.getPos());
    }

    /**
     * variable := VAR
     *           | TRUE
     *           | FALSE
     */
    public static ASTValue<String> Variable(Parser p) throws ParseException {
        return new ASTValue<String>(
            "Variable",
            p.eat(Type.VAR, Type.TRUE, Type.FALSE)
        );
    }
    
}
