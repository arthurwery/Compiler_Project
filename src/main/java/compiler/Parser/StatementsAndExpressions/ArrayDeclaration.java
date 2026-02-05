package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class ArrayDeclaration extends Expression {
    String type;
    Identifier identifier;
    ArrayInitialisation arrayInitialisation;

    public ArrayDeclaration(String type, Identifier identifier, ArrayInitialisation arrayInitialisation) {
        this.type = type;
        this.identifier = identifier;
        this.arrayInitialisation = arrayInitialisation;
        this.kind = "ArrayDeclaration";
    }

    public String getType() {
        return this.type;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public ArrayInitialisation getArrayInitialisation() {
        return this.arrayInitialisation;
    }
    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\ttype: " + this.type + ",\n" +
                "\tidentifier: " + this.identifier + ",\n" +
                "\tvalue: " + (arrayInitialisation != null ? arrayInitialisation.toString().replaceAll("\n", "\n\t") : null) + "\n" +
                "}";
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ArrayDeclaration other){
            return this.identifier.equals(other.identifier) && this.type.equals(other.type)&& this.kind.equals(other
                    .kind) && this.arrayInitialisation.equals(other.arrayInitialisation);
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) throws SemanticException {

        if (arrayInitialisation != null) {
            arrayInitialisation.accept(visitor);
        }
        visitor.visit(this);
    }
}
