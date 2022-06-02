module com.example.welcometojava {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.welcometojava to javafx.fxml;
    exports com.example.welcometojava;
}