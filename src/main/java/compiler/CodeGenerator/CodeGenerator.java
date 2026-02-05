package compiler.CodeGenerator;

import compiler.Parser.StatementsAndExpressions.*;
import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CodeGenerator implements Opcodes {

    private final Statement ast;
    private ClassWriter cw;
    private MethodVisitor mv;
    private MethodVisitor mvholding;
    private String containerName = "GeneratedClass";
    private final SymbolTableCode symbolTable;
    private int variableCounter = 0;
    private int holdingCounter;
    private boolean topLevel;
    private boolean hasEncounteredMain;
    private final List<Pair<String, ClassWriter>> structs = new ArrayList<>();

    public CodeGenerator(Statement ast) {
        this.ast = ast;
        this.symbolTable = new SymbolTableCode();
        this.variableCounter = 0;
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
    public CodeGenerator(Statement ast, String filename) {
        this.ast = ast;
        this.symbolTable = new SymbolTableCode();
        this.variableCounter = 0;
        this.containerName = filename;
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }

    public byte[] generate() {
        structs.add(new Pair<>(containerName, cw));
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, containerName, null, "java/lang/Object", null);
        topLevel = true;
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();

        generateCodeStatement(ast);

        if (!hasEncounteredMain) {
            mv.visitInsn(Opcodes.RETURN);
            mv.visitEnd();
            mv.visitMaxs(-1, -1);  // Ensure max stack and locals are computed automatically
        }

        // End the main class
        cw.visitEnd();

        // Process and write structs
        for (Pair<String, ClassWriter> struct : structs) {
            byte[] structBytes = struct.getValue().toByteArray();
            try (FileOutputStream stream = new FileOutputStream(struct.getKey() + ".class")) {
                stream.write(structBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return cw.toByteArray();
    }

    private void generateFunction(FunctionDeclaration node) {
        if (topLevel) {
            topLevel = false;
            mvholding = mv;
            holdingCounter = variableCounter;
        }

        String name = node.getIdentifier().getName();
        System.out.println("Generating function: " + name);
        String descriptor = getMethodDescriptor(node);
        String type = node.getType();
        boolean isMainFunction = Objects.equals(name, "main") && Objects.equals(type, "void");

        if (isMainFunction) {
            variableCounter = holdingCounter;
            mv = mvholding;
            hasEncounteredMain = true;
        } else {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, name, descriptor, null, null);
        }
        mv.visitCode();

        symbolTable.startFunction();
        variableCounter = 0;

        if (node.getParameters() instanceof Arguments arguments) {
            for (Expression param : arguments.getArguments()) {
                if (param instanceof Parameter parameter) {
                    String paramName = parameter.getIdentifier();
                    int index = variableCounter++;
                    symbolTable.add(paramName, index);
                }
            }
        }

        generateCodeStatement(node.getBody());

        if (descriptor.endsWith("V"))
            mv.visitInsn(RETURN);

        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        symbolTable.endFunction();

        variableCounter = holdingCounter;
        mv = mvholding;
        topLevel = true;
    }


    private void generateCodeStatement(Statement node) {
        if (node == null) return;
        if (topLevel) {
            if (node instanceof FunctionDeclaration) {
                generateFunction((FunctionDeclaration) node);
            } else if (node instanceof Program) {
                generateCodeProgram((Program) node);
            } else if (node instanceof Struct) {
                generateCodeStruct((Struct) node);
            } else if (node instanceof Declaration) {
                generateTopLevelDeclaration((Declaration) node);
            } else {
                throw new RuntimeException("Invalid statement at top level, type: " + node.getClass());
            }
        } else {
            if (node instanceof Block) {
                generateCodeBlock((Block) node);
            } else if (node instanceof For) {
                generateCodeFor((For) node);
            } else if (node instanceof Free) {
                generateCodeFree((Free) node);
            } else if (node instanceof If) {
                generateCodeIf((If) node);
            } else if (node instanceof Return) {
                generateCodeReturn((Return) node);
            } else if (node instanceof Struct) {
                generateCodeStruct((Struct) node);
            } else if (node instanceof While) {
                generateCodeWhile((While) node);
            } else if (node instanceof Declaration) {
                generateCodeDeclaration((Declaration) node);
            } else if (node instanceof Expression) {
                generateCodeExpression((Expression) node);
            } else {
                throw new RuntimeException("Unknown statement type: " + node.getClass());
            }
        }
    }

    private void generateCodeExpression(Expression node) {
        if (node == null) return;
        if (node instanceof Arguments) {
            generateCodeArguments((Arguments) node);
        } else if (node instanceof ArrayAccess) {
            generateCodeArrayAccess((ArrayAccess) node);
        } else if (node instanceof ArrayDeclaration) {
            generateCodeArrayDeclaration((ArrayDeclaration) node);
        } else if (node instanceof ArrayInitialisation) {
            generateCodeArrayInitialisation((ArrayInitialisation) node);
        } else if (node instanceof Assignment) {
            generateCodeAssignment((Assignment) node);
        } else if (node instanceof BinaryExpression) {
            generateCodeBinaryExpression((BinaryExpression) node);
        } else if (node instanceof Identifier && Objects.equals(node.getType(), "bool")) {
            generateCodeBool((Identifier) node);
        } else if (node instanceof FieldAccess) {
            generateCodeFieldAccess((FieldAccess) node);
        } else if (node instanceof Float_class) {
            generateCodeFloat_class((Float_class) node);
        } else if (node instanceof FunctionCall) {
            generateCodeFunctionCall((FunctionCall) node);
        } else if (node instanceof Identifier) {
            generateCodeIdentifier((Identifier) node);
        } else if (node instanceof Int) {
            generateCodeInt((Int) node);
        } else if (node instanceof Parameter) {
            generateCodeParameter((Parameter) node);
        } else if (node instanceof Str) {
            generateCodeStr((Str) node);
        } else if (node instanceof UnaryExpression) {
            generateCodeUnaryExpression((UnaryExpression) node);
        } else {
            throw new RuntimeException("Unknown expression type: " + node.getClass());
        }
    }

    private void generateCodeBool(Identifier node) {
        if (Objects.equals(node.getName(), "true")) {
            mv.visitInsn(Opcodes.ICONST_1);
        } else {
            mv.visitInsn(Opcodes.ICONST_0);
        }
    }

    private void generateCodeUnaryExpression(UnaryExpression node) {
        generateCodeExpression(node.getExpression());
        switch (node.getOperator()) {
            case "!":
                Label trueLabel = new Label();
                Label endLabel = new Label();
                mv.visitJumpInsn(Opcodes.IFEQ, trueLabel);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitJumpInsn(Opcodes.GOTO, endLabel);
                mv.visitLabel(trueLabel);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitLabel(endLabel);
                break;
            case "-":
                mv.visitInsn(Opcodes.INEG);
                break;
            default:
                throw new RuntimeException("Unknown unary operator: " + node.getOperator());
        }
    }

    private void generateCodeStr(Str node) {
        mv.visitLdcInsn(node.getValue());
    }

    private void generateCodeParameter(Parameter node) {
        String name = node.getIdentifier();
        int index = variableCounter++;
        symbolTable.add(name, index);
    }

    private void generateCodeInt(Int node) {
        mv.visitLdcInsn(node.getValue());
    }

    private void generateCodeIdentifier(Identifier node) {
        String name = node.getName();
        String type = node.getType();

        // Handle boolean literals directly
        if ("true".equals(name)) {
            mv.visitInsn(Opcodes.ICONST_1);
            return;
        } else if ("false".equals(name)) {
            mv.visitInsn(Opcodes.ICONST_0);
            return;
        }

        Integer index = symbolTable.lookupIndex(name);
        if (index != null) {
            if (symbolTable.isGlobal(name)) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, containerName, name, getTypeDescriptor(type));
            } else {
                mv.visitVarInsn(getLoadOpcode(type), index);
            }
        } else {
            throw new RuntimeException("Unknown variable: " + name);
        }
    }

    private int getLoadOpcode(String type) {
        return switch (type) {
            case "int", "bool" -> Opcodes.ILOAD;
            case "float" -> Opcodes.FLOAD;
            case "string" -> Opcodes.ALOAD;
            default -> {
                if (isStructType(type)) {
                    yield Opcodes.ALOAD;
                } else {
                    throw new RuntimeException("Unknown type: " + type);
                }
            }
        };
    }


    private int getStoreOpcode(String type) {
        return switch (type) {
            case "int", "bool" -> Opcodes.ISTORE;
            case "float" -> Opcodes.FSTORE;
            case "string" -> Opcodes.ASTORE;
            default -> {
                if (isStructType(type)) {
                    yield Opcodes.ASTORE;
                } else {
                    throw new RuntimeException("Unknown type: " + type);
                }
            }
        };
    }

    private void generateCodeFunctionCall(FunctionCall node) {
        if (isBuiltInFunction(node.getIdentifier().getName())) {
            generateBuiltInFunctionCall(node);
        } else {
            generateUserDefinedFunctionCall(node);
        }
    }

    private boolean isBuiltInFunction(String functionName) {
        return Arrays.asList("readInt", "readFloat", "writeInt", "writeFloat", "write", "writeln").contains(functionName);
    }

    private void generateBuiltInFunctionCall(FunctionCall node) {
        String functionName = node.getIdentifier().getName();
        String variableType = null;
        if (node.getArguments() instanceof Arguments arguments) {
            for (Expression argument : arguments.getArguments()) {
                generateCodeExpression(argument);
                variableType = argument.getType();
            }
        }
        switch (functionName) {
            case "readInt":
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
                mv.visitTypeInsn(NEW, "java/util/Scanner");
                mv.visitInsn(DUP);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
                break;
            case "readFloat":
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
                mv.visitTypeInsn(NEW, "java/util/Scanner");
                mv.visitInsn(DUP);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextFloat", "()F", false);
                break;
            case "writeInt":
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitInsn(Opcodes.SWAP);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream","print", "(I)V", false);
                break;
            case "writeFloat":
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitInsn(SWAP);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(F)V", false);
                break;
            case "write":
                String currentDescriptor = getTypeDescriptorBuiltInFunctions(variableType);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitInsn(SWAP);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", currentDescriptor, false);
                break;
            case "writeln":
                String descriptor = getTypeDescriptorBuiltInFunctions(variableType);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitInsn(SWAP);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", descriptor, false);
                break;
            default:
                throw new RuntimeException("Unknown built-in function: " + functionName);
        }
    }

    private String getTypeDescriptorBuiltInFunctions(String type) {
        String descriptor;
        if (Objects.equals(type, "int")){
            descriptor = "(I)V";
        } else if (Objects.equals(type, "float")){
            descriptor = "(F)V";
        } else if (Objects.equals(type, "bool")){
            descriptor = "(Z)V";
        }else {
            descriptor = "(Ljava/lang/String;)V";
        }
        return descriptor;
    }

    private void generateUserDefinedFunctionCall(FunctionCall node) {
        if (node.getArguments() instanceof Arguments arguments) {
            for (Expression argument : arguments.getArguments()) {
                generateCodeExpression(argument);
            }
        }
        String descriptor = getMethodDescriptor(node);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, containerName, node.getIdentifier().getName(), descriptor, false);
    }


    private String getMethodDescriptor(FunctionCall node) {
        StringBuilder descriptor = new StringBuilder();
        descriptor.append("(");
        if (node.getArguments() instanceof Arguments arguments) {
            for (Expression argument : arguments.getArguments()) {
                descriptor.append(getTypeDescriptor(argument.getType()));
            }
        }
        descriptor.append(")");
        descriptor.append(getTypeDescriptor(node.getType()));
        return descriptor.toString();
    }

    private void generateCodeFloat_class(Float_class node) {
        mv.visitLdcInsn(node.getValue());
    }

    private void generateCodeFieldAccess(FieldAccess node) {
        generateCodeExpression(node.getIdentifier());
        String fieldName = ((Identifier) node.getField()).getName();
        mv.visitFieldInsn(Opcodes.GETFIELD, containerName, fieldName, getTypeDescriptor(node.getType()));
    }

    private void generateTopLevelDeclaration(Declaration node) {
        FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, node.getIdentifier().getName(), getTypeDescriptor(node.getType()), null, null);
        fv.visitEnd();
        if (node.getValue() != null) {
            generateCodeExpression(node.getValue());
            mv.visitFieldInsn(Opcodes.PUTSTATIC, containerName, node.getIdentifier().getName(), getTypeDescriptor(node.getType()));
        }

        // Add to symbol table
        String name = node.getIdentifier().getName();
        symbolTable.add(name, variableCounter++); // Use unique index for global variables
    }

    private void generateCodeDeclaration(Declaration node) {
        if (node.getValue() != null) {
            generateCodeExpression(node.getValue());
        } else {
            switch (node.getType()) {
                case "int":
                    mv.visitInsn(Opcodes.ICONST_0);
                    break;
                case "float":
                    mv.visitInsn(Opcodes.FCONST_0);
                    break;
                case "bool":
                    mv.visitInsn(Opcodes.ICONST_0);
                    break;
                case "string":
                    mv.visitInsn(Opcodes.ACONST_NULL);
                    break;
                default:
                    if (isStructType(node.getType())) {
                        mv.visitInsn(Opcodes.ACONST_NULL);
                    } else {
                        throw new RuntimeException("Unknown type: " + node.getType());
                    }
            }
        }

        String name = node.getIdentifier().getName();
        int index = variableCounter++;
        symbolTable.add(name, index);
        mv.visitVarInsn(getStoreOpcode(node.getType()), index);
    }

    private void generateCodeBinaryExpression(BinaryExpression node) {
        generateCodeExpression(node.getLeft());
        generateCodeExpression(node.getRight());

        if (node.getLeft().getType().equals("float") || node.getRight().getType().equals("float")) {
            if (node.getLeft().getType().equals("int")) {
                mv.visitInsn(Opcodes.I2F);
            } else if (node.getRight().getType().equals("int")) {
                mv.visitInsn(Opcodes.I2F);
            }
        }

        Label trueLabel = new Label();
        Label endLabel = new Label();

        switch (node.getOperator()) {
            case "+":
                mv.visitInsn(node.getType().equals("float") ? Opcodes.FADD : Opcodes.IADD);
                break;
            case "-":
                mv.visitInsn(node.getType().equals("float") ? Opcodes.FSUB : Opcodes.ISUB);
                break;
            case "*":
                mv.visitInsn(node.getType().equals("float") ? Opcodes.FMUL : Opcodes.IMUL);
                break;
            case "/":
                mv.visitInsn(node.getType().equals("float") ? Opcodes.FDIV : Opcodes.IDIV);
                break;
            case "%":
                if (node.getType().equals("int")) {
                    mv.visitInsn(Opcodes.IREM);
                } else {
                    throw new RuntimeException("Unknown binary operator for type: " + node.getType());
                }
                break;
            case "&&":
                mv.visitInsn(Opcodes.IAND);
                break;
            case "||":
                mv.visitInsn(Opcodes.IOR);
                break;
            case "==":
                mv.visitJumpInsn(Opcodes.IF_ICMPEQ, trueLabel);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitJumpInsn(Opcodes.GOTO, endLabel);
                mv.visitLabel(trueLabel);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitLabel(endLabel);
                break;
            case "!=":
                mv.visitJumpInsn(Opcodes.IF_ICMPNE, trueLabel);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitJumpInsn(Opcodes.GOTO, endLabel);
                mv.visitLabel(trueLabel);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitLabel(endLabel);
                break;
            case "<":
                mv.visitJumpInsn(Opcodes.IF_ICMPLT, trueLabel);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitJumpInsn(Opcodes.GOTO, endLabel);
                mv.visitLabel(trueLabel);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitLabel(endLabel);
                break;
            case "<=":
                mv.visitJumpInsn(Opcodes.IF_ICMPLE, trueLabel);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitJumpInsn(Opcodes.GOTO, endLabel);
                mv.visitLabel(trueLabel);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitLabel(endLabel);
                break;
            case ">":
                mv.visitJumpInsn(Opcodes.IF_ICMPGT, trueLabel);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitJumpInsn(Opcodes.GOTO, endLabel);
                mv.visitLabel(trueLabel);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitLabel(endLabel);
                break;
            case ">=":
                mv.visitJumpInsn(Opcodes.IF_ICMPGE, trueLabel);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitJumpInsn(Opcodes.GOTO, endLabel);
                mv.visitLabel(trueLabel);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitLabel(endLabel);
                break;
            default:
                throw new RuntimeException("Unknown binary operator: " + node.getOperator());
        }
    }

    private void generateCodeAssignment(Assignment node) {
        generateCodeExpression(node.getValue());
        String name = node.getIdentifier().getName();
        String type = node.getIdentifier().getType();
        Integer index = symbolTable.lookupIndex(name);
        if (index == null) {
            throw new RuntimeException("Unknown variable: " + name);
        }
        if (symbolTable.isGlobal(name)) {
            mv.visitFieldInsn(Opcodes.PUTSTATIC, containerName, name, getTypeDescriptor(type));
        } else {
            mv.visitVarInsn(getStoreOpcode(type), index);
        }
    }

    private void generateCodeArrayInitialisation(ArrayInitialisation node) {
        generateCodeExpression(node.getSize());
        String type = node.getType();
        int arrayType = getArrayType(type);
        mv.visitIntInsn(Opcodes.NEWARRAY, arrayType);
    }

    private void generateCodeArrayDeclaration(ArrayDeclaration node) {
        generateCodeArrayInitialisation(node.getArrayInitialisation());
        String name = node.getIdentifier().getName();
        int index = variableCounter++;
        symbolTable.add(name, index);
        mv.visitVarInsn(Opcodes.ASTORE, index);
    }

    private void generateCodeArrayAccess(ArrayAccess node) {
        generateCodeExpression(node.getIdentifier());
        generateCodeExpression(node.getIndex());
        String arrayType = node.getIdentifier().getType();
        mv.visitInsn(getArrayLoadOpcode(arrayType));
    }

    private int getArrayLoadOpcode(String type) {
        return switch (type) {
            case "int" -> Opcodes.IALOAD;
            case "float" -> Opcodes.FALOAD;
            case "bool" -> Opcodes.BALOAD;
            case "string" -> Opcodes.AALOAD;
            default -> throw new RuntimeException("Unknown array type: " + type);
        };
    }

    private void generateCodeArguments(Arguments node) {
        for (Expression argument : node.getArguments()) {
            generateCodeExpression(argument);
        }
    }

    private void generateCodeWhile(While node) {
        symbolTable.enterScope();
        Label startLabel = new Label();
        Label endLabel = new Label();
        mv.visitLabel(startLabel);
        generateCodeExpression(node.getCondition());
        mv.visitJumpInsn(Opcodes.IFEQ, endLabel);
        generateCodeStatement(node.getBody());
        mv.visitJumpInsn(Opcodes.GOTO, startLabel);
        mv.visitLabel(endLabel);
        symbolTable.exitScope();
    }


    private void generateCodeStruct(Struct node) {
        String structName = node.getIdentifier().getName();
        String innerClassName = containerName + "$" + structName;

        // Create a new ClassWriter for the struct
        ClassWriter structWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        structWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, innerClassName, null, "java/lang/Object", null);

        // Add fields to the struct
        StringBuilder constructorDesc = new StringBuilder();
        constructorDesc.append("(");
        for (Expression field : node.getFields()) {
            if (field instanceof Declaration fieldDecl) {
                String fieldName = fieldDecl.getIdentifier().getName();
                String fieldType = getTypeDescriptor(fieldDecl.getType());
                constructorDesc.append(fieldType);

                // Handle nested structs
                if (fieldType.contains("$")) {
                    structWriter.visitInnerClass(containerName + "$" + fieldDecl.getType(), containerName, fieldDecl.getType(), Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC);
                }
                structWriter.visitField(Opcodes.ACC_PUBLIC, fieldName, fieldType, null, null).visitEnd();
            } else {
                throw new RuntimeException("Unknown field type in struct: " + field.getClass());
            }
        }
        constructorDesc.append(")V");

        // Generate default constructor for the struct
        MethodVisitor constructor = structWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", constructorDesc.toString(), null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0); // this
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        int index = 1;
        for (Expression field : node.getFields()) {
            if (field instanceof Declaration fieldDecl) {
                constructor.visitVarInsn(Opcodes.ALOAD, 0);
                String fieldType = getTypeDescriptor(fieldDecl.getType());
                constructor.visitVarInsn(getLoadOpcode(fieldDecl.getType()), index);
                index += (fieldType.equals("J") || fieldType.equals("D")) ? 2 : 1;
                constructor.visitFieldInsn(Opcodes.PUTFIELD, innerClassName, fieldDecl.getIdentifier().getName(), fieldType);
            }
        }

        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(-1, -1);  // Ensure max stack and locals are computed automatically
        constructor.visitEnd();

        structWriter.visitEnd();
        structWriter.visitNestHost(containerName);
        structWriter.visitInnerClass(innerClassName, containerName, structName, ACC_PUBLIC | ACC_STATIC);
        cw.visitInnerClass(innerClassName, containerName, structName, ACC_PUBLIC | ACC_STATIC);
        cw.visitNestMember(innerClassName);
        structs.add(new Pair<>(innerClassName, structWriter));
    }

    private boolean isStructType(String type) {
        for (Pair<String, ClassWriter> struct : structs) {
            if (struct.getKey().equals(containerName + "$" + type)) {
                return true;
            }
        }
        return false;
    }

    private void generateCodeReturn(Return node) {
        if (node.getValue() != null) {
            generateCodeExpression(node.getValue());
            mv.visitInsn(getReturnOpcode(node.getValue().getType()));
        } else {
            mv.visitInsn(Opcodes.RETURN);
        }
    }

    private int getReturnOpcode(String type) {
        return switch (type) {
            case "int" -> Opcodes.IRETURN;
            case "float" -> Opcodes.FRETURN;
            case "boolean" -> Opcodes.IRETURN;
            case "string" -> Opcodes.ARETURN;
            default -> {
                if (isStructType(type)) {
                    yield Opcodes.ARETURN;
                } else {
                    throw new RuntimeException("Unknown return type: " + type);
                }
            }
        };
    }

    private void generateCodeProgram(Program node) {
        for (Statement stmt : node.getStatements()) {
            generateCodeStatement(stmt);
        }
    }

    private void generateCodeIf(If node) {
        symbolTable.enterScope();
        Label elseLabel = new Label();
        Label endLabel = new Label();
        generateCodeExpression(node.getCondition());
        mv.visitJumpInsn(Opcodes.IFEQ, elseLabel);
        generateCodeStatement(node.getConsequent());
        mv.visitJumpInsn(Opcodes.GOTO, endLabel);
        mv.visitLabel(elseLabel);
        if (node.getAlternate() != null) {
            generateCodeStatement(node.getAlternate());
        }
        mv.visitLabel(endLabel);
        symbolTable.exitScope();
    }


    private String getMethodDescriptor(FunctionDeclaration node) {
        StringBuilder descriptor = new StringBuilder();
        descriptor.append("(");
        if (node.getParameters() instanceof Arguments arguments) {
            for (Expression param : arguments.getArguments()) {
                if (param instanceof Parameter parameter) {
                    String paramType = parameter.getType();
                    descriptor.append(getTypeDescriptor(paramType));
                }
            }
        }
        descriptor.append(")");
        descriptor.append(getTypeDescriptor(node.getType()));
        return descriptor.toString();
    }

    private void generateCodeFree(Free node) {
        String identifier = node.getIdentifier();
        Integer index = symbolTable.lookupIndex(identifier);
        if (index == null) {
            throw new RuntimeException("Unknown variable: " + identifier);
        }
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitVarInsn(Opcodes.ASTORE, index);
    }

    private void generateCodeFor(For node) {
        Label startLabel = new Label();
        Label endLabel = new Label();
        symbolTable.enterScope();
        generateCodeExpression(node.getInitialization());
        mv.visitLabel(startLabel);
        generateCodeExpression(node.getCondition());
        mv.visitJumpInsn(Opcodes.IFEQ, endLabel);
        generateCodeStatement(node.getBody());
        generateCodeExpression(node.getIncrement());
        mv.visitJumpInsn(Opcodes.GOTO, startLabel);
        mv.visitLabel(endLabel);
        symbolTable.exitScope();
    }

    private void generateCodeBlock(Block node) {
        symbolTable.enterScope();
        for (Statement stmt : node.getStatements()) {
            generateCodeStatement(stmt);
        }
        symbolTable.exitScope();
    }

    private String getTypeDescriptor(String type) {
        return switch (type) {
            case "int" -> "I";
            case "float" -> "F";
            case "bool" -> "Z";
            case "void" -> "V";
            case "string" -> "Ljava/lang/String;";
            default -> {
                if (isStructType(type)) {
                    yield "L" + containerName + "$" + type + ";";
                } else {
                    throw new RuntimeException("Unknown type: " + type);
                }
            }
        };
    }

    private int getArrayType(String type) {
        return switch (type) {
            case "int" -> Opcodes.T_INT;
            case "float" -> Opcodes.T_FLOAT;
            case "bool" -> Opcodes.T_BOOLEAN;
            default -> throw new RuntimeException("Unknown array type: " + type);
        };
    }
}