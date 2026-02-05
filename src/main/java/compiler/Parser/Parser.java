package compiler.Parser;
import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Lexer.TokenType;
import compiler.Parser.StatementsAndExpressions.*;
import compiler.Parser.StatementsAndExpressions.Float_class;




public class Parser {
    private final Lexer lexer;
    private Symbol currentToken;
    private Symbol lookahead;

    public Parser(Lexer lexer){
        this.lexer = lexer;
        this.currentToken = lexer.getNextSymbol();
        this.lookahead = lexer.getNextSymbol();
    }

    private Symbol read_symbol(){
        Symbol TokenTemp = this.currentToken;
        this.currentToken = lookahead;
        if(lookahead != null){
            this.lookahead = lexer.getNextSymbol();
        }
        return TokenTemp;
    }

    public Statement getAST(){
        return parseProgram();
    }
    private Statement parseProgram(){
        Program program = new Program();
        while(currentToken.getType() != TokenType.EOF){
            program.addStatement(parseStatement());
        }
        return program;
    }

    private Statement parseStatement (){
        // Si final ou si type (int,bool,float,string)
        if (currentToken.getValue().equals("struct")){
            return parseStruct();
        }
        if(currentToken.getType().equals(TokenType.FREE)){
            read_symbol();
            if(!match(TokenType.IDENTIFIER)){
                throw new RuntimeException("Expected identifier");
            }
            String identifier = read_symbol().getValue();
            if(!match(TokenType.END_OF_INSTRUCTION)){
                throw new RuntimeException("Expected ';'");
            }
            read_symbol();
            return new Free(identifier);
        }
        if (currentToken.getValue().equals("def")){
            return parseFunctionDeclaration();
        }
        if (currentToken.getValue().equals("final") || currentToken.getType() == TokenType.TYPE || (currentToken.getType() == TokenType.IDENTIFIER && lookahead.getType()==TokenType.IDENTIFIER)){
            return parseDeclaration();
        }
        // ADD IF ELSE
        if (currentToken.getType() == TokenType.WHILE || currentToken.getType() == TokenType.FOR || currentToken.getType() == TokenType.CONTROLFLOW|| currentToken.getType() == TokenType.RETURN){
            return parseControlFlow();
        }
        if (currentToken.getType() == TokenType.IDENTIFIER && lookahead.getType() == TokenType.ASSIGNMENT_OPERATOR){
            return parseAssignment(true);
        }

        return parseExpression(true);
    }

    private Statement parseStruct(){
        if(!currentToken.getValue().equals("struct")){
            throw new RuntimeException("Expected 'struct'");
        }
        read_symbol();
        String identifier_name = read_symbol().getValue();
        Identifier identifier = new Identifier(identifier_name);
        if(!match(TokenType.LBRACE)){
            throw new RuntimeException("Expected '{'");
        }
        read_symbol();
        Struct struct = new Struct(identifier);
        while(currentToken.getType() == TokenType.TYPE){
            struct.addField(parseDeclaration());
        }
        if(!match(TokenType.RBRACE)){
            throw new RuntimeException("Expected '}'");
        }
        read_symbol();
        return struct;
    }

    private Statement parseFunctionDeclaration(){
        if(!currentToken.getValue().equals("def")){
            throw new RuntimeException("Expected 'struct'");
        }
        read_symbol();
        if( !(match(TokenType.TYPE) || match(TokenType.IDENTIFIER))){
            throw new RuntimeException("Expected type");
        }
        String type = read_symbol().getValue();
        if (currentToken.getType().equals(TokenType.LBRACKET)){
            read_symbol();
            if (!match(TokenType.RBRACKET)){
                throw new RuntimeException("Expected ']'");
            }
            read_symbol();
            type = type + "[]";
        }
        if(!match(TokenType.IDENTIFIER)){
            throw new RuntimeException("Expected identifier");
        }
        String identifier_name = read_symbol().getValue();
        Identifier identifier = new Identifier(identifier_name);
        if(!match(TokenType.LPAREN)){
            throw new RuntimeException("Expected '('");
        }
        read_symbol();
        Expression arguments;
        if(match(TokenType.RPAREN)){
            arguments = null;
        }
        else {
            arguments = parseArgumentsFunctionDeclaration();
        }
        if(!match(TokenType.RPAREN)){
            throw new RuntimeException("Expected ')'");
        }
        read_symbol();
        if (match(TokenType.END_OF_INSTRUCTION)){
            read_symbol();
            return new FunctionDeclaration(type,identifier,arguments, null);
        }
        if(!match(TokenType.LBRACE)){
            throw new RuntimeException("Expected '{'");
        }
        read_symbol();
        Block block = new Block();
        block.addStatement(parseStatement());
        while(currentToken.getType() != TokenType.RBRACE) {
            block.addStatement(parseStatement());
            if (currentToken.getType() == TokenType.EOF){
                throw new RuntimeException("Expected '}'");
            }
        }
        read_symbol();
        return new FunctionDeclaration(type,identifier,arguments, block);
    }
    private Statement parseControlFlow(){
        switch (currentToken.getType()){
            case WHILE:
                return parseWhile();
            case FOR:
                return parseFor();
            case CONTROLFLOW:
                return parseIf();
            case RETURN:
                read_symbol();
                Expression expression = parseExpression(true);
                return new Return(expression);
            default:
                return parseFunctionCall();
        }
    }
    private Expression parseFunctionCall(){
        if(currentToken.getType() == TokenType.IDENTIFIER && lookahead.getType() == TokenType.LPAREN){
            String identifier_name = read_symbol().getValue();
            Identifier identifier = new Identifier(identifier_name);
            read_symbol();
            if(match(TokenType.RPAREN)){
                read_symbol();
                return new FunctionCall(identifier,null);
            }
            Expression arguments = parseArguments();
            if(match(TokenType.RPAREN)){
                read_symbol();
                return new FunctionCall(identifier, arguments);
            }
            throw new RuntimeException("Expected ')'");

        }
        return parseTerm();
    }

    private Expression parseArgumentsFunctionDeclaration(){
        Arguments arguments = new Arguments();
        arguments.addArgument(parseDeclarationForParameters());

        while(currentToken.getType().equals(TokenType.COMMA)){
            read_symbol();
            arguments.addArgument(parseDeclarationForParameters());
        }
        return arguments;
    }

    private Expression parseDeclarationForParameters(){

        if (!match(TokenType.TYPE)){
            throw new RuntimeException("Expected type");
        }
        String type = read_symbol().getValue();
        if(currentToken.getType() == TokenType.LBRACKET) {
            read_symbol();
            if (!match(TokenType.RBRACKET)) {
                throw new RuntimeException("Expected ']'");
            }
            read_symbol();
            type = type + "[]";
        }
        if (!match(TokenType.IDENTIFIER)){
            throw new RuntimeException("Expected identifier");
        }
        String identifier = read_symbol().getValue();
        return new Parameter(type, identifier);
    }

// Operator precedence:
//     function and constructor calls
//     parenthesis
//     index operator
//     structure field access operator .
//     *,/,%
//     +,-, unary -
//     ==, !=, <, >, <=, >=
//     &&, ||
    private Expression parseExpression(boolean semicolon){
        Expression result =  parseMultipleConditions();
        if (semicolon){
            if (currentToken.getType() == TokenType.END_OF_INSTRUCTION){
                read_symbol();
            }else {
                System.out.println(currentToken);
                throw new RuntimeException("Expected ';'");
            }
        }
        return result;
    }

    private Expression parseAssignment(boolean semicolon){
        if(!match(TokenType.IDENTIFIER)){
            throw new RuntimeException("Expected identifier");
        }
        String identifier_name = read_symbol().getValue();
        Identifier identifier = new Identifier(identifier_name);
        if(!match(TokenType.ASSIGNMENT_OPERATOR)){
            throw new RuntimeException("Expected '='");
        }
        read_symbol();
        Expression value = parseExpression(semicolon);
        return new Assignment(identifier, value);
    }

    private Expression parseMultipleConditions(){
        Expression left = parseConditions();
        while(currentToken.getValue().equals("&&") || currentToken.getValue().equals("||")){
            String operator = read_symbol().getValue();
            Expression right = parseConditions();
            left = new BinaryExpression(left, right, operator);
        }
        return left;
    }

    private Expression parseConditions(){
        Expression left = parseAddition();
        while(currentToken.getValue().equals("==") || currentToken.getValue().equals("!=") || currentToken.getValue().equals(">") || currentToken.getValue().equals("<") || currentToken.getValue().equals(">=") || currentToken.getValue().equals("<=")){
            String operator = read_symbol().getValue();
            Expression right = parseAddition();
            left = new BinaryExpression(left, right, operator);
        }
        return left;
    }

    private Expression parseAddition(){
        Expression left = parseUnary();
        while(currentToken.getValue().equals("+") || currentToken.getValue().equals("-")){
            String operator = read_symbol().getValue();
            Expression right = parseUnary();
            left = new BinaryExpression(left, right, operator);
        }
        return left;
    }

    private Expression parseMultiplication(){
        Expression left = parseFieldAccessOperator();
        while(currentToken.getValue().equals("*") || currentToken.getValue().equals("/") || currentToken.getValue().equals("%")){
            String operator = read_symbol().getValue();
            Expression right = parseFieldAccessOperator();
            left = new BinaryExpression(left, right, operator);
        }
        return left;
    }

    private Expression parseFieldAccessOperator(){
        Expression left = parseAccessArray();
        while(currentToken.getValue().equals(".")){
            read_symbol();
            Expression right = parseAccessArray();
            left = new FieldAccess(left, right);
        }
        return left;
    }

    private Expression parseAccessArray(){
        Expression left = parseFunctionCall();
        while(currentToken.getType().equals(TokenType.LBRACKET)){
            read_symbol();
            Expression right = parseExpression(false);
            if(!match(TokenType.RBRACKET)){
                throw new RuntimeException("Expected ']'");
            }
            read_symbol();
            left = new ArrayAccess(left, right);
        }
        return left;
    }

    private Expression parseUnary(){
        if(currentToken.getValue().equals("-")){
            read_symbol();
            Expression operand = parseMultiplication();
            return new UnaryExpression(operand, "-");
        }
        return parseMultiplication();
    }


    private boolean match(TokenType type){
        return currentToken.getType().equals(type);
    }



    private Statement parseWhile(){
        if(!match(TokenType.WHILE)){
            throw new RuntimeException("Expected 'while'");
        }
        read_symbol();
        if(!match(TokenType.LPAREN)){
            throw new RuntimeException("Expected '('");
        }
        read_symbol();
        Expression condition = parseMultipleConditions();
        if(!match(TokenType.RPAREN)){
            throw new RuntimeException("Expected ')'");
        }
        read_symbol();
        if(!match(TokenType.LBRACE)){
            throw new RuntimeException("Expected '{'");
        }
        read_symbol();
        Block block = new Block();
        block.addStatement(parseStatement());
        while(currentToken.getType() != TokenType.RBRACE) {
            block.addStatement(parseStatement());
            if (currentToken.getType() == TokenType.EOF){
                throw new RuntimeException("Expected '}'");
            }
        }
        read_symbol();
        return new While(condition, block);
    }

    private Statement parseFor(){
        if(!match(TokenType.FOR)){
            throw new RuntimeException("Expected 'for'");
        }
        read_symbol();
        if(!match(TokenType.LPAREN)){
            throw new RuntimeException("Expected '('");
        }
        read_symbol();
        Expression initialization = parseAssignment(false);
        if(!match(TokenType.COMMA)){
            throw new RuntimeException("Expected ','");
        }
        read_symbol();
        Expression condition = parseMultipleConditions();
        if(!match(TokenType.COMMA)){
            throw new RuntimeException("Expected ','");
        }
        read_symbol();
        Expression increment = parseAssignment(false);
        if(!match(TokenType.RPAREN)){
            throw new RuntimeException("Expected ')'");
        }
        read_symbol();
        if(!match(TokenType.LBRACE)){
            throw new RuntimeException("Expected '{'");
        }
        read_symbol();
        Block block = new Block();
        block.addStatement(parseStatement());
        while(currentToken.getType() != TokenType.RBRACE) {
            block.addStatement(parseStatement());
            if (currentToken.getType() == TokenType.EOF){
                throw new RuntimeException("Expected '}'");
            }
        }
        read_symbol();
        return new For(initialization, condition, increment, block);
    }

    private Statement parseIf(){
        if(!(currentToken.getValue().equals("if"))){
            throw new RuntimeException("Expected 'if'");
        }
        read_symbol();
        if(!match(TokenType.LPAREN)){
            throw new RuntimeException("Expected '('");
        }
        read_symbol();
        Expression condition = parseMultipleConditions();
        if(!match(TokenType.RPAREN)){
            throw new RuntimeException("Expected ')'");
        }
        read_symbol();
        if(!match(TokenType.LBRACE)){
            throw new RuntimeException("Expected '{'");
        }
        read_symbol();
        Block block = new Block();
        block.addStatement(parseStatement());
        while(currentToken.getType() != TokenType.RBRACE) {
            block.addStatement(parseStatement());
            if (currentToken.getType() == TokenType.EOF){
                throw new RuntimeException("Expected '}'");
            }
        }
        if(!match(TokenType.RBRACE)){
            throw new RuntimeException("Expected '}'");
        }
        read_symbol();
        if(!match(TokenType.CONTROLFLOW) || !(currentToken.getValue().equals("else")) ){
            return new If(condition, block,null);
        }
        read_symbol();
        if(!match(TokenType.LBRACE)){
            throw new RuntimeException("Expected '{'");
        }
        read_symbol();
        Block alternate = new Block();
        alternate.addStatement(parseStatement());
        while(currentToken.getType() != TokenType.RBRACE) {
            alternate.addStatement(parseStatement());
            if (currentToken.getType() == TokenType.EOF){
                throw new RuntimeException("Expected '}'");
            }
        }
        if(!match(TokenType.RBRACE)){
            throw new RuntimeException("Expected '}'");
        }
        read_symbol();
        return new If(condition, block, alternate);
    }

    private Expression parseArguments(){
        Arguments arguments = new Arguments();
        arguments.addArgument(parseExpression(false));
        while(currentToken.getType().equals(TokenType.COMMA)){
            read_symbol();
            arguments.addArgument(parseExpression(false));
        }
        return arguments;

    }

    // final int a = 5;
    private Expression parseDeclaration(){
        boolean isFinal = false;
        boolean isArray = false;
        if (currentToken.getValue().equals("final")){
            isFinal = true;
            read_symbol();
        }
        if (!(match(TokenType.TYPE) || match(TokenType.IDENTIFIER))){
            throw new RuntimeException("Expected type");
        }
        String type = read_symbol().getValue();
        if (currentToken.getType().equals(TokenType.LBRACKET)){
            read_symbol();
            if (!match(TokenType.RBRACKET)){
                throw new RuntimeException("Expected ']'");
            }
            read_symbol();
            isArray = true;
            type = type + "[]";
        }
        if (!match(TokenType.IDENTIFIER)){
            throw new RuntimeException("Expected identifier");
        }

        String identifier_name = read_symbol().getValue();
        Identifier identifier = new Identifier(identifier_name);
        if(currentToken.getType().equals(TokenType.END_OF_INSTRUCTION)){
            if(isFinal){
                throw new RuntimeException("Expected '=' with final declaration");
            }else{
                read_symbol();
                return new Declaration(false,type, identifier, null);
            }
        }
        if(!match(TokenType.ASSIGNMENT_OPERATOR)){
            throw new RuntimeException("Expected '='");
        }
        read_symbol();
        if(isArray){
            if ( !(match(TokenType.IDENTIFIER) || match(TokenType.TYPE))){
                throw new RuntimeException("Expected identifier or type");
            }
            String identifier_or_type = read_symbol().getValue();
            if(!match(TokenType.LBRACKET)){
                throw new RuntimeException("Expected '['");
            }
            read_symbol();
            Expression size = parseExpression(false);
            if(!match(TokenType.RBRACKET)){
                throw new RuntimeException("Expected ']'");
            }
            read_symbol();
            if(!match(TokenType.END_OF_INSTRUCTION)){
                throw new RuntimeException("Expected ';'");
            }
            read_symbol();
            identifier_or_type+= "[]";
            ArrayInitialisation arrayInitialisation = new ArrayInitialisation(identifier_or_type, size);
            return new ArrayDeclaration(type, identifier,arrayInitialisation);

        }
        Expression value = parseExpression(true);
        return new Declaration(isFinal, type, identifier, value);
    }



    private Expression parseTerm(){
        switch (currentToken.getType()){
            case SPECIAL_CHARACTER:
                read_symbol();
                if (currentToken.getType() != TokenType.STRING ){
                    if (currentToken.getType() == TokenType.SPECIAL_CHARACTER){
                        read_symbol();
                        return new Str("");
                    }
                    throw new RuntimeException("Expected string");
                }
                Str tempstr = new Str(currentToken.getValue());
                read_symbol();
                if (currentToken.getValue().equals("\"")){
                    read_symbol();
                    return tempstr;
                } else {
                    throw new RuntimeException("Expected '\"' or \"'\" ");
                }
            case INT:
                return new Int(Integer.parseInt(read_symbol().getValue()));
            case FLOAT:
                return new Float_class(Float.parseFloat(read_symbol().getValue()));
            case IDENTIFIER:
                String identifier = read_symbol().getValue();
                return new Identifier(identifier);
            case LPAREN:
                read_symbol();
                Expression expression = parseExpression(false);
                if(currentToken.getType() != TokenType.RPAREN){
                    throw new RuntimeException("Expected ')'");
                }
                read_symbol();
                return expression;
            default:
                throw new RuntimeException("Unexpected token: " + currentToken);
        }
    }

}
