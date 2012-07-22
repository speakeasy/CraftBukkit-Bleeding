package net.minecraft.server;

public class DuplicateOpeningCommentException extends CommentException {

    public DuplicateOpeningCommentException() {
        super();
    }

    public DuplicateOpeningCommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateOpeningCommentException(String message) {
        super(message);
    }

    public DuplicateOpeningCommentException(Throwable cause) {
        super(cause);
    }
}

