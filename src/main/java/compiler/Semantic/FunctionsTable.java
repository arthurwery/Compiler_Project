package compiler.Semantic;

import compiler.Parser.StatementsAndExpressions.FunctionDeclaration;

import java.util.HashMap;
import java.util.Map;

public class FunctionsTable {
    private Map<String, FunctionDeclaration> functions = new HashMap<>();

    public void addFunction(FunctionDeclaration funcDecl) {
        functions.put(funcDecl.getIdentifier().getName(), funcDecl);
    }

    public FunctionDeclaration lookup(String name) {
        return functions.get(name);
    }

    public boolean isDeclared(String name) {
        return functions.containsKey(name);
    }

}
