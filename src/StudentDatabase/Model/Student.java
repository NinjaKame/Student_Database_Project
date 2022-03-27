package StudentDatabase.Model;

import java.util.Random;

public class Student {
    private final int studentID;
    private String firstName;
    private String lastName;
    private Gender stuGender;
    private String stuMajor;

    public Student(int studentID, String firstName, String lastName, Gender gender, String major) {
        if (studentID == 0){
            int createID = 0;
            while (createID < 100000){
                Random rnd = new Random();
                createID = rnd.nextInt(999999);
                System.out.println("****Generating ID...");
            }
            this.studentID = createID;
        } else {
            this.studentID = studentID;
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.stuGender = gender;
        this.stuMajor = major;
    }

    @Override
    public int hashCode() {
        System.out.println("hash code call");
        return firstName.hashCode() + lastName.hashCode() +93;
    }

    @Override
    public boolean equals(Object obj) {
//        System.out.println("equals method call");
        if (this == obj){
            return true;
        } else if (obj instanceof Student){
            return this.studentID == ((Student) obj).studentID ;
        } else {
            return false;
        }
    }

    public int getStudentID() {
        return studentID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getStuGender() {
        return stuGender;
    }

    public void setStuGender(Gender stuGender) {
        this.stuGender = stuGender;
    }

    public String getStuMajor() {
        return stuMajor;
    }

    public void setStuMajor(String stuMajor) {
        this.stuMajor = stuMajor;
    }
}
