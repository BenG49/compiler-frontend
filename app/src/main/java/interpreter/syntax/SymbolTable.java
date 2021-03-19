package interpreter.syntax;

import java.util.HashMap;

import interpreter.semantics.FuncData;
import interpreter.semantics.VarData;

public class SymbolTable {
    private HashMap<String, VarData> vars;
    private HashMap<String, FuncData> funcs;

    public SymbolTable() {
        vars = new HashMap<String, VarData>();
        funcs = new HashMap<String, FuncData>();
    }
    public SymbolTable(SymbolTable copy) {
        this.vars = new HashMap<String, VarData>(copy.vars);
        this.funcs = new HashMap<String, FuncData>(copy.funcs);
    }

    public boolean vcontains(String var) {
        return vars.keySet().contains(var);
    }
    
    public void vput(String var, VarData data) {
        vars.put(var, data);
    }

    public VarData vget(String var) {
        return vars.get(var);
    }


    public boolean fcontains(String func) {
        return funcs.keySet().contains(func);
    }
    
    public void fput(String func, FuncData data) {
        funcs.put(func, data);
    }

    public FuncData fget(String var) {
        return funcs.get(var);
    }
}
