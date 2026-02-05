package compiler.CodeGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTableCode {
    private Map<String, Integer> globalScope;
    private Stack<Stack<Map<String, Integer>>> localScopes;

    public SymbolTableCode() {
        this.globalScope = new HashMap<>();
        this.localScopes = new Stack<>();
    }

    public void enterScope() {
        Stack<Map<String, Integer>> currentScope = localScopes.peek();
        currentScope.push(new HashMap<>());
    }

    public void exitScope() {
        Stack<Map<String, Integer>> currentScope = localScopes.peek();
        if (!currentScope.empty()) {
            currentScope.pop();
        } else {
            throw new IllegalStateException("No scope to exit.");
        }
    }

    public void startFunction() {
        localScopes.push(new Stack<>());
        Stack<Map<String, Integer>> currentScope = localScopes.peek();
        currentScope.push(new HashMap<>());
    }

    public void endFunction() {
        if (!localScopes.empty()) {
            localScopes.pop();
        } else {
            throw new IllegalStateException("No function to end.");
        }
    }

    public void add(String identifier, int index) {
        if (localScopes.empty()) {
            if (globalScope.containsKey(identifier)) {
                throw new RuntimeException("Variable '" + identifier + "' is already declared in the global scope.");
            }
            globalScope.put(identifier, index);
        } else {
            Map<String, Integer> currentScope = localScopes.peek().peek();
            if (currentScope.containsKey(identifier)) {
                throw new RuntimeException("Variable '" + identifier + "' is already declared in this scope.");
            }
            currentScope.put(identifier, index);
        }
    }

    public Integer lookupIndex(String identifier) {
        if (!localScopes.empty()) {
            Stack<Map<String, Integer>> currentScope = localScopes.peek();
            for (int i = currentScope.size() - 1; i >= 0; i--) {
                Map<String, Integer> scope = currentScope.get(i);
                if (scope.containsKey(identifier)) {
                    return scope.get(identifier);
                }
            }
        }
        return globalScope.get(identifier);
    }
    public boolean isGlobal(String identifier) {
        return globalScope.containsKey(identifier);
    }

}
