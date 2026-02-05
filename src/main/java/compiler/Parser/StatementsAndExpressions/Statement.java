package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitable;
import compiler.Semantic.Visitor;

public abstract class Statement implements Visitable {
    private String type;
    String kind;

    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public void accept(Visitor visitor) throws SemanticException {
        visitor.visit(this);
    }

    public String getType() {
        return this.type;
    }


    public void setType(String type) {
        this.type = type;
    }

}

