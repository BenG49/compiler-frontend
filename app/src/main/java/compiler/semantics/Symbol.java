package compiler.semantics;

import compiler.syntax.Type;

public class Symbol {
    public String var;
    public Type type;
    public String scope;

    public Symbol(String var, String type) {
        this(var, Type.valueOf(type));
    }
    public Symbol(String var, Type type) {
        this.var = var;
        this.type = type;
        // TODO: use scope
    }

    public boolean equals(Symbol other) {
        return equals(other.var, other.type);
    }
    public boolean equals(String var, Type type) {
        return this.var.equals(var) && this.type == type;
    }
    
}
