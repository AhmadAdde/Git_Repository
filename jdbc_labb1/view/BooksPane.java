package com.example.jdbc_labb1.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.jdbc_labb1.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BooksPane extends VBox {
    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private ComboBox<SearchMode> searchModeBox;
    private ComboBox<Genre> genreSearchBox;
    private TextField searchField;
    protected Button searchButton;

    private MenuBar menuBar;
    private Menu fileMenu;
    protected Menu manageMenu;

    final Controller controller;
    public BooksPane(BooksDbToBeHandled booksDb) {
        controller = new Controller(booksDb, this);
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.setResizable(true);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus();

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, genreSearchBox, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();

        booksTable.setEditable(false);
        booksTable.setPlaceholder(new Label("No rows to display"));
        //Book book = booksTable.getSelectionModel().getSelectedItem();

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Genre> genreCol = new TableColumn<>("Genre");
        TableColumn<Book, Long> ratingCol = new TableColumn<>("Rating");
        TableColumn<Book, ArrayList> authorCol = new TableColumn<>("Author");
        //TableColumn<Book, Date> authorDobCol = new TableColumn<>("Date of birth");
        booksTable.getColumns().addAll(titleCol, isbnCol, genreCol, ratingCol, authorCol);//, authorDobCol
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.2));
        authorCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.4));
        isbnCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.2));

        // define how to fill data for each cell,
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("authors"));
        //authorDobCol.setCellValueFactory(new PropertyValueFactory<>("authorDob"));

        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");


        searchModeBox.getSelectionModel().getSelectedItem();
        genreSearchBox = new ComboBox<>();
        genreSearchBox.setPromptText("Genre");
        genreSearchBox.getItems().addAll(Genre.values());
        genreSearchBox.setVisible(false);
        searchModeBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(searchModeBox.getValue().equals(SearchMode.Genre)){
                    searchField.setVisible(false);
                    genreSearchBox.setVisible(true);
                } else {
                    searchField.setVisible(true);
                    genreSearchBox.setVisible(false);
                }
            }
        });

        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                if(searchModeBox.getValue().equals(SearchMode.Genre) && genreSearchBox.getValue() != null){
                    Genre genre = genreSearchBox.getValue();
                    controller.onSearchSelected(genre.toString(), mode);
                }else if (!searchModeBox.getValue().equals(SearchMode.Genre)) {
                    controller.onSearchSelected(searchFor, mode);
                }
            }
        });
        searchButton.setDisable(true);
    }

    private void initMenus() {

        fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addEventHandler(ActionEvent.ACTION, e -> controller.exit());
        MenuItem connectItem = new MenuItem("Connect to Db");
        connectItem.addEventHandler(ActionEvent.ACTION, connectEH());
        MenuItem disconnectItem = new MenuItem("Disconnect");
        disconnectItem.addEventHandler(ActionEvent.ACTION, disConnectEH());
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        manageMenu = new Menu("Manage");
        MenuItem addBookItem = new MenuItem("Add Book");
        addBookItem.addEventHandler(ActionEvent.ACTION, addBookEH());
        MenuItem removeItem = new MenuItem("Remove Book");
        removeItem.addEventHandler(ActionEvent.ACTION, removeBookEH());
        MenuItem addAuthorItem = new MenuItem("Add Author");
        addAuthorItem.addEventHandler(ActionEvent.ACTION, addAuthorsEH());
        MenuItem ratingItem = new MenuItem("Give rating");
        ratingItem.addEventHandler(ActionEvent.ACTION, updateRatingEH());
        manageMenu.getItems().addAll(addBookItem, removeItem, addAuthorItem, ratingItem);
        manageMenu.setDisable(true);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, manageMenu);
    }

    private EventHandler connectEH(){
        EventHandler<ActionEvent> connectEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UserDialog userDialog = new UserDialog();
                Optional<User> result = userDialog.showAndWait();
                User user = null;
                if (result.isPresent()){
                    user = result.get();

                    controller.connectToDB(user.getUserName(), user.getPsw());
                }
            }
        };
        return connectEventHandler;
    }

    public EventHandler disConnectEH(){
        EventHandler<ActionEvent> disConnectEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.disConnectDB();
                booksInTable.clear();

            }
        };
        return disConnectEventHandler;
    }

    private EventHandler addBookEH(){
        EventHandler<ActionEvent> addBookEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AddBookDialog addBookDialog = new AddBookDialog();
                Optional<Book> result = addBookDialog.showAndWait();
                Book bookTmp = null;
                if (result.isPresent()) {
                    bookTmp = result.get();
                    controller.onAddBookSelected(bookTmp.getIsbn().toString(), bookTmp.getTitle(), bookTmp.getGenre());
                } else {
                    System.out.println("Canceled");
                }
            }
        };
        return addBookEventHandler;
    }

    private EventHandler removeBookEH(){
        EventHandler<ActionEvent> removeBookEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                RemoveBookDialog removeBookDialog = new RemoveBookDialog();
                Optional<ISBN> result = removeBookDialog.showAndWait();
                ISBN isbnString;
                if (result.isPresent()) {
                    isbnString = result.get();
                    controller.onRemoveBookSelected(isbnString.toString());
                } else {
                    System.out.println("Canceled");
                }
            }
        };
        return removeBookEventHandler;
    }

    private EventHandler addAuthorsEH(){
        EventHandler<ActionEvent> addAuthorEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AddAuthorDialog addAuthorDialog = new AddAuthorDialog();
                Optional<Author> result = addAuthorDialog.showAndWait();
                Author author = null;
                if (result.isPresent()) {
                    author = result.get();
                    controller.onAddAuthorSelected(author.getIsbnToWrite(), author.getAuthorName(), author.getDob());
                } else {
                    System.out.println("Canceled");
                }
            }
        };
        return addAuthorEventHandler;
    }

    private EventHandler updateRatingEH(){
        EventHandler<ActionEvent> ratingEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GiveRating giveRatingDialog = new GiveRating();
                Optional<Book> result = giveRatingDialog.showAndWait();
                Book bookTmp = null;
                if (result.isPresent()) {
                    bookTmp = result.get();
                    controller.onRatingSelected(bookTmp.getIsbn().toString(),  bookTmp.getRating());
                } else {
                    System.out.println("Canceled");
                }
            }
        };
        return ratingEventHandler;
    }

}
