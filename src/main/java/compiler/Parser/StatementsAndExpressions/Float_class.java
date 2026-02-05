package compiler.Parser.StatementsAndExpressions;

public class Float_class extends Expression {
    float value;

    public Float_class(float value) {
        this.value = value;
        this.kind = "Float";
        this.setType("float");
    }

    public String toString() {
        return "{\n" +
                "\t" + "kind: " + this.kind + ",\n" +
                "\t" + "value: " + this.value + "\n" +
                "}";
    }

    public float getValue() {
        return this.value;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Float_class other){
            return this.value == other.value && this.kind.equals(other.kind);
        }
        return false;
    }

}
