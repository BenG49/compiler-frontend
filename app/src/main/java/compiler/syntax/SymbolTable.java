package compiler.syntax;

import java.util.HashMap;

import compiler.semantics.VarData;

public class SymbolTable {
    private HashMap<String, VarData> vars;

    public SymbolTable() {
        vars = new HashMap<String, VarData>();
    }

    public boolean contains(String var) {
        return vars.keySet().contains(var);
    }
    
    public void put(String var, VarData data) {
        vars.put(var, data);
    }

    public VarData get(String var) {
        return vars.get(var);
    }
}
