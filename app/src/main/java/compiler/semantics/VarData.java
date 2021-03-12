package compiler.semantics;

import compiler.syntax.Type;

public class VarData {
    public String var;
    public Type type;
    public String scope;

    public VarData(String var, Type type, String scope) {
        this.var = var;
        this.type = type;
        this.scope = scope;
    }
}
