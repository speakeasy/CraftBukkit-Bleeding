package net.minecraft.server;

public class IllegalCharacter extends CommentException {

    public IllegalCharacter() {
        super();
    }

    public IllegalCharacter(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalCharacter(String message) {
        super(message);
    }

    public IllegalCharacter(Throwable cause) {
        super(cause);
    }
}

