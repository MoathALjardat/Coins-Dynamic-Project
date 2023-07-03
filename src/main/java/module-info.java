module com.example.dp2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.dp2 to javafx.fxml;
    exports com.example.dp2;
}