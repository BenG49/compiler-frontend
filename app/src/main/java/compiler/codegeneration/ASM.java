package compiler.codegeneration;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ASM {

    public static void test() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "GeneratedClass", null, "java/lang/Object", null);
        cw.visitEnd();

        for (byte b:cw.toByteArray())
            System.out.print(String.format("%02X ", b));
    }
}
