
import compiler.Parser.StatementsAndExpressions.*;
import compiler.Semantic.Semantic;
import org.junit.Test;

import java.io.StringReader;
import java.lang.reflect.Field;

import compiler.Lexer.Lexer;
import compiler.Parser.Parser;

import static org.junit.Assert.fail;

public class testSemantic {

    @Test
    public void TestSimpleAssignment() {
        String input = "int a = 2;int b = a; int c = b + 2;";
        semanticTest(input);
    }
    @Test
    public void TestTypeError() {
        String input = "int a = 2;int b = a; int c = b + 2.0;";
        semanticTest(input);
    }

    @Test
    public void TestTypeErrorAssignment() {
        String input = "int a ; a = \"test\";";
        semanticTest(input);
    }
    @Test
    public void SimpleDeclaration() {
        String input = "int a ;";
        semanticTest(input);
    }

    @Test
    public void testStruct() {
        String input = "struct void {\n" +
                "    int x;\n" +
                "    int y;\n" +
                "}";
        semanticTest(input);
    }
    @Test
    public void testStructError() {
        String input = "struct void {\n" +
                "    int x;\n" +
                "    int y;\n" +
                "}";
        semanticTest(input);
    }
    @Test
    public void testStructSemantic() {
        String input = "struct Point {\n" +
                "    int x;\n" +
                "    int y;\n" +
                "}";
        semanticTest(input);
    }
    @Test
    public void testStructSemanticTypeError() {
        String input = "struct Point {\n" +
                "    int x = 2.0;\n" +
                "    int y;\n" +
                "}";
        semanticTest(input);
    }

    @Test
    public void checkOperator() {
        String input = "bool a = \"test\" == \"test\";";
        semanticTest(input);
    }

    @Test
    public void checkOperatorError() {
        String input = "bool a = \"test\" >= \"test\";";
        semanticTest(input);
    }

    @Test
    public void checkFunctionCall() {
        String input = "def int test(int a, int b) {\n" +
                "    return a + b;\n" +
                "}\n" +
                "int a = test(2, 3);";
        semanticTest(input);
    }

    @Test
    public void checkFunctionCallErrorSize() {
        String input = "def int test(int a, int b) {\n" +
                "    return a + b;\n" +
                "}\n" +
                "int a = test(2, 3,4);";
        semanticTest(input);
    }

    @Test
    public void checkFunctionCallErrorType() {
        String input = "def int test(int a, int b) {\n" +
                "    return a + b;\n" +
                "}\n" +
                "int a = test(2.0, 3.0);";
        semanticTest(input);
    }

    @Test
    public void checkFunctionCallTypeErrorReturn() {
        String input = "def int test(int a, int b) {\n" +
                "    return a + b;\n" +
                "}\n" +
                "int a = test(2 + 4 + 5, test(1,2.0));";
        semanticTest(input);
    }

    @Test
    public void TypeErrorArray() {
        String input =
                "int[] c = int[5];" +
                        "int a = c + 5;";
        semanticTest(input);
    }

    @Test
    public void TypeErrorReadInt() {
        String input =
                "float c = readFloat(5);" +
                        "int a = c + 5;";
        semanticTest(input);
    }
    @Test
    public void checkFunctionReturnError() {
        String input = "def int test(int a, int b) {\n" +
                "    return 2;\n" +
                "}\n" +
                "def float test2(int a, int b) {\n" +
            "    return test(1,2);\n" +
                    "}\n" +
                "int a = test(0,1);" + "float b = test2(0,1);";
        semanticTest(input);
    }

    // TO DOOOOOOOOOOOOOO
    @Test
    public void get_variables_from_arguments() {
        String input = "def int test(int a, int b) {\n" +
                "    return c+d;\n" +
                "}\n" +
                "def float test2(int a, int b) {\n" +
                "    return test(1,2);\n" +
                "}\n" +
                "int a = test(0,1);" + "float b = test2(0,1);";
        semanticTest(input);
    }

    @Test
    public void check_conditions_if() {
        String input = "if(4 + 4) {\n" +
                "    int a = 2;\n" +
                "} else {\n" +
                "    int a = 3;\n" +
                "}";
        semanticTest(input);
    }

    @Test
    public void check_conditions_while() {
        String input = "while(4 + 4) {\n" +
                "    int a = 2;\n" +
                "}";
        semanticTest(input);
    }

    @Test
    public void check_conditions_for() {
        String input = "int i;" +
                "for(i = 0, 1+1, i = i + 1) {\n" +
                "    int a = 2;\n" +
                "}";
        semanticTest(input);
    }

    @Test
    public void check_scope() {
        String input = "int a = 2;\n" +
                "{\n" +
                "    int z = 3;\n" +
                "}\n" +
                "int b = a;";
        semanticTest(input);
    }
    @Test
    public void testArrayDeclarationAndInitialization() {
        String input = "int[] c = int[cell(5)+8 *3];";
        semanticTest(input);
    }

    @Test
    public void check_scopeError() {
        String input = "//Good luck\n" +
                "\n" +
                "final string message = \"Hello\";\n" +
                "final bool run = true;\n" +
                "\n" +
                "struct Point {\n" +
                "    int x;\n" +
                "    int y;\n" +
                "}\n" +
                "\n" +
                "int a = 3;\n" +
                "\n" +
                "def int square(int v) {\n" +
                "    return v*v;\n" +
                "}\n" +
                "\n" +
                "def void main() {\n" +
                "    int value = readInt();\n" +
                "    Point p = Point(a, a+value);\n" +
                "    writeInt(square(value));\n" +
                "    writeln();\n" +
                "    int i;\n" +
                "    for (i=1, i<a, i = i+1) {\n" +
                "        while (value!=0) {\n" +
                "            if (run){\n" +
                "                value = value - 1;\n" +
                "            } else {\n" +
                "                write(message);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    i = (i+2)*2;\n" +
                "}";
        semanticTest(input);
    }
    

    private void semanticTest(String input) {
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Semantic semantic = new Semantic(parser);
        try {
            semantic.semantic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void expectSemanticException(String input, String expectedMessage) {
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        Semantic semantic = new Semantic(parser);
        try {
            semantic.semantic();
            fail("Expected an exception to be thrown: " + expectedMessage);
        } catch (Exception e) {
            // Check if the caught exception message is what we expect
            assert e.getMessage().contains(expectedMessage);
        }
    }

    @Test
    public void basic_type_operations() {
        String input = "def void main() {\n" +
                "    int a = 5;\n" +
                "    float b = 3.0;\n" +
                "    string c = \"test\";\n" +
                "    bool d = true;\n" +
                "\n" +
                "    float result = a + b;  \n" +
                "    string combined = c + \" success\";  \n" +
                "    bool comparison = (a == 5); \n" +
                "\n" +
                "    writeln(\"Result: \", result);\n" +
                "    writeln(\"Combined: \", combined);\n" +
                "    writeln(\"Comparison: \", comparison);\n" +
                "}\n";
        semanticTest(input);
    }

    @Test
    public void func_call_param_float() {
        String input = "def float multiply(int x, float y) {\n" +
                "    return x * y; \n" +
                "}\n" +
                "def void main() {\n" +
                "    int a = 3;\n" +
                "    float b = 2.0;\n" +
                "    writeln(multiply(a, b));\n" +
                "}";
        semanticTest(input);
    }

    @Test
    public void conditional_loop() {
        String input = "def void main() {\n" +
                "    int count = 0;\n" +
                "\n" +
                "    int i ; \n" +
                "    for (i = 0, i < 10, i = i + 1) {\n" +
                "        if (i % 2 == 0) {\n" +
                "            count = count + 1;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    writeln(\"Even count: \", count);\n" +
                "}";
        semanticTest(input);
    }

    @Test
    public void struct_test_basic() {
        String input = "struct Point {\n" +
                "    int x;\n" +
                "    int y;\n" +
                "}\n" +
                "\n" +
                "def void main() {\n" +
                "    Point p = Point(3, 4);\n" +
                "    writeln(\"Point x: \", p.x);\n" +
                "    writeln(\"Point y: \", p.y);\n" +
                "}";
        semanticTest(input);
    }

    @Test
    public void same_name_func_and_struct() {
        String input = "struct Point {\n" +
                "    int x;\n" +
                "    int y;\n" +
                "}\n" +
                "\n" +
                "def void Point() {\n" +
                "    writeln(\"Hello\");\n" +
                "}\n" +
                "\n" +
                "def void main() {\n" +
                "    Point();\n" +
                "}";
        semanticTest(input);
    }
}
