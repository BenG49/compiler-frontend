package compiler.syntax;

import java.util.HashMap;

import compiler.semantics.VarData;

public class SymbolTable {
    private HashMap<String, VarData> vars;
    private HashMap<String, VarData> funcs;

    public SymbolTable() {
        vars = new HashMap<String, VarData>();
        funcs = new HashMap<String, VarData>();
    }
    public SymbolTable(SymbolTable copy) {
        this.vars = new HashMap<String, VarData>(copy.vars);
        this.funcs = new HashMap<String, VarData>(copy.funcs);
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
    
    public void fput(String func, VarData data) {
        funcs.put(func, data);
    }

    public VarData fget(String var) {
        return funcs.get(var);
    }
}
