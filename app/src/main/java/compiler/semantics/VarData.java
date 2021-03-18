package compiler.semantics;

import compiler.syntax.Type;

public class VarData {
    public Type type;
    public String scope;
    public boolean function;

    public VarData (Type type, String scope, boolean function) {
        this.type = type;
        this.scope = scope;
        this.function = function;
    }

    public String toString() {
        return "("+type+", "+scope+", "+function+")";
    }
}
