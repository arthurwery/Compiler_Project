package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class For extends Statement {
    Expression initialization;
    Expression condition;
    Expression increment;
    Statement body;

    public For(Expression initialization, Expression condition, Expression increment, Statement body) {
        this.initialization = initialization;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
        this.kind = "For";
    }

    public Statement getBody() {
        return this.body;
    }

    public Expression getInitialization() {
        return this.initialization;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public Expression getIncrement() {
        return this.increment;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tinitialization: " + (initialization != null ? initialization.toString().replaceAll("\n", "\n\t") : null) + ",\n" +
                "\tcondition: " + (condition != null ? condition.toString().replaceAll("\n", "\n\t") : null) + ",\n" +
                "\tincrement: " + (increment != null ? increment.toString().replaceAll("\n", "\n\t") : null) + ",\n" +
                "\tbody: " + (body != null ? body.toString().replaceAll("\n", "\n\t") : null) + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof For other){
            return this.initialization.equals(other.initialization) && this.condition.equals(other.condition) && this.increment.equals(other.increment) && this.body.equals(other.body) && this.kind.equals(other.kind);
        }
        return false;
    }

    @Override
    public void accept(Visitor v) throws SemanticException {
        v.visit(this);
        initialization.accept(v);
        condition.accept(v);
        increment.accept(v);
        body.accept(v);

    }

}
