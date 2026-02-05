package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class BinaryExpression extends Expression {
    public Expression left;
    public Expression right;
    String operator;

    public BinaryExpression(Expression left, Expression right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.kind = "BinaryExpression";
    }

    public Expression getLeft(){
        return this.left;
    }

    public Expression getRight(){
        return this.right;
    }

    public String getOperator(){
        return this.operator;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\tleft: " + (left != null ? left.toString().replaceAll("\n", "\n\t") : null) + ",\n" +
                "\tright: " + (right != null ? right.toString().replaceAll("\n", "\n\t") : null) + ",\n" +
                "\toperator: " + this.operator + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BinaryExpression other){
            return this.left.equals(other.left) && this.right.equals(other.right) && this.operator.equals(other.operator) && this.kind.equals(other.kind);
        }
        return false;
    }


    @Override
    public void accept(Visitor visitor) throws SemanticException {
        if (left != null) {
            left.accept(visitor);
        }
        if (right != null) {
            right.accept(visitor);
        }
        if (right != null && left != null) {
            boolean mixed = mixedExpression(left.getType(), right.getType());
            if(!mixed && !left.getType().equals(right.getType()) ) {
                throw new SemanticException("OperatorError in binary expression: " + left.getType() + " " + operator + " " + right.getType());
            }
            checkOperator(mixed ? "float" : left.getType());
        }
        visitor.visit(this);
    }

    private void checkOperator(String type) throws SemanticException {
        if (type.equals("int")) {
            if (isArithmeticOperator(operator) || operator.equals("%")) {
                setType("int");
            } else if (isComparisonOperator(operator)) {
                setType("bool");
            } else {
                throw new SemanticException("OperatorError : Invalid operator for an int : " + operator);
            }
        } else if (type.equals("float")) {
            if (isArithmeticOperator(operator)) {
                setType("float");
            } else if (isComparisonOperator(operator)) {
                setType("bool");
            } else {
                throw new SemanticException("OperatorError : Invalid operator for a double : " + operator);
            }
        } else if (type.equals("bool")) {
            if (isComparisonOperator(operator)) {
                setType("bool");
            } else {
                throw new SemanticException("OperatorError : Invalid operator for a boolean : " + operator);
            }
        } else if (type.equals("string")) {
            if (operator.equals("+")) {
                setType("string");
            } else if (operator.equals("==") || operator.equals("!=")) {
                setType("bool");
            } else {
                throw new SemanticException("OperatorError : Invalid operator for a string : " + operator);
            }
        } else {
            throw new SemanticException("OperatorError : Invalid type for binary expression : " + type);
        }
    }



    private boolean isArithmeticOperator(String operator) {
        return "+".equals(operator) || "-".equals(operator) || "*".equals(operator) || "/".equals(operator);
    }

    private boolean isComparisonOperator(String operator) {
        return "<".equals(operator) || ">".equals(operator) || "==".equals(operator) || "!=".equals(operator) || "<=".equals(operator) || ">=".equals(operator);
    }

    private boolean mixedExpression(String type1, String type2) {
        return (type1.equals("int") && type2.equals("float")) || (type1.equals("float") && type2.equals("int")) ;
    }
}
