package dev.quantumentangled.blog.exceptions;

public class UsernameAlreadyExistsException extends Exception {
    
    public UsernameAlreadyExistsException() {
        super();
    }

    public UsernameAlreadyExistsException(String msg) {
        super(msg);
    }

}
