import static org.junit.Assert.assertNotNull;

import compiler.Lexer.Symbol;
import compiler.Lexer.TokenType;
import org.junit.Test;

import java.io.StringReader;
import compiler.Lexer.Lexer;

public class TestLexer {
    
    @Test
    public void testBasique() {
        String input = "struct Point{}";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Symbol symbol = lexer.getNextSymbol();
        while (symbol.getType() != TokenType.EOF){
            System.out.println(symbol);
            symbol =lexer.getNextSymbol();
        }
        System.out.println(symbol);
    }
    @Test
    public void testString2() {
        String input = "\\\\";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Symbol symbol = lexer.getNextSymbol();
        while (symbol.getType() != TokenType.EOF){
            System.out.println(symbol);
            symbol =lexer.getNextSymbol();
        }
    }
    @Test
    public void testArray() {
        String input = "int[] a;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Symbol symbol = lexer.getNextSymbol();
        while (symbol.getType() != TokenType.EOF){
            System.out.println(symbol);
            symbol =lexer.getNextSymbol();
        }
    }
    @Test
    public void testFloat() {
        String input = "a = 0098;b=002565.2663;c = 2.3365.355.2232;point.x";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Symbol symbol = lexer.getNextSymbol();
        while (symbol.getType() != TokenType.EOF){
            System.out.println(symbol);
            symbol =lexer.getNextSymbol();
        }
    }
    @Test
    public void testIngi() {
        String input = "//Good luck\n" +
                "\n" +
                "final string message = \"Hello\";\n" +
                "final bool run = true;\n" +
                "\n" +
                "struct Point {\n" +
                "    int x;\n" +
                "    int y;\n" +
                "}\n" +
                "print(x.y); \n" +
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
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Symbol symbol = lexer.getNextSymbol();
        while (symbol.getType() != TokenType.EOF){
            System.out.println(symbol);
            symbol =lexer.getNextSymbol();
        }
    }



}
