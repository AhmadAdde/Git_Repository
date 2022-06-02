package com.example.jdbc_labb1.view;

import com.example.jdbc_labb1.model.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static javafx.scene.control.Alert.AlertType.*;
/** The controller class that connection the returned data from the model interface and the
 * application client-view. It handles every single query made by user in the client application
 * in a separate thread, receiving data from the model interface and updating the client view after that.
 * It also handles the errors from db and show the reason behind them to the user.*/
public class Controller{

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onSearchSelected(String searchFor, SearchMode mode){
        new Thread() {
            @Override
            public void run() {
                try {
                    if (searchFor != null && searchFor.length() > 1) {
                        List<Book> result = null;
                        switch (mode) {
                            case Title:
                                result = booksDb.searchBooksByTitle(searchFor);
                                break;
                            case ISBN:
                                result = booksDb.searchBooksByISBN(searchFor);
                                break;
                            case Genre:
                                result = booksDb.searchBooksByGenre(searchFor);
                                break;
                            case Rating:
                                result = booksDb.searchBooksByRating(searchFor);
                                break;
                            case Author:
                                result = booksDb.searchBooksByAuthor(searchFor);
                                break;
                            default:
                                result= new ArrayList<>();
                        }
                        if (result == null || result.isEmpty()) {
                            javafx.application.Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("No results found.", INFORMATION);
                                }
                            });
                        } else {
                            final List<Book> finalList = result;
                            javafx.application.Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    booksView.displayBooks(finalList);
                                }
                            });
                        }
                    } else {
                        javafx.application.Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                booksView.showAlertAndWait("Enter a search string!", WARNING);
                            }
                        });
                    }
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            booksView.showAlertAndWait("Database error: " + e.getMessage(), ERROR);
                        }
                    });
                }
            }
        }.start();
    }

    protected void connectToDB(String userName, String psw) {
        new Thread(){
            @Override
            public void run(){
                try {
                    booksDb.connect(userName, psw);
                    javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    booksView.searchButton.setDisable(false);
                                    booksView.manageMenu.setDisable(false);
                                }
                            });
                }catch (BooksDbException ex){
                    javafx.application.Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    booksView.searchButton.setDisable(true);
                                    booksView.manageMenu.setDisable(true);
                                    booksView.showAlertAndWait( "connecting to DB failed: " + ex.getMessage() , ERROR);
                                }
                            }
                    );
                }
            }
        }.start();
    }

    protected void disConnectDB() {
        new Thread(){
            @Override
            public void run() {
                try {
                    booksDb.disconnect();
                    javafx.application.Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    booksView.searchButton.setDisable(true);
                                    booksView.manageMenu.setDisable(true);
                                }
                            });
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait( "Disconnecting to DB failed: " + e.getMessage(), ERROR);
                                }
                            });
                }
            }
        }.start();
    }

    protected void onAddBookSelected(String ISBN, String title, Genre genre){
        new Thread(){
            @Override
            public void run() {
                try {
                    booksDb.addBook(ISBN, title, genre);
                }
                catch (BooksDbException e) {
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            booksView.showAlertAndWait("Inserting failed: " + e.getMessage(), ERROR);
                        }
                    });
                }
            }
        }.start();
    }

    protected void onRemoveBookSelected(String ISBN){
        new Thread(){
            @Override
            public void run() {
                try {
                    if (!booksDb.removeBook(ISBN)){
                        javafx.application.Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                booksView.showAlertAndWait("The book doesn't exist ", INFORMATION);
                            }
                        });
                    }
                } catch (BooksDbException e) {
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            booksView.showAlertAndWait("Removing failed: " + e.getMessage(), ERROR);
                        }
                    });
                }
            }
        }.start();
    }

    protected void onAddAuthorSelected(String ISBN, String name, Date dob){
        new Thread() {
            @Override
            public void run() {
                try {
                    booksDb.addAuthor(ISBN, name, dob);
                } catch (BooksDbException | SQLException e) {
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            booksView.showAlertAndWait("Inserting author failed: " + e.getMessage(), ERROR);
                        }
                    });
                }
            }
        }.start();
    }

    protected void onRatingSelected(String isbn, double value){
        new Thread(){
            @Override
            public void run() {
                try {
                    if (!booksDb.setRating(isbn, value))
                        javafx.application.Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                booksView.showAlertAndWait("The book doesn't exist! ", INFORMATION);
                            }
                        });
                }catch (BooksDbException e){
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            booksView.showAlertAndWait("Updating rating failed: " + e.getMessage(), ERROR);
                        }
                    });
                }
            }
        }.start();
    }

    protected void exit() {
        new Thread(){
            @Override
            public void run() {
                try {
                    booksDb.disconnect();
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.exit(0);
                        }
                    });
                }catch (Exception e){
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            booksView.showAlertAndWait("Exit failed: " + e.getMessage(), ERROR);
                        }
                    });
                }
            }
        }.start();
    }
}
