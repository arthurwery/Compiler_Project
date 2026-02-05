package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class UnaryExpression extends Expression {
    Expression expression;
    String operator;

    public UnaryExpression(Expression expression, String operator) {
        this.expression = expression;
        this.operator = operator;
        this.kind = "UnaryExpression";
    }

    public Expression getExpression() {
        return expression;
    }
    public String getOperator() {
        return operator;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\texpression: " + (expression != null ? expression.toString().replaceAll("\n", "\n\t") : null) + ",\n" +
                "\toperator: " + this.operator + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UnaryExpression other){
            return this.expression.equals(other.expression) && this.operator.equals(other.operator) && this.kind.equals(other.kind);
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) throws SemanticException {

        expression.accept(visitor);
        if (expression != null) {
            if(!expression.getType().equals("int") && !expression.getType().equals("float")){
                throw new SemanticException("OperatorError in unary expression: " + operator + " " + expression.getType());
            }
        }
        visitor.visit(this);
    }

}
