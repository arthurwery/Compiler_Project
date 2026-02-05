package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class ArrayInitialisation extends Expression {
    String type;
    Expression size;

    public ArrayInitialisation(String type,Expression size) {
        this.type = type;
        this.size = size;
        this.kind = "ArrayInitialisation";
    }

    public String getType() {
        return this.type;
    }

    public Expression getSize() {
        return this.size;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\ttype: " + this.type + ",\n" +
                "\tsize: " + (size != null ? size.toString().replaceAll("\n", "\n\t") : null) + "\n" +
                "}";
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ArrayInitialisation other){
            return this.type.equals(other.type)&& this.kind.equals(other
                    .kind) && this.size.equals(other.size);
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) throws SemanticException {

        if (size != null) {
            size.accept(visitor);
        }
        visitor.visit(this);

    }

}
