package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

import java.util.ArrayList;

public class Block extends Statement {
    ArrayList<Statement> statements;

    public Block() {
        this.statements = new ArrayList<>();
        this.kind = "Block";
    }

    public void addStatement(Statement statement) {
        this.statements.add(statement);
    }

    public ArrayList<Statement> getStatements() {
        return this.statements;
    }

    public Statement getReturnStatement() {
        for (Statement statement : statements) {
            if (statement instanceof Return) {
                return statement;
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\tkind: ").append(this.kind).append(",\n");
        sb.append("\tstatements: [");
        for (Statement statement : this.statements) {
            sb.append("\n\t").append(statement.toString().replaceAll("\n", "\n\t")).append(",");
        }
        if (!this.statements.isEmpty()) {
            sb.setLength(sb.length() - 1); // Remove trailing comma
        }
        sb.append("\n\t]\n}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Block other){
            if(this.statements.size() != other.statements.size()){
                return false;
            }
            for(int i = 0; i < this.statements.size(); i++){
                if(!this.statements.get(i).equals(other.statements.get(i))){
                    return false;
                }
            }
            return this.kind.equals(other.kind);
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) throws SemanticException {
        visitor.visit(this);
        for (Statement statement : statements) {
            statement.accept(visitor);
        }

    }
}
