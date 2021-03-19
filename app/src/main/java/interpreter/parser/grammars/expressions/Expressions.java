package interpreter.parser.grammars.expressions;

import java.util.ArrayList;
import java.util.List;

import interpreter.exception.semantics.InvalidTypeException;
import interpreter.exception.semantics.MismatchedArgCountException;
import interpreter.exception.semantics.ReturnArgCountException;
import interpreter.exception.InterpretException;
import interpreter.syntax.SymbolTable;
import interpreter.syntax.Type;
import interpreter.parser.Parser;
import interpreter.parser.grammars.ast.*;

public class Expressions {
    /**
     * program := statementlist
     */
    public static ASTNode<ASTNode<?>> Program(Parser p, SymbolTable t) throws InterpretException {
        return new ASTNode<ASTNode<?>>(
            "Program", Type.BLANK, StatementList(p, t)
        );
    }

    /**
     * statementlist := statement...
     */
    public static ASTNode<ASTNode<?>> StatementList(Parser p, SymbolTable t) throws InterpretException {
        List<ASTNode<?>> statements = new ArrayList<ASTNode<?>>();
        while (p.l.hasNext()) {
            ASTNode<?> temp = Statement(p, t);
            if (temp != null)
                statements.add(temp);
        }

        return new ASTNode<ASTNode<?>>(
            "StatementList", Type.BLANK, statements
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
    public static ASTNode<?> Statement(Parser p, SymbolTable t) throws InterpretException {
        ASTNode<?> out;
        Type nextType = p.l.nextType();

        // ifstatement
        if (nextType == Type.IF)
            out = IfStatement(p, t);
        // functiondeclaration
        else if ((nextType == Type.VOID || nextType.within(Type.getVarTypes())) && p.l.nextType(3) == Type.LPAREN)
            out = FunctionDeclaration(p, t);
        // declarestatement
        else if (nextType.within(Type.getVarTypes()))
            out = DeclareStatement(p, t);
        else if (nextType == Type.ID) {
            if (p.l.nextType(2) == Type.LPAREN)
                // functioncall
                out = FunctionCall(p, t);
            else
                // assignstatement
                out = AssignStatement(p, t);
        }
        // whilestatement
        else if (nextType == Type.WHILE)
            out = WhileStatement(p, t);
        // forstatement
        else if (nextType == Type.FOR)
            out = ForStatement(p, t);
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
    public static ASTNode<ASTNode<?>> FunctionDeclaration(Parser p, SymbolTable t) throws InterpretException {
        List<ASTNode<?>> out = new ArrayList<ASTNode<?>>();

        out.add(Values.ReturnTypeLiteral(p));
        out.add(Values.Function(p, t, (Type)out.get(0).branches.get(0)));
        p.eat(Type.LPAREN);

        SymbolTable innerScope = new SymbolTable(t);

        Type nextType = p.l.nextType();
        while (nextType != Type.RPAREN) {
            ASTNode<String> type = Values.VarTypeLiteral(p);
            ASTNode<String> var = Values.Variable(p, innerScope, (Type)out.get(0).branches.get(0));

            out.add(new ASTNode<ASTNode<?>>(
                "DeclareStatement", Type.EQUAL,
                type, var
            ));

            nextType = p.l.nextType();
            if (nextType != Type.RPAREN)
                p.eat(Type.COMMA);
        }
        
        p.eatMultiple(Type.RPAREN, Type.LB);
        // TODO: add support for returning tuples
        out.add(BlockStatementList(p, innerScope, new Type[] {(Type)out.get(0).branches.get(0)}));
        p.eat(Type.RB);

        return new ASTNode<ASTNode<?>>(
            "FunctionDeclaration", Type.FUNC,
            out
        );
    }
    
    /**
     * functioncall := FUNC 
     *                     binaryexpression
     *                   | stringliteral
     *                   | truefalseliteral
     *                   | variable
     *                 COMMA
     *                 ...
     *                 RPAREN
     */
    public static ASTNode<ASTNode<?>> FunctionCall(Parser p, SymbolTable t) throws InterpretException {
        List<ASTNode<?>> out = new ArrayList<ASTNode<?>>();
        out.add(Values.Function(p, t));
        p.eat(Type.LPAREN);

        Type nextType = p.l.nextType();
        String name = (String)out.get(0).branches.get(0);
        int argCount = t.fget(name).args.size();

        // TODO: check function type, add functions to vardata
        for (int i = 0; i < argCount; i++) {
            out.add(Literal(p, t, t.fget(name).args.get(i)));

            nextType = p.l.nextType();
            if (nextType == Type.COMMA)
                p.eat(Type.COMMA);

            nextType = p.l.nextType();
        }
        p.eat(Type.RPAREN);

        return new ASTNode<ASTNode<?>>(
            "FunctionCall", Type.CALL,//(String)out.get(0).branches.get(0),
            out
        );
    }

    /**
     * blockstatementlist := statement... RB
     */
    public static ASTNode<ASTNode<?>> BlockStatementList(Parser p, SymbolTable t, Type[] returnType) throws InterpretException {
        List<ASTNode<?>> statements = new ArrayList<ASTNode<?>>();
        Type nextType = p.l.nextType();
        while (nextType != Type.RB) {
            ASTNode<?> temp;
            if (returnType != null && nextType == Type.RETURN)
                temp = ReturnStatement(p, t, returnType);
            else
                temp = Statement(p, t);

            if (temp != null)
                statements.add(temp);

            nextType = p.l.nextType();
        }

        return new ASTNode<ASTNode<?>>(
            "BlockStatementList", Type.BLANK,
            statements
        );
    }

    /**
     * returnstatement := RETURN
     *                      ( binaryexpression
     *                      | truefalseliteral
     *                      | stringliteral
     *                      | variable
     *                      | functioncall
     *                        ""
     *                      | COMMA)...
     */
    public static ASTNode<ASTNode<?>> ReturnStatement(Parser p, SymbolTable t, Type[] returnType) throws InterpretException {
        List<ASTNode<?>> out = new ArrayList<ASTNode<?>>();
        p.eat(Type.RETURN);

        Type nextType = p.l.nextType();
        for (int var = 1; var <= returnType.length; var++) {
            if (var > returnType.length)
                throw new ReturnArgCountException(p.l.next().index, returnType.length, var);
            
            out.add(Literal(p, t, returnType[var-1]));
            
            nextType = p.l.nextType();

            if (nextType != Type.NEWLINE) {
                p.eat(Type.COMMA);
                nextType = p.l.nextType();
            }
        }

        return new ASTNode<ASTNode<?>>(
            "ReturnStatement", Type.RETURN,
            out
        );
    }

    /**
     * whilestatement := WHILE LPAREN boolexpression RPAREN LB blockstatementlist RB
     */
    public static ASTNode<ASTNode<?>> WhileStatement(Parser p, SymbolTable t) throws InterpretException {
        List<ASTNode<?>> out = new ArrayList<ASTNode<?>>();

        p.eatMultiple(Type.WHILE, Type.LPAREN);
        out.add(BoolExp.BoolExpression(p, t));
        p.eatMultiple(Type.RPAREN, Type.LB);
        out.add(BlockStatementList(p, new SymbolTable(t), null));
        p.eat(Type.RB);

        return new ASTNode<ASTNode< ?>>(
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
    public static ASTNode<ASTNode<?>> ForStatement(Parser p, SymbolTable t) throws InterpretException {
        List<ASTNode<?>> out = new ArrayList<ASTNode<?>>();

        p.eatMultiple(Type.FOR, Type.LPAREN);
        
        // TODO: add case for iterable
        Type nextType = p.l.nextType();
        if (nextType != Type.COMMA)
            out.add(DeclareStatement(p, t));

        p.eat(Type.COMMA);
        nextType = p.l.nextType();
        if (nextType != Type.COMMA)
            out.add(BoolExp.BoolExpression(p, t));

        p.eat(Type.COMMA);
        nextType = p.l.nextType();
        if (nextType != Type.RPAREN)
            out.add(AssignStatement(p, t));

        p.eatMultiple(Type.RPAREN, Type.LB);
        out.add(BlockStatementList(p, new SymbolTable(t), null));
        p.eat(Type.RB);

        return new ASTNode<ASTNode<?>>(
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
     *                           | variable
     *                           | functioncall
     *                             ""
     */
    public static ASTNode<?> DeclareStatement(Parser p, SymbolTable t) throws InterpretException {
        List<ASTNode<?>> out = new ArrayList<ASTNode<?>>();
        Type varType = p.l.nextType();
        // type
        out.add(Values.VarTypeLiteral(p));
        out.add(Values.Variable(p, t, varType));

        Type nextType = p.l.nextType();
        if (nextType == Type.COMMA) {
            while (nextType != Type.NEWLINE) {
                p.eat(Type.COMMA);
                out.add(Values.Variable(p, t, varType));
                nextType = p.l.nextType();
            }

            return new ASTNode<ASTNode<?>>(
                "DeclareStatement", Type.EQUAL,
                out);
        } else if (nextType == Type.NEWLINE)
            return new ASTNode<ASTNode<?>>(
                "DeclareStatement", Type.EQUAL,
                out);

        // EQUALS
        p.eat(Type.EQUAL);
        
        out.add(Literal(p, t, varType));
        
        return new ASTNode<ASTNode<?>>(
            "DeclareStatement", Type.EQUAL,
            out
        );
    }

    /**
     * assignstatement := variable
     *                        EQUALS varassignment
     *                       | assignoperator
     *                            ""
     *                          | binaryexpression
     */
    public static ASTNode<ASTNode<?>> AssignStatement(Parser p, SymbolTable t) throws InterpretException {
        List<ASTNode<?>> out = new ArrayList<ASTNode<?>>();
        // variable
        out.add(Values.Variable(p, t));

        Type varType = t.vget((String)out.get(0).branches.get(0)).type;
        Type nextType = p.l.nextType();
        // EQUALS
        if (nextType == Type.EQUAL) {
            p.eat(Type.EQUAL);

            out.add(Literal(p, t, varType));
        // TODO: add string addition
        } else if (varType.within(Type.FLOAT_ID, Type.INT_ID)) {
            Type[] temp = Operators.AssignOperator(p);
            // temp list to add to manual addition node (ex 1++ -> + 1 1)
            List<ASTNode<?>> addNode = new ArrayList<ASTNode<?>>();

            addNode.add(out.get(0));
            // +=
            if (temp[0] != temp[1])
                // x+=1 -> x=x+1
                addNode.add(BinExp.BinaryExpression(p, t));
            // ++
            else
                addNode.add(new ASTNode<Integer>("IntLiteral", Type.INT, 1));
            
            out.add(new ASTNode<ASTNode<?>>(
                "BinaryExpression", temp[0],
                addNode
            ));
        }
        
        return new ASTNode<ASTNode<?>>(
            "AssignStatement", Type.EQUAL,
            out
        );
    }

    /**
     * varassignment := variable
     *                | function
     *                | stringliteral
     *                | truefalseliteral
     *                | binaryexpression
     */
    public static ASTNode<?> Literal(Parser p, SymbolTable t, Type varType) throws InterpretException {
        if (p.l.nextType() == Type.ID) {
            ASTNode<?> temp;
            String assignment;
            if (p.l.nextType(2) == Type.LPAREN) {
                temp = FunctionCall(p, t);
                assignment = (String)((ASTNode<?>)temp.branches.get(0)).branches.get(0);
            } else {
                temp = Values.Variable(p, t);
                assignment = (String)temp.branches.get(0);
            }

            if (varType != t.vget(assignment).type)
                throw new InvalidTypeException(p.l.next().index, t.vget(assignment).type, varType);
            
            return temp;
        }

        // stringliteral
        else if (varType == Type.STR_ID)
            return Values.StringLiteral(p);
        // truefalseliteal
        else if (varType == Type.BOOL_ID)
            return Values.TrueFalseLiteral(p);
        // binaryexpression
        else if (varType.within(Type.INT_ID, Type.FLOAT_ID))
            return BinExp.BinaryExpression(p, t);
        else
            throw new InvalidTypeException(p.l.next().index, p.l.nextType(), varType);
    }

    /**
     * ifstatement := IF LPAREN boolexpression RPAREN LB blockstatementlist RB
     *                    ""
     *                  | ELSE LB blockstatementlist RB
     *                  | ELSE ifstatement
     */
    public static ASTNode<ASTNode<?>> IfStatement(Parser p, SymbolTable t) throws InterpretException {
        List<ASTNode<?>> out = new ArrayList<ASTNode<?>>();

        p.eatMultiple(Type.IF, Type.LPAREN);
        out.add(BoolExp.BoolExpression(p, t));
        p.eatMultiple(Type.RPAREN, Type.LB);
        out.add(BlockStatementList(p, new SymbolTable(t), null));
        p.eat(Type.RB);
        
        Type nextType = p.l.nextType();
        // ELSE
        if (nextType == Type.ELSE) {
            p.eat(nextType);

            nextType = p.l.nextType();
            // ifstatement
            if (nextType == Type.IF)
                out.add(IfStatement(p, t));
            // LB blockstatementlist RB
            else {
                p.eat(Type.LB);
                out.add(BlockStatementList(p, new SymbolTable(t), null));
                p.eat(Type.RB);
            }
        }
        
        return new ASTNode<ASTNode<?>>(
            "IfStatement", Type.IF,
            out
        );
    }
    
}
