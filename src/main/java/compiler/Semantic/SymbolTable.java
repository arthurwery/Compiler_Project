package compiler.Semantic;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {
    private Map<String, String> globalScope;
    private Stack<Stack<Map<String, String>>> localScopes;

    public SymbolTable() {
        this.globalScope = new HashMap<>();
        this.localScopes = new Stack<>();
    }

    public void enterScope() {
        Stack<Map<String, String>> currentScope = localScopes.peek();
        currentScope.push(new HashMap<>());
    }

    public void exitScope() {
        Stack<Map<String, String>> currentScope = localScopes.peek();
        if (!currentScope.empty()) {
            currentScope.pop();
        } else {
            throw new IllegalStateException("No scope to exit.");
        }
    }

    public void startFunction() {
        localScopes.push(new Stack<>());
        Stack<Map<String, String>> currentScope = localScopes.peek();
        currentScope.push(new HashMap<>());
    }

    public Map<String, String> getCurrentScope() {
        return localScopes.peek().peek();
    }

    public Stack<Stack<Map<String, String>>> getLocalScopes() {
        return localScopes;
    }

    public void endFunction() {
        if (!localScopes.empty()) {
            localScopes.pop();
        } else {
            throw new IllegalStateException("No function to end.");
        }
    }

    public void add(String identifier, String type) throws SemanticException {
        if (localScopes.empty()) {
            if (globalScope.containsKey(identifier)) {
                throw new SemanticException("Variable '" + identifier + "' is already declared in the global scope.");
            }
            globalScope.put(identifier, type);
            return;
        } else {
            Map<String, String> currentScope = localScopes.peek().peek();
            if (currentScope.containsKey(identifier)) {
                throw new SemanticException("Variable '" + identifier + "' is already declared in this scope.");
            }
            currentScope.put(identifier, type);
        }
    }

    public String lookup(String identifier) {
        if (!localScopes.empty()) {
            Stack<Map<String, String>> currentScope = localScopes.peek();
            for (int i = localScopes.size() - 1; i >= 0; i--) {
                Map<String, String> scope = currentScope.get(i);
                if (scope.containsKey(identifier)) {
                    return scope.get(identifier);
                }
            }
        }
        if (globalScope.containsKey(identifier)) {
            return globalScope.get(identifier);
        }
        return null;
    }

    public boolean isDeclared(String identifier) {
        return lookup(identifier) != null;
    }
}