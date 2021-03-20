package compiler.semantics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import compiler.syntax.Type;

public class FuncData {
    public List<Type> type;
    public List<Type> args;

    public FuncData(Type type, Type args) {
        this(new ArrayList<Type>(Arrays.asList(type)), new ArrayList<Type>(Arrays.asList(args)));
    }
    public FuncData(Type type, Type... args) {
        this(new ArrayList<Type>(Arrays.asList(type)), new ArrayList<Type>(Arrays.asList(args)));
    }
    public FuncData(List<Type> type, Type... args) {
        this(type, new ArrayList<Type>(Arrays.asList(args)));
    }
    public FuncData(List<Type> type, List<Type> args) {
        this.type = type;
        this.args = args;
    }

    public String toString() {
        return "("+type+", "+args+")";
    }
}
