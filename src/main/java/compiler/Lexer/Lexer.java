package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class Lexer {
    private Reader input;
    private int currentChar;
    private StringBuilder word ;
    private boolean isString = false;
    private boolean isComment = false;
    private boolean isFloat = false;
    private static final Map<String, TokenType> specialCharacters ;
    static {
        specialCharacters = new HashMap<String, TokenType>();
        specialCharacters.put("=",TokenType.ASSIGNMENT_OPERATOR);
        specialCharacters.put("%",TokenType.OPERATOR);
        specialCharacters.put(";",TokenType.END_OF_INSTRUCTION);
        specialCharacters.put("(",TokenType.LPAREN);
        specialCharacters.put(")",TokenType.RPAREN);
        specialCharacters.put("{",TokenType.LBRACE);
        specialCharacters.put("}",TokenType.RBRACE);
        specialCharacters.put("[",TokenType.LBRACKET);
        specialCharacters.put("]",TokenType.RBRACKET);
        specialCharacters.put(",",TokenType.COMMA);
        specialCharacters.put("/",TokenType.OPERATOR);
        specialCharacters.put("*",TokenType.OPERATOR);
        specialCharacters.put("+",TokenType.OPERATOR);
        specialCharacters.put("-",TokenType.MINUS);
        specialCharacters.put(">",TokenType.OPERATOR);
        specialCharacters.put("<",TokenType.OPERATOR);
        specialCharacters.put("!",TokenType.FUNCTION);
        specialCharacters.put("&",TokenType.OPERATOR);
        specialCharacters.put("|",TokenType.OPERATOR);
        specialCharacters.put(".",TokenType.DOT);
        specialCharacters.put(":",TokenType.SPECIAL_CHARACTER);
        specialCharacters.put("\"",TokenType.SPECIAL_CHARACTER);
        specialCharacters.put("'",TokenType.SPECIAL_CHARACTER);
        specialCharacters.put("\\",TokenType.SPECIAL_CHARACTER);
    }
    private static final Map<String, TokenType> specialWords ;
    static {
        specialWords = new HashMap<String, TokenType>();
        specialWords.put("var",TokenType.VARIABLE_OPERATOR);
        specialWords.put("int",TokenType.TYPE);
        specialWords.put("string",TokenType.TYPE);
        specialWords.put("bool",TokenType.TYPE);
        specialWords.put("float",TokenType.TYPE);
        specialWords.put("true",TokenType.IDENTIFIER);
        specialWords.put("false",TokenType.IDENTIFIER);
        specialWords.put("if",TokenType.CONTROLFLOW);
        specialWords.put("else",TokenType.CONTROLFLOW);
        specialWords.put("while",TokenType.WHILE);
        specialWords.put("for",TokenType.FOR);
        specialWords.put("return",TokenType.RETURN);
        specialWords.put("func",TokenType.TYPE);
        specialWords.put("final",TokenType.VARIABLE_OPERATOR);
        specialWords.put("struct",TokenType.TYPE);
        specialWords.put("interface",TokenType.TYPE);
        specialWords.put("def",TokenType.TYPE);
        specialWords.put("void",TokenType.TYPE);
        specialWords.put("null",TokenType.TYPE);
        specialWords.put("chr",TokenType.FUNCTION);
        specialWords.put("len",TokenType.FUNCTION);
        specialWords.put("floor",TokenType.FUNCTION);
        specialWords.put("free",TokenType.FREE);
    }
    private static final Map<String, TokenType> specialOperators ;
    static {
        specialOperators = new HashMap<String, TokenType>();
        specialOperators.put("==",TokenType.OPERATOR);
        specialOperators.put("!=",TokenType.OPERATOR);
        specialOperators.put(">=",TokenType.OPERATOR);
        specialOperators.put("<=",TokenType.OPERATOR);
        specialOperators.put("&&",TokenType.OPERATOR);
        specialOperators.put("||",TokenType.OPERATOR);
        specialOperators.put("\\\"",TokenType.OPERATOR);
        specialOperators.put("//",TokenType.OPERATOR);
    }


    public Lexer(Reader input){
        this.input = input;
        this.word = new StringBuilder();
        readChar();
    }

    private void readChar() {
        try {
            currentChar = input.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isSpecialCharacter(char c){
        return specialCharacters.containsKey(String.valueOf(c));
    }
    private boolean isSpace(char c){
        return c == ' ' || c == '\n' || c == '\t';
    }
    private boolean isNumeric(StringBuilder word){
        try {
            Double.parseDouble(word.toString());
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
    private Symbol numericHandle(StringBuilder word){
        if (isFloat){
            float f=Float.parseFloat(word.toString());
            isFloat=false;
            return new Symbol(TokenType.FLOAT, Float.toString(f));
        } else{
            int i=Integer.parseInt(word.toString());
            return new Symbol(TokenType.INT, Integer.toString(i));
        }
    }

    private boolean isQuote(char c){
        return c == '"' || c == '\'';
    }

    private Symbol checkNextSymbol(char charToCheck){
        if(isSpecialCharacter(charToCheck) || isSpace(charToCheck)){
            if(word.isEmpty()) {
                if (!isSpace(charToCheck)){
                    readChar();
                    String combination = String.valueOf(charToCheck) + String.valueOf((char) currentChar);
                    if (specialOperators.containsKey(combination)){
                        if (combination.equals("//")){
                            isComment = true;
                            readChar();
                            return null;
                        }
                        if(combination.equals("\\\"")){
                            word.append("\"");
                            readChar();
                            return null;
                        }
                        readChar();
                        return new Symbol(specialOperators.get(combination), combination);
                    }
                    if(isQuote(charToCheck)){
                        isString = !isString;
                    }
                    return new Symbol(specialCharacters.get(String.valueOf(charToCheck)), String.valueOf(charToCheck));
                }
                else{
                    readChar();
                    return null;
                }
            }
            else{
                if (isComment){
                    if (charToCheck == '\n'){
                        isComment = false;
                        readChar();
                        word= new StringBuilder();
                        return null;
                    }
                    readChar();
                    return null;
                }
                if (isString && !isQuote(charToCheck)){
                    word.append(charToCheck);
                    readChar();
                    return null;
                }if (isString && isQuote(charToCheck)){
                    Symbol symbol = new Symbol(TokenType.STRING, word.toString());
                    return symbol;
                }
                if (isNumeric(word)) {
                    // Si c'est un nombre
                    if(charToCheck== '.'){
                        word.append(charToCheck);
                        readChar();
                        isFloat=true;
                        return null;
                    }
                    return numericHandle(word);
                } else {
                    // Si c'est un mot
                    if(specialWords.containsKey(word.toString())){
                        Symbol symbol = new Symbol(specialWords.get(word.toString()), word.toString());
                        return symbol;
                    }
                    Symbol symbol = new Symbol(TokenType.IDENTIFIER, word.toString());
                    return symbol;
                }
            }

        }
        else {
            // Si c'est un caractère normal
            word.append(charToCheck);
            readChar();
            return null;
        }
    }

    public Symbol getNextSymbol() {
        word = new StringBuilder();
        while(currentChar != -1){
            char charToCheck = (char) currentChar;
            Symbol result = checkNextSymbol(charToCheck);
            if (result != null) {
                return result;
            }
        }
        if (word.isEmpty()){
            return new Symbol(TokenType.EOF, "EOF");
        }
        // CHECK POUR LE DERNIER SYMBOLE (currentChar == -1)
        if (specialCharacters.containsKey(word.toString())) {
            // Si c'est un caractère spécial
            Symbol symbol = new Symbol(specialCharacters.get(word.toString()), word.toString());
            return symbol;
        } else {
            if (isNumeric(word)) {
                // Si c'est un nombre
                return numericHandle(word);
            } else {
                // Si c'est un mot
                Symbol symbol = new Symbol(TokenType.IDENTIFIER, word.toString());
                return symbol;
            }
        }
    }
}
