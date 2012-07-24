package net.minecraft.server;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;

public class CraftBukkitCommentsTest {

    static class ImportedItem {
        final String qualified;
        //final String name;
        final Pattern qualifiedPattern;
        final Pattern unqualifiedPattern;
        int count = 0;

        ImportedItem(final List<ImportedItem> fullImports, final Matcher matcher) throws DuplicateImportException {
            qualified = matcher.group(1);
            String name = matcher.group(3);
            for (final ImportedItem imported : fullImports) {
                if (imported.qualified.equals(qualified)) {
                    throw new DuplicateImportException();
                }
            }
            qualifiedPattern = Pattern.compile("[^a-zA-Z\\.\"]" + Pattern.quote(qualified) + "[^a-zA-Z]");
            unqualifiedPattern = Pattern.compile("[^a-zA-Z\\.\"]" + name + "[^a-zA-Z]");
        }

        void checkLine(String line, boolean isComment) throws CommentException {
            if (qualifiedPattern.matcher(line).find()) {
                throw new DuplicateImportException(qualified + " was imported already for " + line);
            }
            final Matcher matcher = unqualifiedPattern.matcher(line);
            if (!isComment) {
                if (matcher.find()) {
                    throw new IllegalBukkitCallException(qualified + " was imported as CraftBukkit for " + line);
                }
            } else {
                while (matcher.find()) {
                    count++;
                }
            }
        }
    }

    static final Charset charset = Charset.forName("UTF-8");
    static final Pattern PRECEDING_SINGLE_LINE_COMMENT = Pattern.compile("^\\s*// CraftBukkit.*$",Pattern.DOTALL);
    static final Pattern BLOCK_COMMENT_START = Pattern.compile("^\\s*/[/\\*] CraftBukkit start.*$",Pattern.DOTALL);
    static final Pattern BLOCK_COMMENT_END = Pattern.compile("^\\s*// CraftBukkit end.*$",Pattern.DOTALL);
    static final Pattern SINGLE_LINE_COMMENT = Pattern.compile("^.*// CraftBukkit.*$", Pattern.DOTALL);
    static final Pattern IMPORT = Pattern.compile("^import (([a-z]+\\.)+([A-Z][A-Za-z]+));.*$", Pattern.DOTALL);
    static final Pattern FULLY_QUALIFIED = Pattern.compile("[^a-zA-Z\\.\"](([a-z]+\\.)+([A-Z][A-Za-z]+))[^a-zA-Z]");
    static final Set<String> EXCEPTIONS;
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
        ImmutableSet.Builder<String> exceptions = ImmutableSet.builder();
        for (String file : DIRECTORY.list(
                new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".java");
                    }
                })) {
            exceptions.add(file.substring(0, file.indexOf('.')));
        }
        EXCEPTIONS = exceptions.build();
    }

    @Test
    public void checkAllComments() {
        int lineCount = 0;
        for (File file : DIRECTORY.listFiles()) {
            try {
                final List lines = Files.readLines(file, charset);
                parseLines(lines);
                lineCount += lines.size();
            } catch (Throwable t) {
                throw new Error(file.toString(), t);
            }
        }
        System.out.println("Checked " + lineCount + " lines of code");
    }

    static void parseLines(List<String> lines) throws CommentException {
        final List<ImportedItem> fullImports = new ArrayList<ImportedItem>();
        final Map<String, Integer> qualifiedCalls = new HashMap<String, Integer>();
        Matcher matcher;
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
            if ((matcher = IMPORT.matcher(line)).matches()) {
                if (openComment || tempComment || SINGLE_LINE_COMMENT.matcher(line).matches()) {
                    fullImports.add(new ImportedItem(fullImports, matcher));
                    flagged = true;
                } else if (!line.startsWith("import java.")) {
                    throw new IllegalBukkitCallException(message(lineNumber, line));
                }
            } else if (SINGLE_LINE_COMMENT.matcher(line).matches()) {
                for (final ImportedItem imported : fullImports) {
                    imported.checkLine(line, true);
                }
                flagged = true;
            } else if (!(openComment || tempComment)) {
                for (final ImportedItem imported : fullImports) {
                    try {
                        imported.checkLine(line, false);
                    } catch (Throwable ex) {
                        throw new IllegalBukkitCallException("line(" + (lineNumber + 1) + ')', ex);
                    }
                }
                if (hasComment(line)) {
                    throw new UnknownCommentException(message(lineNumber, line));
                }
                if (FULLY_QUALIFIED.matcher(line).find()) {
                    throw new IllegalBukkitCallException(message(lineNumber, line));
                }
            } else {
                matcher = FULLY_QUALIFIED.matcher(line);
                while (matcher.find()) {
                    final String qualified = matcher.group(1);
                    if (qualified.startsWith(CraftBukkitCommentsTest.class.getPackage().getName())) continue;
                    if (EXCEPTIONS.contains(matcher.group(3))) continue;
                    final Integer previous = qualifiedCalls.put(qualified, lineNumber);
                    if (previous != null) {
                        throw new DuplicateFullyQualifiedCallException(
                                message(lineNumber, line) + '\n' +
                                "from" + '\n' +
                                message(previous, lines.get(previous)));
                    }
                }
                for (ImportedItem imported : fullImports) {
                    try {
                        imported.checkLine(line, true);
                    } catch (DuplicateImportException ex) {
                        throw new DuplicateImportException("line(" + (lineNumber + 1) + ')', ex);
                    }
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
        for (final ImportedItem imported : fullImports) {
            if (imported.count < 2) {
                throw new SingleUseBukkitImportException(imported.qualified);
            }
        }
    }

    static String message(final int lineNumber, final String line) {
        return "line(" + (lineNumber + 1) + "): " + line;
    }

    static boolean hasComment(final String line) {
        // Do NOT replace this with regex; regex is infinitely slower
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
