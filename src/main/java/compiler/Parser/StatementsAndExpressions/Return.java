package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.Visitor;

public class Return extends Statement {
    Expression value;

    public Return(Expression value) {
        this.value = value;
        this.kind = "Return";
    }

    public Expression getValue() {
        return value;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tvalue: " + this.value.toString().replaceAll("\n", "\n\t") + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Return other){
            return this.value.equals(other.value) && this.kind.equals(other.kind);
        }
        return false;
    }

}
