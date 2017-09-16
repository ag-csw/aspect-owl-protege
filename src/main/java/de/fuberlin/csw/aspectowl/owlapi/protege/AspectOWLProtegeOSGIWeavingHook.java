package de.fuberlin.csw.aspectowl.owlapi.protege;

import aj.org.objectweb.asm.*;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

public class AspectOWLProtegeOSGIWeavingHook implements WeavingHook {
    @Override
    public void weave(WovenClass wovenClass) {
        System.out.format("  === Weaving class %s. ===\n", wovenClass.getClassName());

        final ClassReader classReader = new ClassReader(wovenClass.getBytes());
        final ClassWriter classWriter = new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS);
        final ClassVisitor classVisitor = new TClassVisitor(classWriter);

        classReader.accept(classVisitor, Opcodes.ASM5);
        byte[] code = classWriter.toByteArray();

        wovenClass.setBytes(classWriter.toByteArray());
    }

    class TClassVisitor extends ClassVisitor {

        private final ClassVisitor classVisitor;

        public TClassVisitor(final ClassVisitor classVisitor) {
            super(Opcodes.ASM5, classVisitor);
            this.classVisitor = classVisitor;
        }
        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, name, interfaces);
        }
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

            if("say".equals(name)){

                MethodVisitor mv= classVisitor.visitMethod(access, name, desc, signature, exceptions);
                return new TMethodVisitor(mv);

            }
            return null;
        }
    }

    class TMethodVisitor extends MethodVisitor {

        public TMethodVisitor(MethodVisitor methodVisitor){
            super(Opcodes.ASM5,methodVisitor);
        }

        @Override
        public void visitCode(){

            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("      === This was woven into the receiver's bytecode at runtime! ===");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            super.visitCode();
        }
    }
}
