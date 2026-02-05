package compiler.Parser.StatementsAndExpressions;

import compiler.Semantic.SemanticException;
import compiler.Semantic.Visitor;

public class Declaration extends Expression {
    Boolean isFinal;
    String type;
    Identifier identifier;
    Expression value;
    public Declaration(Boolean isFinal, String type, Identifier identifier, Expression value) {
        this.isFinal = isFinal;
        this.type = type;
        this.identifier = identifier;
        this.value = value;
        this.kind = "Declaration";
    }

    public boolean getIsFinal(){
        return this.isFinal;
    }

    public String getType(){
        return this.type;
    }

    public Identifier getIdentifier(){
        return this.identifier;
    }

    public Expression getValue(){
        return this.value;
    }

    public String toString() {
        return "{\n" +
                "\tkind: " + this.kind + ",\n" +
                "\ttype: " + this.type + ",\n" +
                "\tidentifier: " + this.identifier + ",\n" +
                "\tisFinal: " + this.isFinal + ",\n" +
                "\tvalue: " + (value != null ? value.toString().replaceAll("\n", "\n\t") : null) + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Declaration other){
            if(other.value == null && this.value == null){
                return this.isFinal.equals(other.isFinal) && this.type.equals(other.type) && this.identifier.equals(other.identifier);
            }
            if(other.value == null || this.value == null){
                return false;
            }else {
                return this.isFinal.equals(other.isFinal) && this.type.equals(other.type) && this.identifier.equals(other.identifier) && this.value.equals(other.value);
            }
        }
        return false;
    }


    @Override
    public void accept(Visitor visitor) throws SemanticException {
        visitor.visit(this);
        if (identifier != null) {
            identifier.accept(visitor);
        }
        if (value != null) {
            value.accept(visitor);
        }



    }
}
