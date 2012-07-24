package net.minecraft.server;

import static org.junit.Assert.assertTrue;
import static net.minecraft.server.CraftBukkitCommentsTest.*;

import java.util.Arrays;

import org.junit.Test;


public class CraftBukkitCommentsTestTest {

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

    @Test(expected = NoCraftBukkitCommentException.class)
    public void checkNoComment() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "}"
                ));
    }

    @Test(expected = UnknownCommentException.class)
    public void checkImproperCaseComment() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "    // Craftbukkit - removed tests",
                "}"
                ));
    }

    @Test(expected = IllegalBukkitCallException.class)
    public void checkBukkitImport() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "import org.bukkit.AwesomeClass;",
                "",
                "public class Testing {",
                "}"
                ));
    }

    @Test(expected = IllegalBukkitCallException.class)
    public void checkLibraryImport() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "import org.apache.commons.lang.Validate;",
                "",
                "public class Testing {",
                "}"
                ));
    }

    @Test(expected = IllegalBukkitCallException.class)
    public void checkImportedBukkitCall() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "import org.bukkit.craftbukkit.CraftPlayer; // CraftBukkit",
                "",
                "public class Testing {",
                "",
                "    public CraftPlayer getPlayer() {",
                "        return player;",
                "    }",
                "",
                "}"
                ));
    }

    @Test(expected = SingleUseBukkitImportException.class)
    public void checkSingleUseBukkitImport() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "import org.bukkit.craftbukkit.CraftPlayer; // CraftBukkit",
                "",
                "public class Testing {",
                "",
                "    public CraftPlayer getPlayer() { // CraftBukkit",
                "        return player;",
                "    }",
                "",
                "}"
                ));
    }

    @Test(expected = DuplicateFullyQualifiedCallException.class)
    public void checkDuplicateFullyQualifiedCall() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "",
                "    // CraftBukkit start",
                "    public org.bukkit.Player getPlayer() {",
                "        return org.bukkit.Player.getPlayer();",
                "    }",
                "    // CraftBukkit end",
                "",
                "}"
                ));
    }

    @Test(expected = DuplicateImportException.class)
    public void checkImportedFullyQualifiedCall() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "import org.bukkit.Player; // CraftBukkit",
                "",
                "public class Testing {",
                "",
                "    // CraftBukkit start",
                "    public Player getPlayer() {",
                "        return org.bukkit.Player.getPlayer();",
                "    }",
                "    // CraftBukkit end",
                "",
                "}"
                ));
    }

    @Test(expected = IllegalBukkitCallException.class)
    public void checkBukkitCall() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "",
                "    public org.bukkit.craftbukkit.Player getPlayer() {",
                "        return player;",
                "    }",
                "",
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

    @Test(expected = UnclosedCommentException.class)
    public void checkUnclosedBlockComment() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "    /* CraftBukkit start",
                "}"
                ));
    }

    @Test(expected = DuplicateOpeningCommentException.class)
    public void checkBlockDuplicateOpenningComment() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "    /* CraftBukkit start",
                "    // CraftBukkit start",
                "    // CraftBukkit end */",
                "    // CraftBukkit end",
                "}"
                ));
    }

    @Test(expected = DuplicateOpeningCommentException.class)
    public void checkDuplicateBlockOpenningComment() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "    /* CraftBukkit start",
                "    /* CraftBukkit start",
                "    // CraftBukkit end */",
                "    // CraftBukkit end */",
                "}"
                ));
    }

    @Test(expected = DuplicateOpeningCommentException.class)
    public void checkNestedComment() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "    // CraftBukkit start",
                "    // CraftBukkit start",
                "    // CraftBukkit end",
                "    // CraftBukkit end",
                "}"
                ));
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

    @Test(expected = UnexpectedEndOfFile.class)
    public void checkNoNewlineAtEndOfFile() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "    // CraftBukkit - I forgot newline!!",
                "}"
                ));
    }

    @Test(expected = IllegalCharacter.class)
    public void checkCarriageReturn() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "    // CraftBukkit - I use bad carriage return\r",
                "}"
                ));
    }

    @Test(expected = IllegalCharacter.class)
    public void checkTab() throws CommentException {
        parseLines(Arrays.<String>asList(
                "package " + getClass().getPackage().getName() + ';',
                "",
                "public class Testing {",
                "\t// CraftBukkit - I use bad tab",
                "}"
                ));
    }

    @Test
    public void checkStartMatcher() { // TODO: Remove, not needed
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
    public void checkEndMatcher() { // TODO: Remove, not needed
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

}
