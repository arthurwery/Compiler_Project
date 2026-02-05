package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

import java.util.ArrayList;

public class Arguments extends Expression  {
    ArrayList<Expression> arguments_list;

    public Arguments() {
        this.arguments_list = new ArrayList<>();
        this.kind = "Arguments";
    }

    public ArrayList<Expression> getArguments() {
        return this.arguments_list;
    }

    public void addArgument(Expression argument) {
        this.arguments_list.add(argument);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\tkind: ").append(this.kind).append(",\n");
        sb.append("\targuments: [");
        for (Expression argument : this.arguments_list) {
            sb.append("\n\t").append(argument.toString().replaceAll("\n", "\n\t")).append(",");
        }
        if (!this.arguments_list.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("\n\t]\n}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Arguments other){
            return this.arguments_list.equals(other.arguments_list) && this.kind.equals(other.kind);
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) throws SemanticException {

        for (Expression argument : arguments_list) {
            argument.accept(visitor);
        }
        visitor.visit(this);
    }
}
