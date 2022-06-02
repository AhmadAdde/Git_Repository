package com.example.jdbc_labb1.view;

import com.example.jdbc_labb1.model.ISBN;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class RemoveBookDialog extends Dialog<ISBN> {

    private final TextField isbnField = new TextField();

    public RemoveBookDialog() {
        buildRemoveBookDialog();
    }

    private void buildRemoveBookDialog() {

        this.setTitle("Remove a book");
        this.setResizable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Isbn "), 1, 2);
        grid.add(isbnField, 2, 2);
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
        this.setResultConverter(new Callback<ButtonType, ISBN>() {
            @Override
            public ISBN call(ButtonType b) {
                ISBN result = null;
                if (b == buttonTypeOk) {
                    if (isValidData()) {
                        result = ISBN.createIsbn(isbnField.getText());
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
                    showErrorAlert("Form error", "Invalid ISBN");
                }
            }
        });
    }

    private boolean isValidData() {
        if (!ISBN.isValidIsbn(isbnField.getText())) {
            return false;
        }
        return true;
    }

    private void clearFormData() {
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
