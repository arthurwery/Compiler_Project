package compiler.CodeGenerator;

import java.io.FileOutputStream;
import java.io.IOException;

public class BytecodeSaver {
    public static void saveBytecodeToFile(byte[] bytecode, String className) {
        try (FileOutputStream fos = new FileOutputStream(className + ".class")) {
            fos.write(bytecode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
