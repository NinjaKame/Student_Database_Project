module Student.Database.Project {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;

    opens StudentDatabase;
    opens StudentDatabase.Controller;
    opens StudentDatabase.Model;
    opens StudentDatabase.Service;
}