
import compiler.Parser.StatementsAndExpressions.*;
import org.junit.Test;

import java.io.StringReader;
import java.lang.reflect.Field;

import compiler.Lexer.Lexer;
import compiler.Parser.Parser;

public class testParser {
    @Test
    public void TestFonctionAndAddition() {
        String input = "cell(5) + 3;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program ast = (Program) parser.getAST();
        Program program = new Program();
        Arguments arguments = new Arguments();
        arguments.addArgument(new Int(5));
        Statement binaryExpression = new BinaryExpression(new FunctionCall(new Identifier("cell"), arguments), new Int(3), "+");
        program.addStatement(binaryExpression);
        assert ast.equals(program);
    }

    @Test
    public void TestFonctionAdditionAndUnary() {
        String input = "-cell(5) + 3;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        Arguments arguments = new Arguments();
        arguments.addArgument(new Int(5));
        Statement binaryExpression = new BinaryExpression(new UnaryExpression(new FunctionCall(new Identifier("cell"), arguments), "-"), new Int(3), "+");
        program.addStatement(binaryExpression);
        assert ast.equals(program);

    }

    // -------ASSIGNMENTS TESTS-------
    @Test
    public void testDeclarationWithAddition() {
        String input = "int x = 1 + 2 + 3;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program ast = (Program) parser.getAST();
        Program program = new Program();
        Expression binaryExpression = new BinaryExpression(new BinaryExpression(new Int(1), new Int(2), "+"), new Int(3), "+");
        program.addStatement(new Declaration(false, "int", new Identifier("x"), binaryExpression));

    }

    @Test
    public void testDeclarationWithoutValue() {
        String input = "int x;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program ast = (Program) parser.getAST();
        Program program = new Program();
        program.addStatement(new Declaration(false, "int", new Identifier("x"), null));
        assert ast.equals(program);

    }

    @Test
    public void testDeclarationFinalWithoutValue() {
        String input = "final int x;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        // ASSERT if parser.getAST() throws an error
        try {
            System.out.println(parser.getAST());
        } catch (Exception e) {
            assert "java.lang.RuntimeException: Expected '=' with final declaration".equals(e.toString());
        }

    }

    @Test
    public void testDeclarationFinalWithValue() {
        String input = "final int x = 1 + 2 + 3;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program ast = (Program) parser.getAST();
        Program program = new Program();
        Expression binaryExpression = new BinaryExpression(new BinaryExpression(new Int(1), new Int(2), "+"), new Int(3), "+");
        program.addStatement(new Declaration(true, "int", new Identifier("x"), binaryExpression));
        assert ast.equals(program);

    }

    @Test
    public void testDeclarationFinalWithoutType() {
        String input = "final  x = 1 + 2 + 3;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        try {
            System.out.println(parser.getAST());
        } catch (Exception e) {
            assert "java.lang.RuntimeException: Expected identifier".equals(e.toString());
        }
    }

    @Test
    public void testDeclarationArray() {
        String input = "int[] c = int[5];";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program ast = (Program) parser.getAST();
        Program program = new Program();
        program.addStatement(new ArrayDeclaration("int[]", new Identifier("c"), new ArrayInitialisation("int", new Int(5))));
        assert ast.equals(program);
    }

    @Test
    public void testDeclarationArrayWithExpression() {
        String input = "int[] c = int[cell(5)+8 *3];";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program ast = (Program) parser.getAST();
        Program program = new Program();
        Arguments arguments = new Arguments();
        arguments.addArgument(new Int(5));
        Expression binaryExpression = new BinaryExpression(new FunctionCall(new Identifier("cell"), arguments), new BinaryExpression(new Int(8), new Int(3), "*"), "+");
        program.addStatement(new ArrayDeclaration("int[]", new Identifier("c"), new ArrayInitialisation("int", binaryExpression)));
        assert ast.equals(program);
    }


    @Test
    public void testFunctionCallWithExpression() {
        String input = "print(1 + 2 + 3);";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        Expression binaryExpression = new BinaryExpression(new BinaryExpression(new Int(1), new Int(2), "+"), new Int(3), "+");
        Arguments arguments = new Arguments();
        arguments.addArgument(binaryExpression);
        program.addStatement(new FunctionCall(new Identifier("print"), arguments));
        assert ast.equals(program);
    }

    @Test
    public void TestAssignment() {
        String input = " x = 5;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        program.addStatement(new Assignment(new Identifier("x"), new Int(5)));
        assert ast.equals(program);
    }

    @Test
    public void testFunctionWithArgumentsAndMultipleParameters() {
        String input = "print(print(1,2,3),2,3);";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        Arguments arguments = new Arguments();
        Arguments arguments2 = new Arguments();
        arguments2.addArgument(new Int(1));
        arguments2.addArgument(new Int(2));
        arguments2.addArgument(new Int(3));
        arguments.addArgument(new FunctionCall(new Identifier("print"), arguments2));
        arguments.addArgument(new Int(2));
        arguments.addArgument(new Int(3));
        program.addStatement(new FunctionCall(new Identifier("print"), arguments));
        assert ast.equals(program);
    }

    @Test
    public void testFor() {
        String input = "int i = 0; for(i = 0, i < 10, i = i + 1) { print(i); }";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        program.addStatement(new Declaration(false, "int", new Identifier("i"), new Int(0)));
        Expression binaryExpression = new Assignment(new Identifier("i"), new BinaryExpression(new Identifier("i"), new Int(1), "+"));
        Arguments arguments = new Arguments();
        arguments.addArgument(new Identifier("i"));
        Block block = new Block();
        block.addStatement(new FunctionCall(new Identifier("print"), arguments));
        program.addStatement(new For(new Assignment(new Identifier("i"), new Int(0)), new BinaryExpression(new Identifier("i"), new Int(10), "<"), binaryExpression, block));
        assert ast.equals(program);
    }

    @Test
    public void testWhile() {
        String input = "while(1 < 10) { print(1);int x =4; }";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        Arguments arguments = new Arguments();
        arguments.addArgument(new Int(1));
        Block block = new Block();
        block.addStatement(new FunctionCall(new Identifier("print"), arguments));
        block.addStatement(new Declaration(false, "int", new Identifier("x"), new Int(4)));
        program.addStatement(new While(new BinaryExpression(new Int(1), new Int(10), "<"), block));
        assert ast.equals(program);
    }

    @Test
    public void testConditions() {
        String input = "bool x = 1 > 2 && (1 < 3 || true);";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        System.out.println(ast);
        Program program = new Program();
        Expression binaryExpression = new BinaryExpression(new BinaryExpression(new Int(1), new Int(2), ">"), new BinaryExpression(new BinaryExpression(new Int(1), new Int(3), "<"), new Identifier("true"), "||"), "&&");
        program.addStatement(new Declaration(false, "bool", new Identifier("x"), binaryExpression));
        assert ast.equals(program);

    }

    @Test
    public void testIf() {
            String input = "if(1 > 2) { print(1); } else { print(2); }";
            StringReader reader = new StringReader(input);
            Lexer lexer = new Lexer(reader);
            Parser parser = new Parser(lexer);
            Statement ast = parser.getAST();
            Program program = new Program();
            Arguments arguments = new Arguments();
            arguments.addArgument(new Int(1));
            Block block = new Block();
            block.addStatement(new FunctionCall(new Identifier("print"), arguments));
            Block block2 = new Block();
            Arguments arguments1 = new Arguments();
            arguments1.addArgument(new Int(2));
            block2.addStatement(new FunctionCall(new Identifier("print"), arguments1));
            program.addStatement(new If(new BinaryExpression(new Int(1), new Int(2), ">"), block, block2));
            assert ast.equals(program);
    }

    @Test
    public void testIfWithoutElse() {
        String input = "if(1 > 2) { print(1); }";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        Arguments arguments = new Arguments();
        arguments.addArgument(new Int(1));
        Block block = new Block();
        block.addStatement(new FunctionCall(new Identifier("print"), arguments));
        program.addStatement(new If(new BinaryExpression(new Int(1), new Int(2), ">"), block, null));
        assert ast.equals(program);
    }

    @Test
    public void testEnnonce() {
        String input = "int x = 1 + 2;\n" +
                "int y = 3 + 4;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program ast = (Program) parser.getAST();
        Program program = new Program();
        program.addStatement(new Declaration(false, "int", new Identifier("x"), new BinaryExpression(new Int(1), new Int(2), "+")));
        program.addStatement(new Declaration(false, "int", new Identifier("y"), new BinaryExpression(new Int(3), new Int(4), "+")));
        assert ast.equals(program);
    }

    @Test
    public void testMultipleStatements() {
        String input = "int x = 1 + 2;\n" +
                "int y = 3 + 4;\n" +
                "int z = x + y;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Program ast = (Program) parser.getAST();
        Program program = new Program();
        program.addStatement(new Declaration(false, "int", new Identifier("x"), new BinaryExpression(new Int(1), new Int(2), "+")));
        program.addStatement(new Declaration(false, "int", new Identifier("y"), new BinaryExpression(new Int(3), new Int(4), "+")));
        program.addStatement(new Declaration(false, "int", new Identifier("z"), new BinaryExpression(new Identifier("x"), new Identifier("y"), "+")));
        assert ast.equals(program);
    }

    @Test
    public void testStruct() {
        String input = "struct Point {\n" +
                "    int x;\n" +
                "    int y;\n" +
                "}";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        Struct struct = new Struct(new Identifier("Point"));
        struct.addField(new Declaration(false, "int", new Identifier("x"), null));
        struct.addField(new Declaration(false, "int", new Identifier("y"), null));
        program.addStatement(struct);
        assert ast.equals(program);
    }



    @Test
    public void testFunctionDeclaration() {
        String input = "def Point add(int x, int y) {\n" +
                "    int x = 3;\n" +
                "int y =4;}" +
                "add(5);";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        Block block = new Block();
        System.out.println(ast);
        block.addStatement(new Declaration(false, "int", new Identifier("x"), new Int(3)));
        block.addStatement(new Declaration(false, "int", new Identifier("y"), new Int(4)));
        program.addStatement(new FunctionDeclaration("Point", new Identifier("add"), null, block));
        assert ast.equals(program);

    }

    @Test
    public void testArray() {
        String input = "int[] a = a[5+(4*3)];";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        program.addStatement(new ArrayDeclaration("int[]", new Identifier("a"), new ArrayInitialisation("a", new BinaryExpression(new Int(5), new BinaryExpression(new Int(4), new Int(3), "*"), "+"))));
        assert ast.equals(program);
    }

    @Test
    public void testAssignmentWithAStructType() {
        String input = "Point p = 4 +5;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        program.addStatement(new Declaration(false, "Point", new Identifier("p"), new BinaryExpression(new Int(4), new Int(5), "+")));
        assert ast.equals(program);
    }

    @Test
    public void testFieldAccess() {
        String input = "p.a.a;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        FieldAccess fieldAccess = new FieldAccess(new FieldAccess(new Identifier("p"), new Identifier("a")), new Identifier("a"));
        program.addStatement(fieldAccess);
        assert ast.equals(program);

    }
    @Test
    public void testFieldAccessWithArray() {
        String input = "p.a[2];";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        FieldAccess fieldAccess = new FieldAccess(new Identifier("p"), new ArrayAccess(new Identifier("a"), new Int(2)));
        program.addStatement(fieldAccess);
        assert ast.equals(program);
    }

    @Test
    public void testFieldAccessWithArrayWithOtherFieldAccess() {
        String input = "p.a[2].b;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Statement ast = parser.getAST();
        Program program = new Program();
        FieldAccess fieldAccess = new FieldAccess(new FieldAccess(new Identifier("p"), new ArrayAccess(new Identifier("a"), new Int(2))), new Identifier("b"));
        program.addStatement(fieldAccess);
        assert ast.equals(program);
    }

}
