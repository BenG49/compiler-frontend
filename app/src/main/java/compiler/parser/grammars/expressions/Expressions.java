package compiler.parser.grammars.expressions;

import java.util.ArrayList;
import java.util.List;

import compiler.Empty;
import compiler.exception.semantics.InvalidTypeException;
import compiler.exception.CompileException;
import compiler.syntax.Type;
import compiler.parser.Parser;
import compiler.parser.grammars.ast.*;
import compiler.semantics.VarData;

public class Expressions {
    /**
     * program := statementlist
     */
    public static ASTNode<String, ASTNode<?, ?>> Program(Parser p) throws CompileException {
        return new ASTNode<String, ASTNode<?, ?>>(
            "Program", "",
            StatementList(p)
        );
    }

    /**
     * statementlist := statement...
     */
    public static ASTNode<String, ASTNode<?, ?>> StatementList(Parser p) throws CompileException {
        List<ASTNode<?, ?>> statements = new ArrayList<ASTNode<?, ?>>();
        while (p.l.hasNext()) {
            ASTNode<?, ?> temp = Statement(p);
            if (temp != null)
                statements.add(temp);
        }

        return new ASTNode<String, ASTNode<?, ?>>(
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
    public static ASTNode<?, ?> Statement(Parser p) throws CompileException {
        ASTNode<?, ?> out;
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
     *                            declarestatement...
     *                       RPAREN LB blockstatementlist RB
     */
    public static ASTNode<String, ASTNode<?, ?>> FunctionDeclaration(Parser p, Type r) throws CompileException {
        List<ASTNode<?, ?>> out = new ArrayList<ASTNode<?, ?>>();

        if (r == null)
            out.add(Values.ReturnTypeLiteral(p));
        else
            out.add(new ASTNode<Empty, Type>("ReturnTypeLiteral", new Empty(), r));
        String name = p.eat(Type.FUNC);

        Type nextType = p.l.nextType();
        while (nextType != Type.RPAREN) {
            ASTNode<Empty, Type> type = Values.VarTypeLiteral(p);
            ASTNode<Empty, String> var = Values.Variable(p, true);
            p.t.put((String)var.branches.get(0), new VarData((Type)type.branches.get(0), name));

            out.add(new ASTNode<Type, ASTNode<?, ?>>(
                "DeclareStatement", Type.EQUAL,
                type, var
            ));

            nextType = p.l.nextType();
            if (nextType != Type.RPAREN)
                p.eat(Type.COMMA);
        }
        
        p.eat(Type.RPAREN);
        p.eat(Type.LB);
        out.add(BlockStatementList(p));
        p.eat(Type.RB);

        return new ASTNode<String, ASTNode<?, ?>>(
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
    public static ASTNode<String, ASTNode<?, ?>> FunctionCall(Parser p) throws CompileException {
        List<ASTNode<?, ?>> out = new ArrayList<ASTNode<?, ?>>();
        String func = p.eat(Type.FUNC);
        Type nextType = p.l.nextType();

        // TODO: check function type, add functions to vardata
        while (nextType != Type.RPAREN) {
            // VAR
            if (nextType == Type.VAR)
                out.add(Values.Variable(p, false));
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

        return new ASTNode<String, ASTNode<?, ?>>(
            "FunctionCall", func.substring(0, func.length()-1).replaceAll(" ", ""),
            out
        );
    }

    /**
     * blockstatementlist := statement... RB
     */
    public static ASTNode<String, ASTNode<?, ?>> BlockStatementList(Parser p) throws CompileException {
        List<ASTNode<?, ?>> statements = new ArrayList<ASTNode<?, ?>>();
        Type nextType = p.l.nextType();
        while (nextType != Type.RB) {
            ASTNode<?, ?> temp;
            if (nextType == Type.RETURN)
                temp = ReturnStatement(p);
            else
                temp = Statement(p);

            if (temp != null)
                statements.add(temp);

            nextType = p.l.nextType();
        }

        return new ASTNode<String, ASTNode<?, ?>>(
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
    public static ASTNode<Type, ASTNode<?, ?>> ReturnStatement(Parser p) throws CompileException {
        List<ASTNode<?, ?>> out = new ArrayList<ASTNode<?, ?>>();
        p.eat(Type.RETURN);

        Type nextType = p.l.nextType();
        while (nextType != Type.NEWLINE) {
            if (nextType.within(Type.TRUE, Type.FALSE))
                out.add(Values.TrueFalseLiteral(p));
            else if (nextType.within(Type.STR))
                out.add(Values.StringLiteral(p));
            else if (nextType.within(Type.VAR))
                out.add(Values.Variable(p, false));
            else
                out.add(BinExp.BinaryExpression(p));
            
            nextType = p.l.nextType();

            if (nextType != Type.NEWLINE) {
                p.eat(Type.COMMA);
                nextType = p.l.nextType();
            }
        }

        return new ASTNode<Type, ASTNode<?, ?>>(
            "ReturnStatement", Type.RETURN,
            out
        );
    }

    /**
     * whilestatement := WHILE LPAREN boolexpression RPAREN LB blockstatementlist RB
     */
    public static ASTNode<Type, ASTNode<?, ?>> WhileStatement(Parser p) throws CompileException {
        List<ASTNode<?, ?>> out = new ArrayList<ASTNode<?, ?>>();

        p.eat(Type.WHILE);
        p.eat(Type.LPAREN);
        out.add(BoolExp.BoolExpression(p));
        p.eat(Type.RPAREN);
        p.eat(Type.LB);
        out.add(BlockStatementList(p));
        p.eat(Type.RB);

        return new ASTNode<Type, ASTNode<?, ?>>(
            "WhileExpression", Type.WHILE,
            out
        );
    }

    /**
     * forstatement := FOR LPAREN
     *                             declarestatement
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
    public static ASTNode<Type, ASTNode<?, ?>> ForStatement(Parser p) throws CompileException {
        List<ASTNode<?, ?>> out = new ArrayList<ASTNode<?, ?>>();

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

        return new ASTNode<Type, ASTNode<?, ?>>(
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
    public static ASTNode<?, ?> DeclareStatement(Parser p) throws CompileException {
        List<ASTNode<?, ?>> out = new ArrayList<ASTNode<?, ?>>();
        Type type = p.l.nextType();
        // variable
        out.add(Values.VarTypeLiteral(p));

        Type nextType = p.l.nextType();
        if (nextType == Type.FUNC)
            return FunctionDeclaration(p, type);

        out.add(Values.Variable(p, true));

        p.t.put((String)out.get(1).branches.get(0), new VarData(type, ""));
        
        if (nextType == Type.COMMA) {
            while (nextType != Type.NEWLINE) {
                p.eat(Type.COMMA);
                ASTNode<Empty, String> var = Values.Variable(p, true);
                out.add(var);
                p.t.put((String)var.branches.get(0), new VarData(type, ""));
                nextType = p.l.nextType();
            }

            return new ASTNode<Type, ASTNode<?, ?>>(
                "DeclareStatement", Type.EQUAL,
                out
            );
        } else if (nextType == Type.NEWLINE) {
            return new ASTNode<Type, ASTNode<?, ?>>(
                "DeclareStatement", Type.EQUAL,
                out
            );
        }

        // EQUALS
        p.eat(Type.EQUAL);
        
        nextType = p.l.nextType();
        // stringliteral
        if (nextType == Type.STR) {
            if (nextType == type)
                out.add(Values.StringLiteral(p));
            else
                throw new InvalidTypeException(p.l.getPos(), Type.STR, type);
        }
        // truefalseliteral
        else if (nextType.within(Type.TRUE, Type.FALSE)) {
            if (nextType == type)
                out.add(Values.TrueFalseLiteral(p));
            else
                throw new InvalidTypeException(p.l.getPos(), nextType, type);
        }
        // binaryexpression
        else
            out.add(BinExp.BinaryExpression(p));
        
        
        return new ASTNode<Type, ASTNode<?, ?>>(
            "DeclareStatement", Type.EQUAL,
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
    public static ASTNode<Type, ASTNode<?, ?>> AssignStatement(Parser p) throws CompileException {
        List<ASTNode<?, ?>> out = new ArrayList<ASTNode<?, ?>>();
        // variable
        out.add(Values.Variable(p, false));

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
            List<ASTNode<?, ?>> addNode = new ArrayList<ASTNode<?, ?>>();

            addNode.add(out.get(0));
            // +=
            if (temp[0] != temp[1])
                // x+=1 -> x=x+1
                addNode.add(BinExp.BinaryExpression(p));
            // ++
            else
                addNode.add(new ASTNode<Empty, Integer>("IntLiteral", new Empty(), 1));
            
            out.add(new ASTNode<Type, ASTNode<?, ?>>(
                "BinaryExpression", temp[0],
                addNode
            ));
        }
        
        return new ASTNode<Type, ASTNode<?, ?>>(
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
    public static ASTNode<Type, ASTNode<?, ?>> IfStatement(Parser p) throws CompileException {
        final String name = "IfStatement";
        List<ASTNode<?, ?>> out = new ArrayList<ASTNode<?, ?>>();

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
        
        return new ASTNode<Type, ASTNode<?, ?>>(
            name, Type.IF,
            out
        );
    }
    
}
