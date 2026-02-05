package compiler;

import compiler.CodeGenerator.BytecodeSaver;
import compiler.CodeGenerator.CodeGenerator;
import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Lexer.TokenType;
import compiler.Parser.StatementsAndExpressions.Statement;
import compiler.Parser.Parser;
import compiler.Semantic.Semantic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Files;

public class Compiler {
    public static String extractFileName(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        int index = fileName.lastIndexOf('.');
        return (index != -1) ? fileName.substring(0, index) : fileName;
    }
    public static void main(String[] args) {
        if(args.length == 0){
            System.exit(1);
        }
        else if(args[0].equals("-lexer")){
            try {
                FileReader reader = new FileReader(args[1]);
                Lexer lexer = new Lexer(reader);
                Parser parser = new Parser(lexer);
                System.out.println(parser.getAST());
                List<Symbol> symbols = new ArrayList<>();
                Symbol symbol;
                while ((symbol = lexer.getNextSymbol()).getType() != TokenType.EOF) {
                    symbols.add(symbol);
                }
                symbols.add(symbol);

                for (Symbol s : symbols) {
                    System.out.println(s);
                }
            }
            catch (Exception e){
                System.exit(1);
            }
        }
        else if(args[0].equals("-parser")){
            try {
                FileReader reader = new FileReader(args[1]);
                Lexer lexer = new Lexer(reader);
                Parser parser = new Parser(lexer);
                Statement ast = parser.getAST();
                System.out.println(ast.toString());
            }
            catch (Exception e){
                System.out.println("Error");
                System.exit(1);
            }
        }
        else if(args[0].equals("-semantic")){
            try {
                Path filePath = Path.of(args[0]);
                String input = Files.readString(filePath);
                StringReader reader = new StringReader(input);
                Lexer lexer = new Lexer(reader);
                Parser parser = new Parser(lexer);
                Semantic semantic = new Semantic(parser);
                semantic.semantic();
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                System.exit(1);
            }

        }
        else{
            try {
                /*
                Path filePath = Path.of(args[0]);
                String input = Files.readString(filePath);
                StringReader reader = new StringReader(input);
                 */
                Path filePath = Path.of(args[0]);
                // Get all lines of the file manually with for
                String input = "";
                for (String line : Files.readAllLines(filePath)) {
                    input+= line + "\n";
                }
                System.out.println(input);
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
                System.out.println(args[0]);
                String filename = extractFileName(args[0]);
                System.out.println("First : "+filename);
                // Generate bytecode
                System.out.println("Filename: "+extractFileName(args[2]));
                CodeGenerator codeGenerator = new CodeGenerator(semantic.getAST(),extractFileName(args[2]));
                byte[] bytecode = codeGenerator.generate();
                BytecodeSaver.saveBytecodeToFile(bytecode, extractFileName(args[2]));
                try (FileOutputStream fos = new FileOutputStream(args[2])) {
                    fos.write(bytecode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Code generation completed successfully.");
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }

}
