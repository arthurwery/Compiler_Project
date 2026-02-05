import compiler.CodeGenerator.BytecodeSaver;
import compiler.Semantic.Semantic;
import compiler.CodeGenerator.CodeGenerator;
import compiler.Lexer.Lexer;
import compiler.Lexer.TokenType;
import compiler.Parser.Parser;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class testCodegen {

    private List<String> result(String filename) {
        List<String> output = new ArrayList<>();
        Path workingDirectory = Paths.get("../testfolder/01/");
        String commandToExecute = "java -cp . " + filename;

        ProcessBuilder pb = new ProcessBuilder(commandToExecute.split(" "));
        pb.directory(workingDirectory.toFile());

        try {
            Process process = pb.start();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                output = bufferedReader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }
    private void codeGenTest(String input) {

        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        //while (lexer.getNextSymbol().getType() != TokenType.EOF) {
        // System.out.println(lexer.getNextSymbol());
        //}
        Parser parser = new Parser(lexer);
        Semantic semantic = new Semantic(parser);
        try {
            semantic.semantic();
            System.out.println(semantic.getAST());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Generate bytecode
        CodeGenerator codeGenerator = new CodeGenerator(semantic.getAST(),"test");
        byte[] bytecode = codeGenerator.generate();
        BytecodeSaver.saveBytecodeToFile(bytecode, "test");
        try (FileOutputStream fos = new FileOutputStream("test.class")) {
            fos.write(bytecode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> output = result("test");
        System.out.println(output);
    }

    private void printBytecode(byte[] bytecode) {
        try {
            ClassReader cr = new ClassReader(bytecode);
            cr.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            throw new RuntimeException("Error printing bytecode: " + e.getMessage(), e);
        }
    }



    private void printBytecodeInHex(byte[] bytecode) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytecode) {
            hexString.append(String.format("%02X ", b));
        }
        System.out.println(hexString.toString().trim());
    }

    @Test
    public void TestIf() {
        String input =
                "def void main() {\n" +
                        "    int i = 1 ;\n" +
                        "    if (i == 1) {\n" +
                        "        writeInt(1) ;\n" +
                        "    } else {\n" +
                        "        writeInt(0) ;\n" +
                        "    }\n" +
                        "}";
        codeGenTest(input);
        assertArrayEquals(new String[]{"1"}, result("test").toArray());
    }

    @Test
    public void TestForLoop() {
        String input = "def void main() {\n" +
                "    int i = 1 ;\n" +
                "    for (i = 1, i <= 10, i = i+1) {\n" +
                "        writeInt(i);\n" +
                "    }\n" +
                "}";
        codeGenTest(input);
        assertArrayEquals(new String[]{"12345678910"}, result("test").toArray());
    }

    @Test
    public void TestFunctionSquare() {
        String input = "def int square(int x) {\n" +
                "    return x * x;\n" +
                "}\n" +
                "\n" +
                "def void main() {\n" +
                "    int x = 5;\n" +
                "    int y = square(x);\n" +
                "    writeInt(y);\n" +
                "}";
        codeGenTest(input);
        assertArrayEquals(new String[]{"25"}, result("test").toArray());
    }

    @Test
    public void TestWriteln() {
        String input = "    def int square(int x) {\n" +
                "    return x * x;\n" +
                "}\n" +
                "\n" +
                "def void main() {\n" +
                "    int x = 6;\n" +
                "    int y = square(5);\n" +
                "    writeln(y);\n" +
                "}";
        codeGenTest(input);
        assertArrayEquals(new String[]{"25"}, result("test").toArray());
    }

    @Test
    public void TestWritelnString() {
        String input = "def void main() {\n" +
                "    writeln(\"\");\n" +
                "}" ;
        codeGenTest(input);
    }
    @Test
    public void TestWriteString() {
        String input = "def void main() {\n" +
                "        write(\"test\n\");\n" +
                "        write(2);\n" +
                "    }";
        codeGenTest(input);
    }
    @Test
    public void TestWriteInt() {
        String input = "def void main() {\n" +
                "        writeInt(2);\n" +
                "    }";
        codeGenTest(input);
    }
    @Test
    public void TestWriteFloat() {
        String input = "def void main() {\n" +
                "        writeFloat(2.5);\n" +
                "    }";
        codeGenTest(input);
    }
    @Test
    public void testttt() {
        String input =
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
        codeGenTest(input);

    }
}