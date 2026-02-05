package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.Visitor;

public class Free extends Statement {
    String identifier;

    public Free(String identifier) {
        this.identifier = identifier;
        this.kind = "Free";
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tidentifier: " + this.identifier + "\n" +
                "}";
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Free other){
            return this.identifier.equals(other.identifier) && this.kind.equals(other.kind);
        }
        return false;
    }

}
