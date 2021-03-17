package compiler.semantics;

import compiler.syntax.Type;

public class VarData {
    public Type type;
    public String scope;

    public VarData (Type type, String scope) {
        this.type = type;
        this.scope = scope;
    }

    public String toString() {
        return type+", "+scope;
    }
}
