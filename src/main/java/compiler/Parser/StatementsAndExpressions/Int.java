package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.Visitor;

public class Int extends Expression {
    int value;

    public Int(int value) {
        this.value = value;
        this.kind = "Int";
        this.setType("int");
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tvalue: " + this.value + "\n" +
                "}";
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Int other){
            return this.value == other.value && this.kind.equals(other.kind);
        }
        return false;
    }

}
