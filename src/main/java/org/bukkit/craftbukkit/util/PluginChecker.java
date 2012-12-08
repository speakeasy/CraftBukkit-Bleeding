package org.bukkit.craftbukkit.util;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Warning;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * This class houses a {@link ClassVisitor} to inspect classes in a jar, and a
 * static {@link #check(java.io.File)} method to invoke this visitor on all
 * classes in a jar.
 */
public class PluginChecker extends ClassVisitor {

    /**
     * Warnings which shall be issued to this plugin. A set is used so that
     * warning may only be issued once.
     */
    private Set<String> warnings = new HashSet<String>();
    private final MethodChecker methodChecker = new MethodChecker();

    private PluginChecker() {
        super(Opcodes.ASM4);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // Methods themselves cannot do any harm, we only care what is in them.
        return methodChecker;
    }

    /**
     * Strictly speaking this class does not check all possible entry points,
     * however it is very unlikely that a plugin will be performing
     * <code>instanceof</code> checks etc without getting the restricted item
     * via a means we do check.
     */
    private class MethodChecker extends MethodVisitor {

        public MethodChecker() {
            super(Opcodes.ASM4);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            checkImplementationAccess(owner);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            checkImplementationAccess(owner);
            checkThreadAccess(owner);
            checkReflection(owner, name, desc);
            checkGC(owner, name, desc);
        }

        private void checkImplementationAccess(String owner) {
            if (owner.startsWith("net/minecraft/server") || owner.startsWith("org/bukkit/craftbukkit")) {
                warnings.add("#1: Attempts to access and interact with implementation specific classes (net.minecraft.server, org.bukkit.craftbukkit)."
                        + " This can lead to unexpected server behaviour.");
            }
        }

        private void checkThreadAccess(String owner) {
            if (owner.equals("java/lang/Thread")) {
                warnings.add("#2: Attempts to deal with threads directly. The Bukkit scheduler should be used instead. Usage of threads can lead to poor performance and bad reliability.");
            }
        }

        private void checkReflection(String owner, String name, String desc) {
            if ((owner.equals("java/lang/reflect/Field") || owner.equals("java/lang/reflect/Method")) && name.equals("setAccessible") && desc.equals("(Z)V")) {
                warnings.add("#3: Attempts to bypass Java access control mechanisms. This may lead to unexpected server behaviour.");
            }
        }

        private void checkGC(String owner, String name, String desc) {
            if (owner.equals("java/lang/System") && name.equals("gc") && desc.equals("()V")) {
                warnings.add("#4: Invokes manual garbage collection on the system. This can lead to random lag spikes.");
            }
        }
    }

    /**
     * Call {@link #checkFile(java.io.File)} on all files in a folder.
     *
     * @param folder the folder to check
     */
    public static void checkFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                checkFile(file);
            }
        }
    }

    /**
     * Check a file to make sure the classes within meet all defined quality
     * requirements and print out warnings for any standards violated.
     *
     * @param file the file to check
     */
    private static void checkFile(File file) {
        try {
            JarFile jar = new JarFile(file);
            PluginChecker checker = new PluginChecker();

            for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
                JarEntry entry = entries.nextElement();
                // We only care about class files
                if (entry.getName().endsWith(".class")) {
                    InputStream in = jar.getInputStream(entry);
                    byte[] b = ByteStreams.toByteArray(in);
                    in.close();

                    ClassReader cr = new ClassReader(b);
                    cr.accept(checker, 0);
                }
            }

            if (Bukkit.getServer().getWarningState() != Warning.WarningState.OFF && !checker.warnings.isEmpty()) {
                Bukkit.getServer().getLogger().warning("The plugin contained in file " + file + " violates one or more quality checks. Please be aware of any issues this may pose.");
                for (String warning : checker.warnings) {
                    Bukkit.getServer().getLogger().warning(warning);
                }
            }
        } catch (Exception ex) {
            Bukkit.getServer().getLogger().log(Level.WARNING, "Error whilst inspecting plugin file " + file, ex);
        }
    }
}
