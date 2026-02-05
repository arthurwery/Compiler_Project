package compiler.Parser.StatementsAndExpressions;


import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

import java.util.ArrayList;

public class Program extends Statement {
    ArrayList<Statement> statements;

    public Program() {
        this.statements = new ArrayList<>();
        this.kind = "Program";
    }

    public ArrayList<Statement> getStatements() {
        return this.statements;
    }

    public void addStatement(Statement statement) {
        this.statements.add(statement);
    }
    public Statement getStatement(int index) {
        return this.statements.get(index);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\t").append("kind: ").append(this.kind).append(",\n");
        sb.append("\t").append("statements: ").append("[\n");
        for (Statement statement : this.statements) {
            sb.append("\t").append(statement.toString().replaceAll("\n", "\n\t")).append(",\n");
        }
        sb.append("\t").append("]\n");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Program other){
            if(this.statements.size() != other.statements.size()) return false;
            for(int i = 0; i < this.statements.size(); i++){
                if(!this.statements.get(i).equals(other.statements.get(i))) return false;
            }
            return this.kind.equals(other.kind);
        }
        return false;
    }

    @Override
    public void accept(Visitor v) throws SemanticException {
        v.visit(this);
        System.out.println(this.statements.size());
        for (Statement statement : this.statements) {
            statement.accept(v);
        }

    }
}
