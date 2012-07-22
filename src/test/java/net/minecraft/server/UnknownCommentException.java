package net.minecraft.server;

public class UnknownCommentException extends CommentException {

    public UnknownCommentException() {
        super();
    }

    public UnknownCommentException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownCommentException(String message) {
        super(message);
    }

    public UnknownCommentException(Throwable cause) {
        super(cause);
    }
}

