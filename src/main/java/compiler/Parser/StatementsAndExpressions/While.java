package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class While extends Statement {
    Expression condition;
    Statement body;

    public While(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
        this.kind = "While";
    }

    public Expression getCondition() {
        return this.condition;
    }
    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tcondition: " + (condition != null ? condition.toString().replaceAll("\n", "\n\t") : null) + ",\n" +
                "\tbody: " + (body != null ? body.toString().replaceAll("\n", "\n\t") : null) + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof While other){
            return this.condition.equals(other.condition) && this.body.equals(other.body) && this.kind.equals(other.kind);
        }
        return false;
    }

    @Override
    public void accept(Visitor v) throws SemanticException {
        v.visit(this);
        body.accept(v);
        condition.accept(v);

    }


    public Statement getBody() {
        return this.body;
    }

}
