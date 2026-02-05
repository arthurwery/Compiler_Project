package compiler.Semantic;


import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import compiler.Parser.StatementsAndExpressions.Declaration;
import compiler.Parser.StatementsAndExpressions.Program;
import compiler.Parser.StatementsAndExpressions.Statement;

import java.io.StringReader;

public class Semantic {

    Parser parser;
    Statement ast;
    Visitor visitor ;
    public Semantic(Parser parser){
        this.parser = parser;
        this.ast = parser.getAST();
        this.visitor = new SemanticVisitor();
    }

    public void semantic() throws Exception {
        this.ast.accept(this.visitor);
    }

    public Statement getAST(){
        return this.ast;
    }
}
