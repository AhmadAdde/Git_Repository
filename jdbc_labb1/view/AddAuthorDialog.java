package com.example.jdbc_labb1.view;

import com.example.jdbc_labb1.model.Author;
import com.example.jdbc_labb1.model.ISBN;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import java.sql.Date;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class AddAuthorDialog extends Dialog<Author> {

    private final TextField nameField = new TextField();
    private final TextField dateField = new TextField();
    private final TextField isbnField = new TextField();

    public AddAuthorDialog() {
        buildAddAuthorDialog();
    }

    private void buildAddAuthorDialog() {

        this.setTitle("Add a new author");
        this.setResizable(false); // really?

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Name "), 1, 1);
        grid.add(nameField, 2, 1);
        grid.add(new Label("Date of birth "), 1, 2);
        grid.add(dateField, 2, 2);
        grid.add(new Label("Isbn "), 1, 3);
        grid.add(isbnField, 2, 3);
        dateField.setPromptText("YYYY-MM-DD");
        isbnField.setPromptText("13 digits");

        this.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        // this callback returns the result from our dialog, via
        // Optional<FooBook> result = dialog.showAndWait();
        // FooBook book = result.get();
        // see DialogExample, line 31-34
        this.setResultConverter(new Callback<ButtonType, Author>() {
            @Override
            public Author call(ButtonType b) {
                Author result = null;
                if (b == buttonTypeOk) {
                    if (isValidData()) {
                        result = new Author(nameField.getText(), Date.valueOf(dateField.getText()));
                        result.isbnToWrite(isbnField.getText());
                    }
                }
                clearFormData();
                return result;
            }
        });

        // add an event filter to keep the dialog active if validation fails
        // (yes, this is ugly in FX)
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
        Pattern datePattern = Pattern.compile("[12]{1}[0-9]{3}[-][01]{1}[0-9]{1}[-][0-2]{1}[0-9]{1}");
        if (!datePattern.matcher(dateField.getText()).matches() || !Date.valueOf(dateField.getText()).before(Date.valueOf(LocalDate.now()))){
            return false;
        }
        if (!ISBN.isValidIsbn(isbnField.getText())) {
            return false;
        }
        if (nameField.getText().isEmpty()){
            return false;
        }
        return true;
    }

    private void clearFormData() {
        nameField.setText("");
        dateField.setText("");
        isbnField.setText("");
    }

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private void showErrorAlert(String title, String info) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(info);
        errorAlert.show();
    }
}
