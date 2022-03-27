package StudentDatabase.Controller;

import StudentDatabase.Model.Gender;
import StudentDatabase.Model.Student;
import StudentDatabase.Service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UIController implements Initializable {
    private static final StudentService connectDB = new StudentService();
    private final Map<Integer, Student> studentList = new HashMap<>();
    ObservableList<Student> tableList;
//    String[] suggest = {"male","female","mechanical"};

    @FXML
    private TableView<Student> tableView;

    @FXML
    private TableColumn<Student, Integer> idCol;

    @FXML
    private TableColumn<Student, String> firstNameCol, lastNameCol, majorCol;

    @FXML
    private TableColumn<Student, Gender> sexCol;

    @FXML
    private TextField studentIDfield, firstNameField, lastNameField, searchFill;

    @FXML
    private ChoiceBox<String> genderChoice, majorChoice;

    @FXML
    private Label isValidID, isValidFirst, isValidLast, isSelectedSex, isSelectedMajor, totalLabel;

    @FXML
    private ToggleGroup searchFilter;

    private boolean checkInput() {
        String checkName = "^[A-Z]{1}[a-z]{1,}$";
        Pattern pattern = Pattern.compile(checkName);
        Thread thread1 = new Thread(() -> {
            if (!studentIDfield.getText().isBlank()) {
                boolean checkID = checkID();
            }
        });
        Thread thread2 = new Thread(() -> {
            String stuFirstName = firstNameField.getText();
            Matcher matcher = pattern.matcher(stuFirstName);
            if (!matcher.matches()) {
                isValidFirst.setVisible(true);
            }
        });
        Thread thread3 = new Thread(() -> {
            String stuLastName = lastNameField.getText();
            Matcher matcher = pattern.matcher(stuLastName);
            if (!matcher.matches()) {
                isValidLast.setVisible(true);
            }
        });
        Thread thread4 = new Thread(() -> {
            String stuSex = genderChoice.getValue();
            String stuMajor = majorChoice.getValue();
            if (stuSex == null) {
                isSelectedSex.setVisible(true);
            }
            if (stuMajor == null) {
                isSelectedMajor.setVisible(true);
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return !isValidID.isVisible() && !isValidFirst.isVisible() && !isValidLast.isVisible()
                && !isSelectedSex.isVisible() && !isSelectedMajor.isVisible();
    }

    private Student createInput() {
        int inputID;
        if (studentIDfield.getText().isBlank()) {
            inputID = 0;
        } else {
            inputID = Integer.parseInt(studentIDfield.getText());
        }
        Gender inputGender = Gender.valueOf(genderChoice.getValue());
        return new Student(inputID, firstNameField.getText(), lastNameField.getText(), inputGender, majorChoice.getValue());
    }

    private void clearInput() {
        studentIDfield.clear();
        firstNameField.clear();
        lastNameField.clear();
        genderChoice.setValue(null);
        majorChoice.setValue(null);
    }

    public boolean addStudent(ActionEvent e) {
        if (checkInput()) {
            if (studentIDfield.getText().isBlank()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("You haven't input the student ID yet!!!");
                alert.setContentText("Do you want to create a new student with random ID");
                ButtonType answer = alert.showAndWait().get();
                if (answer == ButtonType.CANCEL) {
                    System.out.println("False to add");
                    return false;
                }
            }
            Student student = createInput();
            if (studentList.containsKey(student.getStudentID())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Failed to add new student to the list\n" + "Student with ID: " + student.getStudentID() + " already exists in the list.");
                alert.setContentText("Please input another ID for the new student.");
                alert.show();
                return false;
            } else {
                studentList.put(student.getStudentID(), student);
                connectDB.insertData(student);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Student " + student.getFirstName() + " " + student.getLastName() + " with ID: " + student.getStudentID() + " successfully added to the list.");
                alert.setContentText("Welcome to Marvel Universe !!!");
                alert.show();
                clearInput();
                refreshTable();
                return true;
            }
        } else {
            System.out.println("False to add");
            return false;
        }
    }

    public void deleteStudent(ActionEvent e) {
        if (studentIDfield.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Could NOT found the student to DELETE.");
            alert.setContentText("Please input the studentID.");
            alert.show();
        } else if (checkID()) {
            Integer delStudentID = Integer.valueOf(studentIDfield.getText());
            if (studentList.containsKey(delStudentID)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("You're about to delete student with ID: " + studentIDfield.getText());
                alert.setContentText("Are you sure you want to delete?");
                ButtonType answer = alert.showAndWait().get();
                if (answer == ButtonType.OK) {
                    connectDB.deleteData(studentList.get(delStudentID));
                    studentList.remove(delStudentID);
                    clearInput();
                    refreshTable();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Could NOT found the student with ID: " + studentIDfield.getText());
                alert.setContentText("Please check the studentID again.");
                alert.show();
                System.out.println("False to delete");
            }
        } else {
            System.out.println("False to delete");
        }
    }

    public void updateStudent(ActionEvent e) {
        if (checkInput() && !studentIDfield.getText().isBlank()) {
            Integer updateStudentID = Integer.valueOf(studentIDfield.getText());
            if (studentList.containsKey(updateStudentID)) {
                studentList.get(updateStudentID).setFirstName(firstNameField.getText());
                studentList.get(updateStudentID).setLastName(lastNameField.getText());
                studentList.get(updateStudentID).setStuGender(Gender.valueOf(genderChoice.getValue()));
                studentList.get(updateStudentID).setStuMajor(majorChoice.getValue());
                connectDB.updateData(createInput());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Student with ID: " + updateStudentID + " successfully updated information.");
                alert.setContentText("Welcome to Marvel Universe !!!");
                alert.show();
                clearInput();
                refreshTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Could NOT found the student with ID: " + updateStudentID);
                alert.setContentText("Please check the studentID again.");
                alert.show();
            }
        } else if (studentIDfield.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Could NOT found the student to UPDATE information.");
            alert.setContentText("Please input the studentID.");
            alert.show();
//            isValidID.setVisible(true);
            System.out.println("False to update");
        } else {
            System.out.println("False to update");
        }
    }

    public void printData(ActionEvent e) {
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A3, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        Stage primaryStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        if (printerJob.showPrintDialog(primaryStage.getOwner()) && printerJob.printPage(pageLayout, tableView)) {
            printerJob.endJob();
        }
    }

    private boolean checkID() {
        String checkID = "^[1-9]{1}[0-9]{5}$";
        String stuID = studentIDfield.getText();
        Pattern pattern = Pattern.compile(checkID);
        Matcher matcher = pattern.matcher(stuID);
        if (matcher.matches()) {
            return true;
        } else {
            isValidID.setVisible(true);
            return false;
        }
    }

    public void suggestTyping(MouseEvent e) {
        System.out.println("calling inside initialize");
        String filter = ((RadioButton) searchFilter.getSelectedToggle()).getId();
//        connectDB.getSuggestData(filter)
//                .forEach(System.out::print);
        TextFields.bindAutoCompletion(searchFill, connectDB.getSuggestData(filter));
    }

    public void searchStudent(ActionEvent e) {
        if (searchFilter.getSelectedToggle() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Failed to perform searching student.");
            alert.setContentText("Please select the search filter.");
            alert.show();
        } else {
            String filter = ((RadioButton) searchFilter.getSelectedToggle()).getId();
            String value = searchFill.getText();
            tableList = FXCollections.observableArrayList(connectDB.searchData(filter, value));
            if (tableList.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Could NOT found any student with " + ((RadioButton) searchFilter.getSelectedToggle()).getText() + ": " + value);
                alert.setContentText("Please check the input searching again.");
                totalLabel.setText("Search result:\n" +
                        "Found 0 students.");
                alert.showAndWait();
                searchFill.clear();
            } else {
                tableView.setItems(tableList);
                totalLabel.setText("Search result:\n" +
                        "Found " + tableList.size() + " students.");
//                tableView.refresh();
            }
        }
    }

    public void clearSearch(ActionEvent e) {
        if (searchFilter.getSelectedToggle() != null) {
            searchFilter.getSelectedToggle().setSelected(false);
        }
        searchFill.clear();
        refreshTable();
    }

    public void clearIDWarning() {
        if (isValidID.isVisible()) {
            studentIDfield.clear();
            isValidID.setVisible(false);
        }
    }

    public void clearFirstNameWarning() {
        if (isValidFirst.isVisible()) {
            firstNameField.clear();
            isValidFirst.setVisible(false);
        }
    }

    public void clearLastNameWarning() {
        if (isValidLast.isVisible()) {
            lastNameField.clear();
            isValidLast.setVisible(false);
        }
    }

    public void clearMajorWarning() {
        if (isSelectedMajor.isVisible()) {
            isSelectedMajor.setVisible(false);
        }
    }

    public void clearGenderWarning() {
        if (isSelectedSex.isVisible()) {
            isSelectedSex.setVisible(false);
        }
    }

    private void refreshTable() {
        tableList = FXCollections.observableArrayList(connectDB.loadData());
        tableView.setItems(tableList);
        totalLabel.setText("Total " + studentList.size() + " students.");
        tableView.refresh();
        System.out.println("refreshTable method call");
    }

    public void showSelectRow(MouseEvent e) {
        clearIDWarning();
        clearFirstNameWarning();
        clearLastNameWarning();
        clearGenderWarning();
        clearMajorWarning();
        Student selectedStudent = tableView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            studentIDfield.setText(String.valueOf(selectedStudent.getStudentID()));
            firstNameField.setText(selectedStudent.getFirstName());
            lastNameField.setText(selectedStudent.getLastName());
            genderChoice.setValue(String.valueOf(selectedStudent.getStuGender()));
            majorChoice.setValue(selectedStudent.getStuMajor());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        sexCol.setCellValueFactory(new PropertyValueFactory<>("stuGender"));
        majorCol.setCellValueFactory(new PropertyValueFactory<>("stuMajor"));
        System.out.println("initialize method call");
        List<Student> mySqlList = connectDB.loadData();
        mySqlList.forEach(student -> studentList.put(student.getStudentID(), student));
        tableList = FXCollections.observableArrayList(mySqlList);
        tableView.setItems(tableList);
        totalLabel.setText("Total " + studentList.size() + " students.");
//        TextFields.bindAutoCompletion(searchFill, suggest);
    }


//    This is code for practicing functional interfaces and java8 stream API

    public static void main(String[] args) {

        List<Student> studentStream = connectDB.loadData()
                .stream()
                .sorted(Comparator.comparing(Student::getFirstName))
//                .filter(s -> s.getStuGender().equals(Gender.MALE))
//                .filter(s -> s.getStuMajor().equals("Business Administrator"))
//                .map(Student::getFirstName)
//                .distinct()
//                .limit(5)
                .collect(Collectors.toList());
//        studentStream.forEach(s -> System.out.println(s.getFirstName() + " " + s.getLastName() + "  " + s.getStuGender()));

//        Map<Integer,Student> studentMap = studentStream.stream()
////                .limit(5)
//                .collect(Collectors.toMap(Student::getStudentID, s -> s));
//
//        studentMap.forEach((x, y) -> System.out.println(x + " ID " + y.getFirstName() + " students."));

        studentStream.stream()
                .collect(Collectors.groupingBy(Student::getStuMajor))
                .forEach((x, y) -> {
                    System.out.println("----------" + x + "----------" + Thread.currentThread().getName());
                    y.forEach(s -> System.out.print(s.getFirstName() + s.getLastName() + ", "));
                    System.out.println();
                });

        studentStream.stream()
                .collect(Collectors.groupingBy(Student::getStuMajor, Collectors.counting()))
                .forEach((x, y) -> System.out.println(x + " contains " + y + " students."));

//        studentStream.parallelStream().forEach(s -> System.out.println(Thread.currentThread().getName() + ": " + s.getLastName() + " " + s.getStuGender()));

        Predicate<Student> predicate = t -> t.getStuGender().equals(Gender.MALE);
        Supplier<Student> supplier = () -> studentStream.get(new Random().nextInt(40));
        Consumer<Student> consumer = t -> {
            String test = t.getStudentID() + " " + t.getFirstName() + " " + t.getLastName() + " " + t.getStuGender();
            System.out.println(test);};

        System.out.println(predicate.test(supplier.get()));
        consumer.accept(supplier.get());

        Function<Student,String> function = student -> {
            System.out.println("---------------------------------");
            return student.getFirstName() + " " +student.getLastName() + " " + student.getStuGender() + " " + student.getStuMajor();
        };
        int randomIndex = new Random().nextInt(40);
        System.out.println(randomIndex);
        System.out.println(function.apply(connectDB.loadData().get(randomIndex)));

        Function<Student,Boolean> function1 = predicate::test;
        System.out.println(function1.apply(connectDB.loadData().get(randomIndex)));
        /*
                            <String fx:value="Business Administrator" />
                            <String fx:value="Mechanical" />
                            <String fx:value="Electrical" />
                            <String fx:value="Construction" />
                            <String fx:value="Computer Science" />
                            <String fx:value="Chemical" />
                            <String fx:value="Biological" />
                            <String fx:value="Materials" />
                            <String fx:value="Petroleum geology" />
        */
    }
}
