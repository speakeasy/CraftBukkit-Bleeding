package net.minecraft.server;

public class DuplicateImportException extends CommentException {

    public DuplicateImportException() {
        super();
    }

    public DuplicateImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateImportException(String message) {
        super(message);
    }

    public DuplicateImportException(Throwable cause) {
        super(cause);
    }
}

