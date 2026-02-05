package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class FunctionCall extends Expression {
    Identifier identifier;
    Expression arguments;

    public FunctionCall(Identifier identifier, Expression arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
        this.kind = "FunctionCall";
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public Expression getArguments() {
        return this.arguments;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tidentifier: " + this.identifier + ",\n" +
                "\targuments: " + (arguments != null ? arguments.toString().replaceAll("\n", "\n\t") : null) + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FunctionCall other){
            return this.identifier.equals(other.identifier) && this.arguments.equals(other.arguments) && this.kind.equals(other.kind);
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) throws SemanticException {

        if (arguments != null) {
            arguments.accept(visitor);
        }
        visitor.visit(this);
    }

}
