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
     * statement :=     ifstatement
     *                | declarestatement
     *                | assignstatement
     *                | whilestatement
     *                | forstatement
     *                | functioncall
     *                | functiondeclaration
     *                | ""
     *              NEWLINE
     */
    public static AST Statement(Parser p) throws ParseException {
        AST out;
        Type nextType = p.l.nextType();

        // ifstatement
        if (nextType == Type.IF)
            out = IfStatement(p);
        // declarestatement
        else if (nextType.within(Type.getVarTypes()))
            out = DeclareStatement(p);
        // assignstatement
        else if (nextType == Type.VAR)
            out = AssignStatement(p);
        else if (nextType == Type.WHILE)
            out = WhileStatement(p);
        else if (nextType == Type.FOR)
            out = ForStatement(p);
        else if (nextType == Type.VOID)
            out = FunctionDeclaration(p, null);
        else {
            p.eat(Type.NEWLINE);
            return null;
        }

        p.eat(Type.NEWLINE);

        return out;
    }
    
    /**
     * functiondeclaration :=     vartypeliteral
     *                          | VOID
     *                       FUNC
     *                            vartypeliteral variable...
     *                       RPAREN LB blockstatementlist RB
     */
    public static ASTNode<String> FunctionDeclaration(Parser p, Type r) throws ParseException {
        List<AST> out = new ArrayList<AST>();

        if (r == null)
            out.add(Values.ReturnTypeLiteral(p));
        else
            out.add(new ASTNode<Type>("ReturnTypeLiteral", r));
        String name = p.eat(Type.FUNC);

        Type nextType = p.l.nextType();
        while (nextType != Type.RPAREN) {
            out.add(Values.VarTypeLiteral(p));
            out.add(Values.Variable(p));
            nextType = p.l.nextType();
            if (nextType != Type.RPAREN)
                p.eat(Type.COMMA);
        }
        
        p.eat(Type.RPAREN);
        p.eat(Type.LB);
        out.add(BlockStatementList(p));
        p.eat(Type.RB);

        return new ASTNode<String>(
            "FunctionDeclaration", name.substring(0, name.length()-1).replaceAll(" ", ""),
            out
        );
    }
    
    /**
     * functioncall := FUNC 
     *                     binaryexpression
     *                   | stringliteral
     *                   | truefalseliteral
     *                   | VAR
     *                 COMMA
     *                 ...
     *                 RPAREN
     */
    public static ASTNode<String> FunctionCall(Parser p) throws ParseException {
        List<AST> out = new ArrayList<AST>();
        String func = p.eat(Type.FUNC);
        Type nextType = p.l.nextType();

        while (nextType != Type.RPAREN) {
            // VAR
            if (nextType == Type.VAR)
                out.add(Values.Variable(p));
            // stringliteral
            else if (nextType == Type.STR)
                out.add(Values.StringLiteral(p));
            // truefalseliteral
            else if (nextType.within(Type.TRUE, Type.FALSE))
                out.add(Values.TrueFalseLiteral(p));
            // binaryexpression
            else
                out.add(BinExp.BinaryExpression(p));
            
            nextType = p.l.nextType();
            if (nextType == Type.COMMA)
                p.eat(Type.COMMA);

            nextType = p.l.nextType();
        }
        p.eat(Type.RPAREN);

        return new ASTNode<String>(
            "FunctionCall", func.substring(0, func.length()-1).replaceAll(" ", ""),
            out
        );
    }

    /**
     * blockstatementlist := statement... RB
     */
    public static ASTNode<String> BlockStatementList(Parser p) throws ParseException {
        List<AST> statements = new ArrayList<AST>();
        Type nextType = p.l.nextType();
        while (nextType != Type.RB) {
            AST temp;
            if (nextType == Type.RETURN)
                temp = ReturnStatement(p);
            else
                temp = Statement(p);

            if (temp != null)
                statements.add(temp);

            nextType = p.l.nextType();
        }

        return new ASTNode<String>(
            "BlockStatementList", "",
            statements
        );
    }

    /**
     * returnstatement := RETURN
     *                      ( binaryexpression
     *                      | truefalseliteral
     *                      | stringliteral
     *                      | variable
     *                      
     *                        ""
     *                      | COMMA)...
     */
    public static ASTNode<Type> ReturnStatement(Parser p) throws ParseException {
        List<AST> out = new ArrayList<AST>();
        p.eat(Type.RETURN);

        Type nextType = p.l.nextType();
        while (nextType != Type.NEWLINE) {
            if (nextType.within(Type.TRUE, Type.FALSE))
                out.add(Values.TrueFalseLiteral(p));
            else if (nextType.within(Type.STR))
                out.add(Values.StringLiteral(p));
            else if (nextType.within(Type.VAR))
                out.add(Values.Variable(p));
            else
                out.add(BinExp.BinaryExpression(p));
            
            nextType = p.l.nextType();

            if (nextType != Type.NEWLINE) {
                p.eat(Type.COMMA);
                nextType = p.l.nextType();
            }
        }

        return new ASTNode<Type>(
            "ReturnStatement", Type.RETURN,
            out
        );
    }

    /**
     * whilestatement := WHILE LPAREN boolexpression RPAREN LB blockstatementlist RB
     */
    public static ASTNode<Type> WhileStatement(Parser p) throws ParseException {
        List<AST> out = new ArrayList<AST>();

        p.eat(Type.WHILE);
        p.eat(Type.LPAREN);
        out.add(BoolExp.BoolExpression(p));
        p.eat(Type.RPAREN);
        p.eat(Type.LB);
        out.add(BlockStatementList(p));
        p.eat(Type.RB);

        return new ASTNode<Type>(
            "WhileExpression", Type.WHILE,
            out
        );
    }

    /**
     * forstatement := FOR LPAREN
     *                             declareexprression
     *                           | ""
     *                         COMMA
     *                             boolexpression
     *                           | ""
     *                         COMMA
     *                             assignstatement
     *                           | ""
     *                   | vartypeliteral variable IN iterable
     *                 RPAREN LB blockstatementlist RB
     */
    public static ASTNode<Type> ForStatement(Parser p) throws ParseException {
        List<AST> out = new ArrayList<AST>();

        p.eat(Type.FOR);
        p.eat(Type.LPAREN);
        
        // TODO: add case for iterable
        Type nextType = p.l.nextType();
        if (nextType != Type.COMMA)
            out.add(DeclareStatement(p));

        p.eat(Type.COMMA);
        nextType = p.l.nextType();
        if (nextType != Type.COMMA)
            out.add(BoolExp.BoolExpression(p));

        p.eat(Type.COMMA);
        nextType = p.l.nextType();
        if (nextType != Type.RPAREN)
            out.add(AssignStatement(p));

        p.eat(Type.RPAREN);
        p.eat(Type.LB);
        out.add(BlockStatementList(p));
        p.eat(Type.RB);

        return new ASTNode<Type>(
            "ForStatement", Type.FOR,
            out
        );
    }

    /**
     * declarestatement := vartypeliteral variable
     *                         ""
     *                       | COMMA variable...
     *                       | EQUALS 
     *                             binaryexpression
     *                           | stringliteral
     *                           | truefalseliteral
     *                             ""
     */
    public static AST DeclareStatement(Parser p) throws ParseException {
        List<AST> out = new ArrayList<AST>();
        Type temp = p.l.nextType();
        // variable
        out.add(Values.VarTypeLiteral(p));
        out.add(Values.Variable(p));

        Type nextType = p.l.nextType();
        if (nextType == Type.FUNC)
            return FunctionDeclaration(p, temp);
        
        if (nextType == Type.COMMA) {
            while (nextType != Type.NEWLINE) {
                p.eat(Type.COMMA);
                out.add(Values.Variable(p));
                nextType = p.l.nextType();
            }

            return new ASTNode<Type>(
                "DeclareExpression", Type.EQUAL,
                out
            );
        }

        // EQUALS
        p.eat(Type.EQUAL);
        
        nextType = p.l.nextType();
        // stringliteral
        if (nextType == Type.STR)
            out.add(Values.StringLiteral(p));
        // truefalseliteral
        else if (nextType.within(Type.TRUE, Type.FALSE))
            out.add(Values.TrueFalseLiteral(p));
        // binaryexpression
        else
            out.add(BinExp.BinaryExpression(p));
        
        
        return new ASTNode<Type>(
            "DeclareExpression", Type.EQUAL,
            out
        );
    }

    /**
     * assignstatement := variable
     *                        EQUALS 
     *                            binaryexpression
     *                          | stringliteral
     *                          | truefalseliteral
     *                   | assignoperator
     *                        ""
     *                      | binaryexpression
     */
    public static ASTNode<Type> AssignStatement(Parser p) throws ParseException {
        List<AST> out = new ArrayList<AST>();
        // variable
        out.add(Values.Variable(p));

        Type nextType = p.l.nextType();
        // EQUALS
        if (nextType == Type.EQUAL) {
            p.eat(Type.EQUAL);

            nextType = p.l.nextType();
            // stringliteral
            if (nextType == Type.STR)
                out.add(Values.StringLiteral(p));
            // truefalseliteal
            else if (nextType.within(Type.TRUE, Type.FALSE))
                out.add(Values.TrueFalseLiteral(p));
            // binaryexpression
            else
                out.add(BinExp.BinaryExpression(p));
        } else {
            Type[] temp = Operators.AssignOperator(p);
            // temp list to add to manual addition node
            List<AST> addNode = new ArrayList<AST>();

            addNode.add(out.get(0));
            // +=
            if (temp[0] != temp[1])
                // x+=1 -> x=x+1
                addNode.add(BinExp.BinaryExpression(p));
            // ++
            else
                addNode.add(new ASTValue<Integer>("IntLiteral", 1));
            
            out.add(new ASTNode<Type>(
                "BinaryExpression", temp[0],
                addNode
            ));
        }
        
        return new ASTNode<Type>(
            "AssignStatement", Type.EQUAL,
            out
        );
    }

    /**
     * ifstatement := IF LPAREN boolexpression RPAREN LB blockstatementlist RB
     *                    ""
     *                  | ELSE LB blockstatementlist RB
     *                  | ELSE ifstatement
     */
    public static ASTNode<Type> IfStatement(Parser p) throws ParseException {
        final String name = "IfStatement";
        List<AST> out = new ArrayList<AST>();

        p.eat(Type.IF);
        p.eat(Type.LPAREN);
        out.add(BoolExp.BoolExpression(p));
        p.eat(Type.RPAREN);

        p.eat(Type.LB);
        out.add(BlockStatementList(p));
        p.eat(Type.RB);
        
        Type nextType = p.l.nextType();
        // ELSE
        if (nextType == Type.ELSE) {
            p.eat(nextType);

            nextType = p.l.nextType();
            // ifstatement
            if (nextType == Type.IF)
                out.add(IfStatement(p));
            // LB blockstatementlist RB
            else {
                p.eat(Type.LB);
                out.add(BlockStatementList(p));
                p.eat(Type.RB);
            }
        }
        
        return new ASTNode<Type>(
            name, Type.IF,
            out
        );
    }
    
}
