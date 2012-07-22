package net.minecraft.server;

public class UnclosedCommentException extends CommentException {

    public UnclosedCommentException() {
        super();
    }

    public UnclosedCommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnclosedCommentException(String message) {
        super(message);
    }

    public UnclosedCommentException(Throwable cause) {
        super(cause);
    }
}

