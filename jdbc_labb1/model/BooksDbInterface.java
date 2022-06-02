package com.example.jdbc_labb1.model;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * A main interface that the view client application communicates with the main model functionalities through.
 * The main model class implements this interface and defines every single method to do a specific task.
 *
 * **/

public interface BooksDbInterface {

    /** The user needs to input a valid username and password to be able to connect and retrieve data from the db*/
    public boolean connect(String userName, String psw) throws BooksDbException;

    /** Drops the connection between the user and db*/
    public void disconnect() throws BooksDbException;

    /** Enable the user to input a search word, that will be used to search for a title of
     * a book in db through querying it. As a book or more whose title matches the search word
     * is found, the method retrieves these books and returns them as a list.*/
    public List<Book> searchBooksByTitle(String title) throws BooksDbException;

    /** Enable the user to input a search word (13 digits), that will be used to search for a ISBN-number of
     * a book in db through querying it. As a book or more whose ISBN-number matches the search word
     * is found, the method retrieves these books and returns them as a list.*/
    public List<Book> searchBooksByISBN(String isbn) throws BooksDbException;

    /** The user is supposed to choose one of some possible genres that this method will
     * use to search for books in this specific genre in db. Data about books is then
     * retrieved and returned to be shown in the application view.*/
    public List<Book> searchBooksByGenre(String genre) throws BooksDbException;

    /** The user is supposed to choose a rating that a book or more have in db,
     *  books is then retrieved and returned to be shown in the application view.*/
    public List<Book> searchBooksByRating(String rating) throws BooksDbException;

    /**
     * Returns all books written by a specific author, whose name is input by user.*/
    public List<Book> searchBooksByAuthor(String authorName) throws BooksDbException;

    /**
     * Enable the user to add a new book. A not-already-existing isbn-number, a title and a genre need to be input by user.
     * In this stage, the user can neither give a rating to the book nor add author to it.*/
    public boolean addBook(String isbn, String title, Genre genre) throws BooksDbException;

    /**
     * Enable the user to add a new author to a book. An already-existing isbn-number,
     * author's name and his/her date of birth need to be input by user.*/
    public boolean addAuthor(String isbn, String name, Date dob) throws BooksDbException, SQLException;

    /**
     * Enable the user to remove an already-existing book from the db, requiring an existing ISBN-number.
     * The book will be the removed from all tables i db.*/
    public boolean removeBook(String isbn) throws BooksDbException;

    /**
     * Enable user to give rating to an already-existing book in db, requiring an existing ISBN-number.*/
    public boolean setRating(String isbn, double value) throws BooksDbException;

}
