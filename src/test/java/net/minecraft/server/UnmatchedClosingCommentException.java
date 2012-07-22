package net.minecraft.server;

public class UnmatchedClosingCommentException extends CommentException {

    public UnmatchedClosingCommentException() {
        super();
    }

    public UnmatchedClosingCommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnmatchedClosingCommentException(String message) {
        super(message);
    }

    public UnmatchedClosingCommentException(Throwable cause) {
        super(cause);
    }
}

