package com.example.jdbc_labb1;

import com.example.jdbc_labb1.model.BooksDbToBeHandled;
import com.example.jdbc_labb1.view.BooksPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        BooksDbToBeHandled booksDb = new BooksDbToBeHandled();
        BooksPane root = new BooksPane(booksDb);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Books Database Client");
        primaryStage.setOnCloseRequest(event -> {
            try {
                booksDb.disconnect();
            } catch (Exception e) {}
        });

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> root.disConnectEH());
        primaryStage.show();
    }

    public static void main(String[] args) throws SQLException {
        launch();

    }
}