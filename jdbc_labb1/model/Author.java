package com.example.jdbc_labb1.model;

import java.sql.Date;

/**
 * A representation of an author object, that is used by the Book class giving information
 * about the author(s) of a specific book
 *
 * **/

public class Author {

    private String authorName;
    private Date dob;
    private String isbnToWrite;

    /**
     * @param authorName the name (first and last name) of the author, type String.
     * @param dob date of birth of an author, type sql.Date.
     **/
    public Author(String authorName, Date dob) {
        this.authorName = authorName;
        this.dob = dob;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getIsbnToWrite() {
        return isbnToWrite;
    }

    public Date getDob() {
        return dob;
    }

    public void isbnToWrite(String isbn){
        this.isbnToWrite = isbn;
    }

    @Override
    public String toString() {
        return authorName + ", " + dob.toString() + "\n ";
    }
}