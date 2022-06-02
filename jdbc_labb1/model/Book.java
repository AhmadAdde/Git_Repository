package com.example.jdbc_labb1.model;

import java.util.ArrayList;

/**
 * The class Book represents a book object inc. its ISBN-number, title, genre, and rating.
 *
 * **/
public class Book {

    private ISBN isbn;
    private String title;
    private Genre genre;
    private double rating;
    private ArrayList<Author> author;

    /**
     * @param isbn is of a specific pattern that is created and checked in the ISBN class.
     * @param genre is an enum value.
     *
     * **/
    public Book(String isbn, String title, Genre genre, double rating) {
        this.isbn = ISBN.createIsbn(isbn);
        this.title = title;
        this.genre = genre;
        this.rating = rating;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public Genre getGenre() {
        return genre;
    }

    public double getRating() {
        return rating;
    }

    public ArrayList<Author> getAuthors() {
        return author;
    }

    public void setAuthor(ArrayList<Author> author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return title + ", " + isbn + ", " + genre + ", " + rating;
    }

}
