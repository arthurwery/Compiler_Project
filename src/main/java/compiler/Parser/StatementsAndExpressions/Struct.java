package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

import java.util.ArrayList;

public class Struct extends Statement {
    Identifier identifier;
    ArrayList<Expression> fields;

    public Struct(Identifier identifier) {
        this.identifier = identifier;
        this.fields = new ArrayList<>();
        this.kind = "Struct";
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public ArrayList<Expression> getFields() {
        return this.fields;
    }

    public void addField(Expression field) {
        this.fields.add(field);
    }

    // Struct class
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\t").append("kind: ").append(this.kind).append(",\n");
        sb.append("\t").append("identifier: ").append(this.identifier).append(",\n");
        sb.append("\t").append("fields: ").append("[\n");
        for (Expression field : this.fields) {
            sb.append("\t").append(field.toString().replaceAll("\n", "\n\t")).append(",\n");
        }
        sb.append("\t").append("]\n");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Struct other){
            if(this.fields.size() != other.fields.size()) return false;
            for(int i = 0; i < this.fields.size(); i++){
                if(!this.fields.get(i).equals(other.fields.get(i))) return false;
            }
            return this.identifier.equals(other.identifier) && this.kind.equals(other.kind);
        }
        return false;
    }

    @Override
    public void accept(Visitor v) throws SemanticException {
        System.out.println("Visiting struct");
        v.visit(this);
        for (Expression field : this.fields) {
            field.accept(v);
        }

    }
}
