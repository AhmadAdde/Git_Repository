package com.example.jdbc_labb1.model;

/**
 * A representation of exception that can be used in the main model class, giving the user
 * a massage (and eventually the reason) why a certain data failure happens.
 * The class Exception is extended.
 *
 * **/

public class BooksDbException extends Exception{

    public BooksDbException(String msg, Exception cause) {
        super(msg, cause);
    }

    public BooksDbException(String msg) {
        super(msg);
    }

    public BooksDbException() {
        super();
    }
}
