package com.example.jdbc_labb1.view;

import com.example.jdbc_labb1.model.Book;
import com.example.jdbc_labb1.model.ISBN;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class GiveRating extends Dialog<Book> {
    private final TextField isbnField = new TextField();
    private final ComboBox<Integer> ratingChoice = new ComboBox(FXCollections
            .observableArrayList(1, 2, 3, 4, 5));

    public GiveRating() {
        buildGiveRatingDialog();
    }

    private void buildGiveRatingDialog() {

        this.setTitle("Give rating");
        this.setResizable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Isbn "), 1, 1);
        grid.add(isbnField, 2, 1);
        grid.add(new Label("Rating "), 1, 2);
        grid.add(ratingChoice, 2, 2);
        isbnField.setPromptText("13 digits");


        this.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        this.setResultConverter(new Callback<ButtonType, Book>() {
            @Override
            public Book call(ButtonType b) {
                Book result = null;
                if (b == buttonTypeOk) {
                    if (isValidData()) {
                        result = new Book(
                                isbnField.getText(),
                                null,
                                null,
                                ratingChoice.getValue().doubleValue()
                        );
                    }
                }

                clearFormData();
                return result;
            }
        });

        Button okButton = (Button) this.getDialogPane().lookupButton(buttonTypeOk);
        okButton.addEventFilter(ActionEvent.ACTION, new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!isValidData()) {
                    event.consume();
                    showErrorAlert("Form error", "Invalid input");
                }
            }
        });
    }

    private boolean isValidData() {
        if (!ISBN.isValidIsbn(isbnField.getText())) {
            return false;
        }
        if (ratingChoice.getValue() == null){
            return false;
        }
        return true;
    }

    private void clearFormData() {
        isbnField.setText("");
        ratingChoice.setValue(null);
    }

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private void showErrorAlert(String title, String info) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(info);
        errorAlert.show();
    }
}
