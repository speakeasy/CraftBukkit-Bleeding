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
    static final Pattern SINGLE_LINE_COMMENT = Pattern.compile("^.*// CraftBukkit.*$", Pattern.DOTALL);
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

    @Test(expected = UnknownCommentException.class)
    public void checkUnknownComment() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "    // not-cb comment",
                "}"
                ));
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
                openLine = lineNumber;
                flagged = true;
                continue;
            }
            if (BLOCK_COMMENT_END.matcher(line).matches()) {
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
            } else if (!(openComment || tempComment)) {
                if (line.contains("org.bukkit") /* TODO Add any CraftBukkit imported items to .contains */) {
                    throw new IllegalBukkitCallException(message(lineNumber, line));
                }
                if (hasComment(line)) {
                    throw new UnknownCommentException(message(lineNumber, line));
                }
            }
            tempComment = false;
        }
        if (openComment) {
            throw new UnclosedCommentException(message(openLine, lines.get(openLine)));
        }
        if (!flagged) {
            throw new NoCraftBukkitCommentException();
        }
    }

    static String message(final int lineNumber, final String line) {
        return "line(" + (lineNumber + 1) + "): " + line;
    }

    static boolean hasComment(final String line) {
        boolean quote = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                quote = !quote;
            } else if (quote) {
                if (character == '\\') {
                    i++;
                }
            } else if (character == '/') {
                i++;
                if (i == line.length()) {
                    return false;
                }
                character = line.charAt(i);
                if (character == '/' || character == '*') return true;
            }
        }
        return false;
    }
}
