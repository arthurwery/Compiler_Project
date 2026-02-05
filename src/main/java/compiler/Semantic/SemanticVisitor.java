package compiler.Semantic;

import compiler.Parser.StatementsAndExpressions.*;

import java.util.ArrayList;
import java.util.Set;

public class SemanticVisitor implements Visitor{

    private SymbolTable symbolTable;
    private FunctionsTable functionsTable;
    private StructTable structTable;
    public SemanticVisitor() {
        this.symbolTable = new SymbolTable();
        this.functionsTable = new FunctionsTable();
        this.structTable = new StructTable();
    }



    @Override
    public void visit(Arguments arguments) throws SemanticException {
        for (Expression argument : arguments.getArguments()) {
            argument.accept(this);
            this.visit(argument);
        }

    }

    @Override
    public void visit(ArrayAccess arrayAccess) throws SemanticException{
        Expression arrayIdentifier = arrayAccess.getIdentifier();
        arrayIdentifier.accept(this);

        String arrayType = symbolTable.lookup(arrayIdentifier.toString());
        if (arrayType == null) {
            throw new SemanticException("ScopeError: Array '" + arrayIdentifier + "' is not declared.");
        }
        if (!arrayType.endsWith("[]")) {
            throw new SemanticException("TypeError : Variable '" + arrayIdentifier + "' is not an array type.");
        }

        Expression index = arrayAccess.getIndex();
        index.accept(this);
        if (!index.getType().equals("int")) {
            throw new SemanticException("TypeError : in array access: " + arrayType + "[" + index.getType() + "]");
        }
    }

    @Override
    public void visit(ArrayDeclaration arrayDeclaration) throws SemanticException{
        String identifier = arrayDeclaration.getIdentifier().getName();
        String type = arrayDeclaration.getType();
        if (symbolTable.isDeclared(identifier)) {
            throw new SemanticException("ScopeError : Identifier '" + identifier + "' is already declared.");
        }

        symbolTable.add(identifier, type);

        if (arrayDeclaration.getArrayInitialisation() != null) {
            arrayDeclaration.getArrayInitialisation().accept(this);
            this.visit(arrayDeclaration.getArrayInitialisation());
            if (!type.equals(arrayDeclaration.getArrayInitialisation().getType())) {
                throw new SemanticException("TypeError : in array declaration: " + type + " " + identifier + " = " + arrayDeclaration.getArrayInitialisation().getType());
            }
        }

    }

    @Override
    public void visit(ArrayInitialisation arrayInitialisation) throws SemanticException{
        if (arrayInitialisation.getSize() != null) {
            Expression size = arrayInitialisation.getSize();
            size.accept(this);

            if (!size.getType().equals("int")) {
                throw new SemanticException("TypeError : in array initialisation: " + arrayInitialisation.getType() + "[" + size.getType() + "]");
            }
        }
    }

    @Override
    public void visit(Assignment assignment) throws SemanticException {
        if (!symbolTable.isDeclared(assignment.getIdentifier().getName())) {
            throw new SemanticException("ScopeError : Identifier '" + assignment.getIdentifier().getName() + "' is not declared.");
        }

        String lookup = symbolTable.lookup(assignment.getIdentifier().getName());
        assignment.setType(lookup);
        if (assignment.getValue() != null) {
            assignment.getValue().accept(this);
            this.visit(assignment.getValue());
            if (!assignment.getType().equals(assignment.getValue().getType())) {
                throw new SemanticException("TypeError : in assignment: " + assignment.getType() + " " + assignment.getIdentifier().getName() + " = " + assignment.getValue().getType());
            }
        }
    }

    @Override
    public void visit(BinaryExpression binaryExpression) throws SemanticException {
        this.visit(binaryExpression.left);
        this.visit(binaryExpression.right);
    }

    @Override
    public void visit(Block block) throws SemanticException{
    }

    @Override
    public void visit(Declaration declaration) throws SemanticException {

        if (symbolTable.isDeclared(declaration.getIdentifier().getName())) {
            throw new SemanticException("ScopeError : Identifier '" + declaration.getIdentifier().getName() + "' is already declared.");
        }
        symbolTable.add(declaration.getIdentifier().getName(), declaration.getType());
        if (declaration.getValue() != null) {
            declaration.getValue().accept(this);
            this.visit(declaration.getValue());

            if (!declaration.getType().equals(declaration.getValue().getType())) {
                throw new SemanticException("TypeError in declaration: " + declaration.getType() + " " + declaration.getIdentifier().getName() + " = " + declaration.getValue().getType());
            }
        }

    }

    @Override
    public void visit(Expression expression) throws SemanticException {
        if (expression instanceof Identifier){
            try {
                visit((Identifier) expression);
            } catch (SemanticException e) {
                e.printStackTrace();
            }
        }
        if(expression instanceof Assignment){
            try {
                visit((Assignment) expression);
            } catch (SemanticException e) {
                e.printStackTrace();
            }
        }
        if (expression instanceof BinaryExpression){
            try {
                visit((BinaryExpression) expression);
            } catch (SemanticException e) {
                e.printStackTrace();
            }
        }
        if (expression instanceof Int){
            visit((Int) expression);
        }
        if (expression instanceof Str){
            visit((Str) expression);
        }
        if (expression instanceof UnaryExpression){
            visit((UnaryExpression) expression);
        }

    }

    @Override
    public void visit(FieldAccess fieldAccess) throws SemanticException{

    }

    @Override
    public void visit(For forLoop) throws SemanticException {
        symbolTable.enterScope();
        if (forLoop.getCondition() == null) {
            throw new SemanticException("MissingConditionError : For loop does not have a condition.");
        }
        forLoop.getCondition().accept(this);
        this.visit(forLoop.getCondition());
        if (!forLoop.getCondition().getType().equals("bool")) {
            throw new SemanticException("MissingConditionError : For loop condition is not a boolean.");
        }
        symbolTable.exitScope();
    }

    @Override
    public void visit(Free free) throws SemanticException{

    }

    @Override
    public void visit(FunctionCall functionCall) throws SemanticException {
        // readInt, readFloat, readString, writeInt, writeFloat, write, writeln
        if(functionCall.getIdentifier().getName().equals("readInt")){
            functionCall.setType("int");
            return;
        }
        if(functionCall.getIdentifier().getName().equals("readFloat")){
            functionCall.setType("float");
            return;
        }
        if(functionCall.getIdentifier().getName().equals("readString")){
            functionCall.setType("str");
            return;
        }
        if(functionCall.getIdentifier().getName().equals("writeInt") || functionCall.getIdentifier().getName().equals("writeFloat")|| functionCall.getIdentifier().getName().equals("write")|| functionCall.getIdentifier().getName().equals("writeln")){
            functionCall.setType("void");
            return;
        }
        if (!functionsTable.isDeclared(functionCall.getIdentifier().getName()) && !structTable.isDeclared(functionCall.getIdentifier().getName())) {
            throw new SemanticException("ScopeError : Function '" + functionCall.getIdentifier().getName() + "' is not declared.");
        }
        if (structTable.isDeclared(functionCall.getIdentifier().getName())) {
            Struct struct = structTable.lookup(functionCall.getIdentifier().getName());
            functionCall.setType(struct.getIdentifier().getName());
            ArrayList<Expression> fields = struct.getFields();
            if (functionCall.getArguments() != null) {
                functionCall.getArguments().accept(this);
                this.visit(functionCall.getArguments());
                if (functionCall.getArguments() instanceof Arguments) {
                    for (int i = 0; i < fields.size(); i++) {
                        if (!fields.get(i).getType().equals(((Arguments) functionCall.getArguments()).getArguments().get(i).getType())) {
                            throw new SemanticException("ArgumentError : Argument " + i + " in function call is different from parameter " + i + " in struct declaration.");
                        }
                    }
                }
            }
            return;
        }
        if (structTable.isDeclared(functionCall.getIdentifier().getName())) {
            Struct struct = structTable.lookup(functionCall.getIdentifier().getName());
            functionCall.setType(struct.getIdentifier().getName());
            ArrayList<Expression> fields = struct.getFields();
            if (functionCall.getArguments() != null) {
                functionCall.getArguments().accept(this);
                this.visit(functionCall.getArguments());
                if (functionCall.getArguments() instanceof Arguments) {
                    for (int i = 0; i < fields.size(); i++) {
                        if (!fields.get(i).getType().equals(((Arguments) functionCall.getArguments()).getArguments().get(i).getType())) {
                            throw new SemanticException("ArgumentsError : Argument " + i + " in function call is different from parameter " + i + " in struct declaration.");
                        }
                    }
                }
            }
            return;
        }
        FunctionDeclaration function = functionsTable.lookup(functionCall.getIdentifier().getName());
        functionCall.setType(function.getType());
        if (functionCall.getArguments() != null) {
            functionCall.getArguments().accept(this);
            this.visit(functionCall.getArguments());
            if (function.getParameters() != null) {
                if (function.getParameters() instanceof Arguments){
                    function.getParameters().accept(this);
                    this.visit(function.getParameters());
                    if (functionCall.getArguments() instanceof Arguments)
                        visit((Arguments) function.getParameters(), (Arguments) functionCall.getArguments());
                }
            }
        }

    }

    public void visit(Arguments arguments_declaration,Arguments arguments_call) throws SemanticException {
        if (arguments_declaration.getArguments().size() != arguments_call.getArguments().size()) {
            throw new SemanticException("ArgumentError : Number of arguments in function call is different from the number of parameters in function declaration.");
        }
        for (int i = 0; i < arguments_call.getArguments().size(); i++) {
            Expression argument = arguments_call.getArguments().get(i);
            argument.accept(this);
            this.visit(argument);
            if (!arguments_declaration.getArguments().get(i).getType().equals(argument.getType())) {
                throw new SemanticException("ArgumentError : Argument " + i + " in function call is different from parameter " + i + " in function declaration.");
            }
        }
    }

    @Override
    public void visit(FunctionDeclaration functionDeclaration) throws SemanticException {
        if (functionsTable.isDeclared(functionDeclaration.getIdentifier().getName())) {
            throw new SemanticException("ScopeError : Function '" + functionDeclaration.getIdentifier().getName() + "' is already declared.");
        }
        functionsTable.addFunction(functionDeclaration);
        symbolTable.startFunction();
        if (functionDeclaration.getParameters() != null) {
            functionDeclaration.getParameters().accept(this);

            if(functionDeclaration.getParameters() instanceof Arguments) {
                for (Expression parameter : ((Arguments) functionDeclaration.getParameters()).getArguments()) {
                    if (parameter instanceof Parameter) {
                        symbolTable.add(((Parameter) parameter).getIdentifier(), ((Parameter) parameter).getType());
                    }
                }
            }
            functionDeclaration.getBody().accept(this);
        }
        else{functionDeclaration.getBody().accept(this);}

        if(functionDeclaration.getBody() instanceof Block) {
            if (((Block) functionDeclaration.getBody()).getReturnStatement() == null && !functionDeclaration.getType().equals("void")) {
                throw new SemanticException("FunctionError : Function '" + functionDeclaration.getIdentifier().getName() + "' does not have a return statement.");
            }
            if (!functionDeclaration.getType().equals("void")){
            this.visit((Return) ((Block) functionDeclaration.getBody()).getReturnStatement());
            if (((Block) functionDeclaration.getBody()).getReturnStatement() != null) {
                if (!((Block) functionDeclaration.getBody()).getReturnStatement().getType().equals(functionDeclaration.getType())) {
                    throw new SemanticException("ReturnError : Function '" + functionDeclaration.getIdentifier().getName() + "' has a return statement with a different type.");
                }
            }}
        }
        symbolTable.endFunction();

    }

    @Override
    public void visit(Identifier identifier) throws SemanticException {
        if (identifier.getName().equals("true") || identifier.getName().equals("false")) {
            identifier.setType("bool");
            return;
        }
        String lookup = symbolTable.lookup(identifier.getName());
        if (lookup == null) {
            throw new SemanticException("ScopeError : Identifier '" + identifier.getName() + "' is not declared.");
        }
        identifier.setType(lookup);

    }

    @Override
    public void visit(If ifStatement) throws SemanticException {
        symbolTable.enterScope();
        if (ifStatement.getCondition() == null) {
            throw new SemanticException("MissingConditionError : If does not have a condition.");
        }
        ifStatement.getCondition().accept(this);
        if (!ifStatement.getCondition().getType().equals("bool")) {
            throw new SemanticException("MissingConditionError : if condition is not a boolean.");
        }
        symbolTable.exitScope();

    }

    @Override
    public void visit(Int intValue) throws SemanticException{

    }

    @Override
    public void visit(Parameter parameter) throws SemanticException{

    }

    @Override
    public void visit(Program program) throws SemanticException {

    }

    @Override
    public void visit(Return returnStatement) throws SemanticException {
        returnStatement.getValue().accept(this);
        returnStatement.setType(returnStatement.getValue().getType());
    }

    @Override
    public void visit(Statement statement) throws SemanticException {
        if(statement instanceof Declaration){
            try {
                visit((Declaration) statement);
            } catch (SemanticException e) {
                e.printStackTrace();
            }
        }
        if(statement instanceof Expression){
            visit((Expression) statement);
        }
        if(statement instanceof FunctionDeclaration){
            visit((FunctionDeclaration) statement);
        }
        if(statement instanceof Struct){
            try {
                visit((Struct) statement);
            } catch (SemanticException e) {
                e.printStackTrace();
            }
        }
        if(statement instanceof While){
            try {
                visit((While) statement);
            } catch (SemanticException e) {
                e.printStackTrace();
            }
        }
        if(statement instanceof For){
            try {
                visit((For) statement);
            } catch (SemanticException e) {
                e.printStackTrace();
            }
        }
        if(statement instanceof If){
            try {
                visit((If) statement);
            } catch (SemanticException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void visit(Str str) throws SemanticException{

    }

    @Override
    public void visit(Struct struct) throws SemanticException {
        if(structTable.isDeclared(struct.getIdentifier().getName())){
            throw new SemanticException("StructError : Struct '" + struct.getIdentifier().getName() + "' is already declared.");
        }

        Set<String> words_reserved = Set.of(
                "int", "str", "while", "for", "if", "else", "return", "true", "false", "null", "def","final","struct","bool","float","void"
        );
        if(words_reserved.contains(struct.getIdentifier().getName())){
            throw new SemanticException("StructError : Struct '" + struct.getIdentifier().getName() + "' is a reserved word.");
        }
        structTable.add(struct);
        symbolTable.startFunction();
        for (Expression field : struct.getFields()) {
            field.accept(this);
            this.visit(field);
        }
        symbolTable.endFunction();
    }

    @Override
    public void visit(UnaryExpression unaryExpression) throws SemanticException{
        this.visit(unaryExpression.getExpression());


    }

    @Override
    public void visit(While whileLoop) throws SemanticException {
        symbolTable.enterScope();
        if (whileLoop.getCondition() == null) {
            throw new SemanticException("MissingConditionError : While loop does not have a condition.");
        }
        whileLoop.getCondition().accept(this);
        this.visit(whileLoop.getCondition());
        if (!whileLoop.getCondition().getType().equals("bool")) {
            throw new SemanticException("MissingConditionError : While loop condition is not a boolean.");
        }
        symbolTable.exitScope();

    }
}
