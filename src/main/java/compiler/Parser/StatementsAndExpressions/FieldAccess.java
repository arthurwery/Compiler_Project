package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class FieldAccess extends Expression {
    Expression identifier;
    Expression field;

    public FieldAccess(Expression identifier, Expression field) {
        this.identifier = identifier;
        this.field = field;
        this.kind = "FieldAccess";
    }

    public Expression getIdentifier() {
        return this.identifier;
    }

    public Expression getField() {
        return this.field;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tidentifier: " + this.identifier.toString().replaceAll("\n", "\n\t") + ",\n" +
                "\tfield: " + this.field.toString().replaceAll("\n", "\n\t") + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FieldAccess other){
            return this.identifier.equals(other.identifier) && this.field.equals(other.field) && this.kind.equals(other.kind);
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) throws SemanticException {

        if (identifier != null) {
            identifier.accept(visitor);
        }
        if (field != null) {
            field.accept(visitor);
        }
        visitor.visit(this);
    }

}
