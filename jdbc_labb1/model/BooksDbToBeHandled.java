package com.example.jdbc_labb1.model;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * The main model class that connects, communicates and queries the database. It implements the
 * BooksDbInterface and defines all the methods declared there for a different specific task.
 * The view application communicates with this class through the interface named above.
 * **/
public class BooksDbToBeHandled implements BooksDbInterface{

    private Connection connection;
    private Statement statement;
    private PreparedStatement prStatement;
    private ResultSet resultSet;

    private final String jdbc;

    public BooksDbToBeHandled() {
        connection = null;
        resultSet = null;
        prStatement = null;
        jdbc = "jdbc:mysql://@/sql_booksdb";
    }

    /**Takes a string of the username and password and connect to the db*/
    @Override
    public boolean connect(String userName, String psw)  throws BooksDbException{
        try {
            connection = DriverManager.getConnection(jdbc, "root", "password");
            return true;

        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
    }

    /**Disconnect from the db*/
    @Override
    public void disconnect() throws BooksDbException {
        try {
            if(connection != null) connection.close();
            if (prStatement != null) prStatement.close();
        }catch (Exception e){
            throw new BooksDbException(e.getMessage(), e);
        }
    }

    /** Enable the user to input a search word, that will be used to search for a title of
     * a book in db through querying it. As a book or more whose title matches the search word
     * is found, the method retrieves these books and returns them as a list.*/
    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        searchTitle = searchTitle.toLowerCase();
            try {
                prStatement = connection.prepareStatement("SELECT * FROM sql_booksdb.t_book WHERE title LIKE ?");
                prStatement.setString(1, "%" + searchTitle + "%");
                resultSet = prStatement.executeQuery();

                while (resultSet.next()) {
                    Genre genre = Genre.valueOf(resultSet.getString("genre").toUpperCase());
                    Book book = new Book(resultSet.getString("isbn"), resultSet.getString("title"), genre, resultSet.getInt("rating"));
                    result.add(book);
                }
                result = getAuthors(result);
                return result;
            } catch (SQLException e) {
                throw new BooksDbException(e.getMessage(), e);
            }finally {
                try {
                    resultSet.close();
                } catch (SQLException e) {}
            }
    }

    /** Enable the user to input a search word (13 digits), that will be used to search for a ISBN-number of
     * a book in db through querying it. As a book or more whose ISBN-number matches the search word
     * is found, the method retrieves these books and returns them as a list.*/
    @Override
    public List<Book> searchBooksByISBN(String searchISBN) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        try {
            prStatement = connection.prepareStatement("SELECT * FROM sql_booksdb.t_book WHERE isbn LIKE ?");
            prStatement.setString(1, "%" + searchISBN + "%");
            resultSet = prStatement.executeQuery();
            while (resultSet.next()) {
                Genre genre = Genre.valueOf(resultSet.getString("genre").toUpperCase());
                Book book = new Book(resultSet.getString("isbn"), resultSet.getString("title"), genre, resultSet.getInt("rating"));
                result.add(book);
            }
            result = getAuthors(result);
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }finally {
            try {
                resultSet.close();
            } catch (SQLException e) {}
        }
    }

    /** The user is supposed to choose one of some possible genres that this method will
     * use to search for books in this specific genre in db. Data about books is then
     * retrieved and returned to be shown in the application view.*/
    @Override
    public List<Book> searchBooksByGenre(String searchGenre) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        Genre genre = Genre.valueOf(searchGenre);
        try {
            prStatement = connection.prepareStatement("SELECT * FROM sql_booksdb.t_book WHERE genre = ?");
            prStatement.setString(1, genre.toString());
            resultSet = prStatement.executeQuery();
            while (resultSet.next()) {
                    Book book = new Book(resultSet.getString("isbn"), resultSet.getString("title"), genre, resultSet.getInt("rating"));
                    result.add(book);
            }

            result = getAuthors(result);
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }finally {
            try {
                resultSet.close();
            } catch (SQLException e) {}
        }
    }

    /** The user is supposed to choose a rating that a book or more have in db,
     *  books is then retrieved and returned to be shown in the application view.*/
    @Override
    public List<Book> searchBooksByRating(String searchRating) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        try {
            prStatement = connection.prepareStatement("SELECT * FROM sql_booksdb.t_book WHERE rating = ?");
            prStatement.setString(1, searchRating);
            resultSet = prStatement.executeQuery();
            while (resultSet.next()) {
                Genre genre = Genre.valueOf(resultSet.getString("genre").toUpperCase());
                Book book = new Book(resultSet.getString("isbn"), resultSet.getString("title"), genre, resultSet.getInt("rating"));
                result.add(book);
            }

            result = getAuthors(result);
            return result;
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }finally {
            try {
                resultSet.close();
            } catch (SQLException e) {}
        }
    }

    /** Returns all books written by a specific author, whose name is input by user.*/
    @Override
    public List<Book> searchBooksByAuthor(String authorName) throws BooksDbException {
        List<Book> result = new ArrayList<>();
        authorName = authorName.toLowerCase();
        try {
            prStatement = connection.prepareStatement("SELECT sql_booksdb.t_writtenby.isbn, title, genre, rating " +
                    "FROM sql_booksdb.t_writtenby, sql_booksdb.t_book, sql_booksdb.t_author " +
                    "WHERE sql_booksdb.t_writtenby.authorID IN " +
                    "(SELECT authorID FROM sql_booksdb.t_author WHERE name LIKE ?) " +
                    "AND sql_booksdb.t_writtenby.authorID = sql_booksdb.t_author.authorID " +
                    "AND sql_booksdb.t_writtenby.isbn = sql_booksdb.t_book.isbn");
            prStatement.setString(1, "%" + authorName + "%");
            connection.setAutoCommit(false);

            resultSet = prStatement.executeQuery();

            connection.commit();

            while (resultSet.next()) {
                Genre genre = Genre.valueOf(resultSet.getString("genre").toUpperCase());
                Book book = new Book(resultSet.getString("isbn"), resultSet.getString("title"), genre, resultSet.getInt("rating"));
                result.add(book);
            }
            result = getAuthors(result);
            return result;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.getCause();
                }
            }
            throw new BooksDbException(e.getMessage(), e);
        }
        finally {
            if (connection != null) {
                try {
                    resultSet.close();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.getCause();
                }
            }
        }
    }

    /** Enable the user to add a new book. A not-already-existing isbn-number, a title and a genre need to be input by user.
     * In this stage, the user can neither give a rating to the book nor add author to it.*/
    @Override
    public boolean addBook(String isbn, String title, Genre genre) throws BooksDbException {
        try {
            prStatement = connection.prepareStatement("INSERT INTO sql_booksdb.t_book (isbn, title, genre) VALUES(?, ?, ?);");
            prStatement.setString(1, isbn);
            prStatement.setString(2, title);
            prStatement.setString(3, String.valueOf(genre));
            int n = prStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
    }

    /** Enable the user to add a new author to a book. An already-existing isbn-number,
     * author's name and his/her date of birth need to be input by user.*/
    @Override
    public boolean addAuthor(String isbn, String name, Date dob) throws BooksDbException, SQLException {
        try {
            statement = connection.createStatement();

            connection.setAutoCommit(false);

            statement.execute("INSERT INTO sql_booksdb.t_author (name, dob) VALUES('" + name + "', '" + dob.toString() + "');");
            statement.execute("INSERT INTO sql_booksdb.t_writtenby (isbn, authorID) VALUES('" + isbn + "', ( SELECT authorID FROM sql_booksdb.t_author WHERE name = '" + name + "'));");

            connection.commit();
            return true;
        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw new BooksDbException(e.getMessage(), e);
        }finally {
            if(statement != null) statement.close();
            if (connection != null) connection.setAutoCommit(true);
        }
    }

    /** Enable the user to remove an already-existing book from the db, requiring an existing ISBN-number.
     * The book will be the removed from all tables i db.*/
    @Override
    public boolean removeBook(String isbnString) throws BooksDbException {
        try {
            prStatement = connection.prepareStatement("DELETE FROM sql_booksdb.t_book  WHERE isbn = ?;");
            prStatement.setString(1, isbnString);
            int n = prStatement.executeUpdate();
            return (n>0);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
    }

    /** Enable user to give rating to an already-existing book in db, requiring an existing ISBN-number.*/
    @Override
    public boolean setRating(String isbn, double value) throws BooksDbException {
        try {
            prStatement = connection.prepareStatement("UPDATE sql_booksdb.t_book SET rating = ? WHERE isbn = ?;");
            prStatement.setDouble(1, value);
            prStatement.setString(2, isbn);
            int n = prStatement.executeUpdate();
            return (n>0);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage(), e);
        }
    }

    /**
     * This is a help method used by most of the methods above to get all authors of
     * every single book of the list it takes as parameter, and returns the same list of
     * books with their belonging author.
     **/
    private List<Book> getAuthors(List<Book> books) throws SQLException {
        try {
            for (Book tmpBook : books) {
                ArrayList<Author> authors = new ArrayList();
                connection.setAutoCommit(false);

                prStatement = connection.prepareStatement("SELECT name, dob FROM sql_booksdb.t_author WHERE authorID IN (SELECT authorID FROM sql_booksdb.t_writtenby WHERE isbn = ?);");

                prStatement.setString(1, tmpBook.getIsbn().toString());
                resultSet = prStatement.executeQuery();

                connection.commit();
                while (resultSet.next()) {
                    Author author = new Author(resultSet.getString("name"), resultSet.getDate("dob"));
                    authors.add(author);
                }
                tmpBook.setAuthor(authors);
            }
            return books;
        }catch (SQLException e){
            if (connection != null) connection.rollback();
            throw e;
        }finally {
            resultSet.close();
            if (connection != null) connection.setAutoCommit(true);
        }
    }
}
