package compiler.Lexer;

public class Symbol {


    private TokenType type;
    private String value;

    public Symbol(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return "Symbol(" + type + "," + value + ")";
    }

}
