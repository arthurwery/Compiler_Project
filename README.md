
# Custom Language Compiler

A full-featured compiler for a statically-typed custom language, written in Java. This project implements a complete compiler pipeline including lexical analysis, parsing, semantic validation, and Java bytecode generation using ASM.

## Overview

This compiler translates source code written in a custom language (`.lang` files) into executable Java bytecode. The compilation process follows a traditional multi-stage architecture with clear separation of concerns between lexing, parsing, semantic analysis, and code generation.

## Language Features

The language supports:

**Data Types:**
- `int` — 32-bit signed integers
- `float` — 32-bit floating-point numbers
- `bool` — boolean values (true/false)
- `string` — immutable text strings
- Arrays and custom structs

**Control Flow:**
- `if`/`else if`/`else` statements
- `for` loops
- `while` loops

**Operations:**
- Arithmetic: `+`, `-`, `*`, `/`, `%` (modulo for integers only)
- Comparison: `==`, `!=`, `<`, `>`, `<=`, `>=`
- Logical: `&&` (and), `||` (or), `!` (negation)
- String concatenation with `+`
- Array/string indexing with `[]`

**Functions:**
- User-defined function declarations and calls
- Optional parameters
- Return statements

**Built-in Functions:**
- `bool !(bool)` — negate a boolean
- `string chr(int)` — convert character code to string
- `int len(string or array)` — get length

**Variables:**
- Type declarations with optional `final` keyword
- Automatic type promotion (int to float in mixed expressions)

## Architecture

The compiler is organized into four main pipeline stages:

### 1. Lexer (`Lexer/`)
Tokenizes source code into a stream of symbols. Handles keywords, operators, literals, and identifiers.

### 2. Parser (`Parser/`)
Builds an Abstract Syntax Tree (AST) from tokens using recursive descent parsing. The AST represents the program structure as nested expression and statement objects.

### 3. Semantic Analyzer (`Semantic/`)
Validates the AST for semantic correctness:
- Type checking and type promotion rules
- Symbol table management
- Function and struct declarations
- Variable scope and redeclaration checks
- Throws `SemanticException` for violations

### 4. Code Generator (`CodeGenerator/`)
Translates the validated AST into Java bytecode (.class files) using the ASM library. Generates optimized bytecode with proper stack management and local variable allocation.

## Building and Running

### Prerequisites
- Java 11+
- Gradle

### Build
```bash
./gradlew build
```

On Windows:
```bash
gradlew.bat build
```

### Compile a Source File
```bash
./gradlew run --args="-compiler path/to/file.lang"
```

This generates a `.class` file that can be executed with the Java runtime.

### Other Compiler Modes

**Lexer only** (output tokens):
```bash
./gradlew run --args="-lexer path/to/file.lang"
```

**Parser only** (output AST):
```bash
./gradlew run --args="-parser path/to/file.lang"
```

**Semantic analysis only**:
```bash
./gradlew run --args="-semantic path/to/file.lang"
```

## Example Program

See [code_example.lang](code_example.lang) for a complete example of the language syntax and features.

## Testing

The project includes test suites for each compiler stage:
- `TestLexer.java` — lexical analysis tests
- `testParser.java` — parsing tests
- `testSemantic.java` — semantic validation tests
- `testCodegen.java` — code generation tests

Run tests with:
```bash
./gradlew test
```

## Current Status

**Fully Implemented:**
- ✅ Lexical analysis
- ✅ Parsing and AST construction
- ✅ Type system and type checking
- ✅ Variable declarations (with `final` support)
- ✅ Function declarations and calls
- ✅ Control flow (for, while, if/else)
- ✅ All operators (arithmetic, logical, comparison)
- ✅ String concatenation
- ✅ Arrays and array operations
- ✅ Structs (custom data types)
- ✅ Bytecode generation and execution

## Project Structure

```
src/main/java/compiler/
├── Compiler.java          # Entry point and CLI
├── Lexer/                 # Tokenization
├── Parser/                # AST construction
│   └── StatementsAndExpressions/  # AST node classes
├── Semantic/              # Type checking and validation
└── CodeGenerator/         # Bytecode generation

build/                    # Compiled output
```

## Implementation Notes

- The compiler uses a visitor pattern for AST traversal during semantic analysis and code generation
- Type promotion (int to float) is handled automatically in mixed expressions
- The ASM library is used for efficient bytecode generation
- All symbols and variables are tracked in symbol tables maintained during compilation

## License

This is an educational compiler project.