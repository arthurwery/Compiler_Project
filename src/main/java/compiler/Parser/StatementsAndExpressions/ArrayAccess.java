package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

import java.util.ArrayList;

public class ArrayAccess extends Expression {
    Expression identifier;
    Expression index;
    public ArrayAccess(Expression identifier, Expression index) {
        this.identifier = identifier;
        this.index = index;
        this.kind = "Array Access";

    }

    public Expression getIdentifier() {
        return this.identifier;
    }

    public Expression getIndex() {
        return this.index;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tidentifier: " + this.identifier.toString().replaceAll("\n", "\n\t") + ",\n" +
                "\tindex: " + this.index.toString().replaceAll("\n", "\n\t") + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ArrayAccess other){
            return this.identifier.equals(other.identifier) && this.index.equals(other.index)&& this.kind.equals(other
                    .kind);
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) throws SemanticException {

        if (identifier != null) {
            identifier.accept(visitor);
        }
        if (index != null) {
            index.accept(visitor);
        }
        visitor.visit(this);
    }
}
