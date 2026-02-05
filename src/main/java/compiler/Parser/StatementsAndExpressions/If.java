package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class If extends Statement {
    Expression condition;
    Statement consequent;
    Statement alternate;

    public If(Expression condition, Statement consequent, Statement alternate) {
        this.condition = condition;
        this.consequent = consequent;
        this.alternate = alternate;
        this.kind = "If";
    }

    public Expression getCondition() {
        return this.condition;
    }

    public Statement getConsequent() {
        return this.consequent;
    }

    public Statement getAlternate() {
        return this.alternate;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tcondition: " + (condition != null ? condition.toString().replaceAll("\n", "\n\t") : null) + ",\n" +
                "\tconsequent: " + (consequent != null ? consequent.toString().replaceAll("\n", "\n\t") : null) + ",\n" +
                "\talternate: " + (alternate != null ? alternate.toString().replaceAll("\n", "\n\t") : null) + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof If other){
            if (this.alternate == null && other.alternate == null) {
                return this.condition.equals(other.condition) && this.consequent.equals(other.consequent) && this.kind.equals(other.kind);
            }
            if (this.alternate == null || other.alternate == null) {
                return false;
            }
            else {
                return this.condition.equals(other.condition) && this.consequent.equals(other.consequent) && this.alternate.equals(other.alternate) && this.kind.equals(other.kind);
            }
        }
        return false;
    }

    @Override
    public void accept(Visitor v) throws SemanticException {
        v.visit(this);
        condition.accept(v);
        consequent.accept(v);
        if (alternate != null) {
            alternate.accept(v);
        }

    }
}
