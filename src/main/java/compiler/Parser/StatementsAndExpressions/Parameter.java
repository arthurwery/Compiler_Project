package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.Visitor;

public class Parameter extends Expression {
    String type;
    String identifier;

    public Parameter(String type, String identifier) {
        this.type = type;
        this.identifier = identifier;
        this.kind = "Parameter";
        this.setType(type);
    }

    public String getType() {
        return this.type;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\ttype: " + this.type + ",\n" +
                "\tidentifier: " + this.identifier + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Parameter other){
            return this.type.equals(other.type) && this.identifier.equals(other.identifier) && this.kind.equals(other.kind);
        }
        return false;
    }


}
