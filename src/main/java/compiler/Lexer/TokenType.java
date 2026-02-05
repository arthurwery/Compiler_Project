package compiler.Lexer;

public enum TokenType {
    OPERATOR("Operator"),
    TYPE("Type"),
    SPECIAL_CHARACTER("Special Character"),
    IDENTIFIER("Identifier"),
    INT("Int"),
    FLOAT("Float"),
    STRING("String"),
    FUNCTION("Function"),
    CONTROLFLOW("Control Flow"),
    VARIABLE_OPERATOR("Variable Operator"),
    ASSIGNMENT_OPERATOR("Assignment Operator"),
    END_OF_INSTRUCTION("End of Instruction"),
    LPAREN("Left Parenthesis"),
    LBRACE("Left Brace"),
    RETURN("Return"),
    RPAREN("Right Parenthesis"),
    RBRACE("Right Brace"),
    MINUS("Minus"),
    COMMA("Comma"),
    FOR("For"),
    WHILE("While"),
    LBRACKET("Left Bracket"),
    RBRACKET("Right Bracket"),
    DOT("Dot"),
    FREE("Free"),
    EOF("End of File");

    private final String name;

    TokenType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}