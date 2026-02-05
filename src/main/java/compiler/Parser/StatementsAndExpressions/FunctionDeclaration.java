package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class FunctionDeclaration extends Statement {
    String type;
    Identifier identifier;
    Expression parameters;
    Statement body;

    public FunctionDeclaration(String type, Identifier identifier, Expression arguments, Statement body) {
        this.type = type;
        this.identifier = identifier;
        this.parameters = arguments;
        this.body = body;
        this.kind = "FunctionDeclaration";

    }

    public String getType() {
        return this.type;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public Expression getParameters() {
        return this.parameters;
    }

    public Statement getBody() {
        return this.body;
    }
    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\ttype: " + this.type + ",\n" +
                "\tidentifier: " + this.identifier + ",\n" +
                "\tparameters: " + (parameters != null ? parameters.toString().replaceAll("\n", "\n\t") : null) + ",\n" +
                "\tbody: " + (body != null ? body.toString().replaceAll("\n", "\n\t") : null) + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FunctionDeclaration other){
            if (this.parameters == null && other.parameters == null) {
                return this.type.equals(other.type) && this.identifier.equals(other.identifier) && this.body.equals(other.body) && this.kind.equals(other.kind);
            }
            if (this.parameters == null || other.parameters == null) {
                return false;
            }
            else {
                return this.type.equals(other.type) && this.identifier.equals(other.identifier) && this.parameters.equals(other.parameters) && this.body.equals(other.body) && this.kind.equals(other.kind);

            }
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) throws SemanticException {
        visitor.visit(this);

    }
}
