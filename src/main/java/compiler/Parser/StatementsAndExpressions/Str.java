package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.Visitor;

public class Str extends Expression {
    String value;

    public Str(String value) {
        this.value = value;
        this.kind = "String";
        this.setType("string");
    }

    public String toString() {
        return "{\n" +
                "\t" + "kind: " + this.kind + ",\n" +
                "\t" + "value: " + this.value + "\n" +
                "}";
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Str other){
            return this.value.equals(other.value) && this.kind.equals(other.kind);
        }
        return false;
    }


}
