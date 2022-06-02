package com.example.jdbc_labb1.model;

/**
 * A representation of how an ISBN-number should look like.
 * It is used by Book Class when it takes an ISBN-string to check if
 * the input matches the pattern, and if not, doesn't accept it.
 **/
public class ISBN {
    private final String isbnStr;
    private static final String isbnPattern = "[0-9]{13}";

    /**
     * When creating a new ISBN-number, the user enters 13 digits that will be taken as
     * a String object parameter to the constructor via creatIsbn().
     * After that it removes all dashes from the String.
     * By comparing the ISBN-number entered by the user with the isbnPattern, it controls if the parameter
     * follows the standard format of an ISBN or not.
     * The BooksDbException is inherited here in order to give ability to send error messages to
     * the user during the executing if the entered ISBN-number is illegal.
     */
    private ISBN(String isbnStr) throws BooksDbException {
        String newStr = isbnStr.replace("-","");
        if (!isValidIsbn(isbnStr)){
            throw new BooksDbException();
        }
        this.isbnStr = newStr;
    }

    public static ISBN createIsbn(String isbnStr) {
        try {
            return new ISBN(isbnStr);
        } catch (BooksDbException e) {
            e.getCause();
        }
        return null;
    }

    public static boolean isValidIsbn(String isbn) {
            return isbn.matches(isbnPattern);
    }

    /**
     * returning a reference variable, of a String object, that refers to the ISBN-number of a book.
     */
    public String getIsbnStr(){
        return isbnStr;
    }

    @Override
    public String toString() {
        return isbnStr;
    }
}