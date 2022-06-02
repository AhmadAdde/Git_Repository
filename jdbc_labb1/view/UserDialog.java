package com.example.jdbc_labb1.view;

import com.example.jdbc_labb1.model.User;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class UserDialog extends Dialog<User>{

    private final TextField UserNameField = new TextField();
    private final PasswordField pswField = new PasswordField();

    public UserDialog() {
        buildAddAuthorDialog();
    }

    private void buildAddAuthorDialog() {

        this.setTitle("Sign in");
        this.setResizable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("User Name "), 1, 1);
        grid.add(UserNameField, 2, 1);
        grid.add(new Label("Password "), 1, 2);
        grid.add(pswField, 2, 2);

        this.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        // this callback returns the result from our dialog, via
        // Optional<FooBook> result = dialog.showAndWait();
        // FooBook book = result.get();
        // see DialogExample, line 31-34
        this.setResultConverter(new Callback<ButtonType, User>() {
            @Override
            public User call(ButtonType b) {
                User result = null;
                if (b == buttonTypeOk) {
                    if (isValidData()) {
                        result = new User(UserNameField.getText(), pswField.getText());
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
                }
            }
        });
    }

    private boolean isValidData() {
        if (pswField.getText().isEmpty()) {
            return false;
        }
        if (UserNameField.getText().isEmpty()){
            return false;
        }
        return true;
    }

    private void clearFormData() {
        UserNameField.setText("");
        pswField.setText("");
    }

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private void showErrorAlert(String title, String info) {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(info);
        errorAlert.show();
    }
}
