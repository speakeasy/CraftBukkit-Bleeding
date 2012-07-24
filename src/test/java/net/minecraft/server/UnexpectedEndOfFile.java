package net.minecraft.server;

public class UnexpectedEndOfFile extends CommentException {

    public UnexpectedEndOfFile() {
        super();
    }

    public UnexpectedEndOfFile(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedEndOfFile(String message) {
        super(message);
    }

    public UnexpectedEndOfFile(Throwable cause) {
        super(cause);
    }
}

