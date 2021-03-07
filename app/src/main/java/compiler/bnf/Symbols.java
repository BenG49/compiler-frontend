package compiler.bnf;

import java.util.HashMap;

public class Symbols {
    private HashMap<String, String> symbols;

    public Symbols(HashMap<String, String> expressions) {
        this.symbols = expressions;
    }

    public boolean evalSymbol(String symbol, String in) {
        symbol = sanitize(symbol);
        if (!symbols.keySet().contains(symbol))
            return false;
        
        String[] expressions = symbols.get(symbol).replaceAll(" ", "").split("[><]");

        for (String e : expressions) {
            if (e.contains("'") {
                // somehow need to increment
            } else if (!evalSymbol(sanitize(e)))
                return false;
        }
        
        return false;
    }

    private String sanitize(String in) {
        String out = in.toLowerCase().replaceAll(" ", "");
        if (!out.contains("<"))
            out = "<"+out;
        if (!out.contains(">"))
            out = ">"+out;
            
        return out;
    }
}
