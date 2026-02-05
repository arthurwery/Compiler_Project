package compiler.Semantic;

import compiler.Parser.StatementsAndExpressions.Struct;

import java.util.HashMap;
import java.util.Map;

public class StructTable {


    private Map<String, Struct> structs = new HashMap<>();

    public void add(Struct structDecl) {
        structs.put(structDecl.getIdentifier().getName(), structDecl);
    }

    public Struct lookup(String name) {
        return structs.get(name);
    }

    public boolean isDeclared(String name) {
        return structs.containsKey(name);
    }
}
