package net.minecraft.server;

import static org.junit.Assert.*;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.io.Files;

public class CraftBukkitCommentsTest {
    static final Charset charset = Charset.forName("UTF-8");
    static final Pattern PRECEDING_SINGLE_LINE_COMMENT = Pattern.compile("^\\s*// CraftBukkit.*$",Pattern.DOTALL);
    static final Pattern BLOCK_COMMENT_START = Pattern.compile("^\\s*// CraftBukkit start.*$",Pattern.DOTALL);
    static final Pattern BLOCK_COMMENT_END = Pattern.compile("^\\s*// CraftBukkit end.*$",Pattern.DOTALL);
    static final Pattern SINGLE_LINE_COMMENT = Pattern.compile("^[^}{;]*[}{;]\\s*// CraftBukkit.*$", Pattern.DOTALL);
    static final File DIRECTORY;
    static {
        String path = "src.main.java.".concat(CraftBukkitCommentsTest.class.getPackage().getName()).replace('.',File.separatorChar);
        final File dir = new File(path);
        if (!dir.isDirectory()) {
            DIRECTORY = new File(".." + File.separatorChar + ".." + File.separatorChar + path);
            assertTrue("Source directory not found!", DIRECTORY.isDirectory());
        } else {
            DIRECTORY = dir;
        }
    }

    @Test
    public void checkAllComments() {
        boolean failed = false;
        for (File file : DIRECTORY.listFiles()) {
            try {
                //if (file.toString().endsWith("VillageSiege.java"))
                parseLines(Files.readLines(file, charset));
            } catch (Throwable t) {
                Logger.getAnonymousLogger().log(Level.SEVERE, file.toString(), t);
                //throw new Error(file.toString(), t);
                failed = true;
            }
        }
        if (failed) throw new Error("failed");
    }

    @Test(expected = UnclosedCommentException.class)
    public void checkUnclosedComment() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "    // CraftBukkit start",
                "}"
                ));
    }

    @Test
    public void checkStartMatcher() {
        final int constant = 0x10;
        final char[] stringChars = "// CraftBukkit start".toCharArray();
        final char[] chars = new char[stringChars.length + constant];
        Arrays.fill(chars, ' ');
        System.arraycopy(stringChars, 0, chars, chars.length - stringChars.length, stringChars.length);
        for (int i = 0; i <= constant; i++) {
            String string = new String(chars, i, chars.length - i);
            assertTrue(
                    '"' + string + "\" fails trigger comment start",
                    BLOCK_COMMENT_START.matcher(string).matches());
        }
    }

    @Test
    public void checkEndMatcher() {
        final int constant = 0x10;
        final char[] stringChars = "// CraftBukkit end".toCharArray();
        final char[] chars = new char[stringChars.length + constant];
        Arrays.fill(chars, ' ');
        System.arraycopy(stringChars, 0, chars, chars.length - stringChars.length, stringChars.length);
        for (int i = 0; i <= constant; i++) {
            String string = new String(chars, i, chars.length - i);
            assertTrue(
                    '"' + string + "\" fails trigger comment end",
                    BLOCK_COMMENT_END.matcher(string).matches());
        }
    }

    @Test(expected = UnmatchedClosingCommentException.class)
    public void checkUnmatchedClosingComment() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "    // CraftBukkit end",
                "}"
                ));
    }

    void parseLines(List<String> lines) throws CommentException {
        boolean flagged = false;
        boolean openComment = false;
        int openLine = 0;
        boolean tempComment = false;
        int lineNumber = 0;
        for (String line; lineNumber < lines.size() && (line = lines.get(lineNumber)) != null; ++lineNumber) {
            if (BLOCK_COMMENT_START.matcher(line).matches()) {
                if (openComment) {
                    throw new DuplicateOpeningCommentException(
                            message(lineNumber, line) + "\n" +
                            "duplicates comment block openned at\n" +
                            message(openLine, lines.get(openLine)));
                }
                openComment = true;
                //out.println("Open:" + message(lineNumber, line));
                openLine = lineNumber;
                flagged = true;
                continue;
            }
            if (BLOCK_COMMENT_END.matcher(line).matches()) {
                //System.out.println("close:" + message(lineNumber, line));
                if (!openComment) {
                    throw new UnmatchedClosingCommentException(message(lineNumber, line));
                }
                openComment = false;
                continue;
            }
            if (PRECEDING_SINGLE_LINE_COMMENT.matcher(line).matches()) {
                tempComment = true;
                flagged = true;
                continue;
            }
            if (SINGLE_LINE_COMMENT.matcher(line).matches()) {
                flagged = true;
            } else if (line.contains("org.bukkit")) {
                if (!(openComment || tempComment)) {
                    throw new IllegalBukkitCallException(message(lineNumber, line));
                }
                flagged = true;
            }
            //if (line.matches())
            tempComment = false;
        }
        if (openComment) {
            throw new UnclosedCommentException(message(openLine, lines.get(openLine)));
        }
        if (!flagged) {
            throw new NoCraftBukkitCommentException();
        }
    }

    String message(final int lineNumber, final String line) {
        return "line(" + (lineNumber + 1) + "): " + line;
    }
}
