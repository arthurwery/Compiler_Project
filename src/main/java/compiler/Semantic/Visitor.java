package compiler.Semantic;

import compiler.Parser.StatementsAndExpressions.*;

public interface Visitor {

    void visit(Arguments arguments) throws SemanticException;
    void visit(ArrayAccess arrayAccess) throws SemanticException;
    void visit(ArrayDeclaration arrayDeclaration) throws SemanticException;
    void visit(ArrayInitialisation arrayInitialisation) throws SemanticException;
    void visit(Assignment assignment) throws SemanticException;
    void visit(BinaryExpression binaryExpression) throws SemanticException;
    void visit(Block block) throws SemanticException;
    void visit(Declaration declaration) throws SemanticException;
    void visit(Expression expression)throws SemanticException;
    void visit(FieldAccess fieldAccess)throws SemanticException;
    void visit(For forLoop) throws SemanticException;
    void visit(Free free)throws SemanticException;
    void visit(FunctionCall functionCall) throws SemanticException;
    void visit(FunctionDeclaration functionDeclaration) throws SemanticException;
    void visit(Identifier identifier) throws SemanticException;
    void visit(If ifStatement) throws SemanticException;
    void visit(Int intValue)throws SemanticException;
    void visit(Parameter parameter)throws SemanticException;
    void visit(Program program) throws SemanticException;
    void visit(Return returnStatement) throws SemanticException;
    void visit(Statement statement) throws SemanticException;
    void visit(Str str)throws SemanticException;
    void visit(Struct struct) throws SemanticException;
    void visit(UnaryExpression unaryExpression)throws SemanticException;
    void visit(While whileLoop) throws SemanticException;


}
