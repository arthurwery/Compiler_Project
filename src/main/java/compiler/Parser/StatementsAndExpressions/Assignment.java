package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class Assignment extends Expression {
    Identifier identifier;
    Expression value;

    public Assignment(Identifier identifier, Expression value) {
        this.identifier = identifier;
        this.value = value;
        this.kind = "Assignment";
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Expression getValue() {
        return value;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tidentifier: " + this.identifier + ",\n" +
                "\tvalue: " + (value != null ? value.toString().replaceAll("\n", "\n\t") : null) + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Assignment other){
            return this.identifier.equals(other.identifier) && this.value.equals(other.value)&& this.kind.equals(other
                    .kind);
        }
        return false;
    }


    @Override
    public void accept(Visitor visitor) throws SemanticException {

        if (identifier != null) {
            identifier.accept(visitor);
        }
        if (value != null) {
            value.accept(visitor);
        }
        visitor.visit(this);
    }

}
